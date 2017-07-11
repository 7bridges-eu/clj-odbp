(ns clj-odbp.commands.record
  (require [clj-odbp.specs.record :as specs]
           [clj-odbp.utils :refer [encode decode]])
  (import [java.io DataInputStream]))

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
