(ns clj-odbp.core
  (:require [clj-odbp.net :as net]
            [clj-odbp.utils :refer [defcommand]]
            [clj-odbp.commands.db :as db]
            [clj-odbp.commands.record :as record]))

(defcommand connect-server
  [username password]
  db/connect-request
  db/connect-response)

(defcommand shutdown-server
  [username password]
  db/shutdown-request
  db/shutdown-response)

(defcommand db-open
  [db-name username password]
  db/db-open-request
  db/db-open-response)

(defcommand db-create
  [session-id db-name & opts]
  db/db-create-request
  db/db-create-response)

(defn db-close
  []
  (with-open [socket (net/create-socket)]
    (-> socket
        (net/write-request db/db-close-request))
    {}))

(defcommand db-exist
  [session-id db-name]
  db/db-exist-request
  db/db-exist-response)

(defcommand db-drop
  [session-id db-name]
  db/db-drop-request
  db/db-drop-response)

(defcommand db-size
  [session-id]
  db/db-size-request
  db/db-size-response)

(defcommand db-countrecords
  [session-id]
  db/db-countrecords-request
  db/db-countrecords-response)

(defcommand db-reload
  [session-id]
  db/db-reload-request
  db/db-reload-response)

(defcommand record-load
  [session-id record-id record-position]
  record/record-load-request
  record/record-load-response)

(defcommand record-create
  [session-id record-content]
  record/record-create-request
  record/record-create-response)

(defcommand record-update
  [session-id cluster-id cluster-position record-content]
  record/record-update-request
  record/record-update-response)

(defcommand record-delete
  [session-id cluster-id cluster-position]
  record/record-delete-request
  record/record-delete-response)
