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

(ns clj-odbp.binary.serialize.types
  (:require [clj-odbp.constants :as const]
            [clj-odbp.binary.serialize
             [common :as c]
             [int :as i]
             [varint :as v]]))

(def orient-types
  "Map custom orient types to their respective byte identifier."
  {:boolean-type (byte 0) :integer-type (byte 1) :short-type (byte 2)
   :long-type (byte 3) :float-type (byte 4) :double-type (byte 5)
   :datetime-type (byte 6) :string-type (byte 7) :keyword-type (byte 7)
   :binary-type (byte 8) :embedded-record-type (byte 9)
   :embedded-list-type (byte 10) :embedded-set-type (byte 11)
   :embedded-map-type (byte 12) :link-type (byte 13) :link-list-type (byte 14)
   :link-set-type (byte 15) :link-map-type (byte 16) :byte-type (byte 17)
   :custom-type (byte 20) :decimal-type (byte 21) :any-type (byte 23)
   :nil-type (byte 0)})

(defn link?
  "Check if `v` is a valid OrientDB link. e.g.: \"#21:1\""
  [v]
  (when (string? v)
    (re-matches #"#\d+:\d+" v)))

(defn link-list?
  "Check if `l` is a valid OrienDB link list. e.g.: [\"#21:1\" \"#21:2\"]"
  [l]
  (when (sequential? l)
    (every? link? l)))

(defn link-set?
  "Check if `s` is a valid OrientDB link set. e.g.: #{\"#21:1\" \"#21:2\"}"
  [s]
  (when (set? s)
    (every? link? s)))

(defn link-map?
  "Check if `m` is a valid OrientDB link map. e.g.: {\"test\" \"#21:2\"}"
  [m]
  (when (map? m)
    (let [values (vals m)]
      (every? link? values))))

(defn embedded-record?
  "Check if `r` is a valid OrientDB embedded record. eg:

  {:_class \"User\" :name \"Test\"}"
  [r]
  (when (map? r)
    (or (contains? r :_class)
        (contains? r "@type")
        (contains? r :_version))))

(deftype OrientBinary [value])

(defn orient-binary
  [value]
  {:pre [(vector? value)]}
  (->OrientBinary value))

(defn get-type
  "Return a keyword the identifies the type of `v.` e.g.

  (get-type true) => :boolean-type"
  [v]
  (cond
    (nil? v) :nil-type
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

(defmulti serialize
  "Serialize `value` based on its type.
  It optionally accepts an `offset` which will be used to calculate the position
  of `value` from the beginning of the record."
  (fn [value & offset] (get-type value)))

(defmethod serialize :nil-type
  ([value]
   [])
  ([value offset]
   (serialize value)))

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
  "Serialize a key-value according to OrientDB specification.
   See: http://orientdb.com/docs/last/Record-Schemaless-Binary-Serialization.html#linkmap"
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
  "Serialize `value` in a vector of bytes with the byte representing its type
  coming first. e.g.:

  (serialize-list-item true) => [0 1]"
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
  "Transform the record `record-map` into a custom structure. eg.:

  (get-structure {:_class \"User\" :name \"Test\"}) =>
  [{:key-type 7, :field-name :_class, :position 0, :type 7, :value \"User\"}
   {:key-type 7, :field-name :name, :position 0, :type 7, :value \"Test\"}]"
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
  "Calculate the total `headers` size. `fixed-header-int` is needed to
  distinguish the calculation of the header size of a record from that of an
  embedded map."
  [headers fixed-header-int]
  (+ 1                                  ; closing header
     (reduce
      (fn [acc k]
        (+ acc (count (serialize k)) fixed-header-int))
      0
      headers)))

(defn serialize-structure-values
  "Serialize the values inside `structure` according to their type."
  [structure]
  (map
   (fn [s]
     (let [v (:value s)]
       (assoc s :serialized-value (serialize v))))
   structure))

(defn oemap-positions
  "Calculate the position of the values in `structure`, offsetting the first
  value with `offset.`"
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
  "Convert `value` in an int32. e.g.: (orient-int32 1) => [0 0 0 1]"
  [value]
  (i/int32 (int value)))

(defn positions->orient-int32
  "Convert the positions in `structure` in int32."
  [structure]
  (map #(update % :position orient-int32) structure))

(defn oemap->structure
  "Trasform the embedded map `data-map` into a structure. e.g.:

  (oemap->structure {:test 1} 0) =>
  ({:key-type 7, :field-name :test, :position [0 0 0 12], :type 1, :value 1,
    :serialized-value [2]})"
  [data-map offset]
  (-> (get-structure data-map)
      serialize-structure-values
      (oemap-positions offset)
      positions->orient-int32))

(defn serialize-elements
  "Serialize the elements in `header` returning a vector sorted by `key-order`."
  [header key-order]
  (reduce
   (fn [acc hk]
     (if (= hk :position)
       (conj acc (get header hk))
       (let [position (:position header)]
         (when-not (= position 0)
           (conj acc (serialize (get header hk)))))))
   []
   key-order))

(defn serialize-headers
  "Serialize the elements in `structure` returning a sequence sorted by
  `key-order`."
  [structure key-order]
  (mapcat
   #(serialize-elements % key-order)
   structure))

(defn serialize-data
  "Retrieve the :serialized-value inside `structure`."
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
  "Determine the structure of the first element of `record-map`."
  [record-map offset]
  (let [f (first record-map)
        k (first f)
        v (second f)
        hsize (header-size (keys record-map) const/fixed-header-int)
        type-v (get-type v)]
    {:key-type (get orient-types (get-type k))
     :field-name k
     :type (get orient-types type-v)
     :value v
     :serialized-value (serialize v (+ 1 offset hsize))
     :position (if (= :nil-type type-v)
                 0
                 (+ 1 offset hsize))}))

(defn rest-elem
  "Determine the structure of all but the first element of `record-map`."
  [record-map first-elem]
  (reduce
   (fn [acc [k v]]
     (let [last-elem (last acc)
           serialized-elem (:serialized-value last-elem)
           size-le (count serialized-elem)
           type-v (get-type v)
           pos (if (= :nil-type type-v)
                 0
                 (+ size-le (:position last-elem)))]
       (conj
        acc
        {:key-type (get orient-types (get-type k))
         :field-name k
         :type (get orient-types type-v)
         :value v
         :position pos
         :serialized-value (serialize v pos)})))
   (conj [] first-elem)
   (rest record-map)))

(defn record-map->structure
  "Transform the record `record-map` into a structure. e.g.:

  (record-map->structure {:_class \"User\" :name \"Test\"} 0) =>
  ({:key-type 7, :field-name :_class, :type 7, :value \"User\",
    :serialized-value [8 85 115 101 114], :position [0 0 0 24]}
   {:key-type 7, :field-name :name, :type 7, :value \"Test\",
    :position [0 0 0 29], :serialized-value [8 84 101 115 116]})"
  [record-map offset]
  (->> (first-elem record-map offset)
       (rest-elem record-map)
       positions->orient-int32))

(defn remove-meta-data
  "Remove from `v` the entries whose keyword name starts with \"_\"."
  [v]
  (->> (keys v)
       (filter #(clojure.string/starts-with? (name %) "_"))
       (apply dissoc v)))

(defmethod serialize :embedded-record-type
  ([value]
   (serialize value 0))
  ([value offset]
   (let [size (count value)
         size-varint (v/varint-unsigned size)
         class (get value :_class "")
         serialized-class (serialize class)
         serialized-class-size (count serialized-class)
         first-elem-pos (+ offset serialized-class-size)
         plain-record (remove-meta-data value)
         structure (record-map->structure plain-record first-elem-pos)
         key-order [:field-name :position :type]
         serialized-headers (serialize-headers structure key-order)
         end-headers [(byte 0)]
         serialized-data (serialize-data structure)]
     (-> (concat const/serialization-version
                 serialized-class serialized-headers
                 end-headers serialized-data)
         flatten
         vec))))

(defmethod serialize :custom-type
  [value]
  value)
