(ns clj-odbp.binary.deserialize.record-test
  (:require [clj-odbp.binary.deserialize.record :as r]
            [clj-odbp.binary.deserialize.buffer :as b]
            [midje.sweet :refer :all]))

(def data
  [0 16 76 111 99 97 116 105 111 110 18 98 111 111 108 95 116 114 117 101 0 0 0
   108 0 20 98 111 111 108 95 102 97 108 115 101 0 0 0 109 0 8 110 97 109 101 0
   0 0 110 7 12 115 101 99 116 111 114 0 0 0 115 7 10 109 101 97 110 115 0 0 0
   119 14 16 100 111 99 117 109 101 110 116 0 0 0 124 9 8 99 111 115 116 0 0 0
   -100 21 6 115 101 116 0 0 0 -91 11 0 1 0 8 67 97 115 97 6 49 120 49 4 46 0 44
   0 0 8 85 115 101 114 8 110 97 109 101 0 0 0 -106 7 6 97 103 101 0 0 0 -101 1
   0 8 84 101 115 116 84 0 0 0 1 0 0 0 1 55 6 23 1 2 1 6 7 8 116 101 115 116])

(def input-record
  {:record-content data})

(def expected-record
  {:_class "Location"
   :_version 0
   :bool_true true
   :bool_false false
   :name "Casa"
   :sector "1x1"
   :means ["#23:0" "#22:0"]
   :document {:_class "User" :name "Test" :age 42}
   :set #{1 "test" 3}
   :cost 5.5M})

(facts "Deserialization of a record"
       (fact "Record - should return the expected-record"
             (r/deserialize-record input-record) => expected-record))
