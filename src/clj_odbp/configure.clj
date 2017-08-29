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

(ns clj-odbp.configure
  (:require [taoensso.timbre :as log]
            [taoensso.timbre.appenders.3rd-party.rolling :as appender]))

(def config
  "Initial configuration map to set up the connection to OrientDB server and
  the logging system."
  (atom {:host "localhost"
         :port 2424
         :log-file "log/clj_odbp.log"
         :log-level :fatal}))

(defn configure-timbre
  "Set up timbre using the values in `config`."
  []
  (log/merge-config!
   {:level (:log-level @config)
    :appenders
    {:spit (appender/rolling-appender
            {:path (:log-file @config) :pattern :daily})}}))

(defn configure-driver
  "Reset global `config` with the contents of `m`. e.g.:

  (configure-driver {:host \"test\"}) => {:host \"test\" :port 2424}"
  [m]
  (reset! config (merge @config m))
  (configure-timbre))

(configure-timbre)
