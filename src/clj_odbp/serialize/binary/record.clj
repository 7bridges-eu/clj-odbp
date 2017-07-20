(ns clj-odbp.serialize.binary.record
  (:require [clj-odbp.serialize.binary.common :as c]
            [clj-odbp.serialize.binary.otypes :as otypes]
            [clj-odbp.serialize.binary.varint :as v])
  (:import [java.io ByteArrayOutputStream DataOutputStream]
           [java.lang Float]
           [java.text SimpleDateFormat]))

(defprotocol Serialization
  (serialize [value]))

(defn serialize-by-type
  [value]
  (if (satisfies? otypes/OrientType value)
    (.serialize value)
    (serialize value)))

(defn short-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Short
  Serialization
  (serialize [value]
    (short-type value)))

(defn integer-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Integer
  Serialization
  (serialize [value]
    (integer-type value)))

(defn long-type
  [value]
  (v/varint-unsigned value))

(extend-type java.lang.Long
  Serialization
  (serialize [value]
    (long-type value)))

(defn byte-type
  [value]
  value)

(extend-type java.lang.Byte
  Serialization
  (serialize [value]
    (byte-type value)))

(defn boolean-type
  [value]
  (if value
    (byte 1)
    (byte 0)))

(extend-type java.lang.Boolean
  Serialization
  (serialize [value]
    (boolean-type value)))

(defn float-type
  [value]
  (let [bos (ByteArrayOutputStream. 4)
        dos (DataOutputStream. bos)]
    (.writeFloat dos value)
    (.toByteArray bos)))

(extend-type java.lang.Float
  Serialization
  (serialize [value]
    (float-type value)))

(defn double-type
  [value]
  (let [bos (ByteArrayOutputStream. 8)
        dos (DataOutputStream. bos)]
    (.writeDouble dos value)
    (.toByteArray bos)))

(extend-type java.lang.Double
  Serialization
  (serialize [value]
    (double-type value)))

(defn string-type
  [value]
  (let [bytes (.getBytes value)]
    (c/bytes-type bytes)))

(extend-type java.lang.String
  Serialization
  (serialize [value]
    (string-type value)))

(defn keyword-type
  [value]
  (string-type (name value)))

(extend-type clojure.lang.Keyword
  Serialization
  (serialize [value]
    (keyword-type value)))

(defn coll-type
  [value]
  (map serialize-by-type value))

(extend-type clojure.lang.PersistentList
  Serialization
  (serialize [value]
    (coll-type value)))

(extend-type clojure.lang.PersistentVector
  Serialization
  (serialize [value]
    (coll-type value)))

(extend-type clojure.lang.PersistentHashSet
  Serialization
  (serialize [value]
    (coll-type value)))

(defn map-type
  [value]
  (->> value
       vec
       flatten
       (map serialize-by-type)))

(extend-type clojure.lang.PersistentArrayMap
  Serialization
  (serialize [value]
    (map-type value)))

;; (serialization-version:byte)(class-name:string)(header:byte[])(data:byte[])

(defn serialize-header
  []
  )

(defn serialize-data
  [data]
  (serialize-by-type data))

(defn serialize-record [record]
  (serialize-header)
  (serialize-data))
