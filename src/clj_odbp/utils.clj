(ns clj-odbp.utils
  (:require [clj-odbp.net :as net])
  (:import [java.io ByteArrayOutputStream DataOutputStream DataInputStream]))

(defn- validate-message
  [spec message]
  (when-not (every?
             #(contains? spec (first %))
             message)
    (throw (Exception. "The message doesn't respect the spec."))))

(defn encode
  [spec message]
  (let [out (ByteArrayOutputStream.)
        stream (DataOutputStream. out)]
    (validate-message spec message)
    (doseq [[field-name value] message
            :let [function (get spec field-name)]]
      (try
        (apply function [stream value])
        (catch Exception e
          (throw (Exception. (str (.getMessage e) " writing " field-name))))))
    out))

(defn decode
  [^DataInputStream in spec]
  (reduce-kv
   (fn [result field-name f]
     (assoc result field-name (f in)))
   {}
   spec))

(defmacro defcommand
  [command-name args request-handler response-handler]
  `(defn ~command-name
     [~@args]
     (with-open [s# (net/create-socket)]
       (-> s#
           (net/write-request ~request-handler ~@args)
           (net/read-response ~response-handler)))))
