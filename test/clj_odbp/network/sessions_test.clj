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

(ns clj-odbp.network.sessions-test
  (:require [clj-odbp.network.sessions :as s]
            [midje.sweet :refer :all]))

(facts "OrientDB session manipulation"
       (fact "put-session! - insert {:session-id 1} for :db service should return {:db {:session-id 1}}"
             (s/put-session! {:session-id 1} :db) => {:db {:session-id 1}})
       (fact "has-session? - service :db should return true"
             (s/has-session? :db) => true)
       (fact "has-session? - service :server should return false"
             (s/has-session? :server) => false)
       (fact "read-session - read session for :db should return {:session-id 1}"
             (s/read-session :db) => {:session-id 1})
       (fact "read-sessions - read sessions should return {:db {:session-id 1}}"
             (s/read-sessions) => {:db {:session-id 1}})
       (fact "reset-session! - reset session for :db should return {:db {}}"
             (s/reset-session! :db) => {:db {}})
       (fact "reset-sessions! - reset sessions should return {}"
             (s/reset-sessions!) => {}))
