(ns clj-odbp.serialize.binary.record-test
  (:require [clj-odbp.serialize.binary.record :as r]
            [midje.sweet :refer :all])
  (:import [java.text SimpleDateFormat]))

(facts "Serialization of single type"
       (fact "Short - short 1 should return [1]"
             (r/short-type (short 1)) => [1])
       (fact "Integer - integer 1 should return [1]"
             (r/integer-type (int 10)) => [10])
       (fact "Long - long 1000000 should return [192 132 61]"
             (r/long-type (long 1000000)) => [192 132 61])
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
       (fact "String - string 'test' should return [4 116 101 115 116]"
             (vec (r/string-type "test")) => [4 116 101 115 116])
       (fact "Keyword - keyword :test should return [4 116 101 115 116]"
             (vec (r/keyword-type :test)) => [4 116 101 115 116])
       (fact "Vector - vector [1 2 3] should return ([1] [2] [3])"
             (r/coll-type [1 2 3]) => '([1] [2] [3]))
       (fact "Map - map {:name 'test'} should return ([4, 110, 97, 109, 101] [4, 116, 101, 115, 116])"
             (map vec (r/map-type {:name "test"})) =>
             '([4, 110, 97, 109, 101] [4, 116, 101, 115, 116])))
