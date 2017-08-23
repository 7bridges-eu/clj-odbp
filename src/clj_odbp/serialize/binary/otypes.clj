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

(ns clj-odbp.serialize.binary.otypes
  (:require [clj-odbp.constants :as const]
            [clj-odbp.serialize.binary
             [common :as c]
             [int :as i]
             [varint :as v]]))

(def orient-types
  {:boolean-type (byte 0) :integer-type (byte 1) :short-type (byte 2)
   :long-type (byte 3) :float-type (byte 4) :double-type (byte 5)
   :datetime-type (byte 6) :string-type (byte 7) :keyword-type (byte 7)
   :binary-type (byte 8) :embedded-record-type (byte 9)
   :embedded-list-type (byte 10) :embedded-set-type (byte 11)
   :embedded-map-type (byte 12) :link-type (byte 13) :link-list-type (byte 14)
   :link-set-type (byte 15) :link-map-type (byte 16) :byte-type (byte 17)
   :custom-type (byte 20) :decimal-type (byte 21) :any-type (byte 23)})

(deftype OrientBinary [value])

(defn orient-binary
  [value]
  {:pre [(vector? value)]}
  (->OrientBinary value))

(defn link?
  [v]
  (when (string? v)
    (re-matches #"#\d+:\d+" v)))

(defn link-list?
  [l]
  (when (sequential? l)
    (every? link? l)))

(defn link-set?
  [s]
  (when (set? s)
    (every? link? s)))

(defn link-map?
  [m]
  (when (map? m)
    (let [values (vals m)]
      (every? link? values))))

(defn embedded-record?
  [r]
  (when (map? r)
    (or (contains? r :_class)
        (contains? r "@type")
        (contains? r :_version))))

(defn get-type [v]
  (cond
    (instance? Boolean v) :boolean-type
    (instance? Integer v) :integer-type
    (instance? Short v) :integer-type
    (instance? Long v) :integer-type
    (instance? Float v) :float-type
    (instance? Double v) :double-type
    (instance? Byte v) :byte-type
    (instance? java.math.BigDecimal v) :decimal-type
    (instance? java.util.Date v) :datetime-type
    (keyword? v) :keyword-type
    (instance? OrientBinary v) :binary-type
    (link? v) :link-type
    (string? v) :string-type
    (link-list? v) :link-list-type
    (link-set? v) :link-set-type
    (link-map? v) :link-map-type
    (embedded-record? v) :embedded-record-type
    (sequential? v) :embedded-list-type
    (set? v) :embedded-set-type
    (map? v) :embedded-map-type
    :else :custom-type))

(defmulti serialize (fn [value & offset] (get-type value)))

(defmethod serialize :boolean-type
  ([value]
   (if value
     [(byte 1)]
     [(byte 0)]))
  ([value offset]
   (serialize value)))

(defmethod serialize :integer-type
  ([value]
   (v/varint-unsigned value))
  ([value offset]
   (serialize value)))

(defmethod serialize :float-type
  ([value]
   (-> (java.nio.ByteBuffer/allocate 4)
       (.putFloat value)
       .array
       vec))
  ([value offset]
   (serialize value)))

(defmethod serialize :double-type
  ([value]
   (-> (java.nio.ByteBuffer/allocate 8)
       (.putDouble value)
       .array
       vec))
  ([value offset]
   (serialize value)))

(defmethod serialize :byte-type
  ([value]
   [value])
  ([value offset]
   (serialize value)))

(defmethod serialize :decimal-type
  ([value]
   (let [scale (i/int32 (.scale value))
         serialized-value (-> value
                              .unscaledValue
                              .toByteArray
                              vec)
         value-size (i/int32 (count serialized-value))]
     (vec (concat scale value-size serialized-value))))
  ([value offset]
   (serialize value)))

(defmethod serialize :datetime-type
  ([value]
   (serialize (.getTime value)))
  ([value offset]
   (serialize value)))

(defmethod serialize :string-type
  ([value]
   (let [bytes (.getBytes value "UTF-8")]
     (c/bytes-type bytes)))
  ([value offset]
   (serialize value)))

(defmethod serialize :keyword-type
  ([value]
   (serialize (name value)))
  ([value offset]
   (serialize value)))

(defmethod serialize :binary-type
  ([value]
   (c/bytes-type (.value value)))
  ([value offset]
   (serialize value)))

(defmethod serialize :link-type
  ([value]
   (let [rid (clojure.string/split (subs value 1) #":")
         cluster-id (Integer/parseInt (first rid))
         record-position (Integer/parseInt (second rid))
         cid-varint (v/varint-unsigned cluster-id)
         rpos-varint (v/varint-unsigned record-position)]
     (vec (concat cid-varint rpos-varint))))
  ([value offset]
   (serialize value)))

(defmethod serialize :link-list-type
  ([value]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         serialized-items (mapcat serialize value)]
     (vec (concat size-varint serialized-items))))
  ([value offset]
   (serialize value)))

(defmethod serialize :link-set-type
  ([value]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         serialized-items (mapcat serialize value)]
     (vec (concat size-varint serialized-items))))
  ([value offset]
   (serialize value)))

