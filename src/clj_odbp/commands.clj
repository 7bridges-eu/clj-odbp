(ns clj-odbp.commands
  (require [clj-odbp.serializer :as s]
           [clj-odbp.deserialize :as d])
  (import [java.io ByteArrayOutputStream DataOutputStream DataInputStream]))

(def connect-request-spec
  {:command s/write-byte
   :session s/write-int
   :driver-name s/write-string
   :driver-version s/write-string
   :protocol-version s/write-short
   :client-id s/write-string
   :serialization s/write-string
   :token-session s/write-boolean
   :support-push s/write-boolean
   :collect-stats s/write-boolean
   :username s/write-string
   :password s/write-string})

(def connect-response-spec
  {:status d/byte-type
   :response-session d/int-type
   :session-id d/int-type
   :token d/bytes-type})

(defn connect-request
  [username password]
  (let [out (ByteArrayOutputStream.)
        writer (DataOutputStream. out)]
    (-> writer
        (s/encode 
         connect-request-spec
         [[:command 0x2]
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
    out))

(defn connect-response
  [^DataInputStream in]
  (d/decode in connect-response-spec))
