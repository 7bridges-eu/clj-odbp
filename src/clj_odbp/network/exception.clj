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

(ns clj-odbp.network.exception
  (:require [clj-odbp.network.read :as r])
  (:import [java.io ByteArrayInputStream ObjectInputStream]))

(defn deserialize-exception
  "De-serialize OrientDB exception from DataInputStream `in`."
  [in]
  (loop [status (r/byte-type in)
         causes []]
    (if (zero? status)
      causes
      (let [class (r/string-type in)
            message (r/string-type in)]
        (recur (r/byte-type in)
               (conj causes {:class class
                             :message message}))))))

(defn create-exception
  "Create an ExceptionInfo based on the class of the OrientDB exception."
  [causes]
  (ex-info "OrientDB Internal Exception"
           {:caused causes}))

(defn handle-exception
  "De-serialize an OrientDB exception in DataInputStream `in` and throw it."
  [in]
  (let [session-id (r/int-type in)
        token (r/bytes-type in)]
    (-> in
        deserialize-exception
        create-exception
        throw)))

(defmulti manage-exception
  "Manage the exception according to its type."
  :exception-type)

(defmethod manage-exception :default [e]
  (throw (:exception e)))
