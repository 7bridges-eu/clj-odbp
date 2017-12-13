;; Copyright 2017 7bridges s.r.l.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns clj-odbp.network.socket
  (:require [clj-odbp.configure :as c]
            [clj-odbp.network.exception :as e]
            [clj-odbp.logger :refer [log debug]])
  (:import java.io.DataInputStream
           java.net.Socket))

(def ^:const supported-protocol-version 36)

(defn create-socket
  "Connect to the OrientDB server and check the version."
  [host port]
  (let [socket (Socket. host port)
        reader (DataInputStream. (.getInputStream socket))
        version (.readShort reader)]
    (debug log ::create-socket (format "Opening connection to %s:%d" host port))
    (when (> version supported-protocol-version)
      (throw (Exception.
              (str "Unsupported binary protocol version "
                   version "."))))
    (.setSoTimeout socket 5000)
    socket))

(defn write-request
  "Write the result of applying `command` to `args` on the `socket`."
  [^Socket socket command & args]
  (let [out (.getOutputStream socket)
        request (apply command args)]
    (debug log ::write-request (format "request: %s" (vec (.toByteArray request))))
    (.writeTo request out)
    (.flush out))
  socket)

(defn read-response
  "Read the data in `socket` and apply `command` to it. If `socket`
  contains an exception, it is handled by clj-odbp.deserialize.exception/handle-exception."
  [^Socket socket command]
  (let [in (DataInputStream. (.getInputStream socket))
        status (.readByte in)]
    (if (= 0 status)
      (let [response (command in)]
        (debug log ::read-response (format "response: %s" response))
        response)
      (e/handle-exception in))))
