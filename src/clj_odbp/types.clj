(ns clj-odbp.types
  (:import
   [java.text DateFormat SimpleDateFormat]
   [java.util Date]))

(defprotocol OrientType
  (serialize [this]))

(deftype OrientBase64 [value]
  OrientType
  (serialize [this] value))

(defn orient-base64 [value]
  (->OrientBase64 value))

(deftype OrientDate [value]
  OrientType
  (serialize [this]
    (let [formatter (SimpleDateFormat. "dd/MM/yyyy")
          date-without-time (.parse formatter (.format formatter value))
          date->long (.getTime date-without-time)]
      (str date->long "a"))))

(defn orient-date [value]
  (->OrientDate value))

(deftype OrientDateTime [value]
  OrientType
  (serialize [this]
    (let [date->long (.getTime value)]
      (str date->long "t"))))

(defn orient-date-time [value]
  (->OrientDateTime value))
