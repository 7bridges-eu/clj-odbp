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

;;; Taken from: https://github.com/miner/varint
(ns clj-odbp.binary.deserialize.varint)

(defn varint-unsigned-long
  "Transform `v` in a varint unsigned. Return a vector of bytes."
  [v]
  (reduce
   (fn [n shift]
     (bit-or n (bit-shift-left
                (bit-and 0x7F (v shift))
                (* shift 7))))
   0
   (range (count v))))

(defn varint-signed-long
  "Transform `v` in a varint signed. Return a vector of bytes."
  [v]
  (let [signed-long (varint-unsigned-long v)
        left-63 (bit-shift-left signed-long 63)
        right-63 (bit-shift-right left-63 63)
        temp (bit-shift-right (bit-or right-63 signed-long) 1)]
    (bit-or temp (bit-and signed-long (bit-shift-left 1 63)))))
