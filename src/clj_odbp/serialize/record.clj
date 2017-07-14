(ns clj-odbp.serialize.record
  (:require [clj-odbp.types :as types]
            [clojure.data.json :as json])
  (:import [java.text DateFormat SimpleDateFormat]
           [java.util Date]))

(defprotocol Serialization
  (serialize [value]))

(defn serialize-by-type [value]
  (if (satisfies? types/OrientType value)
    (.serialize value)
    (serialize value)))

(defn class-type
  [value]
  (str value "@"))

(defn keyword-type
  [value]
  (name value))

(defn string-type
  [value]
  (str "\\\"" value "\\\""))

(defn integer-type
  [value]
  (.toString value))

(defn long-type
  [value]
  (str (.toString value) "l"))

(defn short-type
  [value]
  (str (.toString value) "s"))

(defn byte-type
  [value]
  (str (.toString value) "b"))

(defn float-type
  [value]
  (str (.toString value) "f"))

(defn double-type
  [value]
  (str (.toString value) "d"))

(defn big-decimal-type
  [value]
  (str (.toString value) "c"))

(defn bool-type
  [value]
  value)

(defn list-type
  [value]
  (let [serialized (map serialize-by-type value)
        comma-separated (apply str (interpose "," serialized))]
    (str "[" comma-separated "]")))

(defn set-type
  [value]
  (let [serialized (map serialize-by-type value)
        comma-separated (apply str (interpose "," serialized))]
    (str "<" comma-separated ">")))

(defn map-type
  [value]
  (let [serialized (for [k (keys value)]
                     (str (name k) ":" (serialize-by-type (get value k))))]
    (apply str (interpose "," serialized))))

(extend-type clojure.lang.Keyword
  Serialization
  (serialize [value]
    (keyword-type value)))

(extend-type java.lang.String
  Serialization
  (serialize [value]
    (string-type value)))

(extend-type java.lang.Integer
  Serialization
  (serialize [value]
    (integer-type value)))

(extend-type java.lang.Long
  Serialization
  (serialize [value]
    (long-type value)))

(extend-type java.lang.Short
  Serialization
  (serialize [value]
    (short-type value)))

(extend-type java.lang.Byte
  Serialization
  (serialize [value]
    (byte-type value)))

(extend-type java.lang.Float
  Serialization
  (serialize [value]
    (float-type value)))

(extend-type java.lang.Double
  Serialization
  (serialize [value]
    (double-type value)))

(extend-type java.math.BigDecimal
  Serialization
  (serialize [value]
    (big-decimal-type value)))

(extend-type java.lang.Boolean
  Serialization
  (serialize [value]
    (bool-type value)))

(extend-type clojure.lang.PersistentList
  Serialization
  (serialize [value]
    (list-type value)))

(extend-type clojure.lang.PersistentVector
  Serialization
  (serialize [value]
    (list-type value)))

(extend-type clojure.lang.PersistentHashSet
  Serialization
  (serialize [value]
    (set-type value)))

(extend-type clojure.lang.PersistentArrayMap
  Serialization
  (serialize [value]
    (map-type value)))

(defn serialize-record [record]
  (let [class-keyword (first (keys record))
        orient-class (class-type class-keyword)
        values (get record class-keyword)
        orient-values (serialize-by-type values)]
    (str orient-class orient-values)))
