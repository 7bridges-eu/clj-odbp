(ns clj-odbp.serialize.binary.int
  (:import [java.nio ByteBuffer]))

(defn int32
  "Serialize an int32. Return a byte-array."
  [n]
  (-> (ByteBuffer/allocate 4)
      (.putInt n)
      .array
      byte-array))

(defn int64
  "Serialize an int64. Return a byte-array."
  [n]
  (-> (ByteBuffer/allocate 8)
      (.putLong n)
      .array
      byte-array))
