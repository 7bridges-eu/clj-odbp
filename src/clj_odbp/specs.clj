(ns clj-odbp.specs
  (require
   [clj-odbp.serialize :as s]
   [clj-odbp.deserialize :as d])
  (import
   [java.io ByteArrayOutputStream DataOutputStream DataInputStream]))

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

;; REQUEST_CONNECT
(def connect-request
  {:command s/byte-type
   :session s/int-type
   :driver-name s/string-type
   :driver-version s/string-type
   :protocol-version s/short-type
   :client-id s/string-type
   :serialization s/string-type
   :token-session s/bool-type
   :support-push s/bool-type
   :collect-stats s/bool-type
   :username s/string-type
   :password s/string-type})

(def connect-response
  {:status d/byte-type
   :response-session d/int-type
   :session-id d/int-type
   :token d/bytes-type})

;; REQUEST_DB_OPEN
(def connect-db-request
  {:command s/byte-type
   :session s/int-type
   :driver-name s/string-type
   :driver-version s/string-type
   :protocol-version s/short-type
   :client-id s/string-type
   :serialization s/string-type
   :token-session s/bool-type
   :support-push s/bool-type
   :collect-stats s/bool-type
   :database-name s/string-type
   :username s/string-type
   :password s/string-type})

(def connect-db-response
  {:status d/byte-type
   :response-session d/int-type
   :session-id d/int-type
   :token d/bytes-type 
   :clusters (d/array-of [d/string-type d/short-type])
   :cluster-config d/bytes-type
   :orient-db-relase d/string-type})

;; REQUEST_SHUTDOWN
(def shutdown-request
  {:command s/byte-type
   :session-id s/int-type
   :username s/string-type
   :password s/string-type})

;; REQUEST_COMMAND
;; (mode:byte)(command-payload-length:int)(class-name:string)(command-payload)
(def command-response
  {:command s/byte-type
   :session-id s/int-type
   :token s/bytes-type})
