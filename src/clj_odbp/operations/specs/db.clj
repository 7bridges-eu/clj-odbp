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

(ns clj-odbp.operations.specs.db
  (:require [clj-odbp.network.read :as r]
            [clj-odbp.network.write :as w]))

;; REQUEST_SHUTDOWN
(def shutdown-request
  {:operation w/byte-type
   :session-id w/int-type
   :username w/string-type
   :password w/string-type})

;; REQUEST_CONNECT
(def connect-request
  {:operation w/byte-type
   :session-id w/int-type
   :driver-name w/string-type
   :driver-version w/string-type
   :protocol-version w/short-type
   :client-id w/string-type
   :serialization w/string-type
   :token-session w/bool-type
   :support-push w/bool-type
   :collect-stats w/bool-type
   :username w/string-type
   :password w/string-type})

(def connect-response
  {:response-session r/int-type
   :session-id r/int-type
   :token r/bytes-type})

;; REQUEST_DB_OPEN
(def db-open-request
  {:operation w/byte-type
   :session-id w/int-type
   :driver-name w/string-type
   :driver-version w/string-type
   :protocol-version w/short-type
   :client-id w/string-type
   :serialization w/string-type
   :token-session w/bool-type
   :support-push w/bool-type
   :collect-stats w/bool-type
   :database-name w/string-type
   :username w/string-type
   :password w/string-type})

(def db-open-response
  {:response-session r/int-type
   :session-id r/int-type
   :token r/bytes-type
   :clusters (r/array-of r/short-type [r/string-type r/short-type])
   :cluster-config r/bytes-type
   :orient-db-relase r/string-type})

;; REQUEST_DB_CREATE
(def db-create-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :database-name w/string-type
   :database-type w/string-type
   :storage-type w/string-type
   :backup-path w/string-type})

(def db-create-response
  {:session-id r/int-type
   :token r/bytes-type})

;; REQUEST_DB_CLOSE
(def db-close-request
  {:operation w/byte-type})

(def db-close-response
  {:session-id r/int-type
   :token r/bytes-type})

;; REQUEST_DB_EXIST
(def db-exist-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :database-name w/string-type
   :server-storage-type w/string-type})

(def db-exist-response
  {:session-id r/int-type
   :token r/bytes-type
   :result r/bool-type})

;; REQUEST_DB_DROP
(def db-drop-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :database-name w/string-type
   :storage-type w/string-type})

(def db-drop-response
  {:session-id r/int-type
   :token r/bytes-type})

;; REQUEST_DB_SIZE
(def db-size-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type})

(def db-size-response
  {:session-id r/int-type
   :token r/bytes-type
   :size r/long-type})

;; REQUEST_DB_COUNTRECORDS
(def db-countrecords-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type})

(def db-countrecords-response
  {:session-id r/int-type
   :token r/bytes-type
   :count r/long-type})

;; REQUEST_DB_RELOAD
(def db-reload-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type})

(def db-reload-response
  {:session-id r/int-type
   :token r/bytes-type
   :clusters (r/array-of r/short-type [r/string-type r/short-type])})
