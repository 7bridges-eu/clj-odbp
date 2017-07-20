(ns clj-odbp.serialize.binary.common-test
  (:require [clj-odbp.serialize.binary.common :as c]
            [midje.sweet :refer :all])
  (:import [java.io ByteArrayOutputStream DataOutputStream]))

(facts "Common binary serialization utilities"
       (fact "Bytes - bytes [116 101 115 116] should return [4 116 101 115 116]"
             (vec (c/bytes-type (byte-array [116 101 115 116]))) =>
             [4 116 101 115 116]))
