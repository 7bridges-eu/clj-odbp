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

(ns clj-odbp.binary.serialize.varint-test
  (:require [clj-odbp.binary.serialize.varint :as v]
            [midje.sweet :refer :all]))

(facts "Serialization of varint type"
       (fact "varint - long '300' should return '[172 2]'"
             (v/varint-signed 300) => [172 2])
       (fact "varint - long '300' should return '[216 4]'"
             (v/varint-unsigned 300) => [216 4]))
