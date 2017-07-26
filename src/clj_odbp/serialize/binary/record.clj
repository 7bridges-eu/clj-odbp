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
  (serialize [value] [value position]))

(defn short-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Short
  OrientType
  (getDataType [value]
    (byte 2))
  (serialize
    ([value] (short-type value))
    ([value position] (serialize value))))

(defn integer-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Integer
  OrientType
  (getDataType [value]
    (byte 1))
  (serialize
    ([value] (integer-type value))
    ([value position] (serialize value))))

(defn long-type
  [value]
  (byte-array (v/varint-unsigned value)))

(extend-type java.lang.Long
  OrientType
  (getDataType [value]
    (byte 3))
  (serialize
    ([value] (long-type value))
    ([value position] (serialize value))))

(defn byte-type
  [value]
  value)

(extend-type java.lang.Byte
  OrientType
  (getDataType [value]
    (byte 17))
  (serialize
    ([value] (byte-type value))
    ([value position] (serialize value))))

(defn boolean-type
  [value]
  (if value
    (byte 1)
    (byte 0)))

(extend-type java.lang.Boolean
  OrientType
  (getDataType [value]
    (byte 0))
  (serialize
    ([value] (boolean-type value))
    ([value position] (serialize value))))

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
  (serialize
    ([value] (float-type value))
    ([value position] (serialize value))))

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
  (serialize
    ([value] (double-type value))
    ([value position] (serialize value))))

(defn string-type
  [value]
  (let [bytes (.getBytes value)]
    (c/bytes-type bytes)))

(extend-type java.lang.String
  OrientType
  (getDataType [value]
    (byte 7))
  (serialize
    ([value] (string-type value))
    ([value position] (serialize value))))

(defn keyword-type
  [value]
  (string-type (name value)))

(extend-type clojure.lang.Keyword
  OrientType
  (getDataType [value]
    (byte 7))
  (serialize
    ([value] (keyword-type value))
    ([value position] (serialize value))))

(defn coll-type
  [value]
  (map serialize value))

(extend-type clojure.lang.PersistentList
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value position] (serialize value))))

(extend-type clojure.lang.PersistentVector
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value position] (serialize value))))

(extend-type clojure.lang.PersistentHashSet
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value position] (serialize value))))

(defn map-type
  [value]
  (->> value
       vec
       flatten
       (map serialize)))

(extend-type clojure.lang.PersistentArrayMap
  OrientType
  (serialize
    ([value] (map-type value))
    ([value position] (serialize value))))

(deftype OrientInt32 [value]
  OrientType
  (serialize [this]
    (i/int32 (int value)))
  (serialize [this position]
    (serialize this)))

(defn orient-int32
  [value]
  (->OrientInt32 value))

(deftype OrientInt64 [value]
  OrientType
  (serialize [this]
    (i/int64 value))
  (serialize [this position]
    (serialize this)))

(defn orient-int64
  [value]
  (->OrientInt64 value))

(deftype OrientDateTime [value]
  OrientType
  (getDataType [this]
    (byte 6))
  (serialize [this]
    (byte-array (v/varint-unsigned (.getTime value))))
  (serialize [this position]
    (serialize this)))

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
      (byte-array (v/varint-unsigned (long (/ date->long 86400))))))
  (serialize [this position]
    (serialize this)))

(defn orient-date [value]
  (->OrientDate value))

