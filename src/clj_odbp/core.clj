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

(ns clj-odbp.core
  (:require [clj-odbp
             [net :as net]
             [utils :refer [defcommand defconnection]]]
            [clj-odbp.operations
             [command :as command]
             [db :as db]
             [record :as record]]))

(defconnection connect-server
  [username password]
  db/connect-request
  db/connect-response
  :server)

(defcommand shutdown-server
  [username password]
  db/shutdown-request
  db/shutdown-response)

(defconnection db-open
  [db-name username password]
  db/db-open-request
  db/db-open-response
  :db)

(defcommand db-create
  [connection db-name & opts]
  db/db-create-request
  db/db-create-response)

(defn db-close
  []
  (with-open [socket (net/create-socket)]
    (-> socket
        (net/write-request db/db-close-request))
    {}))

(defcommand db-exist
  [connection db-name]
  db/db-exist-request
  db/db-exist-response)

(defcommand db-drop
  [connection db-name]
  db/db-drop-request
  db/db-drop-response)

(defcommand db-size
  [connection]
  db/db-size-request
  db/db-size-response)

(defcommand db-countrecords
  [connection]
  db/db-countrecords-request
  db/db-countrecords-response)

(defcommand db-reload
  [connection]
  db/db-reload-request
  db/db-reload-response)

(defcommand record-load
  [connection record-id record-position]
  record/record-load-request
  record/record-load-response)

(defcommand record-create
  [connection record-content]
  record/record-create-request
  record/record-create-response)

(defcommand record-update
  [connection cluster-id cluster-position record-content]
  record/record-update-request
  record/record-update-response)

(defcommand record-delete
  [connection cluster-id cluster-position]
  record/record-delete-request
  record/record-delete-response)

(defcommand select-command
  [session-id command & opts]
  command/select-request
  command/select-response)
