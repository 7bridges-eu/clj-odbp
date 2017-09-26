<p align="center">
<a href="https://7bridges.eu" title="7bridges.eu s.r.l.">
<img src="https://7bridges.eu/img/logo-inline.png" alt="7bridges clj-odbp"
width="500px" height="122px"/></a>
</p>

# clj-odbp

A Clojure driver for [OrientDB](http://orientdb.com/orientdb/) binary protocol.

[![Build Status](https://travis-ci.org/7bridges-eu/clj-odbp.svg?branch=master)](https://travis-ci.org/7bridges-eu/clj-odbp) [![Clojars Project](https://img.shields.io/clojars/v/eu.7bridges/clj-odbp.svg)](https://clojars.org/eu.7bridges/clj-odbp)

## Installation

Add the necessary dependency to your project:

``` clojure
[eu.7bridges/clj-odbp "0.2.1"]
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
user> (let [connection (odbp/connect-server "<username>" "<password>")]
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

Connect to a database and create a record:

``` clojure
user> (let [connection (odbp/db-open "test-db" "<username>" "<password>")]
        (odbp/record-create connection {:_class "Test" :text "test property"}))
```

For further details check [API documentation](https://7bridges-eu.github.io/clj-odbp/).

### Types

The following table shows how OrientDB types map to Clojure types and viceversa.

| OrientDB      | Clojure                                                     |
| :----         | :----                                                       |
| boolean       | `true`, `false`                                             |
| integer       | `(int 42)`                                                  |
| short         | `(short 42)`                                                |
| long          | `42`                                                        |
| float         | `(float 42.5)`                                              |
| double        | `42.5`                                                      |
| decimal       | `(bigdec 42.5)`                                             |
| string        | `"foo"`                                                     |
| binary        | `{:_obinary [100 101]}`                                     |
| date          | not supported                                               |
| datetime      | `(java.util.Date.)`                                         |
| embedded      | `{:_version 0 :_class "Test" :name "foo"}`                  |
| embedded list | `[0 "foo" 1]`                                               |
| embedded set  | `#{0 "foo" 1}`                                              |
| embedded map  | `{:name "foo" :age 42}`                                     |
| link          | `"#20:1"`                                                   |
| link list     | `["#20:1" "#20:2"]`                                         |
| link set      | `#{"#20:1" "#20:2"}`                                        |
| link map      | `{"rid" "#20:1"}`                                           |
| ORidBag       | `{:_oridbag {:bag []}}`                                     |
| ORidTree      | `{:_oridtree {:changes []}}`                                |

## License

Copyright © 2017 7bridges s.r.l.

Distributed under the Apache License 2.0.
