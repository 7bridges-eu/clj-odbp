(ns clj-odbp.serialize.binary.record
  (:require [clj-odbp.serialize.binary.common :as c]
            [clj-odbp.serialize.binary.varint :as v])
  (:import [java.io ByteArrayOutputStream DataOutputStream]
           [java.text SimpleDateFormat]))

(defprotocol OrientType
  (getDataType [value])
  (serialize [value]))

(defn short-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Short
  OrientType
  (getDataType [value]
    (byte 2))
  (serialize [value]
    (short-type value)))

(defn integer-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Integer
  OrientType
  (getDataType [value]
    (byte 1))
  (serialize [value]
    (integer-type value)))

(defn long-type
  [value]
  (v/varint-unsigned value))

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

(deftype OrientDateTime [value]
  OrientType
  (getDataType [this]
    (byte 6))
  (serialize [this]
    (v/varint-unsigned (.getTime value))))

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
      (v/varint-unsigned (long (/ date->long 86400))))))

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
      (.write dos (byte 23) 0 1)
      (.write dos serialized-items 0 serialized-items-len)
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
      (.write dos (byte 23) 0 1)
      (.write dos serialized-items 0 serialized-items-len)
      (.toByteArray bos))))

(defn orient-embedded-set
  [value]
  (->OrientEmbeddedSet value))

(deftype OrientEmbeddedMap [value]
  OrientType
  (getDataType [this]
    (byte 12))
  (serialize [this]
    ;; TODO
    ))

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
          cid-varint (v/varint-unsigned cluster-id)
          rpos-varint (v/varint-unsigned record-position)]
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
      (.write dos serialized-items 0 serialized-items-len)
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
      (.write dos serialized-items 0 serialized-items-len)
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
          size (v/varint-unsigned (count value))
          key-values (first (for [[k v] value]
                              (serialize-key-value k v)))]
      (.write dos size 0 (count size))
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
          precision (re-find #"[0-9]+" (clojure.string/replace value "." ""))
          value-size (v/varint-unsigned (count precision))
          decimals (second (clojure.string/split (str value) #"[.]"))
          scale (v/varint-unsigned (count decimals))
          serialized-value (serialize value)]
      (.write dos scale 0 (count scale))
      (.write dos value-size 0 (count value-size))
      (.write dos serialized-value 0 (count serialized-value))
      (.toByteArray bos))))

;; (serialization-version:byte)(class-name:string)(header:byte[])(data:byte[])

;; (field:field-definition)+
;; field-definition -> (field-type:varint)(field-contents:byte[])
;; field-content -> (field-name:string)(pointer-to-data-structure:int32)
;;                  (data-type:byte)
(defn serialize-header
  [record-values data]
  )

(defn serialize-data
  [data]
  (serialize data))

(defn serialize-record
  [record]
  (let [bos (ByteArrayOutputStream.)
        dos (DataOutputStream. bos)
        version (byte 0)
        class (serialize (first record))
        record-values (get record class)
        data (serialize-data record-values)
        header (serialize-header record-values data)]
    (.write dos version 0 (count version))
    (.write dos class 0 (count class))
    (.write dos header 0 (count header))
    (.write dos data 0 (count data))
    (.toByteArray bos)))
