;; Copyright 2017 7bridges s.r.l.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns clj-odbp.binary.deserialize.buffer
  (:require [clj-odbp.utils :refer [take-upto]]))

(defn to-buffer
  "Transform a sequence into a rewindable buffer."
  [data]
  (atom {:position 0
         :data data
         :total-size (count data)}))

(defn buffer-take!
  "Returns a vector of n elements from the current position on."
  [buffer n]
  (let [{position :position
         data :data} @buffer]
    (swap! buffer assoc :position (+ position n))
    (into []
          (take n (drop position data)))))

(defn buffer-take-while!
  "Returns a vector of n elements while pred is true."
  [buffer pred]
  (let [{position :position data :data} @buffer
        result (take-while pred (drop position data))]
    (swap! buffer assoc :position (+ position (count result)))
    (vec result)))

(defn buffer-take-upto!
  "Returns a vector of n elements while pred is
   true including the first false."
  [buffer pred]
  (let [{position :position data :data} @buffer
        result (take-upto pred (drop position data))]
    (swap! buffer assoc :position (+ position (count result)))
    (vec result)))

(defn buffer-set-position!
  "Sets the current position of the buffer."
  [buffer new-position]
  (swap! buffer assoc :position new-position))

(defn buffer-current-position
  "Returns the current position of the buffer."
  [buffer]
  (:position @buffer))

(defn buffer-reset!
  "Rewinds the buffer to the beginning."
  [buffer]
  (buffer-set-position! buffer 0))

(defn buffer-rest!
  "Returns a vector whit the remaining elements."
  [buffer]
  (let [{size :total-size position :position} @buffer
        remains (- size position)]
    (swap! buffer assoc :position size)
    (into [] (take remains (drop position (:data @buffer))))))
