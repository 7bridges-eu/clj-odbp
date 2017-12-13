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

(ns clj-odbp.operations.record
  (:require [clj-odbp
             [constants :as constants]
             [utils :refer [decode encode parse-rid compose-rid]]]
            [clj-odbp.network.sessions :as session]
            [clj-odbp.binary.deserialize.record :refer [deserialize-record]]
            [clj-odbp.binary.serialize.record :refer [serialize-record]]
            [clj-odbp.operations.specs.record :as specs])
  (:import java.io.DataInputStream))

(defn record-load-request
  [session rid]
  (let [session-id (:session-id session)
        token (:token session)
        [cluster-id record-position] (parse-rid rid)]
    (encode
     specs/record-load-request
     [[:operation 30]
      [:session-id session-id]
      [:token token]
      [:cluster-id cluster-id]
      [:record-position record-position]
      [:fetch-plan "*:0"]
      [:ignore-cache false]
      [:load-tombstone false]])))

(defn record-load-response
  [^DataInputStream in]
  (let [response (decode in specs/record-load-response)
        session (select-keys response [:session-id :token])
        record-to-read? (fn [r] (not= 0 (:payload-status r)))]
    (when-not (empty? (:token session))
      (session/reset-session! :db)
      (session/put-session! session :db))
    (if (record-to-read? response)
      (-> (decode in specs/record-load-content-response)
          (deserialize-record))
      nil)))

;; REQUEST_RECORD_CREATE
(defn record-create-request
  [session record-content]
  (let [session-id (:session-id session)
        token (:token session)
        record-bytes (serialize-record record-content)]
    (encode
     specs/record-create-request
     [[:operation 31]
      [:session-id session-id]
      [:token token]
      [:cluster-id -1]
      [:record-content record-bytes]
      [:record-type constants/record-type-document]
      [:mode 0]])))

(defn record-create-response
  [^DataInputStream in]
  (let [response (decode in specs/record-create-response)
        session (select-keys response [:session-id :token])
        rid (->> (select-keys response [:cluster-id :record-position])
                 vals
                 (apply compose-rid))
        version (:record-version response)]
    (when-not (empty? (:token session))
      (session/reset-session! :db)
      (session/put-session! session :db))
    (merge response
           {:_rid rid
            :_version version})))

;; REQUEST_RECORD_UPDATE
(defn record-update-request
  [session rid record-content]
  (let [session-id (:session-id session)
        token (:token session)
        [cluster-id position-id] (parse-rid rid)
        record-bytes (serialize-record record-content)]
    (encode
     specs/record-update-request
     [[:operation 32]
      [:session-id session-id]
      [:token token]
      [:cluster-id cluster-id]
      [:record-position position-id]
      [:update-content true]
      [:record-content record-bytes]
      [:record-version (get record-content :_version -1)]
      [:record-type constants/record-type-document]
      [:mode 0]])))

(defn record-update-response
  [^DataInputStream in]
  (let [response (decode in specs/record-update-response)
        session (select-keys response [:session-id :token])]
    (when-not (empty? (:token session))
      (session/reset-session! :db)
      (session/put-session! session :db))
    response))

;; REQUEST_RECORD_DELETE
(defn record-delete-request
  [session rid]
  (let [session-id (:session-id session)
        token (:token session)
        [cluster-id record-position] (parse-rid rid)]
    (encode
     specs/record-delete-request
     [[:operation 33]
      [:session-id session-id]
      [:token token]
      [:cluster-id cluster-id]
      [:record-position record-position]
      [:record-version -1]
      [:mode 0]])))

(defn record-delete-response
  [^DataInputStream in]
  (let [response (decode in specs/record-delete-response)
        session (select-keys response [:session-id :token])]
    (when-not (empty? (:token session))
      (session/reset-session! :db)
      (session/put-session! session :db))
    response))
