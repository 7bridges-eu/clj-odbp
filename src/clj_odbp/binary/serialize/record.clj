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

(ns clj-odbp.binary.serialize.record
  (:require [clj-odbp.binary.serialize.types :as ot]
            [clj-odbp.logger :refer [log debug]]))

(defn serialize-record
  "Serialize `record` for OrientDB. `record` must be a Clojure map."
  [record]
  (let [serialized-record (ot/serialize record)]
    (debug log ::serialize-record (str "Binary record content: " serialized-record))
    serialized-record))
