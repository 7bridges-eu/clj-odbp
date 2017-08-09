(ns clj-odbp.serialize.binary.record
  (:require [clj-odbp.constants :as const]
            [clj-odbp.serialize.binary
             [common :as c]
             [int :as i]
             [varint :as v]]))

(defprotocol OrientType
  (getDataType [value])
  (serialize [value] [value offset]))

(defn short-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Short
  OrientType
  (getDataType [value]
    (byte 2))
  (serialize
    ([value] (short-type value))
    ([value offset] (serialize value))))

(defn integer-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Integer
  OrientType
  (getDataType [value]
    (byte 1))
  (serialize
    ([value] (integer-type value))
    ([value offset] (serialize value))))

(defn long-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Long
  OrientType
  (getDataType [value]
    (byte 3))
  (serialize
    ([value] (long-type value))
    ([value offset] (serialize value))))

(defn byte-type
  [value]
  [value])

(extend-type java.lang.Byte
  OrientType
  (getDataType [value]
    (byte 17))
  (serialize
    ([value] (byte-type value))
    ([value offset] (serialize value))))

(defn boolean-type
  [value]
  (if value
    [(byte 1)]
    [(byte 0)]))

(extend-type java.lang.Boolean
  OrientType
  (getDataType [value]
    (byte 0))
  (serialize
    ([value] (boolean-type value))
    ([value offset] (serialize value))))

(defn float-type
  [value]
  (-> (java.nio.ByteBuffer/allocate 4)
      (.putFloat value)
      .array
      vec))

(extend-type java.lang.Float
  OrientType
  (getDataType [value]
    (byte 4))
  (serialize
    ([value] (float-type value))
    ([value offset] (serialize value))))

(defn double-type
  [value]
  (-> (java.nio.ByteBuffer/allocate 8)
      (.putDouble value)
      .array
      vec))

(extend-type java.lang.Double
  OrientType
  (getDataType [value]
    (byte 5))
  (serialize
    ([value] (double-type value))
    ([value offset] (serialize value))))

(defn bigdec-type
  [value]
  (let [scale (i/int32 (.scale value))
        serialized-value (-> value
                             .unscaledValue
                             .toByteArray
                             vec)
        value-size (i/int32 (count serialized-value))]
    (vec (concat scale value-size serialized-value))))

(extend-type java.math.BigDecimal
  OrientType
  (getDataType [value]
    (byte 21))
  (serialize
    ([value] (bigdec-type value))
    ([value offset] (serialize value))))

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
    ([value offset] (serialize value))))

(defn keyword-type
  [value]
  (string-type (name value)))

(extend-type clojure.lang.Keyword
  OrientType
  (getDataType [value]
    (byte 7))
  (serialize
    ([value] (keyword-type value))
    ([value offset] (serialize value))))

(defn coll-type
  [value]
  (map serialize value))

(extend-type clojure.lang.PersistentList
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value offset] (serialize value))))

(extend-type clojure.lang.PersistentVector
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value offset] (serialize value))))

(extend-type clojure.lang.PersistentHashSet
  OrientType
  (serialize
    ([value] (coll-type value))
    ([value offset] (serialize value))))

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
    ([value offset] (serialize value))))

(deftype OrientInt32 [value]
  OrientType
  (serialize [this]
    (i/int32 (int value)))
  (serialize [this offset]
    (serialize this)))

(defn orient-int32
  [value]
  (->OrientInt32 value))

(deftype OrientInt64 [value]
  OrientType
  (serialize [this]
    (i/int64 value))
  (serialize [this offset]
    (serialize this)))

(defn orient-int64
  [value]
  (->OrientInt64 value))

(deftype OrientDateTime [value]
  OrientType
  (getDataType [this]
    (byte 6))
  (serialize [this]
    (long-type (.getTime value)))
  (serialize [this offset]
    (serialize this)))

(defn orient-date-time
  [value]
  (->OrientDateTime value))

(deftype OrientBinary [value]
  OrientType
  (getDataType [this]
    (byte 8))
  (serialize [this]
    (c/bytes-type value))
  (serialize [this offset]
    (serialize this)))

(defn orient-binary
  [value]
  (->OrientBinary value))

(defn serialize-any
  [value]
  (let [v (serialize value)
        t (getDataType value)]
    (into [t] v)))

(deftype OrientEmbeddedList [value]
  OrientType
  (getDataType [this]
    (byte 10))
  (serialize [this]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          serialized-items (into [] (apply concat (map serialize-any value)))
          any [(byte 23)]]
      (vec (concat size-varint any serialized-items))))
  (serialize [this offset]
    (serialize this)))

(defn orient-embedded-list
  [value]
  (->OrientEmbeddedList value))

