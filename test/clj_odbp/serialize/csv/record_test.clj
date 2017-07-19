(ns clj-odbp.serialize.csv.record-test
  (:require [clj-odbp.serialize.csv.record :as r]
            [midje.sweet :refer :all]))

(def test-record {"Location" {:name "Bar" :cost 12.50}})

(facts "Serialization of single types"
       (fact "Class - string 'test' should return 'test@'"
             (r/class-type "test") => "test@")
       (fact "Keyword - keyword :test should return 'test'"
             (r/keyword-type :test) => "test")
       (fact "String - string 'test' should return 'test' with escaped double-quotes"
             (r/string-type "test") => "\"test\"")
       (fact "Integer - integer 1 should return '1'"
             (r/integer-type (int 1)) => "1")
       (fact "Long - long 12345678910 should return '12345678910l'"
             (r/long-type (long 12345678910)) => "12345678910l")
       (fact "Short - short 1 should return '1s'"
             (r/short-type (short 1)) => "1s")
       (fact "Byte - byte 1 should return '1b'"
             (r/byte-type (byte 1)) => "1b")
       (fact "Float - float 1.0 should return '1.0f'"
             (r/float-type (float 1.0)) => "1.0f")
       (fact "Double - double 123456.50 should return '123456.5d'"
             (r/double-type (double 123456.50)) => "123456.5d")
       (fact "BigDecimal - BigDecimal 1.5 should return '1.5c'"
             (r/big-decimal-type (BigDecimal. 1.5)) => "1.5c")
       (fact "Boolean - boolean true should return true"
             (r/bool-type true) => true)
       (fact "List - list (1 2) should return '[1,2]'"
             (r/list-type (list (int 1) (int 2))) => "[1,2]")
       (fact "Set - set #{1 2} should return '&lt;1,2&gt;'"
             (r/set-type #{(int 1) (int 2)}) => "<1,2>")
       (fact "Map - map {:test 1 :cost 2.50} should return 'test:1,cost:2.5f'"
             (r/map-type {:test (int 1) :cost (float 2.50)}) =>
             "test:1,cost:2.5f")
       (fact "Serialize record - serialize of {'Location' {:name 'Bar' :cost 12.50}} should return 'Location@name:'Bar',cost:12.5d'"
             (r/serialize-record test-record) =>
             "Location@name:\"Bar\",cost:12.5d"))
