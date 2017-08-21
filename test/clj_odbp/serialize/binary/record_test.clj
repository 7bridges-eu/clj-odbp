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

(ns clj-odbp.serialize.binary.record-test
  (:require [clj-odbp.serialize.binary.record :as r]
            [midje.sweet :refer :all])
  (:import [java.text SimpleDateFormat]))

(def obinary (r/orient-binary (byte-array [116 101 115 116])))

(defn format-date
  [format s]
  (let [formatter (SimpleDateFormat. format)]
    (.parse formatter s)))

(def odatetime
  (r/orient-date-time
   (format-date "dd/MM/YYYY hh:mm:ss" "19/07/2017 10:30:00")))

(def odatetime-result (vec (r/long-type (.getTime (.value odatetime)))))

(defn rid-comparator [r1 r2]
  (.compareTo (.cluster_id r1) (.cluster_id r2)))

(def oemb (r/orient-embedded {"User" {:name "Test"}}))

(def oemap (r/orient-embedded-map {:test "1"}))

(def record
  {"Location"
   {:binary (r/orient-binary [84 104 105 115 32 105 115 32
                              115 111 109 101 32 98 105 110
                              97 114 121 32 100 97 116 97 10])
    :bool_true true
    :bool_false false
    :name "Casa"
    :sector "1x1"
    :means (r/orient-link-list [(r/orient-link 23 0) (r/orient-link 22 0)])
    :document (r/orient-embedded-map {"name" "Marco" "age" 36})
    :set (r/orient-embedded-set #{1 "Marco" 3})
    :cost 5.5M}})

(def expected-record
  [0 16 76 111 99 97 116 105 111 110 12 98 105 110 97 114
   121 0 0 0 120 8 18 98 111 111 108 95 116 114 117 101 0
   0 0 -110 0 20 98 111 111 108 95 102 97 108 115 101 0 0 0
   -109 0 8 110 97 109 101 0 0 0 -108 7 12 115 101 99 116 111
   114 0 0 0 -103 7 10 109 101 97 110 115 0 0 0 -99 14 16 100
   111 99 117 109 101 110 116 0 0 0 -94 12 8 99 111 115 116 0
   0 0 -65 21 6 115 101 116 0 0 0 -56 11 0 50 84 104 105 115 32
   105 115 32 115 111 109 101 32 98 105 110 97 114 121 32 100 97
   116 97 10 1 0 8 67 97 115 97 6 49 120 49 4 46 0 44 0 4 7 8 110
   97 109 101 0 0 0 -72 7 7 6 97 103 101 0 0 0 -66 3 10 77 97 114
   99 111 72 0 0 0 1 0 0 0 1 55 6 23 3 2 7 10 77 97 114 99 111 3 6])

(facts "Serialization of single types and record"
       (fact "Short - short 1 should return [2]"
             (r/short-type (short 1)) => [2])
       (fact "Integer - integer 1 should return [20]"
             (r/integer-type (int 10)) => [20])
       (fact "Long - long 1000000 should return [128 137 122]"
             (r/long-type (long 1000000)) => [128 137 122])
       (fact "Byte - byte 1 should return byte [1]"
             (r/byte-type (byte 1)) => [(byte 1)])
       (fact "Boolean - boolean true should return byte 1"
             (r/boolean-type true) => [(byte 1)])
       (fact "Boolean - boolean false should return byte 0"
             (r/boolean-type false) => [(byte 0)])
       (fact "Float - float 2.50 should return the bytes [64, 32, 0, 0]"
             (r/float-type (float 2.50)) => [64, 32, 0, 0])
       (fact "Double - double 20000.50 should return the bytes [64 -45 -120 32 0 0 0 0]"
             (r/double-type (double 20000.50)) => [64 -45 -120 32 0 0 0 0])
       (fact "BigDecimal - bigdec 12.34M should return the bytes [0 0 0 2 0 0 0 2 4 -46]"
             (r/bigdec-type 12.34M) => [0 0 0 2 0 0 0 2 4 -46])
       (fact "String - string 'test' should return [8 116 101 115 116]"
             (r/string-type "test") => [8 116 101 115 116])
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
       (fact "OrientDateTime - odatetime should return odatetime-result"
             (vec (.serialize odatetime)) => odatetime-result)
       (fact "OrientEmbedded - oemb should return [8 85 115 101 114 8 110 97 109 101 0 0 0 16 7 0 8 84 101 115 116]"
             (vec (.serialize oemb)) =>
             [8 85 115 101 114 8 110 97 109 101 0 0 0 16 7 0 8 84 101 115 116])
       (fact "OrientEmbeddedList - OrientEmbeddedList (12 13 14) should return [6 23 3 24 3 26 3 28]"
             (vec (.serialize (r/orient-embedded-list '(12 13 14)))) =>
             [6 23 3 24 3 26 3 28])
       (fact "OrientEmbeddedSet - OrientEmbeddedSet #{12 13 14} should return [6 23 3 24 3 26 3 28]"
             (vec (.serialize (r/orient-embedded-set #{12 13 14}))) =>
             (just [6 23 3 24 3 26 3 28] :in-any-order))
       (fact "OrientLink - OrientLink #33:0 should return [66 0]"
             (vec (.serialize (r/orient-link 33 0))) =>
             [66 0])
       (fact "OrientLinkList - OrientLinkList (#33:1 #34:1) should return [4 66 2 68 2]"
             (vec
              (.serialize (r/orient-link-list
                           (list (r/orient-link 33 1) (r/orient-link 34 1))))) =>
             [4 66 2 68 2])
       (fact "OrientLinkSet - OrientLinkSet #{#33:1 #34:1} should return [4 66 2 68 2]"
             (vec
              (.serialize (r/orient-link-set
                           (sorted-set-by rid-comparator
                                          (r/orient-link 33 1)
                                          (r/orient-link 34 1))))) =>
             [4 66 2 68 2])
       (fact "OrientLinkMap - OrientLinkMap {'test' #33:1} should return [2 7 8 116 101 115 116 66 2]"
             (vec
              (.serialize
               (r/orient-link-map {"test" (r/orient-link 33 1)}))) =>
             [2 7 8 116 101 115 116 66 2])
       (fact "OrientEmbeddedMap - oemap should return [2 7 8 116 101 115 116 0 0 0 12 7 2 49]"
             (vec (.serialize oemap)) =>
             [2 7 8 116 101 115 116 0 0 0 12 7 2 49])
       (fact "record - record should return expected-record"
             (vec (r/serialize-record record)) => expected-record))
