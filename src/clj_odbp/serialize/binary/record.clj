(ns clj-odbp.serialize.binary.record
  (:require [clj-odbp.constants :as const]
            [clj-odbp.serialize.binary.common :as c]
            [clj-odbp.serialize.binary.int :as i]
            [clj-odbp.serialize.binary.varint :as v]
            [clojure.string :as string])
  (:import [java.io ByteArrayOutputStream DataOutputStream]
           [java.text SimpleDateFormat]))

(defprotocol OrientType
  (getDataType [value])
  (serialize [value]))

(defn short-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Short
  OrientType
  (getDataType [value]
    (byte 2))
  (serialize [value]
    (short-type value)))

(defn integer-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Integer
  OrientType
  (getDataType [value]
    (byte 1))
  (serialize [value]
    (integer-type value)))

(defn long-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Long
  OrientType
  (getDataType [value]
    (byte 3))
  (serialize [value]
    (long-type value)))

(defn byte-type
  [value]
  value)

(extend-type java.lang.Byte
  OrientType
  (getDataType [value]
    (byte 17))
  (serialize [value]
    (byte-type value)))

(defn boolean-type
  [value]
  (if value
    (byte 1)
    (byte 0)))

(extend-type java.lang.Boolean
  OrientType
  (getDataType [value]
    (byte 0))
  (serialize [value]
    (boolean-type value)))

(defn float-type
  [value]
  (let [bos (ByteArrayOutputStream. 4)
        dos (DataOutputStream. bos)]
    (.writeFloat dos value)
    (.toByteArray bos)))

(extend-type java.lang.Float
  OrientType
  (getDataType [value]
    (byte 4))
  (serialize [value]
    (float-type value)))

(defn double-type
  [value]
  (let [bos (ByteArrayOutputStream. 8)
        dos (DataOutputStream. bos)]
    (.writeDouble dos value)
    (.toByteArray bos)))

(extend-type java.lang.Double
  OrientType
  (getDataType [value]
    (byte 5))
  (serialize [value]
    (double-type value)))

(defn string-type
  [value]
  (let [bytes (.getBytes value)]
    (c/bytes-type bytes)))

(extend-type java.lang.String
  OrientType
  (getDataType [value]
    (byte 7))
  (serialize [value]
    (string-type value)))

(defn keyword-type
  [value]
  (string-type (name value)))

(extend-type clojure.lang.Keyword
  OrientType
  (getDataType [value]
    (byte 7))
  (serialize [value]
    (keyword-type value)))

(defn coll-type
  [value]
  (map serialize value))

(extend-type clojure.lang.PersistentList
  OrientType
  (serialize [value]
    (coll-type value)))

(extend-type clojure.lang.PersistentVector
  OrientType
  (serialize [value]
    (coll-type value)))

(extend-type clojure.lang.PersistentHashSet
  OrientType
  (serialize [value]
    (coll-type value)))

(defn map-type
  [value]
  (->> value
       vec
       flatten
       (map serialize)))

(extend-type clojure.lang.PersistentArrayMap
  OrientType
  (serialize [value]
    (map-type value)))

(deftype OrientInt32 [value]
  OrientType
  (serialize [this]
    (i/int32 (int value))))

(defn orient-int32
  [value]
  (->OrientInt32 value))

(deftype OrientInt64 [value]
  OrientType
  (serialize [this]
    (i/int64 value)))

(defn orient-int64
  [value]
  (->OrientInt64 value))

(deftype OrientDateTime [value]
  OrientType
  (getDataType [this]
    (byte 6))
  (serialize [this]
    (byte-array (v/varint-unsigned (.getTime value)))))

(defn orient-date-time
  [value]
  (->OrientDateTime value))

(deftype OrientDate [value]
  OrientType
  (getDataType [this]
    (byte 19))
  (serialize [this]
    (let [formatter (SimpleDateFormat. "dd/MM/yyyy")
          date (.value this)
          date-without-time (.parse formatter (.format formatter date))
          date->long (.getTime date-without-time)]
      (byte-array (v/varint-unsigned (long (/ date->long 86400)))))))

