(ns clj-odbp.net
  (:require [clojure.java.io :as io]
            [clj-odbp.serializer :as s]
            [clj-odbp.deserialize :as d])
  (:import [java.io DataInputStream DataOutputStream ByteArrayOutputStream]
           [java.net Socket]))

(def ^:const supported-protocol-version 36)

(defn create-socket
  "Connect to the OrientDB server and check the version"
  [host port]
  (let [socket (Socket. host port)
        reader (DataInputStream. (.getInputStream socket))
        version (.readShort reader)]
    (when (> version supported-protocol-version)
      (throw (Exception. 
              (str "Unsupported binary protocol version "
                   version "."))))
    socket))

(defn write-request
  [^Socket socket ^ByteArrayOutputStream request]
  (let [out (.getOutputStream socket)]
    (.writeTo request socket))
  socket)

(defn read-response
  [socket]
  (let [reader (DataInputStream. (.getInputStream socket))]
    (-> buffer
        (d/read-int reader)) 
    socket))
