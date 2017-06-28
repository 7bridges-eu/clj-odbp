(ns clj-odbp.serializer
  (import [java.io DataOutputStream]))


(defn write-byte
  "Writes a single byte and then returns the stream."
  [^DataOutputStream out ^Byte value]
  (.writeByte out value)
  out)

(defn write-string
  "Writes a String and return the stream."
  [^DataOutputStream out ^String value]
  (let [chars (.getBytes value)
        size (count chars)]
    (.writeInt out size)
    (.write out chars 0 size)
    out))

(defn write-short
  "Writes a Short and return the stream."
  [^DataOutputStream out ^Short value]
  (.writeShort out value)
  out)

(defn write-int
  "Writes an Int and return the stream."
  [^DataOutputStream out ^Integer value]
  (.writeInt out value)
  out)

(defn write-boolean
  "Write a boolean in form of a byte (0/1) and return the stream."
  [^DataOutputStream out ^Boolean value]
  (if value
    (.writeByte out 1)
    (.writeByte out 0))
  out)

(defn- validate-message
  [spec message]
  (when-not (every?
             #(contains? spec (first %))
             message)
    (throw (Exception. "The message doesn't respect the spec."))))

(defn encode
  [^DataOutputStream stream spec message]
  (validate-message spec message)
  (doall (map
          (fn [field]
            (let [field-name (first field)
                  value (second field)
                  function (get spec field-name)]
              (try
                (apply function [stream value])
                (catch Exception e 
                  (throw (Exception. (str (.getMessage e) " writing " field-name))))))) 
          message))
  stream)
