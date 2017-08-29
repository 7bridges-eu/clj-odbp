# clj-odbp

A Clojure driver for OrientDB binary protocol.

## Installation

Add the necessary dependency to your project:

``` clojure
[eu.7bridges/clj-odbp "0.1.0"]
```

## Usage

### Driver configuration

`clj-odbp` comes with default settings with regard to OrientDB connection
and logging.

``` clojure
user> (require '[clj-odbp.configure :as c])
user> @c/config
;; => {:log-level :fatal, :log-file "log/clj_odbp.log", :port 2424, :host "localhost", :log-rotation-frequency :daily}
```

To change the default settings, you need to use
`clj-odbp.configure/configure-driver`.

``` clojure
user> (c/configure-driver {:host "my-orientdb-server" :log-level :debug})
user> @c/config
;; => {:log-level :debug, :log-file "log/clj_odbp.log", :port 2424, :host "my-orientdb-server", :log-rotation-frequency :daily}
```

### Driver usage

Connect to an OrientDB server:

``` clojure
user> (require '[clj-odbp.core :as odbp])
user> (odbp/connect-server "<username>" "<password>")
```

Create a new database:

``` clojure
(let [connection (odbp/connect-server "<username>" "<password>")]
  (odbp/db-create connection "test-db"))
```

Check if a database exists:

``` clojure
user> (let [connection (odbp/connect-server "<username>" "<password>")]
        (odbp/db-exist connection "test-db"))
```

Connect to a database and create a vertex:

``` clojure
user> (let [connection (odbp/db-open "test-db" "<username>" "<password>")]
        (odbp/execute-command connection "create class Test extends V"))
```

Connecto to a database and create a record:

``` clojure
user> (let [connection (odbp/db-open "test-db" "<username>" "<password>")]
        (odbp/record-create connection {:_class "Test" :text "test property"}))
```

## License

Copyright © 2017 7bridges s.r.l.

Distributed under the Apache License 2.0.
