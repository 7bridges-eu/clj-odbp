(ns clj-odbp.specs.record
  (:require [clj-odbp.serialize.otype :as s]
            [clj-odbp.deserialize.otype :as d]))

;; REQUEST_RECORD_LOAD
(def record-load-request
  {:operation s/byte-type
   :session-id s/int-type
   ;;   :token s/bytes-type
   :cluster-id s/short-type
   :cluster-position s/long-type
   :fetch-plan s/string-type
   :ignore-cache s/bool-type
   :load-tombstone s/bool-type})

(def record-load-response
  {:session-id d/int-type
   :payload-status d/byte-type
   :record-type (comp char d/byte-type)
   :record-version d/int-type
   :record-content d/bytes-type})

;; REQUEST_RECORD_CREATE
(def record-create-request
  {:operation s/byte-type
   :session-id s/int-type
   :cluster-id s/short-type
   :record-content s/string-type
   :record-type s/byte-type
   :mode s/byte-type})

(def record-create-response
  {:session-id d/int-type
   :cluster-id d/short-type
   :cluster-position d/long-type
   :record-version d/int-type
   :collection-changes (d/array-of d/int-type [d/long-type d/long-type
                                               d/long-type d/long-type
                                               d/int-type])})

;; REQUEST_RECORD_UPDATE
(def record-update-request
  {:operation s/byte-type
   :session-id s/int-type
   :cluster-id s/short-type
   :cluster-position s/long-type
   :update-content s/bool-type
   :record-content s/string-type
   :record-version s/int-type
   :record-type s/byte-type
   :mode s/byte-type})

(def record-update-response
  {:session-id d/int-type
   :record-version d/int-type
   :collection-changes (d/array-of d/int-type [d/long-type d/long-type
                                               d/long-type d/long-type
                                               d/int-type])})

;; REQUEST_RECORD_DELETE
(def record-delete-request
  {:operation s/byte-type
   :session-id s/int-type
   :cluster-id s/short-type
   :cluster-position s/long-type
   :record-version s/int-type
   :mode s/byte-type})

(def record-delete-response
  {:session-id d/int-type
   :deleted d/bool-type})
