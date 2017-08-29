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

(ns clj-odbp.net
  (:require [clj-odbp.configure :as c]
            [clj-odbp.deserialize.exception :as e]
            [taoensso.timbre :as log])
  (:import java.io.DataInputStream
           java.net.Socket))

(def ^:const supported-protocol-version 36)

(defn create-socket
  "Connect to the OrientDB server and check the version."
  []
  (let [{:keys [host port]} @c/config
        socket (Socket. host port)
        reader (DataInputStream. (.getInputStream socket))
        version (.readShort reader)]
    (when (> version supported-protocol-version)
      (throw (Exception.
              (str "Unsupported binary protocol version "
                   version "."))))
    (.setSoTimeout socket 5000)
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
      (e/handle-exception in))))
