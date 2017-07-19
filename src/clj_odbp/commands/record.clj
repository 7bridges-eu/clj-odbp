(ns clj-odbp.commands.record
  (:require [clj-odbp.constants :as constants]
            [clj-odbp.specs.record :as specs]
            [clj-odbp.utils :refer [encode decode]]
            [clj-odbp.serialize.record :refer [serialize-record]])
  (:import [java.io DataInputStream]))

(defn record-load-request
  [session-id id position]
  (encode
   specs/record-load-request
   [[:operation 30]
    [:session-id session-id]
    [:cluster-id id]
    [:cluster-position position]
    [:fetch-plan "*:0"]
    [:ignore-cache false]
    [:load-tombstone false]]))

(defn record-load-response
  [^DataInputStream in]
  (decode
   in
   specs/record-load-response))

;; REQUEST_RECORD_CREATE
(defn record-create-request
  [session-id record-content]
  (let [record-bytes (serialize-record record-content)]
    (encode
     specs/record-create-request
     [[:operation 31]
      [:session-id session-id]
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
  [session-id cluster-id cluster-position record-content]
  (let [record-bytes (serialize-record record-content)]
    (encode
     specs/record-update-request
     [[:operation 32]
      [:session-id session-id]
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
  [session-id cluster-id cluster-position]
  (encode
   specs/record-delete-request
   [[:operation 33]
    [:session-id session-id]
    [:cluster-id cluster-id]
    [:cluster-position cluster-position]
    [:record-version -1]
    [:mode 0]]))

(defn record-delete-response
  [^DataInputStream in]
  (decode
   in
   specs/record-delete-response))
