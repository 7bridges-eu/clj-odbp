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
