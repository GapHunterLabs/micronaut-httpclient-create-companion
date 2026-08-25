<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Micronaut HttpClient Create Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- Warning icon on a Micronaut HttpClient.create(...) call anywhere in
  application code -- the javadoc explicitly says this factory should
  only be used outside a Micronaut application, and that the caller is
  responsible for closing the client to avoid leaking connections.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/micronaut-httpclient-create-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/micronaut-httpclient-create-companion/commits/0.1.0
