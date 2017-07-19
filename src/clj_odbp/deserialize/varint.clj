;;; Taken from: https://github.com/miner/varint
(ns clj-odbp.deserialize.varint)

(defn varint->long [vari]
  (reduce
   (fn [n shift]
     (bit-or n (bit-shift-left (bit-and 0x7F (vari shift)) (* shift 7))))
   0
   (range (count vari))))
