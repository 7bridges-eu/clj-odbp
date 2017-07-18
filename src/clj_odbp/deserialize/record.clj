(ns clj-odbp.deserialize.record
  (:require [instaparse.core :as i]))

(i/defparser record-parser
  "RECORD = token [<COMMA> token]*
    token = [<class>] key value?
    key = ([ALPHA | '-' | '_']*) <':'>
    <value> = bool | string | number | date | datetime | rid | ridbag | binary | list | set | map
    <number> = (byte | int | long | float | double | bigdecimal) [SPACES]
    class = ALPHA <'@'>
    map = <'{'> entry [<COMMA> entry]* <'}'>
    entry = string <':'> value?
    set = <'<'> value [<COMMA> value]* <'>'>
    list = <'['> value [<COMMA> value]* <']'>
    binary = <'_'> (BASE64) <'_'>
    ridbag = <'%'> (BASE64) <';'>
    rid = ('#' DIGITS ':' DIGITS)
    datetime = DIGITS <'t'>
    date = DIGITS <'a'>
    string = <'\"'> [ALPHA | SPACES | PUNCT]* <'\"'>
    bigdecimal = (DIGITS '.' DIGITS) <'c'>
    double = (DIGITS '.' DIGITS) <'d'>
    float = (DIGITS '.' DIGITS) <'f'>
    long = (DIGITS) <'l'>
    int = DIGITS
    byte = DIGITS <'b'>
    bool = 'true' | 'false'
    <COMMA> = ','
    <PUNCT> = #'\\p{Punct}'
    <SPACES> = #'\\p{Space}*'
    <BASE64> = #'[A-Za-z0-9+/=]+'
    <ALPHA> = #'[a-zA-Z0-9]*'
    <DIGITS> = #'-?[0-9]+'")

(defn- to-date [timestamp]
  (java.util.Date. timestamp))

(defn- to-bigdecimal [& args]
  (->> args
       (apply str)
       BigDecimal.))

(defn- to-float [& args]
  (->> args
       (apply str)
       (Float/valueOf)))

(defn- to-double [& args]
  (->> args
       (apply str)
       (Double/valueOf)))

(defn- to-long [& args]
  (->> args
       (apply str)
       (Long/valueOf)))

(defn- to-byte [& args]
  (->> args
       (apply str)
       (Byte/valueOf)))

(defn- to-map [& args]
  (reduce (fn [m v]
            (assoc m
                   (keyword (first v))
                   (second v)))
          {}
          args))

(defn- to-record [& args]
  (reduce (fn [m v]
            (assoc m (first v) (second v)))
          {}
          args))

(def transform-options
  {:RECORD to-record
   :string str
   :datetime (comp to-date to-long)
   :date (comp to-date to-long)
   :bigdecimal to-bigdecimal
   :float to-float
   :double to-double
   :long to-long
   :int read-string
   :byte to-byte
   :bool #(= "true" %)
   :key (comp keyword str)
   :rid str
   :binary str
   :ridbag str
   :token (comp vec list)
   :entry list
   :map to-map
   :list (comp vec list)
   :set (comp set list)})

(defn deserialize-record
  [response]
  (let [raw-content (:record-content response)
        s (apply str (map char raw-content))]
    (->> (record-parser s)
         (i/transform transform-options))))
