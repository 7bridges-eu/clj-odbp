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

(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.types :refer [otype-list call]]
            [clj-odbp.deserialize.binary.buffer :as b]))

(defn deserialize-record
  [record]
  (let [cluster (get record :record-cluster nil)
        position (get record :record-position nil)
        version (get record :record-version 0)
        content (:record-content record)
        buffer (b/to-buffer content)
        result {:_version version}
        add-rid (fn [m] (if (and (nil? cluster) (nil? position))
                         m
                         (assoc result :_rid (str "#" cluster ":" position))))]
    (conj (add-rid result) (call :record-orient-type buffer))))
