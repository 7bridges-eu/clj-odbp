(ns clj-odbp.operations.db
  (:require [clj-odbp.constants :as consts]
            [clj-odbp.specs.db :as specs]
            [clj-odbp.utils :refer [encode decode]])
  (:import [java.io DataInputStream]))

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

;; REQUEST_CONNECT
(defn connect-request
  [username password]
  (encode
   specs/connect-request
   [[:operation 2]
    [:session-id -1]
    [:driver-name "clj-odbp"]
    [:driver-version "0.0.1"]
    [:protocol-version 36]
    [:client-id ""]
    [:serialization "onet_ser_v0"]
    [:token-session true]
    [:support-push false]
    [:collect-stats false]
    [:username username]
    [:password password]]))

(defn connect-response
  [^DataInputStream in]
  (decode
   in
   specs/connect-response))

;; REQUEST_DB_OPEN
(defn db-open-request
  [db-name username password]
  (encode
   specs/db-open-request
   [[:operation 3]
    [:session-id -1]
    [:driver-name "clj-odbp"]
    [:driver-version "0.0.1"]
    [:protocol-version 36]
    [:client-id ""]
    [:serialization "onet_ser_v0"]
    [:token-session true]
    [:support-push false]
    [:collect-stats false]
    [:database-name db-name]
    [:username username]
    [:password password]]))

(defn db-open-response
  [^DataInputStream in]
  (decode
   in
   specs/db-open-response))

;; REQUEST_DB_CREATE
(defn db-create-request
  [connection db-name
   {:keys [db-type storage-type backup-path]
    :or {db-type "graph" storage-type "plocal" backup-path ""}}]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-create-request
     [[:operation 4]
      [:session-id session-id]
      [:token token]
      [:database-name db-name]
      [:database-type db-type]
      [:storage-type storage-type]
      [:backup-path backup-path]])))

(defn db-create-response
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
  [connection db-name]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-exist-request
     [[:operation 6]
      [:session-id session-id]
      [:token token]
      [:database-name db-name]
      [:server-storage-type consts/storage-type-plocal]])))

(defn db-exist-response
  [^DataInputStream in]
  (decode
   in
   specs/db-exist-response))

;; REQUEST_DB_DROP
(defn db-drop-request
  [connection db-name]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-drop-request
     [[:operation 7]
      [:session-id session-id]
      [:token token]
      [:database-name db-name]
      [:storage-type consts/storage-type-plocal]])))

(defn db-drop-response
  [^DataInputStream in]
  {})

;; REQUEST_DB_SIZE
(defn db-size-request
  [connection]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-size-request
     [[:operation 8]
      [:session-id session-id]
      [:token token]])))

(defn db-size-response
  [^DataInputStream in]
  (decode
   in
   specs/db-size-response))

;; REQUEST_DB_COUNTRECORDS
(defn db-countrecords-request
  [connection]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-countrecords-request
     [[:operation 9]
      [:session-id session-id]
      [:token token]])))

(defn db-countrecords-response
  [^DataInputStream in]
  (decode
   in
   specs/db-countrecords-response))

;; REQUEST_DB_RELOAD
(defn db-reload-request
  [connection]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/db-reload-request
     [[:operation 73]
      [:session-id session-id]
      [:token token]])))

(defn db-reload-response
  [^DataInputStream in]
  (decode
   in
   specs/db-reload-response))
