(ns clj-odbp.deserialize.binary.utils
  (:require [clj-odbp.deserialize.binary.buffer :as b])
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
