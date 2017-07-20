(ns clj-odbp.serialize.binary.otypes
  (:require [clj-odbp.serialize.binary.common :as c]
            [clj-odbp.serialize.binary.varint :as v])
  (:import [java.text SimpleDateFormat]))

(defprotocol OrientType
  (serialize [this]))

(deftype OrientBinary [value]
  OrientType
  (serialize [this]
    (c/bytes-type value)))

(defn orient-binary [value]
  (->OrientBinary value))

(deftype OrientDate [value]
  OrientType
  (serialize [this]
    (let [formatter (SimpleDateFormat. "dd/MM/yyyy")
          date (.value this)
          date-without-time (.parse formatter (.format formatter date))
          date->long (.getTime date-without-time)]
      (v/varint (long (/ date->long 86400))))))

(defn orient-date [value]
  (->OrientDate value))

(deftype OrientDateTime [value]
  OrientType
  (serialize [this]
    (v/varint (.getTime (.value this)))))

(defn orient-date-time [value]
  (->OrientDateTime value))
