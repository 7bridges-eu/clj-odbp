(ns clj-odbp.deserialize
  (import [java.io DataInputStream]))

(defn bool-type
  "Read a byte from the input stream and return a boolean. (1=true, 0=false)"
  [^DataInputStream in]
  (let [value (.readByte in)]
    (= 0x1 value)))

(defn byte-type
  "Read a single byte from the input stream."
  [^DataInputStream in]
  (.readByte in))

(defn short-type
  "Read a Short from the input stream."
  [^DataInputStream in]
  (.readShort in))

(defn int-type
  "Read an integer from the input stream."
  [^DataInputStream in]
  (.readInt in))

(defn long-type
  "Read a Long from the input stream."
  [^DataInputStream in]
  (.readLong in))

(defn bytes-type
  "Read a sequence of bytes from the input stream."
  [^DataInputStream in]
  (let [len (int-type in)]
    (if (> len 0)
      (let [buffer (byte-array len)]
        (.read in buffer 0 len)
        (vec buffer))
      [])))

(defn string-type
  "Read a string from the input stream. Format is (length:int)[bytes]"
  [^DataInputStream in]
  (let [len (int-type in)
        buffer (byte-array len)]
    (.read in buffer 0 len)
    (apply str (map char buffer))))

(defn strings-type
  "Read a set of strings from the input stream. Format is (elements:int)[strings]"
  [^DataInputStream in]
  (let [n (int-type in)]
    (vec (repeatedly n
                     #(string-type in)))))

(defn array-of
  "Read an array composed by defined type(s). Format is (elements:short)[values]"
  [functions]
  (fn [^DataInputStream in]
    (let [n (short-type in)]
      (vec
       (repeatedly n
                   #(mapv (fn [f]
                            (apply f [in]))
                          functions))))))

(defn- deserialize-exception
  "De-serialize OrientDB exception from DataInputStream `in`."
  [in]
  (loop [delimiter (byte-type in)
         acc []]
    (if (not= delimiter 0)
      (do
        (let [ex-class (string-type in)
              ex-message (string-type in)]
          (recur (byte-type in) (conj acc (str ex-class ": " ex-message)))))
      acc)))

(defn- format-stacktrace
  "Reduce a vector of strings `v` to a single white-spaced separated string."
  [v]
  (reduce str (interpose " " v)))

(defn handle-exception
  "De-serialize an OrientDB exception in DataInputStream `in` and throw it."
  [in]
  (let [session-id (int-type in)]
    (-> in
        deserialize-exception
        format-stacktrace
        Exception.
        throw)))
