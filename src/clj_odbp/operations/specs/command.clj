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

(ns clj-odbp.operations.specs.command
  (:require [clj-odbp.network.read :as r]
            [clj-odbp.network.write :as w]))

;; REQUEST_COMMAND > SELECT
(def query-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :mode w/byte-type
   :payload-length w/int-type
   :class-name w/string-type
   :text w/string-type
   :non-text-limit w/int-type
   :fetch-plan w/string-type
   :serialized-params w/bytes-type})

;; REQUEST_COMMAND > SQL Command
(def execute-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :mode w/byte-type
   :payload-length w/int-type
   :class-name w/string-type
   :text w/string-type
   :has-simple-params w/bool-type
   :simple-params w/bytes-type
   :has-complex-params w/bool-type
   :complex-params w/bytes-type})

;; REQUEST_COMMAND > Script
(def script-request
  {:operation w/byte-type
   :session-id w/int-type
   :token w/bytes-type
   :mode w/byte-type
   :payload-length w/int-type
   :class-name w/string-type
   :language w/string-type
   :text w/string-type
   :has-simple-params w/bool-type
   :simple-params w/bytes-type
   :has-complex-params w/bool-type
   :complex-params w/bytes-type})

;; REQUEST_COMMAND > Sync response
(def sync-generic-response
  {:session-id r/int-type
   :token r/bytes-type
   :result-type (comp char r/byte-type)})

(def record-response
  {:record-type (comp char r/byte-type)
   :record-cluster r/short-type
   :record-position r/long-type
   :record-version r/int-type
   :record-content r/bytes-type})

;; REQUEST_COMMAND > Async response
(def async-response {})
