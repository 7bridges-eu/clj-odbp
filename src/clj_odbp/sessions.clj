(ns clj-odbp.sessions)

(defonce sessions (atom {}))

(defn put-session!
  "Insert a session for the specific service into the sessions atom."
  [session service]
  (swap! sessions assoc service session))

(defn has-session?
  "Check if there is at least one session for the specified service."
  [service]
  (not (empty? (get @sessions service))))

(defn read-session
  "Read the session for the specified service. TO BE IMPROVED!"
  [service]
  (get @sessions service))

(defn reset-session!
  "Reset the sessions of the specified service."
  [service]
  (swap! sessions assoc service {}))

(defn reset-sessions!
  "Reset the sessions atom."
  []
  (swap! sessions empty))
