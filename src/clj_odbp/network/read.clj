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

(ns clj-odbp.network.read
  (:import [java.io DataInputStream]))

(defn bool-type
  "Read a byte from the input stream and return a boolean. (1=true, 0=false)"
  [^DataInputStream in]
  (let [value (.readByte in)]
    (= 0x1 value)))

(defn byte-type
  "Read a single byte from the input stream."
  [^DataInputStream in]
  (.readByte in))

(defn short-type
  "Read a Short from the input stream."
  [^DataInputStream in]
  (.readShort in))

(defn int-type
  "Read an integer from the input stream."
  [^DataInputStream in]
  (.readInt in))

(defn long-type
  "Read a Long from the input stream."
  [^DataInputStream in]
  (.readLong in))

(defn bytes-type
  "Read a sequence of bytes from the input stream."
  [^DataInputStream in]
  (let [len (int-type in)]
    (if (> len 0)
      (let [buffer (byte-array len)]
        (.read in buffer 0 len)
        (vec buffer))
      [])))

(defn string-type
  "Read a string from the input stream. Format is (length:int)[bytes]"
  [^DataInputStream in]
  (let [buffer (bytes-type in)]
    (apply str (map char buffer))))

(defn strings-type
  "Read a set of strings from the input stream. Format is (elements:int)[strings]"
  [^DataInputStream in]
  (let [n (int-type in)]
    (vec (repeatedly n
                     #(string-type in)))))

(defn array-of
  "Read an array composed by defined type(s). Format is (elements:short)[values]"
  [type-fn functions]
  (fn [^DataInputStream in]
    (let [n (type-fn in)]
      (vec
       (repeatedly n
                   #(mapv (fn [f]
                            (apply f [in]))
                          functions))))))
