(ns clj-odbp.utils-test
  (:require [clj-odbp.utils :as u]
            [midje.sweet :refer :all]))

(facts "Test validate-message"
       (fact "A valid message"
             (u/valid-message?
              {:session-id nil
               :token nil}
              [[:session-id nil]
               [:token nil]]) => true)
       (fact "An invalid message"
             (u/valid-message?
              {:session-id nil
               :token nil}
              [[:session-id nil]
               [:wrong-field nil]]) => false))

(facts "Test take-upto"
       (fact "Takes until is odd (included)"
             (u/take-upto odd? [0 2 4 5 7]) => [0 2 4 5])
       (fact "Returns empty if the vector is empty"
             (u/take-upto odd? []) => [])
       (fact "Returns [0] if the pred is true at start"
             (u/take-upto even? [0 2 4 6]) => [0]))

(facts "Test parse-rid"
       (fact "Exception if the argument is not a valid Rid."
             (u/parse-rid "xxxx") => (throws java.lang.AssertionError))
       (fact "Valid rid string"
             (u/parse-rid "#10:20") => [10 20]))
