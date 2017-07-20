(ns clj-odbp.serialize.binary.common
  (:require [clj-odbp.serialize.binary.varint :as v])
  (:import [java.io ByteArrayOutputStream DataOutputStream]))

(defn bytes-type
  [value]
  (let [bos (ByteArrayOutputStream.)
        dos (DataOutputStream. bos)
        size (count value)
        size-varint (byte-array (v/varint-unsigned size))
        size-varint-len (count size-varint)]
    (.write dos size-varint 0 size-varint-len)
    (.write dos value 0 size)
    (.toByteArray bos)))