(deftype OrientEmbeddedSet [value]
  OrientType
  (getDataType [this]
    (byte 11))
  (serialize [this]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          serialized-items (into [] (apply concat (map serialize-any value)))
          any [(byte 23)]]
      (vec (concat size-varint any serialized-items))))
  (serialize [this offset]
    (serialize this)))

(defn orient-embedded-set
  [value]
  (->OrientEmbeddedSet value))

(deftype OrientLink [cluster-id record-position]
  OrientType
  (getDataType [this]
    (byte 13))
  (serialize [this]
    (let [cid-varint (v/varint-unsigned cluster-id)
          rpos-varint (v/varint-unsigned record-position)]
      (vec (concat cid-varint rpos-varint))))
  (serialize [this offset]
    (serialize this)))

(defn orient-link
  [cluster-id record-position]
  (->OrientLink cluster-id record-position))

(deftype OrientLinkList [value]
  OrientType
  (getDataType [this]
    (byte 14))
  (serialize [this]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          serialized-items (mapcat serialize value)]
      (vec (concat size-varint serialized-items))))
  (serialize [this offset]
    (serialize this)))

(defn orient-link-list
  [value]
  (->OrientLinkList value))

(deftype OrientLinkSet [value]
  OrientType
  (getDataType [this]
    (byte 15))
  (serialize [this]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          serialized-items (mapcat serialize value)]
      (vec (concat size-varint serialized-items))))
  (serialize [this offset]
    (serialize this)))

(defn orient-link-set
  [value]
  (->OrientLinkSet value))

(defn serialize-key-value
  [k v]
  (if-not (map? v)
    (let [key-type [(getDataType k)]
          key-value (serialize k)
          link (serialize v)]
      (vec (concat key-type key-value link)))))

(deftype OrientLinkMap [value]
  OrientType
  (getDataType [this]
    (byte 16))
  (serialize [this]
    (let [size (v/varint-unsigned (count value))
          key-values (mapcat (fn [[k v]] (serialize-key-value k v)) value)]
      (vec (concat size key-values))))
  (serialize [this offset]
    (serialize this)))

(defn orient-link-map
  [value]
  (->OrientLinkMap value))

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

(defn serialize-data
  [structure]
  (->> structure
       (map :serialized-value)))

(deftype OrientEmbeddedMap [value]
  OrientType
  (getDataType [this]
    (byte 12))
  (serialize [this]
    (serialize this 0))
  (serialize [this offset]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          structure (oemap->structure value offset)
          key-order [:key-type :field-name :position :type]
          serialized-headers (serialize-headers structure key-order)
          serialized-data (serialize-data structure)]
      (-> (concat size-varint serialized-headers serialized-data)
          flatten
          vec))))

(defn orient-embedded-map
  [value]
  (->OrientEmbeddedMap value))

(defn first-elem
  [record-map offset]
  (let [f (first record-map)
        k (first f)
        v (second f)
        hsize (header-size (keys record-map) const/fixed-header-int)]
    {:key-type (getDataType k)
     :field-name k
     :type (getDataType v)
     :value v
     :serialized-value (serialize v)
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
        {:key-type (getDataType k)
         :field-name k
         :type (getDataType v)
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

(deftype OrientEmbedded [value]
  OrientType
  (getDataType [this]
    (byte 9))
  (serialize [this offset]
    (let [size (count value)
          size-varint (v/varint-unsigned size)
          class (first (first value))
          serialized-class (serialize class)
          serialized-class-size (count serialized-class)
          record-map (get value class)
          first-elem-pos (dec (+ offset serialized-class-size))
          structure (record-map->structure record-map first-elem-pos)
          key-order [:field-name :position :type]
          serialized-headers (serialize-headers structure key-order)
          end-headers [(byte 0)]
          serialized-data (serialize-data structure)]
      (-> (concat serialized-class serialized-headers
                  end-headers serialized-data)
          flatten
          vec)))
  (serialize [this]
    (serialize this 0)))

(defn orient-embedded
  [value]
  (->OrientEmbedded value))

(defn serialize-record
  "Serialize `record` for OrientDB.
   `record` must be a Clojure map. It can contain Clojure types (string,
   boolean, etc.) or Orient custom types (OrientLink, OrientBinary, etc.)."
  [record]
  (let [version [(byte 0)]
        class (first (first record))
        serialized-class (serialize class)
        serialized-class-size (count serialized-class)
        record-map (get record class)
        structure (record-map->structure record-map serialized-class-size)
        key-order [:field-name :position :type]
        serialized-headers (serialize-headers structure key-order)
        end-headers [(byte 0)]
        serialized-data (serialize-data structure)]
    (-> (concat version serialized-class serialized-headers
                end-headers serialized-data)
        flatten
        vec)))
