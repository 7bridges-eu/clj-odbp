(ns clj-odbp.serialize.otype-test
  (:require [clj-odbp.serialize.otype :as s]
            [midje.sweet :refer :all])
  (import [java.io DataOutputStream ByteArrayOutputStream]))

(defn- provide-output
  [f values]
  (let [buffer (ByteArrayOutputStream.)
        out (DataOutputStream. buffer)]
    (apply f out [values])
    (vec (.toByteArray buffer))))

(facts "Serialization of single types"
       (fact "Bool - false should return a byte 0"
             (provide-output s/bool-type false) => [0])
       (fact "Bool - true should return a byte 1"
             (provide-output s/bool-type true) => [1])
       (fact "Byte - 10 should return a byte 10"
             (provide-output s/byte-type 10) => [10])
       (fact "Bytes - [10 20 30] should return an int and three bytes"
             (provide-output s/bytes-type [10 20 30]) => [0 0 0 3
                                                          10 20 30])
       (fact "Short - negative should return two bytes"
             (provide-output s/short-type -1) => [-1 -1])
       (fact "Short - positive should return two bytes"
             (provide-output s/short-type 10) => [0 10])
       (fact "Int - negative should return four bytes"
             (provide-output s/int-type -1) => [-1 -1 -1 -1])
       (fact "Int - positive should return four bytes"
             (provide-output s/int-type 10) => [0 0 0 10])
       (fact "Long - negative should return eight bytes"
             (provide-output s/long-type -1) => [-1 -1 -1 -1
                                                 -1 -1 -1 -1])
       (fact "Long - positive should return eight bytes"
             (provide-output s/long-type 10) => [0 0 0 0
                                                 0 0 0 10])
       (fact "String - 'abcd' should return eight bytes"
             (provide-output s/string-type "abcd") => [0 0 0 4
                                                       97 98 99 100])
       (fact "Strings - ['abcd' 'efgh'] should return twenty bytes"
             (provide-output s/strings-type '["abcd" "efgh"])
             => [0 0 0 2
                 0 0 0 4 97 98 99 100
                 0 0 0 4 101 102 103 104]))
