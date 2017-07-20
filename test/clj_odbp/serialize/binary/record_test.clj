(ns clj-odbp.serialize.binary.record-test
  (:require [clj-odbp.serialize.binary.record :as r]
            [midje.sweet :refer :all])
  (:import [java.text SimpleDateFormat]))

(def obinary (r/orient-binary (byte-array [116 101 115 116])))

(defn format-date
  [format s]
  (let [formatter (SimpleDateFormat. format)]
    (.parse formatter s)))

(def odate (r/orient-date (format-date "dd/MM/yyyy" "19/07/2017")))
(def odatetime
  (r/orient-date-time
   (format-date "dd/MM/YYYY hh:mm:ss" "19/07/2017 10:30:00")))

(def odate-result [184 238 199 16])
(def odatetime-result (r/long-type (.getTime (.value odatetime))))

(facts "Serialization of single type"
       (fact "Short - short 1 should return [2]"
             (r/short-type (short 1)) => [2])
       (fact "Integer - integer 1 should return [20]"
             (r/integer-type (int 10)) => [20])
       (fact "Long - long 1000000 should return [128 137 122]"
             (r/long-type (long 1000000)) => [128 137 122])
       (fact "Byte - byte 1 should return byte 1"
             (r/byte-type (byte 1)) => (byte 1))
       (fact "Boolean - boolean true should return byte 1"
             (r/boolean-type true) => (byte 1))
       (fact "Boolean - boolean false should return byte 0"
             (r/boolean-type false) => (byte 0))
       (fact "Float - float 2.50 should return the bytes [64, 32, 0, 0]"
             (vec (r/float-type (float 2.50))) => [64, 32, 0, 0])
       (fact "Double - double 20000.50 should return the bytes [64 -45 -120 32 0 0 0 0]"
             (vec (r/double-type (double 20000.50))) =>
             [64 -45 -120 32 0 0 0 0])
       (fact "String - string 'test' should return [8 116 101 115 116]"
             (vec (r/string-type "test")) => [8 116 101 115 116])
       (fact "Keyword - keyword :test should return [8 116 101 115 116]"
             (vec (r/keyword-type :test)) => [8 116 101 115 116])
       (fact "Vector - vector [1 2 3] should return ([2] [4] [6])"
             (r/coll-type [1 2 3]) => '([2] [4] [6]))
       (fact "Map - map {:name 'test'} should return ([8, 110, 97, 109, 101] [8, 116, 101, 115, 116])"
             (map vec (r/map-type {:name "test"})) =>
             '([8, 110, 97, 109, 101] [8, 116, 101, 115, 116]))
       (fact "OrientBinary - OrientBinary [116 101 115 116] should return [8 116 101 115 116]"
             (vec (.serialize obinary)) => [8 116 101 115 116])
       (fact "OrientDate - odate should return odate-result"
             (vec (.serialize odate)) => odate-result)
       (fact "OrientDateTime - odatetime should return odatetime-result"
             (vec (.serialize odatetime)) => odatetime-result))
