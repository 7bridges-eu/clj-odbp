(ns clj-odbp.deserialize
  (import [java.io DataInputStream]))

(defn read-int
  [buffer in]
  (.writeInt buffer (.readInt in)))

