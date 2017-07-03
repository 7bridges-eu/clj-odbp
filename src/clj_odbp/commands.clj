(ns clj-odbp.commands
  (require [clj-odbp.specs :as specs]
           [clj-odbp.net :as net])
  (import [java.io DataInputStream]))

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

(defn connect
  [username password]
  (with-open [socket (net/create-socket)]
    (-> socket
        (net/write-request connect-request username password)
        (net/read-response connect-response))))

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

(defn connect-db
  [db-name username password]
  (with-open [socket (net/create-socket)]
    (-> socket
        (net/write-request connect-db-request db-name username password)
        (net/read-response connect-db-response))))

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

(defn shutdown
  [socket username password]
  (-> socket
      (net/write-request shutdown-request username password)
      (net/read-response shutdown-response)))
