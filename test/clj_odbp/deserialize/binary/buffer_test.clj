(ns clj-odbp.deserialize.binary.buffer-test
  (:require [clj-odbp.deserialize.binary.buffer :as b]
            [midje.sweet :refer :all]))

(def buffer (b/to-buffer [0 1 2 3 4 5 6 7]))

(facts "Testing buffer implementation"
       (b/buffer-reset! buffer)
       (b/buffer-current-position buffer) => 0
       (b/buffer-take! buffer 4) => [0 1 2 3]
       (b/buffer-current-position buffer) => 4
       (b/buffer-rest! buffer) => [4 5 6 7]
       (b/buffer-current-position buffer) => 8
       (b/buffer-set-position! buffer 3)
       (b/buffer-current-position buffer) => 3
       (b/buffer-take-while! buffer #(< % 6)) => [3 4 5]
       (b/buffer-rest! buffer) => [6 7])
