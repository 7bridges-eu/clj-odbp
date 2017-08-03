(ns clj-odbp.deserialize.binary.otypes-test
  (:require [clj-odbp.deserialize.binary.otypes :as t]
            [clj-odbp.deserialize.binary.buffer :as b]
            [midje.sweet :refer :all]))



(facts "Testing basic types."
       (fact "Bool - 0 is false."
             (t/call :bool-orient-type
                     (b/to-buffer [0])) => false)
       (fact "Bool - 1 is true."
             (t/call :bool-orient-type
                     (b/to-buffer [1])) => true)
       (fact "Integer - [0] is 0."
             (t/call :integer-orient-type
                     (b/to-buffer [0])) => (int 0))
       (fact "Integer - [1] is -1."
             (t/call :integer-orient-type
                     (b/to-buffer [1]) 0) => (int -1))
       (fact "Integer - [10] is 5."
             (t/call :integer-orient-type
                     (b/to-buffer [10]) 0) => 5)
       (fact "Integer - [172 2] is 150."
             (t/call :integer-orient-type
                     (b/to-buffer [172 2]) 0) => 150)
       (fact "Short - [0] is 0."
             (t/call :short-orient-type
                     (b/to-buffer [0]) 0) => (int 0))
       (fact "Short - [1] is -1."
             (t/call :short-orient-type
                     (b/to-buffer [1]) 0) => (int -1))
       (fact "Short - [10] is 5."
             (t/call :short-orient-type
                     (b/to-buffer [10]) 0) => 5)
       (fact "Short - [172 2] is 150."
             (t/call :short-orient-type
                     (b/to-buffer [172 2]) 0) => 150)
       (fact "Long - [0] is 0."
             (t/call :long-orient-type
                     (b/to-buffer [0]) 0) => (int 0))
       (fact "Long - [1] is -1."
             (t/call :long-orient-type
                     (b/to-buffer [1]) 0) => (int -1))
       (fact "Long - [10] is 5."
             (t/call :long-orient-type
                     (b/to-buffer [10]) 0) => 5)
       (fact "Long - [172 2] is 150."
             (t/call :long-orient-type
                     (b/to-buffer [172 2]) 0) => 150)
       (fact "Float - [0] is 0."
             (t/call :float-orient-type
                     (b/to-buffer [0 0 0 0]) 0) => (float 0.0))
       (fact "Float - [-65 -128 0 0] is -1."
             (t/call :float-orient-type
                     (b/to-buffer [-65 -128 0 0]) 0) => (float -1.0))
       (fact "Float - [64 -96 0 0] is 5."
             (t/call :float-orient-type
                     (b/to-buffer [64 -96 0 0]) 0) => (float 5.0))
       (fact "Float - [67 22 25 -102] is 150.1"
             (t/call :float-orient-type
                     (b/to-buffer [67 22 25 -102]) 0) => (float 150.1))
       (fact "Double - [0] is 0."
             (t/call :double-orient-type
                     (b/to-buffer [0 0 0 0
                                   0 0 0 0]) 0) => (double 0.0))
       (fact "Double - [-65 -16 0 0 0 0 0 0] is -1."
             (t/call :double-orient-type
                     (b/to-buffer [-65 -16 0 0
                                   0 0 0 0]) 0) => (double -1.0))
       (fact "Double - [64 20 0 0 0 0 0 0] is 5."
             (t/call :double-orient-type
                     (b/to-buffer [64 20 0 0
                                   0 0 0 0]) 0) => (double 5.0))
       (fact "Double - [64 98 -61 -41 10 61 112 -92] is 150.1"
             (t/call :double-orient-type
                     (b/to-buffer [64 98 -61 -41
                                   10 61 112 -92]) 0) => (double 150.12))
       (fact "Datetime - [0] is 1970-01-01T00:00:00"
             (t/call :datetime-orient-type
                     (b/to-buffer [0]) 0) => (java.util.Date. 0))
       (fact "Datetime - [206 130 225 133 176 87] is 2017-07-26T19:25:07"
             (t/call :datetime-orient-type
                     (b/to-buffer [206 130 225
                                   133 176 87]) 0) => (java.util.Date. 1501097107623))
       (fact "String - Empty string"
             (t/call :string-orient-type
                     (b/to-buffer [0]) 0) => "")
       (fact "String - Simple string"
             (t/call :string-orient-type
                     (b/to-buffer [10 77 97 114 99 111]) 0) => "Marco")
       (fact "Binary - Empty raw bytes"
             (t/call :binary-orient-type
                     (b/to-buffer [0]) 0) => [])
       (fact "Binary - 8 raw bytes"
             (t/call :binary-orient-type
                     (b/to-buffer [16
                                   0 1 2 3
                                   4 5 6 7]) 0) => [0 1 2 3 4 5 6 7]))
