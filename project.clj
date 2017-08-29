(defproject org.clojars.7bridges/clj-odbp "0.1.0"
  :description "A Clojure driver for OrientDB binary protocol"
  :url "http://github.com/7bridgeseu/clj-odbp"
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[com.taoensso/timbre "4.10.0"]
                 [org.clojure/clojure "1.8.0"]
                 [midje "1.8.3"]
                 [midje-junit-formatter "0.1.0-SNAPSHOT"]]
  :plugins [[lein-cloverage "1.0.9"]
            [lein-codox "0.10.3"]
            [lein-midje "3.1.3"]]
  :aliases {"coverage" ["cloverage" "--runner" ":midje"]}
  :codox {:project {:name "clj-odbp"}})
