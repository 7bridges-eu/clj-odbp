(defproject clj-odbp "0.1.0-SNAPSHOT"
  :description "A Clojure driver for OrientDB binary protocol"
  :url "http://github.com/7bridgeseu/clj-odbp"
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [midje "1.8.3"]
                 [midje-junit-formatter "0.1.0-SNAPSHOT"]]
  :plugins [[lein-midje "3.1.3"]
            [lein-cloverage "1.0.9"]]
  :aliases {"coverage" ["cloverage" "--runner" ":midje"]})
