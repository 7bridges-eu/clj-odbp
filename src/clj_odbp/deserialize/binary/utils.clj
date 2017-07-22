(ns clj-odbp.deserialize.binary.utils
  (:import [java.io ByteArrayInputStream]))

(defn to-bytearray
  [data]
  (-> data
      byte-array
      ByteArrayInputStream.))

(defn take-n-bytes
  "Return a vector with the first n elements and
  the rest of a sequence"
  [^ByteArrayInputStream stream n]
  (let [b (byte-array n)]
    (do
      (.read stream b)
      (vec b))))
