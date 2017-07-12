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
   :record-type d/byte-type
   :record-version d/int-type
   :record-content d/bytes-type})
