(ns clj-odbp.deserialize.exception-test
  (:require [clj-odbp.deserialize.exception :as e]
            [midje.sweet :refer :all])
  (:import [java.io ByteArrayInputStream DataInputStream]))

(defn- provide-input [bytes]
  (->> bytes
       (map byte)
       byte-array
       ByteArrayInputStream.
       DataInputStream.))

(facts
 (fact
  "deserialize-exception should return a map: {:class ex-class :message ex-message}"
  (let [in (let [ex-class (.getBytes "error")
                 ex-message (.getBytes "test")]
             (provide-input (concat [0 0 0 5]
                                    ex-class
                                    [0 0 0 4]
                                    ex-message
                                    [0])))]
    (e/deserialize-exception in) => {:class "error" :message "test"}[])))
