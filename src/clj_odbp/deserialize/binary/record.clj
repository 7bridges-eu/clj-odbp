(ns clj-odbp.deserialize.binary.record
  (:require [clj-odbp.deserialize.binary.varint :as v]
            [clj-odbp.deserialize.binary.otypes :refer [otype-list call]]
            [clj-odbp.deserialize.binary.buffer :as b]
            [clj-odbp.deserialize.binary.utils :as u]))

(defn string-type
  "Read a string from the buffer."
  [buffer lenght]
  (let [b (b/buffer-take! buffer lenght)]
    (apply str (map char b))))

(defn read-version
  [buffer]
  (int (first (b/buffer-take! buffer 1))))

(defn read-class-name
  [buffer]
  (let [size (v/varint-signed-long (b/buffer-take! buffer 1))]
    (apply str (map char (b/buffer-take! buffer size)))))

(defn read-headers
  "Read and decode the header"
  [buffer]
  (loop [field-size (v/varint-signed-long (b/buffer-take! buffer 1))
         headers []]
    (if (zero? field-size)
      headers
      (let [field-name (string-type buffer field-size)
            field-position (u/read-int32 buffer)
            data-type (int (first (b/buffer-take! buffer 1)))]
        (recur (v/varint-signed-long (b/buffer-take! buffer 1))
               (conj headers
                     {:field-name field-name
                      :field-position field-position
                      :data-type (nth otype-list data-type)}))))))

(defn read-record
  [headers content]
  (reduce
   (fn [record header]
     (let [{key :field-name
            position :field-position
            otype :data-type} header]
       (assoc
        record
        (keyword key)
        (call otype content position))))
   {}
   headers))

(defn deserialize-record
  [record]
  (let [content (:record-content record)
        buffer (b/to-buffer content)
        version (read-version buffer)
        class-name (read-class-name buffer)
        headers (read-headers buffer)]
    {class-name (read-record headers buffer)}))
