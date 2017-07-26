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

(def odate-result [-72 -18 -57 16])
(def odatetime-result (vec (r/long-type (.getTime (.value odatetime)))))

(defn rid-comparator [r1 r2]
  (.compareTo (.cluster_id r1) (.cluster_id r2)))

(def oemap (r/orient-embedded-map {:test "1"}))

(def record {"User" {:name "admin"}})

(facts "Serialization of single types and record"
       (fact "Short - short 1 should return [2]"
             (vec (r/short-type (short 1))) => [2])
       (fact "Integer - integer 1 should return [20]"
             (vec (r/integer-type (int 10))) => [20])
       (fact "Long - long 1000000 should return [-128 -119 122]"
             (vec (r/long-type (long 1000000))) => [-128 -119 122])
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
             (map vec (r/coll-type [1 2 3])) => '([2] [4] [6]))
       (fact "Map - map {:name 'test'} should return ([8, 110, 97, 109, 101] [8, 116, 101, 115, 116])"
             (map vec (r/map-type {:name "test"})) =>
             '([8, 110, 97, 109, 101] [8, 116, 101, 115, 116]))
       (fact "OrientInt32 - OrientInt32 10 should return [0, 0, 0, 10]"
             (vec (.serialize (r/orient-int32 10))) => [0 0 0 10])
       (fact "OrientInt64 - OrientInt64 300 should return [0, 0, 0, 0, 0, 0, 1, 44]"
             (vec (.serialize (r/orient-int64 300))) =>
             [0, 0, 0, 0, 0, 0, 1, 44])
       (fact "OrientBinary - OrientBinary [116 101 115 116] should return [8 116 101 115 116]"
             (vec (.serialize obinary)) => [8 116 101 115 116])
       (fact "OrientDate - odate should return odate-result"
             (vec (.serialize odate)) => odate-result)
       (fact "OrientDateTime - odatetime should return odatetime-result"
             (vec (.serialize odatetime)) => odatetime-result)
       (fact "OrientEmbedded - OrientEmbedded {:name 'test'} should return ([8, 110, 97, 109, 101] [8, 116, 101, 115, 116])"
             (map vec (.serialize (r/orient-embedded {:name "test"}))) =>
             '([8, 110, 97, 109, 101] [8, 116, 101, 115, 116]))
       (fact "OrientEmbeddedList - OrientEmbeddedList (12 13 14) should return [6, 23, 24, 26, 28]"
             (vec (.serialize (r/orient-embedded-list '(12 13 14)))) =>
             [6, 23, 24, 26, 28])
       (fact "OrientEmbeddedSet - OrientEmbeddedSet #{12 13 14} should return [6, 23, 26, 24, 28]"
             (vec (.serialize (r/orient-embedded-list #{12 13 14}))) =>
             [6, 23, 26, 24, 28])
       (fact "OrientRid - OrientRid #33:0 should return [0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 0]"
             (vec (.serialize (r/orient-rid 33 0))) =>
             [0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 0])
       (fact "OrientLinkList - OrientLinkList (#33:1 #34:1) should return [4, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 34,
 0, 0, 0, 0, 0, 0, 0, 1]"
             (vec
              (.serialize (r/orient-link-list
                           (list (r/orient-rid 33 1) (r/orient-rid 34 1))))) =>
             [4, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 1, 0,
              0, 0, 0, 0, 0, 0, 34, 0, 0, 0, 0, 0, 0, 0, 1])
       (fact "OrientLinkSet - OrientLinkSet #{#33:1 #34:1} should return [4, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 34,
 0, 0, 0, 0, 0, 0, 0, 1]"
             (vec
              (.serialize (r/orient-link-set
                           (sorted-set-by rid-comparator
                                          (r/orient-rid 33 1)
                                          (r/orient-rid 34 1))))) =>
             [4, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 1, 0,
              0, 0, 0, 0, 0, 0, 34, 0, 0, 0, 0, 0, 0, 0, 1])
       (fact "OrientLinkMap - OrientLinkMap {'test' #33:1} should return [2, 7, 8, 116, 101, 115, 116, 0, 0, 0, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 1]"
             (vec
              (.serialize
               (r/orient-link-map {"test" (r/orient-rid 33 1)}))) =>
             [2, 7, 8, 116, 101, 115, 116, 0, 0, 0, 0, 0, 0, 0, 33, 0,
              0, 0, 0, 0, 0, 0, 1])
       (fact "OrientDecimal - OrientDecimal 2.50 should return [0, 0, 0, 1, 0, 0, 0, 2, 64, 4, 0, 0, 0, 0, 0, 0]"
             (vec (.serialize (r/orient-decimal 2.50))) =>
             [0, 0, 0, 1, 0, 0, 0, 2, 64, 4, 0, 0, 0, 0, 0, 0])
       (fact "OrientEmbeddedMap - oemap should return [2 7 8 116 101 115 116 0 0 0 11 7 2 49]"
             (vec (.serialize oemap)) =>
             [2 7 8 116 101 115 116 0 0 0 11 7 2 49])
       (fact "record - record should return [0 8 85 115 101 114 8 110 97 109 101 0 0 0 16 7 0 10 97 100 109 105 110]"
             (vec (r/serialize-record record)) =>
             [0 8 85 115 101 114 8 110 97 109 101 0 0 0 16
              7 0 10 97 100 109 105 110]))
