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

(ns clj-odbp.sessions)

(defonce sessions (atom {}))

(defn put-session!
  "Insert a session for the specific service into the sessions atom."
  [session service]
  (swap! sessions assoc service session))

(defn has-session?
  "Check if there is at least one session for the specified service."
  [service]
  (not (empty? (get @sessions service))))

(defn read-session
  "Read the session for the specified service. TO BE IMPROVED!"
  [service]
  (get @sessions service))

(defn read-sessions
  "Read all the sessions."
  []
  @sessions)

(defn reset-session!
  "Reset the sessions of the specified service."
  [service]
  (swap! sessions assoc service {}))

(defn reset-sessions!
  "Reset the sessions atom."
  []
  (swap! sessions empty))
