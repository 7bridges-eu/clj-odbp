(ns clj-odbp.deserialize.binary.otypes
  (:require [clj-odbp.deserialize.binary.varint :as v]
            [clj-odbp.deserialize.binary.buffer :as b]))

(defn bool-orient-type
  "Read a boolean from the stream"
  [buffer position]
  (let [lenght (v/varint-signed-long (b/buffer-take buffer 1))
        b (b/buffer-take buffer lenght)]
    (apply str (map char b))))

(defn integer-orient-type
  "Read a integer from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  (int (v/varint-signed-long (b/buffer-take buffer 1))))

(defn short-orient-type
  "Read a short from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn long-orient-type
  "Read a long from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn float-orient-type
  "Read a float from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn double-orient-type
  "Read a double from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn datetime-orient-type
  "Read a datetime from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn string-orient-type
  "Read a datetime from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  (let [size (v/varint-signed-long (b/buffer-take buffer 1))]
    (apply str (map char (b/buffer-take buffer size)))))

(defn binary-orient-type
  "Read a binary from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn embedded-record-orient-type
  "Read a embedded record from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn embedded-list-orient-type
  "Read a embedded list from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn embedded-set-orient-type
  "Read a embedded set from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn embedded-map-orient-type
  "Read a embedded map from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn link-orient-type
  "Read a link (orid) from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn link-list-orient-type
  "Read a list of links from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn link-set-orient-type
  "Read a set of links from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn link-map-orient-type
  "Read a map of links from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn byte-orient-type
  "Read a byte from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn transient-orient-type
  "Read a transient type from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn date-orient-type
  "Read a date from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn custom-orient-type
  "Read a custom type from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn decimal-orient-type
  "Read a decimal from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn link-bag-orient-type
  "Read a link bag from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(defn any-orient-type
  "Read a 'any' type from the stream"
  [buffer position]
  (b/buffer-set-position buffer position)
  nil)

(def type-list
  [bool-orient-type integer-orient-type short-orient-type
   long-orient-type float-orient-type double-orient-type
   datetime-orient-type string-orient-type binary-orient-type
   embedded-record-orient-type embedded-list-orient-type embedded-set-orient-type
   embedded-map-orient-type link-orient-type link-list-orient-type
   link-set-orient-type link-map-orient-type byte-orient-type
   transient-orient-type date-orient-type custom-orient-type
   decimal-orient-type link-bag-orient-type any-orient-type])
