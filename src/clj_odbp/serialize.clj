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
  "Write a single byte and then return the stream."
  [^DataOutputStream out ^Byte value]
  (.writeByte out value)
  out)

(defn short-type
  "Write a Short and return the stream."
  [^DataOutputStream out ^Short value]
  (.writehort out value)
  out)

(defn int-type
  "Write an Int and return the stream."
  [^DataOutputStream out ^Integer value]
  (.writeInt out value)
  out)

(defn long-type
  "Write a Long and return the stream."
  [^DataOutputStream out ^Long value]
  (.writeLong out value))

(defn string-type
  "Write a String and return the stream."
  [^DataOutputStream out ^String value]
  (let [chars (.getBytes value)
        size (count chars)] 
    (.writeInt out size)
    (.write out chars 0 size)
    out))

(defn strings-type
  "Write a vector of strings and return the stream."
  [^DataOutputStream out v] 
  (let [size (count v)] 
    (.writeInt out size)
    (doall
     (map #(string-type out %) v))
    out))
