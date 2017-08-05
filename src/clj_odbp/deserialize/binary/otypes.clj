(ns clj-odbp.deserialize.binary.otypes
  (:require [clj-odbp.deserialize.binary.varint :as v]
            [clj-odbp.deserialize.binary.buffer :as b])
  (:import [java.nio ByteBuffer]
           [java.math BigInteger BigDecimal]))

(defn- is-varint-part [n]
  (not= (bit-and n 0x80) 0x80))

(def otype-list
  [:bool-orient-type :integer-orient-type :short-orient-type
   :long-orient-type :float-orient-type :double-orient-type
   :datetime-orient-type :string-orient-type :binary-orient-type
   :embedded-record-orient-type :embedded-list-orient-type :embedded-set-orient-type
   :embedded-map-orient-type :link-orient-type :link-list-orient-type
   :link-set-orient-type :link-map-orient-type :byte-orient-type
   :transient-orient-type :date-orient-type :custom-orient-type
   :decimal-orient-type :link-bag-orient-type :any-orient-type])

(defmulti deserialize :otype)
(defmethod deserialize :default [_] nil)

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
    (apply str (map char (b/buffer-take! buffer size)))))

(defmethod deserialize :binary-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)]
    (b/buffer-take! buffer size)))

(defmethod deserialize :embedded-record-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :embedded-list-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)
        collection-type (call :byte-orient-type buffer)
        otype (nth otype-list collection-type)]
    (into [] (repeatedly size
                         #(call otype buffer)))))

(defmethod deserialize :embedded-set-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [size (call :integer-orient-type buffer)
        collection-type (call :byte-orient-type buffer)
        otype (nth otype-list collection-type)]
    (into #{} (repeatedly size
                          #(call otype buffer)))))

(defmethod deserialize :embedded-map-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  nil)

(defmethod deserialize :link-orient-type
  [{:keys [buffer position] :or {position nil}}]
  (when position
    (b/buffer-set-position! buffer position))
  (let [cluster-id (call :integer-orient-type buffer)
        record-position (call :integer-orient-type buffer)]
    {:cluster-id cluster-id :record-position record-position}))

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
       (let [key-index (int (call :byte-orient-type buffer))
             key-type (nth otype-list key-index)
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
  (let [scale (call :integer-orient-type buffer)
        size (call :integer-orient-type buffer)
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
  nil)
