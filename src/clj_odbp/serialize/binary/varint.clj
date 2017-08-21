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
(ns clj-odbp.serialize.binary.varint)

(defn varint-signed
  "Return vector of longs (range: 0-255) according to varint encoding.
   LSB comes first, all bytes except final (MSB) have high bit set indicating
   more to follow."
  [n]
  (loop [vi []  r n]
    (if (zero? (bit-and (bit-not 0x7F) r))
      (conj vi r)
      (recur (conj vi (bit-or 0x80 (bit-and 0x7F r)))
             (unsigned-bit-shift-right r 7)))))

(defn varint-unsigned
  "Encode `n` using ZigZag algorithm."
  [n]
  (let [value (bit-xor (bit-shift-left n 1) (bit-shift-right n 63))]
    (varint-signed value)))
