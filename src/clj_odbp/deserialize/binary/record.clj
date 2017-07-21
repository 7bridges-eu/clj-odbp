(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.varint
             :refer [varint-unsigned-long varint-signed-long]])
  (:import [java.io ByteArrayInputStream]))

(defn bool-type
  "Read a boolean from the stream"
  [buffer]
  nil)

(defn integer-type
  "Read a integer from the stream"
  [buffer]
  nil)

(defn short-type
  "Read a short from the stream"
  [buffer]
  nil)

(defn long-type
  "Read a long from the stream"
  [buffer]
  nil)

(defn float-type
  "Read a float from the stream"
  [buffer]
  nil)

(defn double-type
  "Read a double from the stream"
  [buffer]
  nil)

(defn datetime-type
  "Read a datetime from the stream"
  [buffer]
  nil)

(defn string-type
  "Read a datetime from the stream"
  [buffer]
  nil)

(defn binary-type
  "Read a binary from the stream"
  [buffer]
  nil)

(defn embedded-record-type
  "Read a embedded record from the stream"
  [buffer]
  nil)

(defn embedded-list-type
  "Read a embedded list from the stream"
  [buffer]
  nil)

(defn embedded-set-type
  "Read a embedded set from the stream"
  [buffer]
  nil)

(defn embedded-map-type
  "Read a embedded map from the stream"
  [buffer]
  nil)

(defn link-type
  "Read a link (orid) from the stream"
  [buffer]
  nil)

(defn link-list-type
  "Read a list of links from the stream"
  [buffer]
  nil)

(defn link-set-type
  "Read a set of links from the stream"
  [buffer]
  nil)

(defn link-ma-type
  "Read a map of links from the stream"
  [buffer]
  nil)

(defn byte-type
  "Read a byte from the stream"
  [buffer]
  nil)

(defn transient-type
  "Read a transient type from the stream"
  [buffer]
  nil)

(defn date-type
  "Read a date from the stream"
  [buffer]
  nil)

(defn custom-type
  "Read a custom type from the stream"
  [buffer]
  nil)

(defn decimal-type
  "Read a decimal from the stream"
  [buffer]
  nil)

(defn link-bag-type
  "Read a link bag from the stream"
  [buffer]
  nil)

(defn any-type
  "Read a 'any' type from the stream"
  [buffer]
  nil)

(def type-list
  [bool-type
   (defn integer-type
     "Read a integer from the stream"
     [buffer]
     nil)

   (defn short-type
     "Read a short from the stream"
     [buffer]
     nil)

   (defn long-type
     "Read a long from the stream"
     [buffer]
     nil)

   (defn float-type
     "Read a float from the stream"
     [buffer]
     nil)

   (defn double-type
     "Read a double from the stream"
     [buffer]
     nil)

   (defn datetime-type
     "Read a datetime from the stream"
     [buffer]
     nil)

   (defn string-type
     "Read a datetime from the stream"
     [buffer]
     nil)

   (defn binary-type
     "Read a binary from the stream"
     [buffer]
     nil)

   (defn embedded-record-type
     "Read a embedded record from the stream"
     [buffer]
     nil)

   (defn embedded-list-type
     "Read a embedded list from the stream"
     [buffer]
     nil)

   embedded-list-type
   embedded-set-type
   embedded-map-type
   link-type
   link-list-type
   link-set-type
   link-ma-type
   byte-type
   transient-type
   date-type
   custom-type
   decimal-type
   link-bag-type
   any-type])

(defn- to-bytearray
  [data]
  (-> data
      byte-array
      ByteArrayInputStream.))

(defn- take-n-bytes
  "Return a vector with the first n elements and
  the rest of a sequence"
  [^ByteArrayInputStream stream n]
  (let [b (byte-array n)]
    (do
      (.read stream b)
      (vec b))))

(defn byte-type
  "Read a single byte from the stream."
  [stream]
  (let [b (take-n-bytes stream 1)]
    (first b)))

;; stream[startPosition]) << 24 | (0xff & stream[startPosition + 1]) << 16 | (0xff & stream[startPosition + 2]) << 8 | ((0xff & stream[startPosition + 3]))
(defn read-int32
  "Read a 32 bit integer from the stream."
  [stream]
  (let [buffer (take-n-bytes stream 4)
        one (bit-shift-left (nth buffer 0) 24)
        two (bit-shift-left (bit-and 0xFF (nth buffer 1)) 16)
        three (bit-shift-left (bit-and 0xFF (nth buffer 2)) 8)
        four (bit-and 0xFF (nth buffer 3))]
    (bit-or one two three four)))

(defn read-string
  [stream]
  (let [lenght (varint-signed-long (take-n-bytes stream 1))
        b (take-n-bytes stream lenght)]
    (apply str (map char b))))

(defn string-type
  "Read a string from the stream."
  [stream lenght]
  (let [b (take-n-bytes stream lenght)]
    (apply str (map char b))))

(defn read-headers
  "Read and decode the header"
  [buffer]
  (loop [field-type (varint-signed-long (take-n-bytes buffer 1))
         headers []]
    (if (zero? field-type)
      headers
      (let [field-name (string-type buffer field-type)
            field-position (read-int32 buffer)
            data-type (byte-type buffer)]
        (recur (varint-signed-long (take-n-bytes buffer 1))
               (conj headers
                     {:field-name field-name
                      :field-position field-position
                      :data-type data-type}))))))

(defn deserialize-record
  [record]
  (let [buffer (to-bytearray (:record-content record))
        version (byte-type buffer)
        class-name (read-string buffer)
        headers (read-headers buffer)]
    [version class-name headers]))
