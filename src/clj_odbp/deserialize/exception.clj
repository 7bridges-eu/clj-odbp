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

(ns clj-odbp.deserialize.exception
  (:require [clj-odbp.deserialize.otype :as ot]
            [clj-odbp.sessions :as s]
            [clojure.string :as string]))

(defn deserialize-exception
  "De-serialize OrientDB exception from DataInputStream `in`."
  [in]
  (let [ex-class (ot/string-type in)
        ex-message (ot/string-type in)]
    {:class ex-class :message ex-message}))

(defn create-exception
  "Create an ExceptionInfo based on the class of the OrientDB exception."
  [m]
  (let [fully-qualified-name (:class m)
        message (:message m)
        class-name (last (string/split fully-qualified-name #"\."))]
    (ex-info message {:type (keyword class-name)})))

(defn handle-exception
  "De-serialize an OrientDB exception in DataInputStream `in` and throw it."
  [in]
  (let [session-id (ot/int-type in)
        token (ot/bytes-type in)
        status (ot/byte-type in)]
    (-> in
        deserialize-exception
        create-exception
        throw)))

(defmulti manage-exception :exception-type)

(defmethod manage-exception :default [e]
  (throw (:exception e)))
