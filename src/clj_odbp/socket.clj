(ns clj-odbp.socket
  (:require [clojure.java.io :as io]
            [clj-odbp.serializer :as s]
            [clj-odbp.deserialize :as d])
  (:import [java.io DataInputStream DataOutputStream]
           [java.net Socket]))

(def ^:const current-protocol-version 36)

(defn- create-socket
  "Connect to the OrientDB server and check the version"
  [host port]
  (let [sock (Socket. host port)
        reader (DataInputStream. (.getInputStream sock))
        version (.readShort reader)] 
    sock))

(defn read-response
  [sock]
  (let [reader (DataInputStream. (.getInputStream sock))
        data (ByteArrayOutputStream. )
        buffer (DataOutputStream. data)]
    (-> buffer
        (d/read-int reader)) 
    sock))

(defn connect
  [sock]
  (let [writer (DataOutputStream. (.getOutputStream sock))]
    (-> writer
        (s/write-byte 0x2)
        (s/write-int -1)
        (s/write-string "clj-odbj")
        (s/write-string "0.0.1")
        (s/write-short 36)
        (s/write-string "")
        (s/write-string "ORecordSerializerBinary")
        (s/write-boolean true)
        (s/write-boolean true)
        (s/write-boolean true)
        (s/write-string "root")
        (s/write-string "root")
        (.flush))
    sock))
