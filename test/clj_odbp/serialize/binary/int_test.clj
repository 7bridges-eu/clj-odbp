(ns clj-odbp.serialize.binary.int-test
  (:require [clj-odbp.serialize.binary.int :as i]
            [midje.sweet :refer :all]))

(facts "Serialization of int32 and int64"
       (fact "int32 - int 10 should return '[0 0 0 10]'"
             (i/int32 (int 10)) => [0 0 0 10])
       (fact "int64 - long 300 should return '[0 0 0 0 0 0 1 44]'"
             (i/int64 300) => [0 0 0 0 0 0 1 44]))
