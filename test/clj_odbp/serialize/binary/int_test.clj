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

(ns clj-odbp.serialize.binary.int-test
  (:require [clj-odbp.serialize.binary.int :as i]
            [midje.sweet :refer :all]))

(facts "Serialization of int32 and int64"
       (fact "int32 - int 10 should return '[0 0 0 10]'"
             (vec (i/int32 (int 10))) => [0 0 0 10])
       (fact "int64 - long 300 should return '[0 0 0 0 0 0 1 44]'"
             (vec (i/int64 300)) => [0 0 0 0 0 0 1 44]))
