(ns clj-odbp.deserialize.exception
  (:require [clj-odbp.deserialize.otype :as ot]
            [clj-odbp.sessions :as s]
            [clojure.string :as string]))

(defn deserialize-exception
  "De-serialize OrientDB exception from DataInputStream `in`."
  [in]
  (let [ex-class (ot/string-type in)
        ex-message (ot/string-type in)]
    {:class ex-class :message ex-message}))

(defn create-exception
  "Create an ExceptionInfo based on the class of the OrientDB exception."
  [m]
  (let [fully-qualified-name (:class m)
        message (:message m)
        class-name (last (string/split fully-qualified-name #"\."))]
    (ex-info message {:type (keyword class-name)})))

(defn handle-exception
  "De-serialize an OrientDB exception in DataInputStream `in` and throw it."
  [in]
  (let [session-id (ot/int-type in)
        token (ot/bytes-type in)
        status (ot/byte-type in)]
    (-> in
        deserialize-exception
        create-exception
        throw)))

(defmulti manage-exception :exception-type)

(defmethod manage-exception :default [e]
  (throw (:exception e)))
