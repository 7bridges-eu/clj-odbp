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

(ns clj-odbp.dsl.utils
  (:require [clojure.data.json :as json]))

(defn collify [x]
  (if (coll? x) x [x]))

(defn- mapentry-to-equal [e]
  (let [[k v] e]
    (str (name k) " = " (json/json-str v))))

(defn join-comma [s]
  (->> s
       (interpose ", ")
       (apply str)))

(defn map-to-fields-set [m]
  (->> m
       (map mapentry-to-equal)
       join-comma))

(defn add-parens [s]
  (str "(" s ")"))

(defn map-entry [k v]
  (first {k v}))
