(ns clj-odbp.serialize.binary.int)

(defn int32
  "Serialize an int32.
   See: com.orientechnologies.common.serialization.types.OIntegerSerializer"
  [n]
  (let [one (byte (bit-and (unsigned-bit-shift-right n 24) 0xFF))
        two (byte (bit-and (unsigned-bit-shift-right n 16) 0xFF))
        three (byte (bit-and (unsigned-bit-shift-right n 8) 0xFF))
        four (byte (bit-and (unsigned-bit-shift-right n 0) 0xFF))]
    [one two three four]))

(defn int64
  "Serialize an int64.
   See: com.orientechnologies.common.serialization.types.OLongSerializer"
  [n]
  (let [one (byte (bit-and (unsigned-bit-shift-right n 56) 0xFF))
        two (byte (bit-and (unsigned-bit-shift-right n 48) 0xFF))
        three (byte (bit-and (unsigned-bit-shift-right n 40) 0xFF))
        four (byte (bit-and (unsigned-bit-shift-right n 32) 0xFF))
        five (byte (bit-and (unsigned-bit-shift-right n 24) 0xFF))
        six (byte (bit-and (unsigned-bit-shift-right n 16) 0xFF))
        seven (byte (bit-and (unsigned-bit-shift-right n 8) 0xFF))
        eight (byte (bit-and (unsigned-bit-shift-right n 0) 0xFF))]
    [one two three four five six seven eight]))
