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

(ns clj-odbp.operations.specs.record
  (:require [clj-odbp.network.read :as r]
            [clj-odbp.network.write :as w]))

;; REQUEST_RECORD_LOAD
(def record-load-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :cluster-id w/short-type
   :record-position w/long-type
   :fetch-plan w/string-type
   :ignore-cache w/bool-type
   :load-tombstone w/bool-type})

(def record-load-response
  {:session-id r/int-type
   :token r/bytes-type
   :payload-status r/byte-type})

(def record-load-content-response
  {:record-type (comp char r/byte-type)
   :record-version r/int-type
   :record-content r/bytes-type})

;; REQUEST_RECORD_CREATE
(def record-create-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :cluster-id w/short-type
   :record-content w/bytes-type
   :record-type w/byte-type
   :mode w/byte-type})

(def record-create-response
  {:session-id r/int-type
   :token r/bytes-type
   :cluster-id r/short-type
   :record-position r/long-type
   :record-version r/int-type
   :collection-changes (r/array-of r/int-type [r/long-type r/long-type
                                               r/long-type r/long-type
                                               r/int-type])})

;; REQUEST_RECORD_UPDATE
(def record-update-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :cluster-id w/short-type
   :record-position w/long-type
   :update-content w/bool-type
   :record-content w/bytes-type
   :record-version w/int-type
   :record-type w/byte-type
   :mode w/byte-type})

(def record-update-response
  {:session-id r/int-type
   :token r/bytes-type
   :record-version r/int-type
   :collection-changes (r/array-of r/int-type [r/long-type r/long-type
                                               r/long-type r/long-type
                                               r/int-type])})

;; REQUEST_RECORD_DELETE
(def record-delete-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :cluster-id w/short-type
   :record-position w/long-type
   :record-version w/int-type
   :mode w/byte-type})

(def record-delete-response
  {:session-id r/int-type
   :token r/bytes-type
   :deleted r/bool-type})
