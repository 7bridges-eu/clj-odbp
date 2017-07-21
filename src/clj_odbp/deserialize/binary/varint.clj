;;; Taken from: https://github.com/miner/varint
(ns clj-odbp.deserialize.binary.varint)

(defn varint-unsigned-long [v]
  (reduce
   (fn [n shift]
     (bit-or n (bit-shift-left
                (bit-and 0x7F (v shift))
                (* shift 7))))
   0
   (range (count v))))

(defn varint-signed-long [v]
  (let [signed-long (varint-unsigned-long v)
        left-63 (bit-shift-left signed-long 63)
        right-63 (bit-shift-right left-63 63)
        temp (bit-shift-right (bit-or right-63 signed-long) 1)]
    (bit-or temp (bit-and signed-long (bit-shift-left 1 63)))))
