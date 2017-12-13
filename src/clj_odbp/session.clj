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

(ns clj-odbp.session
  (:require [clj-odbp.logger :refer [debug log]]
            [clj-odbp.network.exception :as ex]
            [clj-odbp.network.socket :as s]))

(defprotocol Session
  "A closable Session."
  (close [this] "Close the session"))

(defrecord OrientSession [socket session-id token]
  Session
  (close [this] (. (:socket this) close)))

(defn macro-debug
  "Log a helpful debug message with `command-name` and the `args` passed to it.
  Check `clj-odbp.configure` for the logging setup."
  [command-name & args]
  (let [c (str command-name)
        as (->> args (interpose ", ") (apply str))]
    (debug log
           (keyword c)
           (format "Called %s with arguments: %s" c as))))

(defmacro defsession
  "Create a function named `command-name` with the argument specified in `args`.
  The result of invoking `request-handler` and `response-handler` is parsed into
  an `OrientSession` and returned.

  E.g.:

  (defsession test
    [arg-1 arg-2]
    request-fn
    response-fn)"
  [command-name args request-fn response-fn]
  `(defn ~command-name
     [~@args]
     (macro-debug ~command-name ~@(remove '#{&} args))
     (try
       (let [{host# :host port# :port} (first ~args)
             socket# (s/create-socket host# port#)
             s# (-> socket#
                    (s/write-request ~request-fn ~@(remove '#{&} (rest args)))
                    (s/read-response ~response-fn))
             {session-id# :session-id token# :token} s#]
         (->OrientSession socket# session-id# token#))
       (catch Exception e#
         (ex/manage-exception {:exception-type (:type (ex-data e#))
                               :exception e#})))))

(defmacro defcommand
  "Create a function named `command-name` with the argument specified in `args`.
  `request-handler` and `response-handler` are the functions which the macro
  invoke.

  E.g.:

  (defcommand test
    [arg-1 arg-2]
    request-fn
    response-fn)"
  [command-name args request-fn response-fn]
  `(defn ~command-name
     [~@args]
     (macro-debug ~command-name ~@(remove '#{&} args))
     (try
       (let [session# (first (vector ~@(remove '#{&} args)))
             socket# (:socket session#)
             auth# (select-keys session# [:session-id :token])]
         (-> socket#
             (s/write-request ~request-fn auth# ~@(remove '#{&} (rest args)))
             (s/read-response ~response-fn)))
       (catch Exception e#
         (ex/manage-exception {:exception-type (:type (ex-data e#))
                               :exception e#})))))