(deftype OrientBinary [value]
  OrientType
  (getDataType [this]
    (byte 8))
  (serialize [this]
    (c/bytes-type value))
  (serialize [this position]
    (serialize this)))

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
         (map serialize)))
  (serialize [this position]
    (serialize this)))

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
      (doall (map #(.write dos % 0 (count %)) serialized-items))
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

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
      (doall (map #(.write dos % 0 (count %)) serialized-items))
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

(defn orient-embedded-set
  [value]
  (->OrientEmbeddedSet value))

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
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

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
      (doall (map #(.write dos % 0 (count %)) serialized-items))
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

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
      (doall (map #(.write dos % 0 (count %)) serialized-items))
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

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
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

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
      (.toByteArray bos)))
  (serialize [this position]
    (serialize this)))

(defn orient-decimal
  [value]
  (->OrientDecimal value))

(defn get-structure
  [record-map]
  (reduce
   (fn [acc k]
     (let [record-map-value (get record-map k)]
       (conj acc {:key-type (getDataType k)
                  :field-name k
                  :position 0
                  :type (getDataType record-map-value)
                  :value record-map-value})))
   []
   (keys record-map)))

(defn header-size
  [headers]
  (reduce
   (fn [acc k]
     (+ acc (count (serialize k)) const/fixed-header-int))
   0
   headers))

(defn serialize-structure-values
  [structure]
  (map
   (fn [s]
     (let [v (:value s)]
       (assoc s :serialized-value (serialize v))))
   structure))

(defn oemap-positions
  [structure offset]
  (let [hsize (header-size (map :field-name structure))]
    (reduce
     (fn [acc s]
       (if (empty? acc)
         (conj acc
               (assoc s :position (+ offset hsize)))
         (conj acc
               (assoc s :position
                      (+ offset
                         (count (:serialized-value (last acc)))
                         (:position (last acc)))))))
     []
     structure)))

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
     (conj acc (serialize (get header hk))))
   []
   key-order))

(defn serialize-headers
  [structure key-order]
  (mapcat
   #(serialize-elements % key-order)
   structure))

(defn write-header
  [^DataOutputStream dos header]
  (if (= (type header) java.lang.Byte)
    (.writeByte dos header)
    (.write dos header 0 (count header))))

(defn serialize-data
  [structure]
  (->> structure
       (map :serialized-value)
       (map byte-array)))

(deftype OrientEmbeddedMap [value]
  OrientType
  (getDataType [this]
    (byte 12))
  (serialize [this]
    (serialize this 0))
  (serialize [this position]
    (let [bos (ByteArrayOutputStream.)
          dos (DataOutputStream. bos)
          size (count value)
          size-varint (byte-array (v/varint-unsigned size))
          size-varint-len (count size-varint)
          structure (oemap->structure value position)
          key-order [:key-type :field-name :position :type]
          serialized-headers (serialize-headers structure key-order)
          serialized-data (serialize-data structure)]
      (.write dos size-varint 0 size-varint-len)
      (doall (map #(write-header dos %) serialized-headers))
      (doall (map #(.write dos % 0 (count %)) serialized-data))
      (.toByteArray bos))))

(defn orient-embedded-map
  [value]
  (->OrientEmbeddedMap value))

(defn first-elem
  [record-map serialized-class]
  (let [f (first record-map)
        k (first f)
        v (second f)
        hsize (header-size (keys record-map))]
    {:key-type (getDataType k)
     :field-name k
     :type (getDataType v)
     :value v
     :serialized-value (serialize v)
     :position (+ (count serialized-class) hsize)}))

(defn rest-elem
  [record-map first-elem]
  (let [r (rest record-map)]
    (reduce
     (fn [acc [k v]]
       (let [pos (+ (count (:serialized-value first-elem))
                    (:position first-elem))]
         (conj acc {:key-type (getDataType k)
                    :field-name k
                    :type (getDataType v)
                    :value v
                    :position pos
                    :serialized-value (serialize v pos)})))
     []
     r)))

(defn record-map->structure
  [record-map serialized-class]
  (let [fe (first-elem record-map serialized-class)
        res (conj [] fe)]
    (if-not (empty? (rest record-map))
      (->> (rest-elem record-map fe)
           (mapcat #(conj res %))
           positions->orient-int32)
      (positions->orient-int32 res))))

(defn serialize-record
  "Serialize `record` for OrientDB.
   `record` must be a Clojure map. It can contain Clojure types (string,
   boolean, etc.) or Orient custom types (OrientRid, OrientBinary, etc.)."
  [record]
  (let [bos (ByteArrayOutputStream.)
        dos (DataOutputStream. bos)
        version (byte 0)
        class (first (first record))
        serialized-class (serialize class)
        record-map (get record class)
        record-values (vals record-map)
        structure (record-map->structure record-map serialized-class)
        key-order [:field-name :position :type]
        serialized-headers (serialize-headers structure key-order)
        serialized-data (serialize-data structure)]
    (.writeByte dos version)
    (.write dos serialized-class 0 (count serialized-class))
    (doall (map #(write-header dos %) serialized-headers))
    (.writeByte dos (byte 0))
    (doall (map #(.write dos % 0 (count %)) serialized-data))
    (.toByteArray bos)))
