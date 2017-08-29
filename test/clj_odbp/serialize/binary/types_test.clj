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

(ns clj-odbp.serialize.binary.types-test
  (:require [clj-odbp.serialize.binary.types :as t]
            [midje.sweet :refer :all])
  (:import [java.text SimpleDateFormat]))

(defn format-date
  [format s]
  (let [formatter (SimpleDateFormat. format)]
    (.parse formatter s)))

(def odatetime
  (format-date "dd/MM/YYYY hh:mm:ss" "19/07/2017 10:30:00"))

(def odatetime-result
  (t/serialize (.getTime odatetime)))

(def oemb {:_class "User" :name "Test"})

(def oemap {:test "1"})

(facts "Serialization of single types and record"
       (fact "Short - short 1 should return [2]"
             (t/serialize (short 1)) => [2])
       (fact "Integer - integer 1 should return [20]"
             (t/serialize (int 10)) => [20])
       (fact "Long - long 1000000 should return [128 137 122]"
             (t/serialize (long 1000000)) => [128 137 122])
       (fact "Byte - byte 1 should return byte [1]"
             (t/serialize (byte 1)) => [(byte 1)])
       (fact "Boolean - boolean true should return byte 1"
             (t/serialize true) => [(byte 1)])
       (fact "Boolean - boolean false should return byte 0"
             (t/serialize false) => [(byte 0)])
       (fact "Float - float 2.50 should return the bytes [64, 32, 0, 0]"
             (t/serialize (float 2.50)) => [64, 32, 0, 0])
       (fact "Double - double 20000.50 should return the bytes [64 -45 -120 32 0 0 0 0]"
             (t/serialize (double 20000.50)) => [64 -45 -120 32 0 0 0 0])
       (fact "BigDecimal - bigdec 12.34M should return the bytes [0 0 0 2 0 0 0 2 4 -46]"
             (t/serialize 12.34M) => [0 0 0 2 0 0 0 2 4 -46])
       (fact "String - string 'test' should return [8 116 101 115 116]"
             (t/serialize "test") => [8 116 101 115 116])
       (fact "Keyword - keyword :test should return [8 116 101 115 116]"
             (t/serialize :test) => [8 116 101 115 116])
       (fact "Binary - orient-binary [1 2 3] should return [6 1 2 3]"
             (t/serialize (t/orient-binary [1 2 3])) => [6 1 2 3])
       (fact "Vector - [1 2 3] should return [6 23 1 2 1 4 1 6]"
             (t/serialize [1 2 3]) => [6 23 1 2 1 4 1 6])
       (fact "Map - map {:name 'test'} should return [2 7 8 110 97 109 101 0 0 0 12 7 8 116 101 115 116]"
             (t/serialize {:name "test"}) => [2 7 8 110 97 109 101 0 0 0 12 7 8 116 101 115 116])
       (fact "DateTime - odatetime should return odatetime-result"
             (t/serialize odatetime) => odatetime-result)
       (fact "Embedded record - oemb should return [0 8 85 115 101 114 8 110 97 109 101 0 0 0 17 7 0 8 84 101 115 116]"
             (t/serialize oemb) => [0 8 85 115 101 114 8 110 97 109 101 0 0 0 17 7 0 8 84 101 115 116])
       (fact "Embedded list - (12 13 14) should return [6 23 1 24 1 26 1 28]"
             (t/serialize '(12 13 14)) => [6 23 1 24 1 26 1 28])
       (fact "Embedded set - #{12 13 14} should return [6 23 1 24 1 26 1 28]"
             (t/serialize #{12 13 14}) =>
             (just [6 23 1 24 1 26 1 28] :in-any-order))
       (fact "Link - #33:0 should return [66 0]"
             (t/serialize "#33:0") => [66 0])
       (fact "Link list - (#33:1 #34:1) should return [4 66 2 68 2]"
             (t/serialize (list "#33:1" "#34:1")) => [4 66 2 68 2])
       (fact "Link set -  #{#33:1 #34:1} should return [4 66 2 68 2]"
             (t/serialize #{"#33:1" "#34:1"}) =>
             (just [4 66 2 68 2] :in-any-order))
       (fact "Link map - {'test' #33:1} should return [2 7 8 116 101 115 116 66 2]"
             (t/serialize {"test" "#33:1"}) => [2 7 8 116 101 115 116 66 2])
       (fact "Embedded map - oemap should return [2 7 8 116 101 115 116 0 0 0 12 7 2 49]"
             (t/serialize oemap) => [2 7 8 116 101 115 116 0 0 0 12 7 2 49]))
