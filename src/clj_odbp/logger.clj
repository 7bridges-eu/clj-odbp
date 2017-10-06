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

(ns clj-odbp.logger
  (:require [clj-odbp.configure :refer [config]])
  (:import [java.util Date]
           [java.text SimpleDateFormat]
           [java.util Locale]))

(def log-levels
  {:debug 0 :info 1 :warning 2 :fatal 3})

(def datetime-formatter
  (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss:SSS" (Locale/getDefault)))

(defprotocol Logger
  (log [this message]))

(defn- generic-log [logger level place & messages]
  (->> (interpose " " messages)
       (apply str)
       (hash-map :level level :place place :message)
       (log logger)))

(defn debug [logger place & messages]
  (generic-log logger :debug place messages))

(defn info [logger place & messages]
  (generic-log logger :info place messages))

(defn warning [logger place & messages]
  (generic-log logger :warning place messages))

(defn fatal [logger place & messages]
  (generic-log logger :warning place messages))

(defn- to-log? [level]
  (let [configured-level (get @config :log-level)]
    (>= (get log-levels level -1)
        (get log-levels configured-level 100))))

(defrecord FileLogger [] Logger
           (log [this message]
             (let [file (get @config :log-file)
                   datetime (.format datetime-formatter (Date.))
                   place (get message :place "Unknown")
                   level (get message :level :info)
                   level-string (-> level name .toUpperCase)
                   message (get message :message "")]
               (when (to-log? level)
                 (spit file
                       (str datetime
                            " [" level-string "] "
                            " [" place "] "
                            message "\n")
                       :append true)))))

(def log
  (FileLogger.))
