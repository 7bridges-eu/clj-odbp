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

(ns clj-odbp.serialize.binary.record
  (:require [clj-odbp.constants :as const]
            [clj-odbp.serialize.binary
             [common :as c]
             [int :as i]
             [otypes :as ot]
             [varint :as v]]))

(defn serialize-record
  "Serialize `record` for OrientDB. `record` must be a Clojure map."
  [record]
  (let [version (vector (get record :_version (byte 0)))
        class (get record :_class "")
        serialized-class (ot/serialize class)
        serialized-class-size (count serialized-class)
        structure (ot/record-map->structure record serialized-class-size)
        key-order [:field-name :position :type]
        serialized-headers (ot/serialize-headers structure key-order)
        end-headers [(byte 0)]
        serialized-data (ot/serialize-data structure)]
    (-> (concat version serialized-class serialized-headers
                end-headers serialized-data)
        flatten
        vec)))
