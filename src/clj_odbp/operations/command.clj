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

(ns clj-odbp.operations.command
  (:require [clj-odbp
             [constants :as constants]
             [utils :refer [encode decode]]]
            [clj-odbp.operations.specs.command :as specs]
            [clj-odbp.binary.serialize.types :as t]
            [clj-odbp.binary.deserialize.record :as deserialize]
            [clj-odbp.network.read :as r])
  (:import java.io.DataInputStream))

(defn get-bytes-type-length [bytes-type]
  (if (empty? bytes-type)
    0
    (+ 4 (count bytes-type))))

(defn get-query-payload-length
  [command fetch-plan serialized-params]
  (+ 4 (count constants/request-command-query)
     4 (count command)
     4                                  ; non-text-limit length
     4 (count fetch-plan)
     (get-bytes-type-length serialized-params)))

(defn get-execute-payload-length
  [command serialized-params]
  (+ 4 (count constants/request-command-execute)
     4 (count command)
     1 (get-bytes-type-length serialized-params)
     1))

(def ^:const params-serializer
  (get-method t/serialize :embedded-record-type))

(defn serialize-params
  [params-name params]
  (if (empty? params)
    ""
    (params-serializer {params-name params} 0)))

;; REQUEST_COMMAND > QUERY
(defn query-request
  [connection command
   {:keys [params non-text-limit fetch-plan]
    :or {params {} non-text-limit -1 fetch-plan "*:0"}}]
  (let [session-id (:session-id connection)
        token (:token connection)
        serialized-params (serialize-params "params" params)]
    (encode
     specs/query-request
     [[:operation 41]
      [:session-id session-id]
      [:token token]
      [:mode constants/request-command-sync-mode]
      [:payload-length (get-query-payload-length command
                                                 fetch-plan
                                                 serialized-params)]
      [:class-name constants/request-command-query]
      [:text command]
      [:non-text-limit non-text-limit]
      [:fetch-plan fetch-plan]
      [:serialized-params serialized-params]])))

(defn- query-list-response
  [^DataInputStream in]
  (let [list-size (r/int-type in)]
    (reduce
     (fn [acc n]
       (case (r/short-type in)
         0 (conj acc (-> (decode in specs/record-response)
                         deserialize/deserialize-record))
         (conj acc nil)))
     []
     (range list-size))))

(defn- query-single-response
  [^DataInputStream in]
  (let [boh (r/short-type in)]
    (-> (decode in specs/record-response)
        deserialize/deserialize-record)))

(defn query-response
  [^DataInputStream in]
  (let [generic-response (decode in specs/sync-generic-response)
        result-type (:result-type generic-response)]
    (case result-type
      \n []
      \l (query-list-response in)
      \s (query-list-response in)
      \r (query-single-response in)
      \w (query-single-response in))))

;; REQUEST_COMMAND > EXECUTE
(defn execute-request
  [connection command
   {:keys [params] :or {params {}}}]
  (let [session-id (:session-id connection)
        token (:token connection)
        serialized-params (serialize-params "parameters" params)]
    (encode
     specs/execute-request
     [[:operation 41]
      [:session-id session-id]
      [:token token]
      [:mode constants/request-command-sync-mode]
      [:payload-length (get-execute-payload-length command
                                                   serialized-params)]
      [:class-name constants/request-command-execute]
      [:text command]
      [:has-simple-params (not (empty? serialized-params))]
      [:simple-params serialized-params]
      [:has-complex-params false]
      [:complex-params []]])))
