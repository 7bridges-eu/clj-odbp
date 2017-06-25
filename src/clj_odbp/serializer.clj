(ns clj-odbp.serializer
  (import [java.io DataOutputStream]))

;; (def ^:const write-map
;;   {:byte write-byte
;;    :string write-string
;;    :short write-short
;;    :bool write-boolean
;;    :int write-int})

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

;; (defn serialize
;;   [^DataOutputStream out message]
;;   (reduce-kv
;;    (fn [o k v]
;;      (prn k v)
;;      (let [f (get write-map k)]
;;        (apply f [o v])))
;;    out
;;    message))
