(ns clj-odbp.operations.record
  (:require [clj-odbp
             [constants :as constants]
             [utils :refer [decode encode]]]
            [clj-odbp.serialize.binary.record :refer [serialize-record]]
            [clj-odbp.deserialize.binary.record :refer [deserialize-record]]
            [clj-odbp.specs.record :as specs])
  (:import java.io.DataInputStream))

(defn record-load-request
  [connection id position]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/record-load-request
     [[:operation 30]
      [:session-id session-id]
      [:token token]
      [:cluster-id id]
      [:cluster-position position]
      [:fetch-plan "*:0"]
      [:ignore-cache false]
      [:load-tombstone false]])))

(defn record-load-response
  [^DataInputStream in]
  (-> (decode
       in
       specs/record-load-response)
      deserialize-record))

;; REQUEST_RECORD_CREATE
(defn record-create-request
  [connection record-content]
  (let [session-id (:session-id connection)
        token (:token connection)
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
  (decode
   in
   specs/record-create-response))

;; REQUEST_RECORD_UPDATE
(defn record-update-request
  [connection cluster-id cluster-position record-content]
  (let [session-id (:session-id connection)
        token (:token connection)
        record-bytes (serialize-record record-content)]
    (encode
     specs/record-update-request
     [[:operation 32]
      [:session-id session-id]
      [:token token]
      [:cluster-id cluster-id]
      [:cluster-position cluster-position]
      [:update-content true]
      [:record-content record-bytes]
      [:record-version -1]
      [:record-type constants/record-type-document]
      [:mode 0]])))

(defn record-update-response
  [^DataInputStream in]
  (decode
   in
   specs/record-update-response))

;; REQUEST_RECORD_DELETE
(defn record-delete-request
  [connection cluster-id cluster-position]
  (let [session-id (:session-id connection)
        token (:token connection)]
    (encode
     specs/record-delete-request
     [[:operation 33]
      [:session-id session-id]
      [:token token]
      [:cluster-id cluster-id]
      [:cluster-position cluster-position]
      [:record-version -1]
      [:mode 0]])))

(defn record-delete-response
  [^DataInputStream in]
  (decode
   in
   specs/record-delete-response))
