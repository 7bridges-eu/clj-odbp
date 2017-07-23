(ns clj-odbp.deserialize.binary.utils
  (:require [clj-odbp.deserialize.binary.buffer :as b]))

(defn read-int32
  "Read a 32 bit integer from the buffer."
  [buffer]
  (let [data (b/buffer-take buffer 4)
        one (bit-shift-left (nth data 0) 24)
        two (bit-shift-left (bit-and 0xFF (nth data 1)) 16)
        three (bit-shift-left (bit-and 0xFF (nth data 2)) 8)
        four (bit-and 0xFF (nth data 3))]
    (bit-or one two three four)))
