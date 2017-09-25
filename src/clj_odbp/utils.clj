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

(ns clj-odbp.utils
  (:require [clj-odbp.network
             [socket :as s]
             [sessions :as sessions]
             [exception :as ex]]
            [clj-odbp.logger :refer [log debug]])
  (:import [java.io ByteArrayOutputStream DataInputStream DataOutputStream]))

(defn valid-message?
  "Validate `message` against `spec`."
  [spec message]
  (every? #(contains? spec (first %)) message))

(defn encode
  "Encode `message` applying for each of its fields the function specified in
  `spec.`"
  [spec message]
  (let [out (ByteArrayOutputStream.)
        stream (DataOutputStream. out)]
    (if (valid-message? spec message)
      (doseq [[field-name value] message
              :let [function (get spec field-name)]]
        (try
          (apply function [stream value])
          (catch Exception e
            (throw (Exception. (str (.getMessage e) " writing " field-name))))))
      (throw (Exception. "The message doesn't respect the spec.")))
    out))

(defn decode
  "Decode the data stream `in` according to `spec`."
  [^DataInputStream in spec]
  (persistent!
   (reduce-kv
    (fn [result field-name f]
      (assoc! result field-name (f in)))
    (transient {})
    spec)))

(defn take-upto
  "Returns a lazy sequence of successive items from coll up to and including
  the first item for which `(pred item)` returns true."
  ([pred]
   (fn [rf]
     (fn
       ([] (rf))
       ([result] (rf result))
       ([result x]
        (let [result (rf result x)]
          (if (pred x)
            (ensure-reduced result)
            result))))))
  ([pred coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (let [x (first s)]
        (cons x (if-not (pred x) (take-upto pred (rest s)))))))))

(defn parse-rid
  "Parse a RID in string format and returns a vector with cluster-id and
  record-position."
  [rid]
  {:pre [(re-matches #"#\d+:\d+" rid)]}
  (let [[_ cluster-id record-position] (re-find #"#(\d+):(\d+)" rid)]
    (mapv #(Integer/parseInt %) [cluster-id record-position])))

(defn compose-rid
  "Given a cluster-id and a record-position returns a RID in string format."
  [cluster-id record-position]
  (str "#" cluster-id ":" record-position))

(defmacro defcommand
  "Create a function named `command-name` that accepts the argument specified
  in `args`. `request-handler` and `response-handler` must be functions. e.g.:

  (defcommand test
    [arg-1 arg-2]
    request-handler-fn
    response-handler-fn)"
  [command-name args request-handler response-handler]
  `(defn ~command-name
     [~@args]
     (debug log
            (keyword ~command-name)
            "Called %s with arguments: %s"
            ~command-name ~@(remove '#{&} args))
     (try
       (with-open [s# (s/create-socket)]
         (-> s#
             (s/write-request ~request-handler ~@(remove '#{&} args))
             (s/read-response ~response-handler)))
       (catch Exception e#
         (ex/manage-exception {:exception-type (:type (ex-data e#))
                               :exception e#})))))

(defmacro defconnection
  "Create a function named `command-name` that accepts the argument specified
  in `args`. `request-handler` and `response-handler` must be functions.
  `service` is a keyword which indicates the service to which the connection
  applies. e.g.:

  (defconnection test
    [arg-1 arg-2]
    request-handler-fn
    response-handler-fn
    :db)"
  [command-name args request-handler response-handler service]
  `(defn ~command-name
     [~@args]
     (debug log
            (keyword ~command-name)
            "Called %s with arguments: %s"
            ~command-name ~@(remove '#{&} args))
     (if (sessions/has-session? ~service)
       (sessions/read-session ~service)
       (try
         (with-open [s# (s/create-socket)]
           (-> s#
               (s/write-request ~request-handler ~@(remove '#{&} args))
               (s/read-response ~response-handler)
               (select-keys [:session-id :token])
               (sessions/put-session! ~service))
           (sessions/read-session ~service))
         (catch Exception e#
           (ex/manage-exception {:exception-type (:type (ex-data e#))
                                 :exception e#}))))))
