(defproject clj-odbp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [midje "1.8.3"]
                 [midje-junit-formatter "0.1.0-SNAPSHOT"]]
  :plugins [[lein-midje "3.1.3"]
            [lein-cloverage "1.0.9"]]
  :aliases {"coverage" ["cloverage" "--runner" ":midje"]})
