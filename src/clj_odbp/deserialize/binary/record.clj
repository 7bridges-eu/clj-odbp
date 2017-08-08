(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.otypes :refer [otype-list call]]
            [clj-odbp.deserialize.binary.buffer :as b]))

(defn deserialize-record
  [record]
  (let [content (:record-content record)
        buffer (b/to-buffer content)]
    (call :record-orient-type buffer)))
