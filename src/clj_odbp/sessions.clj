(ns clj-odbp.sessions)

(defonce sessions (atom []))

(defn put-session!
  "Insert a session into the sessions atom."
  [session]
  (swap! sessions conj session))

(defn has-session?
  "Check if there are at least one session."
  []
  (not (empty? @sessions)))

(defn read-session
  "Read ALWAYS the first element of sessions. TO BE IMPROVED!"
  []
  (first @sessions))

(defn reset-session!
  "Reset the session atom."
  []
  (swap! sessions empty))
