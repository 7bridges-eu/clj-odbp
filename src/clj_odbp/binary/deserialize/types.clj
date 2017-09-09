;; Copyright 2017 7bridges s.r.l.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns clj-odbp.binary.deserialize.types
  (:require [clj-odbp.binary.deserialize.varint :as v]
            [clj-odbp.binary.deserialize.buffer :as b]
            [clj-odbp.binary.deserialize.utils :as u])
  (:import [java.nio ByteBuffer]
           [java.math BigInteger BigDecimal]))

(defn- is-varint-part [n]
  (not= (bit-and n 0x80) 0x80))

(def otype-list
  [:bool-orient-type :integer-orient-type :short-orient-type
   :long-orient-type :float-orient-type :double-orient-type
   :datetime-orient-type :string-orient-type :binary-orient-type
   :record-orient-type :embedded-list-orient-type :embedded-set-orient-type
   :embedded-map-orient-type :link-orient-type :link-list-orient-type
   :link-set-orient-type :link-map-orient-type :byte-orient-type
   :transient-orient-type :date-orient-type :custom-orient-type
   :decimal-orient-type :link-bag-orient-type :any-orient-type])

(defn- get-otype
  [i]
  (->> i
       int
       (nth otype-list)))

(defmulti deserialize :otype)
(defmethod deserialize :default [_]
  (throw (Exception. "Unknown Orient type.")))

(defn call
  ([otype buffer]
   (deserialize {:otype otype :buffer buffer}))
  ([otype buffer position]
   (deserialize {:otype otype :buffer buffer :position position})))

(defmethod deserialize :bool-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [b (first (b/buffer-take! buffer 1))]
    (> b 0)))

(defmethod deserialize :byte-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (byte (first (b/buffer-take! buffer 1))))

(defmethod deserialize :integer-orient-type
  ([{:keys [buffer position] :or {position nil}}]
   (when position
     (b/buffer-set-position! buffer position))
   (let [b (b/buffer-take-upto! buffer is-varint-part)]
     (int (v/varint-signed-long b)))))

(defmethod deserialize :short-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [b (b/buffer-take-upto! buffer is-varint-part)]
    (short (v/varint-signed-long b))))

(defmethod deserialize :long-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [b (b/buffer-take-upto! buffer is-varint-part)]
    (long (v/varint-signed-long b))))

(defmethod deserialize :float-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [b (b/buffer-take! buffer 4)]
    (.getFloat (ByteBuffer/wrap (byte-array b)))))

(defmethod deserialize :double-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [b (b/buffer-take! buffer 8)]
    (.getDouble (ByteBuffer/wrap (byte-array b)))))

(defmethod deserialize :datetime-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [timestamp (call :long-orient-type buffer)]
    (java.util.Date. timestamp)))

(defmethod deserialize :string-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (u/bytes->utf8-str (b/buffer-take! buffer size))))

(defmethod deserialize :binary-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (b/buffer-take! buffer size)))

;; Record deserialization
(defn- string-type
  "Read a string from the buffer."
  [buffer length]
  (let [b (b/buffer-take! buffer length)]
    (u/bytes->utf8-str b)))

(defn- read-version
  [buffer]
  (int (first (b/buffer-take! buffer 1))))

(defn- read-class-name
  [buffer]
  (let [size (v/varint-signed-long (b/buffer-take! buffer 1))]
    (u/bytes->utf8-str (b/buffer-take! buffer size))))

(defn- read-headers
  "Read and decode the header"
  [buffer]
  (loop [field-size (v/varint-signed-long (b/buffer-take! buffer 1))
         headers []]
    (if (zero? field-size)
      headers
      (let [field-name (string-type buffer field-size)
            field-position (u/bytes->integer buffer)
            data-type (int (first (b/buffer-take! buffer 1)))]
        (recur (v/varint-signed-long (b/buffer-take! buffer 1))
               (conj headers
                     {:field-name field-name
                      :field-position field-position
                      :data-type (nth otype-list data-type)
                      :nil? (= 0 field-position)}))))))

(defn- read-record
  [headers content]
  (reduce
   (fn [record header]
     (let [{key :field-name
            position :field-position
            otype :data-type
            is-nil :nil?} header]
       (assoc
        record
        (keyword key)
        (if (true? is-nil)
          nil
          (call otype content position)))))
   {}
   headers))

(defmethod deserialize :record-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [version (read-version buffer)
        class-name (read-class-name buffer)
        headers (read-headers buffer)
        add-class (fn [m] (if (empty? class-name)
                           m
                           (assoc m :_class class-name)))]
    (conj (add-class {}) (read-record headers buffer))))

(defmethod deserialize :embedded-list-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)
        collection-type (get-otype (call :byte-orient-type buffer))]
    (into [] (repeatedly size
                         #(call collection-type buffer)))))

(defmethod deserialize :embedded-set-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)
        collection-type (get-otype (call :byte-orient-type buffer))]
    (into #{} (repeatedly size
                          #(call collection-type buffer)))))

(defn- deserialize-embedded-map-headers
  [buffer n]
  (reduce
   (fn [m _]
     (let [key-type (get-otype (call :byte-orient-type buffer))
           key-value (call key-type buffer)
           position (v/varint-unsigned-long (b/buffer-take! buffer 4))
           value-type (get-otype (call :byte-orient-type buffer))]
       (assoc m key-value value-type)))
   {}
   (range n)))

(defmethod deserialize :embedded-map-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)
        headers (deserialize-embedded-map-headers buffer size)]
    (reduce
     (fn [m k]
       (let [value-type (get headers k)
             value (deserialize {:otype value-type
                                 :buffer buffer})]
         (assoc m k value)))
     {}
     (keys headers))))

(defmethod deserialize :link-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [cluster-id (call :integer-orient-type buffer)
        record-position (call :integer-orient-type buffer)]
    (str "#" cluster-id ":" record-position)))

(defmethod deserialize :link-list-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (into [] (repeatedly size
                         #(call :link-orient-type buffer)))))

(defmethod deserialize :link-set-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (into #{} (repeatedly size
                          #(call :link-orient-type buffer)))))

(defmethod deserialize :link-map-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (reduce
     (fn [acc _]
       (let [key-type (get-otype (call :byte-orient-type buffer))
             key (call key-type buffer)
             value (call :link-orient-type buffer)]
         (assoc acc key value)))
     {}
     (range size))))

(defmethod deserialize :transient-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :date-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :custom-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :decimal-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [scale (u/bytes->integer buffer)
        size (u/bytes->integer buffer)
        data (b/buffer-take! buffer size)]
    (-> data
        byte-array
        BigInteger.
        (BigDecimal. scale))))

(defmethod deserialize :link-bag-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :any-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [otype (get-otype (call :byte-orient-type buffer))]
    (call otype buffer)))
