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

(ns clj-odbp.dsl.core
  (:require [clj-odbp.dsl.utils :as utils]))

(defmacro defstmt [name args body]
  `(defn ~name
     ([~@args]
      (~name {} ~@args))
     ([m# ~@args]
      (-> m#
          ~body))))

(defstmt SELECT [fields]
  (assoc :select
         (utils/collify fields)))

(defstmt INSERT [class]
  (assoc :insert class))

(defstmt UPDATE [class]
  (assoc :update class))

(defstmt SET [fields]
  (assoc :set fields))

(defstmt VALUES [fields]
  (assoc :values fields))

(defstmt FROM [table]
  (assoc :from table))

(defstmt WHERE [conditions]
  (assoc :where [conditions]))

(defn AND [& conditions]
  (apply vector :AND conditions))

(defn OR [& conditions]
  (apply vector :OR conditions))

(defn EQ [field value]
  [:EQ field value])
