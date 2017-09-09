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

(ns clj-odbp.binary.deserialize.utils
  (:require [clj-odbp.binary.deserialize.buffer :as b])
  (:import [java.nio ByteBuffer]))

(defn bytes->integer
  "Read a 32 bit integer from the buffer."
  [buffer]
  (let [data (b/buffer-take! buffer 4)]
    (-> data
        byte-array
        ByteBuffer/wrap
        .getInt)))

(defn bytes->long
  "Read a 64 bit long from the buffer."
  [buffer]
  (let [data (b/buffer-take! buffer 8)]
    (-> data
        byte-array
        ByteBuffer/wrap
        .getLong)))

(defn bytes->utf8-str
  "Transform a sequence of bytes into an UTF-8 encoded string."
  [bytes]
  (-> bytes
      byte-array
      (String. "UTF-8")))
