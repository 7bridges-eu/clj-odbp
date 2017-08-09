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
