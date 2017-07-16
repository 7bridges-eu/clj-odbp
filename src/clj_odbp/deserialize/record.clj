(ns clj-odbp.deserialize.record
  (:require [instaparse.core :as i]))

(i/defparser record-parser
  "RECORD = token [<COMMA> token]*
    token = [<class>] key value?
    key = [<'\"'>] (ALPHA) [<'\"'>] <':'>
    <value> = bool | string | number | rid | ridbag | binary | list | set | map
    class = ALPHA <'@'>
    map = <'{'> (key value?) [<COMMA> (key value)]* <'}'>
    set = <'<'> value [<COMMA> value]* <'>'>
    list = <'['> value [<COMMA> value]* <']'>
    binary = <'_'> (BASE64) <'_'>
    ridbag = <'%'> (BASE64) <';'>
    rid = ('#' DIGITS ':' DIGITS)
    string = <'\"'> ALPHA (SPACES ALPHA)? <'\"'>
    number = (byte | int | long | float | double) SPACES
    double = (DIGITS '.' DIGITS) <\"d\">
    float = (DIGITS '.' DIGITS) <\"f\">
    long = (DIGITS) <'l'>
    int = DIGITS
    byte = DIGITS <'b'>
    bool = 'true' | 'false'
    <COMMA> = ','
    <SPACES> = \" \"*
    <BASE64> = #'[A-Za-z0-9+/=]'+
    <ALPHA> = #'\\w+'
    <DIGITS> = #'[0-9]+'")

(defn- to-float [& args]
  (->> args
       vec
       (apply str)
       (Float/valueOf)))

(defn- to-double [& args]
  (->> args
       vec
       (apply str)
       (Double/valueOf)))

(defn- to-long [& args]
  (->> args
       vec
       (apply str)
       (Long/valueOf)))

(defn- to-byte [& args]
  (->> args
       vec
       (apply str)
       (Byte/valueOf)))

(defn- to-map [& args]
  (apply sorted-map (vec args)))

(defn- to-record [& args]
  (reduce (fn [m v]
            (assoc m (first v) (second v)))
          {}
          args))

(def transform-options
  {:RECORD to-record
   :number identity
   :string identity
   :float to-float
   :double to-double
   :long to-long
   :int read-string
   :byte to-byte
   :bool #(= "true" %)
   :key keyword
   :rid str
   :binary str
   :ridbag str
   :token (comp vec list)
   :map to-map
   :list (comp vec list)
   :set (comp set list)})

(defn deserialize-record
  [response]
  (let [raw-content (:record-content response)
        s (apply str (map char raw-content))]
    s))
