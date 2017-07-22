(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.varint :as v]
            [clj-odbp.deserialize.binary.otypes :refer [type-list]]
            [clj-odbp.deserialize.binary.utils :refer [to-bytearray take-n-bytes]])
  (:import [java.io ByteArrayInputStream]))

(defn read-int32
  "Read a 32 bit integer from the stream."
  [stream]
  (let [buffer (take-n-bytes stream 4)
        one (bit-shift-left (nth buffer 0) 24)
        two (bit-shift-left (bit-and 0xFF (nth buffer 1)) 16)
        three (bit-shift-left (bit-and 0xFF (nth buffer 2)) 8)
        four (bit-and 0xFF (nth buffer 3))]
    (bit-or one two three four)))

(defn string-type
  "Read a string from the stream."
  [stream lenght]
  (let [b (take-n-bytes stream lenght)]
    (apply str (map char b))))

(defn read-version
  [stream]
  (int (first (take-n-bytes stream 1))))

(defn read-class-name
  [stream]
  (let [size (v/varint-signed-long (take-n-bytes stream 1))]
    (apply str (map char (take-n-bytes stream size)))))

(defn read-headers
  "Read and decode the header"
  [buffer]
  (loop [field-size (v/varint-signed-long (take-n-bytes buffer 1))
         headers []]
    (if (zero? field-size)
      headers
      (let [field-name (string-type buffer field-size)
            field-position (read-int32 buffer)
            data-type (int (byte-type buffer))]
        (recur (v/varint-signed-long (take-n-bytes buffer 1))
               (conj headers
                     {:field-name field-name
                      :field-position field-position
                      :data-type (nth type-list data-type)}))))))
(defn read-record
  [headers content]
  (reduce
   (fn [record header]
     (let [{key :field-name
            position :field-position
            reader :data-type} header]
       (assoc
        record
        (keyword key)
        (reader content position))))
   {}
   headers))

(defn deserialize-record
  [record]
  (let [content (:record-content record)
        buffer (to-bytearray content)
        version (read-version buffer)
        class-name (read-class-name buffer)
        headers (read-headers buffer)]
    {class-name (read-record headers content)}))
