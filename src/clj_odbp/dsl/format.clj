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

(ns clj-odbp.dsl.format
  (:require [clj-odbp.dsl.utils :as utils]))

(def statements-order
  {:select [:select :from :where]
   :update [:update :set :where]
   :insert-set [:insert-set :set]
   :insert-from [:insert-from :from]})

(defn- build-sql-with-order [m order]
  (->> (reduce
        (fn [a k]
          (if (contains? m k)
            (conj a (get m k))
            a))
        []
        order)
       (interpose " ")
       (apply str)))

(defn- build-sql [m]
  (cond
    (contains? m :select)
    (build-sql-with-order m (:select statements-order))

    (contains? m :update)
    (build-sql-with-order m (:update statements-order))

    (contains? m :insert-from)
    (build-sql-with-order m (:insert-from statements-order))

    (contains? m :insert-set)
    (build-sql-with-order m (:insert-set statements-order))))

(defmulti ->sql-str key)

(defn- create-sql-elements [m]
  (reduce
   (fn [a e]
     (assoc a (key e)
            (->sql-str e)))
   {}
   m))

(defn ->sql [m]
  (->> m
       create-sql-elements
       build-sql))

(defmethod ->sql-str :insert [data]
  (let [{class :class fields :fields} (second data)]
    (->> (utils/join-comma fields)
         utils/add-parens
         (str "INSERT INTO " class " "))))

(defmethod ->sql-str :select [data]
  (let [fields (second data)]
    (->> (utils/join-comma fields)
         (str "SELECT "))))

(defmethod ->sql-str :from [data]
  (let [table (second data)]
    (cond
      (string? table)
      (str "FROM " table)

      (map? table)
      (str "FROM (" (->sql table) ")"))))
