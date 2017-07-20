(ns clj-odbp.serialize.binary.otypes-test
  (:require [clj-odbp.serialize.binary.otypes :as t]
            [clj-odbp.serialize.binary.record :as r]
            [midje.sweet :refer :all])
  (:import [java.text SimpleDateFormat]))

(def obinary (t/orient-binary (byte-array [116 101 115 116])))

(defn format-date
  [format s]
  (let [formatter (SimpleDateFormat. format)]
    (.parse formatter s)))

(def odate (t/orient-date (format-date "dd/MM/yyyy" "19/07/2017")))
(def odatetime
  (t/orient-date-time
   (format-date "dd/MM/YYYY hh:mm:ss" "19/07/2017 10:30:00")))

(def odate-result [156 247 163 8])
(def odatetime-result (r/long-type (.getTime (.value odatetime))))

(facts "Serialization of custom OrientDB types"
       (fact "OrientBinary - OrientBinary [116 101 115 116] should return [4 116 101 115 116]"
             (vec (.serialize obinary)) => [4 116 101 115 116])
       (fact "OrientDate - odate should return odate-result"
             (vec (.serialize odate)) => odate-result)
       (fact "OrientDateTime - odatetime should return odatetime-result"
             (vec (.serialize odatetime)) => odatetime-result))
