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

(ns clj-odbp.deserialize.network-types-test
  (:require [clj-odbp.deserialize.network-types :as d]
            [midje.sweet :refer :all])
  (:import [java.io ByteArrayInputStream DataInputStream]))

(defn- provide-input [bytes]
  (->> bytes
       (map byte)
       byte-array
       ByteArrayInputStream.
       DataInputStream.))

(facts "Deserialization of single types"
       (fact "Bool - should return false"
             (d/bool-type (provide-input [0])) => false)
       (fact "Bool - should return true"
             (d/bool-type (provide-input [1])) => true)
       (fact "Byte - should return a byte 10"
             (d/byte-type (provide-input [10])) => 10)
       (fact "Short - should return a short with value 1"
             (d/short-type (provide-input [0 1])) => (short 1))
       (fact "Int - should return an int with value 1"
             (d/int-type (provide-input [0 0 0 1])) => (int 1))
       (fact "Long - should return an int with value 1"
             (d/long-type (provide-input [0 0 0 0
                                          0 0 0 1])) => (long 1))
       (fact "Bytes - negative lenght should return an empty vector of bytes []"
             (d/bytes-type (provide-input [-1 -1 -1 -1])) => [])
       (fact "Bytes - should return a vector of bytes [10 20 30 40]"
             (d/bytes-type (provide-input [0 0 0 4
                                           10 20 30 40])) => [10 20 30 40])
       (fact "String - should return a string 'a'"
             (d/string-type (provide-input [0 0 0 1 97])) => "a")
       (fact "String - should return a string 'abcd'"
             (d/string-type (provide-input [0 0 0 4
                                            97 98 99 100])) => "abcd")
       (fact "Strings - should returns a vector of strings ['abcd' 'efgh']"
             (d/strings-type (provide-input [0 0 0 2
                                             0 0 0 4 97 98 99 100
                                             0 0 0 4 101 102 103 104])) => ["abcd" "efgh"])
       (fact
        "Array of - should return a vector of vectors: [['ab' 2] ['cd' 3]]"
        (let [f (d/array-of d/short-type [d/string-type d/short-type])]
          (f (provide-input [0 2
                             0 0 0 2 97 98 0 2
                             0 0 0 2 99 100 0 3])) => [["ab" 2]
                                                       ["cd" 3]])))
