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

(ns clj-odbp.configure)

(def log-config
  "Initial configuration map to set up the logging system."
  (atom {:log-file "log/clj_odbp.log"
         :log-level :fatal}))

(defn configure-log
  "Reset global `log-config` with the contents of `m`. e.g.:

  (configure-log {:log-level :debug}) => {:log-file \"log/clj_odbp.log
                                          :log-level :debug}"
  [m]
  (reset! log-config (merge @log-config m)))
