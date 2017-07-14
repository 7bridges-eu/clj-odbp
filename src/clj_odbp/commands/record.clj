(ns clj-odbp.commands.record
  (:require [clj-odbp.specs.record :as specs]
            [clj-odbp.utils :refer [encode decode]]
            [clj-odbp.serialize.record :refer [serialize-record]])
  (:import [java.io DataInputStream]))

(defn record-load-request
  [session-id id position]
  (encode
   specs/record-load-request
   [[:operation 30]
    [:session-id session-id]
    ;;    [:token []]
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
  (encode
   specs/record-create-request
   [[:operation 31]
    [:session-id session-id]
    [:cluster-id (short -1)]
    [:record-content (.getBytes (serialize-record record-content))]
    [:record-type (byte \d)]
    [:mode 0]]))

(defn record-create-response
  [^DataInputStream in]
  (decode
   in
   specs/record-create-response))
