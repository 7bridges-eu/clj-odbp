(ns clj-odbp.sessions-test
  (:require [clj-odbp.sessions :as s]
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
