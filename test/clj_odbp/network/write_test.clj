;; Copyright 2017 7bridges s.r.l.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns clj-odbp.network.write_test
  (:require [clj-odbp.network.write :as w]
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
             (provide-output w/bool-type false) => [0])
       (fact "Bool - true should return a byte 1"
             (provide-output w/bool-type true) => [1])
       (fact "Byte - 10 should return a byte 10"
             (provide-output w/byte-type 10) => [10])
       (fact "Bytes - [10 20 30] should return an int and three bytes"
             (provide-output w/bytes-type [10 20 30]) => [0 0 0 3
                                                          10 20 30])
       (fact "Short - negative should return two bytes"
             (provide-output w/short-type -1) => [-1 -1])
       (fact "Short - positive should return two bytes"
             (provide-output w/short-type 10) => [0 10])
       (fact "Int - negative should return four bytes"
             (provide-output w/int-type -1) => [-1 -1 -1 -1])
       (fact "Int - positive should return four bytes"
             (provide-output w/int-type 10) => [0 0 0 10])
       (fact "Long - negative should return eight bytes"
             (provide-output w/long-type -1) => [-1 -1 -1 -1
                                                 -1 -1 -1 -1])
       (fact "Long - positive should return eight bytes"
             (provide-output w/long-type 10) => [0 0 0 0
                                                 0 0 0 10])
       (fact "String - '' should return 4 bytes"
             (provide-output w/string-type "") => [-1 -1 -1 -1])
       (fact "String - 'a' should return 5 bytes"
             (provide-output w/string-type "a") => [0 0 0 1 97])
       (fact "String - 'abcd' should return eight bytes"
             (provide-output w/string-type "abcd") => [0 0 0 4
                                                       97 98 99 100])
       (fact "Strings - ['abcd' 'efgh'] should return twenty bytes"
             (provide-output w/strings-type '["abcd" "efgh"])
             => [0 0 0 2
                 0 0 0 4 97 98 99 100
                 0 0 0 4 101 102 103 104]))
