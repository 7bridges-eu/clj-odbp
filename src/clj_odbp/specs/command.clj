(ns clj-odbp.specs.command
  (:require [clj-odbp.serialize.otype :as s]
            [clj-odbp.deserialize.otype :as d]))

;; REQUEST_COMMAND > SELECT
(def select-request
  {:operation s/byte-type
   :session-id s/int-type
   :mode s/byte-type
   :payload-length s/int-type
   :class-name s/string-type
   :text s/string-type
   :non-text-limit s/int-type
   :fetch-plan s/string-type
   :serialized-params s/bytes-type})

;; REQUEST_COMMAND > SQL Command
(def command-request
  {:operation s/byte-type
   :session-id s/int-type
   :mode s/byte-type
   :payload-length s/int-type
   :class-name s/string-type
   :text s/string-type
   :has-simple-params s/bool-type
   :simple-params s/bytes-type
   :has-complex-params s/bool-type
   :complex-params s/bytes-type})

;; REQUEST_COMMAND > Script
(def script-request
  {:operation s/byte-type
   :session-id s/int-type
   :mode s/byte-type
   :payload-length s/int-type
   :class-name s/string-type
   :language s/string-type
   :text s/string-type
   :has-simple-params s/bool-type
   :simple-params s/bytes-type
   :has-complex-params s/bool-type
   :complex-params s/bytes-type})

;; REQUEST_COMMAND > Sync response
(def sync-response
  {:session-id d/int-type})

;; REQUEST_COMMAND > Async response
(def async-response {})
