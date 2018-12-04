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

(ns clj-odbp.network.write
  (:import
   [java.io ByteArrayOutputStream DataOutputStream]))

(defn bool-type
  "Write a boolean in form of a byte (0/1) and return the stream."
  [^DataOutputStream out ^Boolean value]
  (if value
    (.writeByte out 1)
    (.writeByte out 0))
  out)

(defn byte-type
  "Writes a single byte and then returns the stream."
  [^DataOutputStream out ^Byte value]
  (.writeByte out value)
  out)

(defn short-type
  "Writes a Short and return the stream."
  [^DataOutputStream out ^Short value]
  (.writeShort out value)
  out)

(defn int-type
  "Writes an Int and return the stream."
  [^DataOutputStream out ^Integer value]
  (.writeInt out value)
  out)

(defn long-type
  "Writes a Long and return the stream."
  [^DataOutputStream out ^Long value]
  (.writeLong out value))

(defn bytes-type
  "Writes a vector of bytes and then returns the stream."
  [^DataOutputStream out value]
  (let [size (count value)]
    (if (> size 0)
      (do
        (int-type out size)
        (doseq [byte value]
          (byte-type out byte)))
      (int-type out -1)))
  out)

(defn string-type
  "Writes a String and return the stream."
  [^DataOutputStream out ^String value]
  (let [chars (.getBytes value "UTF-8")]
    (bytes-type out chars)
    out))

(defn strings-type
  "Write a vector of strings and return the stream."
  [^DataOutputStream out values]
  (let [size (count values)]
    (int-type out size)
    (doseq [value values]
      (string-type out value))
    out))
