(ns clj-odbp.serialize.binary.types)

(deftype OrientBinary [value])

(defn orient-binary [value]
  (->OrientBinary value))

(deftype OrientDate [value])

(defn orient-date [value]
  (->OrientDate value))

(deftype OrientDateTime [value])

(defn orient-date-time [value]
  (->OrientDateTime value))