(defn serialize-key-value
  [k v]
  (let [key-type [(get orient-types (get-type k))]
        key-value (serialize k)
        link (serialize v)]
    (vec (concat key-type key-value link))))

(defmethod serialize :link-map-type
  ([value]
   (let [size (v/varint-unsigned (count value))
         key-values (mapcat (fn [[k v]] (serialize-key-value k v)) value)]
     (vec (concat size key-values))))
  ([value offset]
   (serialize value)))

(defn serialize-list-item
  [value]
  (let [t [(get orient-types (get-type value))]
        v (serialize value)]
    (into t v)))

(defmethod serialize :embedded-list-type
  ([value]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         serialized-items (vec (apply concat (map serialize-list-item value)))
         any [(byte 23)]]
     (vec (concat size-varint any serialized-items))))
  ([value offset]
   (serialize value)))

(defmethod serialize :embedded-set-type
  ([value]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         serialized-items (vec (apply concat (map serialize-list-item value)))
         any [(byte 23)]]
     (vec (concat size-varint any serialized-items))))
  ([value offset]
   (serialize value)))

(defn get-structure
  [record-map]
  (reduce
   (fn [acc k]
     (let [record-map-value (get record-map k)]
       (conj acc {:key-type (get orient-types (get-type k))
                  :field-name k
                  :position 0
                  :type (get orient-types (get-type record-map-value))
                  :value record-map-value})))
   []
   (keys record-map)))

(defn header-size
  [headers fixed-header-int]
  (+ 1                                  ; closing header
     (reduce
      (fn [acc k]
        (+ acc (count (serialize k)) fixed-header-int))
      0
      headers)))

(defn serialize-structure-values
  [structure]
  (map
   (fn [s]
     (let [v (:value s)]
       (assoc s :serialized-value (serialize v))))
   structure))

(defn oemap-positions
  [structure offset]
  (let [hsize (header-size
               (map :field-name structure) const/fixed-oemap-header-int)]
    (reduce
     (fn [acc s]
       (if (empty? acc)
         (conj acc
               (assoc s :position (+ offset hsize)))
         (conj acc
               (assoc s :position
                      (+ (count (:serialized-value (last acc)))
                         (:position (last acc)))))))
     []
     structure)))

(defn orient-int32
  [value]
  (i/int32 (int value)))

(defn positions->orient-int32
  [structure]
  (map #(update % :position orient-int32) structure))

(defn oemap->structure
  [data-map offset]
  (-> (get-structure data-map)
      serialize-structure-values
      (oemap-positions offset)
      positions->orient-int32))

(defn serialize-elements
  [header key-order]
  (reduce
   (fn [acc hk]
     (if (= hk :position)
       (conj acc (get header hk))
       (conj acc (serialize (get header hk)))))
   []
   key-order))

(defn serialize-headers
  [structure key-order]
  (mapcat
   #(serialize-elements % key-order)
   structure))

(defn serialize-data
  [structure]
  (->> structure
       (map :serialized-value)))

(defmethod serialize :embedded-map-type
  ([value]
   (serialize value 0))
  ([value offset]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         structure (oemap->structure value offset)
         key-order [:key-type :field-name :position :type]
         serialized-headers (serialize-headers structure key-order)
         serialized-data (serialize-data structure)]
     (-> (concat size-varint serialized-headers serialized-data)
         flatten
         vec))))

(defn first-elem
  [record-map offset]
  (let [f (first record-map)
        k (first f)
        v (second f)
        hsize (header-size (keys record-map) const/fixed-header-int)]
    {:key-type (get orient-types (get-type k))
     :field-name k
     :type (get orient-types (get-type v))
     :value v
     :serialized-value (serialize v (+ 1 offset hsize))
     :position (+ 1 offset hsize)}))

(defn rest-elem
  [record-map first-elem]
  (reduce
   (fn [acc [k v]]
     (let [last-elem (last acc)
           serialized-elem (:serialized-value last-elem)
           size-le (count serialized-elem)
           pos (+ size-le (:position last-elem))]
       (conj
        acc
        {:key-type (get orient-types (get-type k))
         :field-name k
         :type (get orient-types (get-type v))
         :value v
         :position pos
         :serialized-value (serialize v pos)})))
   (conj [] first-elem)
   (rest record-map)))

(defn record-map->structure
  [record-map offset]
  (->> (first-elem record-map offset)
       (rest-elem record-map)
       positions->orient-int32))

(defmethod serialize :embedded-record-type
  ([value]
   (serialize value 0))
  ([value offset]
   (let [version (vector (get value :_version (byte 0)))
         size (count value)
         size-varint (v/varint-unsigned size)
         class (get value :_class "")
         serialized-class (serialize class)
         serialized-class-size (count serialized-class)
         first-elem-pos (+ offset serialized-class-size)
         structure (record-map->structure (dissoc value :_class) first-elem-pos)
         key-order [:field-name :position :type]
         serialized-headers (serialize-headers structure key-order)
         end-headers [(byte 0)]
         serialized-data (serialize-data structure)]
     (-> (concat version serialized-class serialized-headers
                 end-headers serialized-data)
         flatten
         vec))))

(defmethod serialize :custom-type
  [value]
  value)
