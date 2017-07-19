(ns clj-odbp.serialize.csv.types-test
  (:require [clj-odbp.serialize.csv.types :as t]
            [midje.sweet :refer :all])
  (:import [java.text DateFormat SimpleDateFormat]
           [java.util Base64]
           [java.util Date]))

(defn string->base64
  [s]
  (let [encoder (Base64/getEncoder)
        bytes (.getBytes s)]
    (.encodeToString encoder bytes)))

(def base64-test (string->base64 "test"))

(defn format-date
  [format s]
  (let [formatter (SimpleDateFormat. format)]
    (.parse formatter s)))

(def date-test (format-date "dd/MM/yyyy" "14/07/2017"))
(def date-time-test
  (format-date "dd/MM/YYYY hh:mm:ss" "14/07/2017 10:30:00"))

(def date-test-result (str (.getTime date-test) "a"))
(def date-time-test-result (str (.getTime date-time-test) "t"))

(def embedded-doc-test {:name "Test" :value 1})
(def map-test {:name "Test" :value 1})

(facts "Serialization of custom OrientDB types"
       (fact "OrientBase64 - base64-test should return '_1007186122100656161_'"
             (.serialize (t/orient-base64 base64-test)) =>
             "_1007186122100656161_")
       (fact "OrientDate - date-test should return '1499983200000a'"
             (.serialize (t/orient-date date-test)) => date-test-result)
       (fact "OrientDateTime - date-test should return '1483349400000t'"
             (.serialize (t/orient-date-time date-time-test)) =>
             date-time-test-result)
       (fact "OrientRecordId - '33:0' should return '#33:0'"
             (.serialize (t/orient-record-id "33:0")) => "#33:0")
       (fact "OrientEmbeddedDocument - embedded-doc-test should return
              '({'name':'Test','value':1})'"
             (.serialize (t/orient-embedded-document embedded-doc-test)) =>
             "({\"name\":\"Test\",\"value\":1})")
       (fact "OrientMap - map-test should return '{'name':'Test','value':1}'"
             (.serialize (t/orient-map map-test)) =>
             "{\"name\":\"Test\",\"value\":1}")
       (fact "OrientRidBag - base64-test should return 'content:dGVzdA=='"
             (.serialize (t/orient-rid-bag base64-test)) =>
             "content:dGVzdA=="))
