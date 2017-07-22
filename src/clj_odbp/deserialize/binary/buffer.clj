(ns clj-odbp.deserialize.binary.buffer)

(defn to-buffer
  [data]
  (atom {:position 0
         :data data
         :total-size (count data)}))

(defn buffer-take
  [buffer size]
  (let [{position :position
         data :data} @buffer]
    (swap! buffer assoc :position (+ position size))
    (into []
          (take size (drop position data)))))

(defn buffer-take-while
  [buffer pred]
  (let [{position :position data :data} @buffer
        result (take-while pred (drop position data))]
    (swap! buffer assoc :position (+ position (count result)))
    (vec result)))

(defn buffer-set-position
  [buffer new-position]
  (swap! buffer assoc :position new-position))

(defn buffer-reset
  [buffer]
  (buffer-set-position buffer 0))

(defn buffer-rest
  [buffer]
  (let [{size :total-size position :position} @buffer
        remains (- size position)]
    (swap! buffer assoc :position size)
    (into [] (take remains (drop position (:data @buffer))))))
