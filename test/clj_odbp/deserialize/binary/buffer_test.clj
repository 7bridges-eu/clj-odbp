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

(ns clj-odbp.deserialize.binary.buffer-test
  (:require [clj-odbp.deserialize.binary.buffer :as b]
            [midje.sweet :refer :all]))

(def buffer (b/to-buffer [0 1 2 3 4 5 6 7]))

(facts "Testing buffer implementation"
       (b/buffer-reset! buffer)
       (b/buffer-current-position buffer) => 0
       (b/buffer-take! buffer 4) => [0 1 2 3]
       (b/buffer-current-position buffer) => 4
       (b/buffer-rest! buffer) => [4 5 6 7]
       (b/buffer-current-position buffer) => 8
       (b/buffer-set-position! buffer 3)
       (b/buffer-current-position buffer) => 3
       (b/buffer-take-while! buffer #(< % 6)) => [3 4 5]
       (b/buffer-rest! buffer) => [6 7])