(defn orient-date [value]
  (->OrientDate value))

(deftype OrientBinary [value]
  OrientType
  (getDataType [this]
    (byte 8))
  (serialize [this]
    (c/bytes-type value)))

(defn orient-binary
  [value]
  (->OrientBinary value))

(deftype OrientEmbedded [value]
  OrientType
  (getDataType [this]
    (byte 9))
  (serialize [this]
    (->> value
         vec
         flatten
         (map serialize))))

(defn orient-embedded
  [value]
  (->OrientEmbedded value))

(deftype OrientEmbeddedList [value]
  OrientType
  (getDataType [this]
    (byte 10))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          serialized-items (map serialize value)
          serialized-items-len (count serialized-items)]
      (.write dos size-varint 0 size-varint-len)
      (.writeByte dos (byte 23))
      (doall (for [si serialized-items] (.write dos si 0 (count si))))
      (.toByteArray bos))))

(defn orient-embedded-list
  [value]
  (->OrientEmbeddedList value))

(deftype OrientEmbeddedSet [value]
  OrientType
  (getDataType [this]
    (byte 11))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          serialized-items (map serialize value)
          serialized-items-len (count serialized-items)]
      (.write dos size-varint 0 size-varint-len)
      (.writeByte dos (byte 23))
      (doall (for [si serialized-items] (.write dos si 0 (count si))))
      (.toByteArray bos))))

(defn orient-embedded-set
  [value]
  (->OrientEmbeddedSet value))

(defn get-headers
  [record-map]
  (let [record-map-keys (keys record-map)]
    (vec
     (for [k record-map-keys]
       (let [record-map-value (get record-map k)]
         {:key-type (getDataType k)
          :field-name k
          :type (getDataType record-map-value)
          :value record-map-value})))))

(defn header-size
  [headers]
  (reduce
   (fn [acc k]
     (+ acc (count (serialize k)) const/fixed-header-int))
   0
   (map :field-name headers)))

(defn serialize-data
  [headers record-map]
  (reduce
   (fn [acc h]
     (let [k (:field-name h)
           v (get record-map k)]
       (conj acc {k (serialize v)})))
   []
   headers))

(defn get-in-data [k data]
  (-> (filter #(= k (first (keys %))) data)
      first
      (get k)))

(defn initialize-position
  [headers data]
  (map
   (fn [h]
     (let [k (:field-name h)
           v (get-in-data k data)]
       (assoc h :position (count v))))
   headers))

(defn calculate-position
  [headers]
  (let [field-keys (map :field-name headers)
        field-values (map :value headers)
        header-size (header-size headers)]
    (zipmap
     field-keys
     (map orient-int32
          (reduce
           (fn [acc v]
             (if (empty? acc)
               (conj acc (+ 1 header-size))
               (conj acc (+ (last acc) (count (serialize v))))))
           []
           field-values)))))

(defn position
  [headers data]
  (let [hs (initialize-position headers data)
        positions (calculate-position hs)]
    (for [h hs]
      (assoc h :position
             (get positions (:field-name h))))))

(defn record-headers-position
  [headers serialized-class]
  (for [h headers]
    (assoc h :position
           (orient-int32 (+ 1
                            (count serialized-class)
                            (.value (:position h)))))))

(defn serialize-header
  [header key-order]
  (reduce
   (fn [acc hk]
     (conj acc (serialize (get header hk))))
   []
   key-order))

(defn serialize-oemap-headers
  [headers data]
  (let [headers-default-pos (position headers data)
        oemap-headers-pos (record-headers-position headers-default-pos 0)]
    (mapcat
     #(serialize-header % [:key-type :field-name :position :type])
     oemap-headers-pos)))

(defn write-header
  [^DataOutputStream dos header]
  (if (= (type header) java.lang.Byte)
    (.writeByte dos header)
    (.write dos header 0 (count header))))

(deftype OrientEmbeddedMap [value]
  OrientType
  (getDataType [this]
    (byte 12))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          headers (get-headers value)
          data (serialize-data headers value)
          serialized-headers (serialize-oemap-headers headers data)]
      (.write dos size-varint 0 size-varint-len)
      (doall (map #(write-header dos %) serialized-headers))
      (doall (map #(.write dos % 0 (count %)) (mapcat vals data)))
      (.toByteArray bos))))

(defn orient-embedded-map
  [value]
  (->OrientEmbeddedMap value))

(deftype OrientRid [cluster-id record-position]
  OrientType
  (getDataType [this]
    (byte 13))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          cid-varint (i/int64 cluster-id)
          rpos-varint (i/int64 record-position)]
      (.write dos cid-varint 0 (count cid-varint))
      (.write dos rpos-varint 0 (count rpos-varint))
      (.toByteArray bos))))

