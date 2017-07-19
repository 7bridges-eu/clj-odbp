;;; Taken from: https://github.com/miner/varint
(ns clj-odbp.serialize.varint)

(defn varint
  "Return vector of longs (range: 0-255) according to varint encoding.
   LSB comes first, all bytes except final (MSB) have high bit set indicating
   more to follow."
  [n]
  (loop [vi []  r n]
    (if (zero? (bit-and (bit-not 0x7F) r))
      (conj vi r)
      (recur (conj vi (bit-or 0x80 (bit-and 0x7F r)))
             (unsigned-bit-shift-right r 7)))))
