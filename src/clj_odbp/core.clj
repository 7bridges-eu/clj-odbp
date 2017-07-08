(ns clj-odbp.core
  (require [clj-odbp.utils :refer [defcommand]]
           [clj-odbp.commands.db :as db]))

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
