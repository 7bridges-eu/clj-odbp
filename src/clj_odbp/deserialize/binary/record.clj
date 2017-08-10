(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.otypes :refer [otype-list call]]
            [clj-odbp.deserialize.binary.buffer :as b]))

(defn deserialize-record
  [record]
  (let [cluster (get record :record-cluster nil)
        position (get record :record-position nil)
        version (get record :record-version nil)
        content (:record-content record)
        buffer (b/to-buffer content)]
    (conj {(keyword "_rid") (str "#" cluster ":" position)}
          (call :record-orient-type buffer))))
