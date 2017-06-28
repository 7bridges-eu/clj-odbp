(ns clj-odbp.net
  (:require [clojure.java.io :as io])
  (:import [java.io DataInputStream DataOutputStream]
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
  [^Socket socket command & args]
  (let [out (.getOutputStream socket)
        request (apply command args)]
    (.writeTo request out)
    (.flush out))
  socket)

(defn read-response
  [^Socket socket command]
  (let [in (DataInputStream. (.getInputStream socket))]
    (command in)))
