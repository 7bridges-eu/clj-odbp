# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Upcoming features

* [#16](https://github.com/7bridges-eu/clj-odbp/issues/16) Support multiple
  connections to different db instances.

## [0.2.2] - (2017-11-16)

### Bugs Fixed

* [#18](https://github.com/7bridges-eu/clj-odbp/issues/18) Handle 'n' response
  type in REQUEST_COMMAND
* Fixed a couple of log messages.

## [0.2.1] - (2017-09-26)

### Bugs fixed

* [#12](https://github.com/7bridges-eu/clj-odbp/issues/12) Fixed a serialization
  problem with `exeute-command` parameters.

## [0.2.0] - (2017-09-25)

### New features

* [#6](https://github.com/7bridges-eu/clj-odbp/issues/3) Remove [Timbre](https://github.com/ptaoussanis/timbre)
  dependency writing an custom logger.
* [#2](https://github.com/7bridges-eu/clj-odbp/issues/2) ORidBag and ORidTree
  support added.

### Changes

* [#8](https://github.com/7bridges-eu/clj-odbp/issues/8) Temporary removed
  session cache.
* [#7](https://github.com/7bridges-eu/clj-odbp/issues/7) Rewrote OrientDB
  exception handling.

## [0.1.0] - (2017-08-31)

* Initial release.
