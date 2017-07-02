(ns clj-odbp.commands
  (require [clj-odbp.specs :as specs]))

(defn connect-request
  [username password] 
  (specs/encode
   specs/connect-request
   [[:command 3]
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
  (specs/decode
   in
   specs/connect-response))

(defn db-open-request
  [db-name username password] 
  (specs/encode 
   specs/db-open-request
   [[:command 3]
    [:session -1]
    [:driver-name "clj-odbp"]
    [:driver-version "0.0.1"]
    [:protocol-version 36]
    [:client-id ""]
    [:serialization "ORecordSerializerBinary"]
    [:token-session true]
    [:support-push true]
    [:collect-stats true]
    [:database-name db-name]
    [:username username]
    [:password password]]))

(defn db-open-response
  [^DataInputStream in]
  (specs/decode
   in
   specs/db-open-response))
