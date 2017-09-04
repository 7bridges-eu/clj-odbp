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

(ns clj-odbp.serialize.binary.common
  (:require [clj-odbp.serialize.binary.varint :as v]))

(defn bytes-type
  "Serialize an array of bytes. `value` must be an array of bytes. eg:

   (bytes-type (.getBytes \"test\" \"UTF-8\"))"
  [value]
  (let [size (count value)
        size-varint (v/varint-unsigned size)]
    (into size-varint value)))