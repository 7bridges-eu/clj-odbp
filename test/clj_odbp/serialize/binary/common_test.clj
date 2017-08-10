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

(ns clj-odbp.serialize.binary.common-test
  (:require [clj-odbp.serialize.binary.common :as c]
            [midje.sweet :refer :all])
  (:import [java.io ByteArrayOutputStream DataOutputStream]))

(facts "Common binary serialization utilities"
       (fact "Bytes - bytes [116 101 115 116] should return [8 116 101 115 116]"
             (vec (c/bytes-type (byte-array [116 101 115 116]))) =>
             [8 116 101 115 116]))
