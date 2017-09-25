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

(ns clj-odbp.binary.serialize.int
  (:import [java.nio ByteBuffer]))

(defn int16
  "Serialize an int16. Return a byte-array."
  [n]
  (-> (ByteBuffer/allocate 2)
      (.putShort n)
      .array
      vec))

(defn int32
  "Serialize an int32. Return a byte-array."
  [n]
  (-> (ByteBuffer/allocate 4)
      (.putInt n)
      .array
      vec))

(defn int64
  "Serialize an int64. Return a byte-array."
  [n]
  (-> (ByteBuffer/allocate 8)
      (.putLong n)
      .array
      vec))
