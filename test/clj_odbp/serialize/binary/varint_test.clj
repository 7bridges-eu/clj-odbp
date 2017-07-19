(ns clj-odbp.serialize.binary.varint-test
  (:require [clj-odbp.serialize.binary.varint :as v]
            [midje.sweet :refer :all]))

(facts "Serialization of varint type"
       (fact "varint - long '300' should return '[172 2]'"
             (v/varint 300) => [172 2]))
