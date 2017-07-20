(ns clj-odbp.serialize.binary.varint-test
  (:require [clj-odbp.serialize.binary.varint :as v]
            [midje.sweet :refer :all]))

(facts "Serialization of varint type"
       (fact "varint - long '300' should return '[172 2]'"
             (v/varint-signed 300) => [172 2])
       (fact "varint - long '300' should return '[216 4]'"
             (v/varint-unsigned 300) => [216 4]))
