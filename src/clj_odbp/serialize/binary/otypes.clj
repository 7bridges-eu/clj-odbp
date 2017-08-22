(ns clj-odbp.serialize.binary.otypes
  (:require [clj-odbp.serialize.binary
             [common :as c]
             [int :as i]
             [varint :as v]]))

(defn link?
  [v]
  (when (string? v)
    (re-matches #"#\d+:\d+" v)))

(defn link-list?
  [l]
  (when (sequential? l)
    (every? link? l)))

(defn link-set?
  [s]
  (when (set? s)
    (every? link? s)))

(defn link-map?
  [m]
  (when (map? m)
    (let [values (vals m)]
      (every? link? values))))

(defn embedded-record?
  [r]
  (when (map? r)
    (or (contains? r :_class)
        (contains? r "@type"))))

(defn get-type [v]
  (cond
    (instance? Boolean v) :boolean-type
    (instance? Integer v) :integer-type
    (instance? Short v) :integer-type
    (instance? Long v) :integer-type
    (instance? Float v) :float-type
    (instance? Double v) :double-type
    (instance? java.util.Date v) :datetime-type
    ;; (instance? OrientBinary v) :binary-type
    (embedded-record? v) :embedded-record
    (instance? Byte v) :byte-type
    (instance? java.math.BigDecimal v) :decimal-type
    (link? v) :link-type
    (string? v) :string-type
    (keyword? v) :keyword-type
    (link-list? v) :link-list-type
    (link-set? v) :link-set-type
    (link-map? v) :link-map-type
    (sequential? v) :embedded-list-type
    (set? v) :embedded-set-type
    (map? v) :embedded-map-type
    :else :any-type))

(defmulti serialize get-type)

(defmethod serialize :boolean-type
  [value]
  (if value
    [(byte 1)]
    [(byte 0)]))

(defmethod serialize :integer-type
  [value]
  (v/varint-unsigned value))

(defmethod serialize :float-type
  [value]
  (-> (java.nio.ByteBuffer/allocate 4)
      (.putFloat value)
      .array
      vec))

(defmethod serialize :double-type
  [value]
  (-> (java.nio.ByteBuffer/allocate 8)
      (.putDouble value)
      .array
      vec))

(defmethod serialize :datetime-type
  [value]
  (serialize (.getTime value)))

(defmethod serialize :decimal-type
  [value]
  (let [scale (i/int32 (.scale value))
        serialized-value (-> value
                             .unscaledValue
                             .toByteArray
                             vec)
        value-size (i/int32 (count serialized-value))]
    (vec (concat scale value-size serialized-value))))

(defmethod serialize :any-type
  [value]
  value)

(defmethod serialize :string-type
  [value]
  (let [bytes (.getBytes value "UTF-8")]
    (c/bytes-type bytes)))

(defmethod serialize :keyword-type
  [value]
  (serialize (name value)))

(defmethod serialize :byte-type
  [value]
  [value])
