(ns clj-odbp.serialize.binary.common
  (:require [clj-odbp.serialize.binary.varint :as v]))

(defn bytes-type
  [value]
  (let [size (count value)
        size-varint (v/varint-unsigned size)]
    (vec (concat size-varint value))))