(defn orient-rid
  [cluster-id record-position]
  (->OrientRid cluster-id record-position))

(deftype OrientLinkList [value]
  OrientType
  (getDataType [this]
    (byte 14))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          serialized-items (map serialize value)
          serialized-items-len (count serialized-items)]
      (.write dos size-varint 0 size-varint-len)
      (doall (for [si serialized-items] (.write dos si 0 (count si))))
      (.toByteArray bos))))

(defn orient-link-list
  [value]
  (->OrientLinkList value))

(deftype OrientLinkSet [value]
  OrientType
  (getDataType [this]
    (byte 15))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          serialized-items (map serialize value)
          serialized-items-len (count serialized-items)]
      (.write dos size-varint 0 size-varint-len)
      (doall (for [si serialized-items] (.write dos si 0 (count si))))
      (.toByteArray bos))))

(defn orient-link-set
  [value]
  (->OrientLinkSet value))

(defn serialize-key-value
  [k v]
  (if-not (map? v)
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          key-type (getDataType k)
          key-value (serialize k)
          link (serialize v)]
      (.writeByte dos key-type)
      (.write dos key-value 0 (count key-value))
      (.write dos link 0 (count link))
      (.toByteArray bos))))

(deftype OrientLinkMap [value]
  OrientType
  (getDataType [this]
    (byte 16))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (byte-array (v/varint-unsigned (count value)))
          key-values (first (for [[k v] value]
                              (serialize-key-value k v)))]
      (.write dos size 0 (count size))
      (.write dos key-values 0 (count key-values))
      (.toByteArray bos))))

(defn orient-link-map
  [value]
  (->OrientLinkMap value))

(deftype OrientDecimal [value]
  OrientType
  (getDataType [this]
    (byte 21))
  (serialize [this]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          value-str (.toString value)
          precision (re-find #"[0-9]+" (string/replace value-str "." ""))
          value-size (i/int32 (count precision))
          decimals (second (string/split value-str #"[.]"))
          scale (i/int32 (count decimals))
          serialized-value (serialize value)]
      (.write dos scale 0 (count scale))
      (.write dos value-size 0 (count value-size))
      (.write dos serialized-value 0 (count serialized-value))
      (.toByteArray bos))))

(defn orient-decimal
  [value]
  (->OrientDecimal value))

(defn serialize-record-headers
  [headers data serialized-class]
  (let [headers-default-pos (position headers data)
        record-headers-pos (record-headers-position
                            headers-default-pos serialized-class)]
    (mapcat
     #(serialize-header % [:field-name :position :type])
     record-headers-pos)))

(defn serialize-record
  [record]
  (let [bos (ByteArrayOutputStream.)
        dos (DataOutputStream. bos)
        version (byte 0)
        class (first (first record))
        serialized-class (serialize class)
        record-map (get record class)
        record-values (vals record-map)
        headers (get-headers record-map)
        data (serialize-data headers record-map)
        serialized-record-headers (serialize-record-headers
                                   headers data serialized-class)]
    (.writeByte dos version)
    (.write dos serialized-class 0 (count serialized-class))
    (doall (map #(write-header dos %) serialized-record-headers))
    (.writeByte dos (byte 0))
    (doall (map #(.write dos % 0 (count %)) (mapcat vals data)))
    (.toByteArray bos)))
