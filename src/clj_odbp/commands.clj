(ns clj-odbp.commands
  (require [clj-odbp.specs :as specs]
           [clj-odbp.net :as net])
  (import [java.io DataInputStream]))

(defmacro defcommand
  [command-name args request-handler response-handler]
  `(defn ~command-name
     [~@args]
     (with-open [s# (net/create-socket)]
       (-> s#
           (net/write-request ~request-handler ~@args)
           (net/read-response ~response-handler)))))

;; REQUEST_CONNECT
(defn- connect-request
  [username password] 
  (specs/encode
   specs/connect-request
   [[:command 2]
    [:session -1]
    [:driver-name "clj-odbp"]
    [:driver-version "0.0.1"]
    [:protocol-version 36]
    [:client-id ""]
    [:serialization "ORecordSerializerBinary"]
    [:token-session true]
    [:support-push true]
    [:collect-stats true]
    [:username username]
    [:password password]]))

(defn- connect-response
  [^DataInputStream in]
  (specs/decode
   in
   specs/connect-response))

(defcommand connect
  [username password]
  connect-request
  connect-response)

;; REQUEST_DB_OPEN
(defn- connect-db-request
  [db-name username password]
  (specs/encode 
   specs/connect-db-request
   [[:command 3]
    [:session -1]
    [:driver-name "clj-odbp"]
    [:driver-version "0.0.1"]
    [:protocol-version 36]
    [:client-id ""]
    [:serialization "ORecordSerializerBinary"]
    [:token-session false]
    [:support-push true]
    [:collect-stats true]
    [:database-name db-name]
    [:username username]
    [:password password]]))

(defn- connect-db-response
  [^DataInputStream in]
  (specs/decode
   in
   specs/connect-db-response))

(defcommand connect-db
  [db-name username password]
  connect-db-request
  connect-db-response)

;; REQUEST_SHUTDOWN
(defn- shutdown-request
  [username password]
  (specs/encode
   specs/shutdown-request
   [[:command 1]
    [:username username]
    [:password password]]))

(defn- shutdown-response
  [^DataInputStream in]
  {})

(defcommand shutdown
  [username password]
  shutdown-request
  shutdown-response)
