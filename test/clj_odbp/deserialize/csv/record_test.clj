(ns clj-odbp.deserialize.csv.record-test
  (:require [clj-odbp.deserialize.csv.record :as record]
            [midje.sweet :refer :all])
  (:import [java.util Date]))

(defn- build-response
  [content]
  (let [b (vec (.getBytes content))]
    {:record-content b}))

(def complex-record "Location@name:\"Casa\",means:[#23:0,#22:0,#24:0],cost:5.5d,sector:\"1x1\",list:<1,2,3>,map:{\"key1\":\"string\",\"key2\":1},binary:_VGhpcyBpcyBzb21lIGJpbmFyeSBkYXRhCg==_,bool_true:true,bool_false:false,date:1500156000000t,datetime:1500237650000t,document:{\"name\":\"Marco\",\"age\":36,\"map\":{\"cell\":1,\"tel\":2}}")

(facts "Deserialization of records with a single type"
       (fact "Null - nil value"
             (-> (build-response "a_key:")
                 record/deserialize-record) => {:a_key nil})
       (fact "Bool - true value"
             (-> (build-response "a_key:true")
                 record/deserialize-record) => {:a_key true})
       (fact "Bool - false value"
             (-> (build-response "a_key:false")
                 record/deserialize-record) => {:a_key false})
       (fact "Byte - negative byte value"
             (-> (build-response "a_key:-1b")
                 record/deserialize-record) => {:a_key (byte -1)})
       (fact "Byte - positive byte value"
             (-> (build-response "a_key:1b")
                 record/deserialize-record) => {:a_key (byte 1)})
       (fact "Int - negative integer value"
             (-> (build-response "a_key:-2")
                 record/deserialize-record) => {:a_key -2})
       (fact "Int - positive integer value"
             (-> (build-response "a_key:2")
                 record/deserialize-record) => {:a_key 2})
       (fact "Long - negative long value"
             (-> (build-response "a_key:-3l")
                 record/deserialize-record) => {:a_key -3})
       (fact "Long - positive long value"
             (-> (build-response "a_key:3l")
                 record/deserialize-record) => {:a_key 3})
       (fact "Float - negative float value"
             (-> (build-response "a_key:-4.4f")
                 record/deserialize-record) => {:a_key (float -4.4)})
       (fact "Float - positive float value"
             (-> (build-response "a_key:4.4f")
                 record/deserialize-record) => {:a_key (float 4.4)})
       (fact "Double - negative double value"
             (-> (build-response "a_key:-5.55d")
                 record/deserialize-record) => {:a_key (double -5.55)})
       (fact "Double - positive double value"
             (-> (build-response "a_key:5.55d")
                 record/deserialize-record) => {:a_key (double 5.55)})
       (fact "BigDecimal - negative bigdecimal value"
             (-> (build-response "a_key:-6.66666c")
                 record/deserialize-record) => {:a_key (bigdec -6.66666)})
       (fact "BigDecimal - positive bigdecimal value"
             (-> (build-response "a_key:6.66666c")
                 record/deserialize-record) => {:a_key (bigdec 6.66666)})
       (fact "String - empty string"
             (-> (build-response "a_key:\"\"")
                 record/deserialize-record) => {:a_key ""})
       (fact "String - simple string"
             (-> (build-response "a_key:\"Hello world!\"")
                 record/deserialize-record) => {:a_key "Hello world!"})
       (fact "Date - simple date"
             (-> (build-response "a_key:1499990400a")
                 record/deserialize-record) => {:a_key (Date. 1499990400)})
       (fact "Datetime - simple date"
             (-> (build-response "a_key:1500000000t")
                 record/deserialize-record) => {:a_key (Date. 1500000000)})
       (fact "Rid - simple rid"
             (-> (build-response "a_key:#0:0")
                 record/deserialize-record) => {:a_key "#0:0"})
       (fact "RidBag - simple ridbag"
             (-> (build-response "a_key:%ABCDEFGHI01234567890;")
                 record/deserialize-record) => {:a_key "ABCDEFGHI01234567890"})
       (fact "Binary - simple binary"
             (-> (build-response "a_key:_ABCDEFGHI01234567890_")
                 record/deserialize-record) => {:a_key "ABCDEFGHI01234567890"})
       (fact "List - mixed list"
             (-> (build-response "a_key:[1,\"a\",9.0d]")
                 record/deserialize-record) => {:a_key [1 "a" (double 9.0)]})
       (fact "Set - mixed set"
             (-> (build-response "a_key:<1,\"a\",9.0d>")
                 record/deserialize-record) => {:a_key #{1 "a" (double 9.0)}})
       (fact "Map - mixed list"
             (-> (build-response "a_key:{\"a\":1,\"b\":\"a\",\"c\":9.0d}")
                 record/deserialize-record) => {:a_key {:a 1 :b "a" :c (double 9.0)}}))

(facts "Deserialization of composite records"
       (fact "Simple record"
             (-> (build-response "a_key:\"Hello\",b_key:1b")
                 record/deserialize-record) => {:a_key "Hello"
                                                :b_key (byte 1)})
       (fact "Complex record"
             (-> (build-response complex-record)
                 record/deserialize-record) => {:name "Casa"
                                                :means ["#23:0" "#22:0" "#24:0"]
                                                :cost (double 5.5)
                                                :sector "1x1"
                                                :list #{1 2 3}
                                                :map {:key1 "string"
                                                      :key2 1}
                                                :binary "VGhpcyBpcyBzb21lIGJpbmFyeSBkYXRhCg=="
                                                :bool_true true
                                                :bool_false false
                                                :date (Date. 1500156000000)
                                                :datetime (Date. 1500237650000)
                                                :document {:name "Marco"
                                                           :age 36
                                                           :map {:cell 1
                                                                 :tel 2}}}))
