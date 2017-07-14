(ns clj-odbp.types
  (:require [clojure.data.json :as json])
  (:import
   [java.text DateFormat SimpleDateFormat]
   [java.util Date]))

(defprotocol OrientType
  (serialize [this]))

(deftype OrientBase64 [value]
  OrientType
  (serialize [this]
    (let [bytes (.getBytes value)
          bytes-to-string (apply str bytes)]
      (str "_" bytes-to-string "_"))))

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

(deftype OrientRecordId [value]
  OrientType
  (serialize [this]
    (str "#" value)))

(defn orient-record-id [value]
  (->OrientRecordId value))

(deftype OrientEmbeddedDocument [value]
  OrientType
  (serialize [this]
    (str "(" (json/write-str value) ")")))

(defn orient-embedded-document [value]
  (->OrientEmbeddedDocument value))

(deftype OrientMap [value]
  OrientType
  (serialize [this]
    (json/write-str value)))

(defn orient-map [value]
  (->OrientMap value))

(deftype OrientRidBag [value]
  OrientType
  (serialize [this]
    (str "content:" value)))

(defn orient-rid-bag [value]
  (->OrientRidBag value))
