(ns clj-odbp.core
  (require [clj-odbp.net :as net]
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

(defcommand connect-db
  [db-name username password]
  db/connect-db-request
  db/connect-db-response)

(defn db-close
  []
  (with-open [socket (net/create-socket)]
    (-> socket
        (net/write-request db/db-close-request))
    {}))

(defcommand record-load
  [session-id record-id record-position]
  record/record-load-request
  record/record-load-response)

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
