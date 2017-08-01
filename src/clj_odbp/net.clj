(ns clj-odbp.net
  (:require [clj-odbp.deserialize.otype :as d])
  (:import java.io.DataInputStream
           java.net.Socket))

(def ^:const supported-protocol-version 36)
(def config
  (atom {:host "localhost"
         :port 2424}))

(defn configure-connection
  [host port]
  (swap! config assoc :host host)
  (swap! config assoc :port port))

(defn create-socket
  "Connect to the OrientDB server and check the version."
  []
  (let [{:keys [host port]} @config
        socket (Socket. host port)
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
  (let [in (DataInputStream. (.getInputStream socket))
        status (.readByte in)]
    (if (= 0 status)
      (command in)
      (d/handle-exception in))))
