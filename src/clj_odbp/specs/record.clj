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

(ns clj-odbp.specs.record
  (:require [clj-odbp.deserialize.otype :as d]
            [clj-odbp.serialize.otype :as s]))

;; REQUEST_RECORD_LOAD
(def record-load-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :cluster-id s/short-type
   :record-position s/long-type
   :fetch-plan s/string-type
   :ignore-cache s/bool-type
   :load-tombstone s/bool-type})

(def record-load-response
  {:session-id d/int-type
   :token d/bytes-type
   :payload-status d/byte-type})

(def record-load-content-response
  {:record-type (comp char d/byte-type)
   :record-version d/int-type
   :record-content d/bytes-type})

;; REQUEST_RECORD_CREATE
(def record-create-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :cluster-id s/short-type
   :record-content s/bytes-type
   :record-type s/byte-type
   :mode s/byte-type})

(def record-create-response
  {:session-id d/int-type
   :token d/bytes-type
   :cluster-id d/short-type
   :record-position d/long-type
   :record-version d/int-type
   :collection-changes (d/array-of d/int-type [d/long-type d/long-type
                                               d/long-type d/long-type
                                               d/int-type])})

;; REQUEST_RECORD_UPDATE
(def record-update-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :cluster-id s/short-type
   :record-position s/long-type
   :update-content s/bool-type
   :record-content s/bytes-type
   :record-version s/int-type
   :record-type s/byte-type
   :mode s/byte-type})

(def record-update-response
  {:session-id d/int-type
   :token d/bytes-type
   :record-version d/int-type
   :collection-changes (d/array-of d/int-type [d/long-type d/long-type
                                               d/long-type d/long-type
                                               d/int-type])})

;; REQUEST_RECORD_DELETE
(def record-delete-request
  {:operation s/byte-type
   :session-id s/int-type
   :token s/bytes-type
   :cluster-id s/short-type
   :record-position s/long-type
   :record-version s/int-type
   :mode s/byte-type})

(def record-delete-response
  {:session-id d/int-type
   :token d/bytes-type
   :deleted d/bool-type})
