(ns clj-odbp.commands.db
  (require [clj-odbp.constants :as consts]
           [clj-odbp.specs.db :as specs]
           [clj-odbp.utils :refer [encode decode]])
  (import [java.io DataInputStream]))

;; REQUEST_CONNECT
(defn connect-request
  [username password]
  (encode
   specs/connect-request
   [[:operation 2]
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

(defn connect-response
  [^DataInputStream in]
  (decode
   in
   specs/connect-response))

;; REQUEST_DB_OPEN
(defn connect-db-request
  [db-name username password]
  (encode
   specs/connect-db-request
   [[:operation 3]
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

(defn connect-db-response
  [^DataInputStream in]
  (decode
   in
   specs/connect-db-response))

;; REQUEST_SHUTDOWN
(defn shutdown-request
  [username password]
  (encode
   specs/shutdown-request
   [[:operation 1]
    [:session-id -1]
    [:username username]
    [:password password]]))

(defn shutdown-response
  [^DataInputStream in]
  {})

;; REQUEST_DB_CLOSE
(defn db-close-request
  []
  (encode
   specs/db-close-request
   [[:operation 5]]))

(defn db-close-response
  [^DataInputStream in]
  {})

;; REQUEST_DB_EXIST
(defn db-exist-request
  [session-id db-name]
  (encode
   specs/db-exist-request
   [[:operation 6]
    [:session-id session-id]
    [:database-name db-name]
    [:server-storage-type consts/storage-type-plocal]]))

(defn db-exist-response
  [^DataInputStream in]
  (decode
   in
   specs/db-exist-response))
