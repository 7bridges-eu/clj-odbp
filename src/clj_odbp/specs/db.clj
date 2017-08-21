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

(ns clj-odbp.specs.db
  (:require [clj-odbp.deserialize.otype :as d]
            [clj-odbp.serialize.otype :as s]))

;; REQUEST_SHUTDOWN
(def shutdown-request
  {:operation s/byte-type
   :session-id s/int-type
   :username s/string-type
   :password s/string-type})

;; REQUEST_CONNECT
(def connect-request
  {:operation s/byte-type
   :session-id s/int-type
   :driver-name s/string-type
   :driver-version s/string-type
   :protocol-version s/short-type
   :client-id s/string-type
   :serialization s/string-type
   :token-session s/bool-type
   :support-push s/bool-type
   :collect-stats s/bool-type
   :username s/string-type
   :password s/string-type})

(def connect-response
  {:response-session d/int-type
   :session-id d/int-type
   :token d/bytes-type})

;; REQUEST_DB_OPEN
(def db-open-request
  {:operation s/byte-type
   :session-id s/int-type
   :driver-name s/string-type
   :driver-version s/string-type
   :protocol-version s/short-type
   :client-id s/string-type
   :serialization s/string-type
   :token-session s/bool-type
   :support-push s/bool-type
   :collect-stats s/bool-type
   :database-name s/string-type
   :username s/string-type
   :password s/string-type})

(def db-open-response
  {:response-session d/int-type
   :session-id d/int-type
   :token d/bytes-type
   :clusters (d/array-of d/short-type [d/string-type d/short-type])
   :cluster-config d/bytes-type
   :orient-db-relase d/string-type})

;; REQUEST_DB_CREATE
(def db-create-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :database-name s/string-type
   :database-type s/string-type
   :storage-type s/string-type
   :backup-path s/string-type})

(def db-create-response
  {:session-id d/int-type
   :token d/bytes-type})

;; REQUEST_DB_CLOSE
(def db-close-request
  {:operation s/byte-type})

(def db-close-response
  {:session-id d/int-type
   :token d/bytes-type})

;; REQUEST_DB_EXIST
(def db-exist-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :database-name s/string-type
   :server-storage-type s/string-type})

(def db-exist-response
  {:session-id d/int-type
   :token d/bytes-type
   :result d/bool-type})

;; REQUEST_DB_DROP
(def db-drop-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :database-name s/string-type
   :storage-type s/string-type})

(def db-drop-response
  {:session-id d/int-type
   :token d/bytes-type})

;; REQUEST_DB_SIZE
(def db-size-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type})

(def db-size-response
  {:session-id d/int-type
   :token d/bytes-type
   :size d/long-type})

;; REQUEST_DB_COUNTRECORDS
(def db-countrecords-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type})

(def db-countrecords-response
  {:session-id d/int-type
   :token d/bytes-type
   :count d/long-type})

;; REQUEST_DB_RELOAD
(def db-reload-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type})

(def db-reload-response
  {:session-id d/int-type
   :token d/bytes-type
   :clusters (d/array-of d/short-type [d/string-type d/short-type])})
