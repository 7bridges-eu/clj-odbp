<p align="center">
<a href="https://7bridges.eu" title="7bridges.eu s.r.l.">
<img src="https://7bridges.eu/img/logo-inline.png" alt="7bridges clj-odbp"
width="500px" height="122px"/></a>
</p>

# clj-odbp

A Clojure driver for [OrientDB](http://orientdb.com/orientdb/) binary protocol.

[![Build Status](https://travis-ci.org/7bridges-eu/clj-odbp.svg?branch=master)](https://travis-ci.org/7bridges-eu/clj-odbp) [![Clojars Project](https://img.shields.io/clojars/v/eu.7bridges/clj-odbp.svg)](https://clojars.org/eu.7bridges/clj-odbp) [![Dependencies Status](https://versions.deps.co/7bridges-eu/clj-odbp/status.svg)](https://versions.deps.co/7bridges-eu/clj-odbp)

## Installation

Add the necessary dependency to your project:

``` clojure
[eu.7bridges/clj-odbp "0.3.1-SNAPSHOT"]
```

## Usage

### Driver usage

Connect to an OrientDB server:

``` clojure
user> (require '[clj-odbp.core :as odbp])
user> (def connection-parameters {:host "localhost" :port 2424})
user> (odbp/connect-server connection-parameters "<username>" "<password>")
```

Create a new database:

``` clojure
user> (with-open [session (odbp/connect-server connection-parameters "<username>" "<password>")]
        (odbp/db-create session "test-db"))
```

Check if a database exists:

``` clojure
user> (with-open [session (odbp/connect-server connection-parameters "<username>" "<password>")]
        (odbp/db-exist session "test-db"))
```

Connect to a database and create a vertex:

``` clojure
user> (with-open [session (odbp/db-open connection-parameters "test-db" "<username>" "<password>")]
        (odbp/execute-command session "create class Test extends V"))
```

Connect to a database and create a record:

``` clojure
user> (with-open [session (odbp/db-open connection-parameters "test-db" "<username>" "<password>")]
        (odbp/record-create session {:_class "Test" :text "test property"}))
```

For further details check [API documentation](https://7bridges-eu.github.io/clj-odbp/).

### Transactions

You can use OrientDB transactions through `clj-odbp.core/execute-script`:

``` clojure
user> (require '[clj-odbp.core :as odbp])
user> (require ’[clj-odbp.constants :as db-const])
user> (def connection-parameters {:host "localhost" :port 2424})
user> (with-open [session (odbp/db-open connection-parameters "test-db" "<username>" "<password>")
                  script "BEGIN\n
                          let account = CREATE VERTEX Account SET name = 'Luke'\n
                          let city = SELECT FROM City WHERE name = 'London' LOCK RECORD\n
                          let e = CREATE EDGE Lives FROM $account TO $city\n
                          COMMIT\n
                          return $e"]
        (odbp/execute-script session script db-const/language-sql))
```

Queries for `clj-odbp.core/execute-script` can be parametrized:

``` clojure
user> (with-open [session (odbp/db-open connection-parameters "test-db" "<username>" "<password>")
                  script "BEGIN\n
                          let account = CREATE VERTEX Account SET name = :account\n
                          let city = SELECT FROM City WHERE name =  :city LOCK RECORD\n
                          let e = CREATE EDGE Lives FROM $account TO $city\n
                          COMMIT\n
                          return $e"
                  params {:account "Luke" :city "London"}]
        (odbp/execute-script session script db-const/language-sql :params params))
```

### Log configuration

`clj-odbp` comes with default settings with regard to logging.

``` clojure
user> (require '[clj-odbp.configure :as c])
user> @c/log-config
;; => {:log-level :fatal, :log-file "log/clj_odbp.log"}
```

To change the default settings, you need to use
`clj-odbp.configure/configure-log`.

``` clojure
user> (c/configure-log {:log-level :debug})
user> @c/log-config
;; => {:log-level :debug, :log-file "log/clj_odbp.log"}
```

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
