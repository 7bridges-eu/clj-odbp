(ns clj-odbp.deserialize.record
  (:require [instaparse.core :as i]))

(i/defparser record-parser
  "RECORD = token [token]+
    token = [<class>] key value <COMMA?>
    key = [<'\"'>] (ALPHA) [<'\"'>] <':'>
    <value> = string | number | rid | binary | list | set | map
    class = ALPHA <'@'>
    map = <'{'> (key value) [<COMMA> (key value)]* <'}'>
    set = <'<'> value [<COMMA> value]* <'>'>
    list = <'['> value [<COMMA> value]* <']'>
    binary = <'_'> (ALPHA | DIGITS) <'_'>
    rid = ('#' DIGITS ':' DIGITS)
    string = <'\"'> ALPHA (SPACES ALPHA)? <'\"'>
    number = (int | long | float | double) SPACES
    double = (DIGITS \".\" DIGITS) <\"d\">
    float = (DIGITS \".\" DIGITS) <\"f\">
    long = (DIGITS) <\"l\">
    int = DIGITS
    <COMMA> = [','|';']
    <SPACES> = \" \"*
    <ALPHA> = #'[a-zA-Z0-9]+'
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

(defn to-map [& args]
  (apply sorted-map (vec args)))

(defn- to-record [& args]
  (reduce {} #(assoc (first %) (second %)) args))

(def transform-options
  {:number identity
   :string identity
   :float to-float
   :double to-double
   :long to-long
   :int read-string
   :key keyword
   :rid str
   :binary str
   :token (comp vec list)
   :map to-map
   :list (comp vec list)
   :set (comp set list)})

(defn deserialize-record
  [response]
  (let [raw-content (:record-content response)
        s (apply str (map char raw-content))]
    s))
