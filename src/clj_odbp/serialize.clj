(ns clj-odbp.serialize
  (import [java.io DataOutputStream]))

(defn bool-type
  "Write a boolean in form of a byte (0/1) and return the stream."
  [^DataOutputStream out ^Boolean value]
  (if value
    (.writeByte out 1)
    (.writeByte out 0))
  out)

(defn byte-type
  "Writes a single byte and then returns the stream."
  [^DataOutputStream out ^Byte value]
  (.writeByte out value)
  out)

(defn short-type
  "Writes a Short and return the stream."
  [^DataOutputStream out ^Short value]
  (.writeShort out value)
  out)

(defn int-type
  "Writes an Int and return the stream."
  [^DataOutputStream out ^Integer value]
  (.writeInt out value)
  out)

(defn long-type
  "Writes a Long and return the stream."
  [^DataOutputStream out ^Long value]
  (.writeLong out value))

(defn bytes-type
  "Writes a vector of bytes and then returns the stream."
  [^DataOutputStream out value]
  (let [size (count value)]
    (if (> size 0)
      (do
        (int-type out size)
        (doseq [byte value]
          (byte-type out byte)))
      (int-type -1)))
  out)

(defn string-type
  "Writes a String and return the stream."
  [^DataOutputStream out ^String value]
  (let [chars (.getBytes value)]
    (bytes-type out chars)
    out))

(defn strings-type
  "Write a vector of strings and return the stream."
  [^DataOutputStream out values]
  (let [size (count values)]
    (.writeInt out size)
    (doseq [value values]
      (string-type out value))
    out))
