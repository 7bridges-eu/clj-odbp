(ns clj-odbp.serialize.record
  (:require [clj-odbp.types :as types])
  (:import
   [java.text DateFormat SimpleDateFormat]
   [java.util Date]))

;; See: http://orientdb.com/docs/last/Record-CSV-Serialization.html

;; Example of record to be CSV serialized:
;; {"Location" {:name "Bar" :description "Bar Mario" :cost 12.50}}

;; Possible solution: parse record recursively, processing elements by
;; dispatching based on type

(defn class-type
  [value]
  (str value "@"))

(defn string-type
  [value]
  (str "\"" value "\""))

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

(defn embedded-document-type
  [value]
  (str "(" value ")"))

(defn list-type
  [value]
  (let [comma-separated (apply str (interpose "," value))]
    (str "[" comma-separated "]")))

(defn set-type
  [value]
  (let [comma-separated (apply str (interpose "," value))]
    (str "<" comma-separated ">")))

(defn map-type
  [value]
  )

(defn rid-bag-type
  [value]
  )

(defprotocol Serialization
  (serialize [value]))

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
