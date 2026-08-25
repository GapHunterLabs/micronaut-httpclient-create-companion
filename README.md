# Micronaut HttpClient Create Companion

Warning icon on a Micronaut `HttpClient.create(...)` static factory
call found anywhere in application code — the javadoc for this exact
method is explicit: "this method should only be used outside the
context of a Micronaut application" and "Within a Micronaut
application use @Inject to inject a client instead", adding that "the
creator is responsible for closing the client to avoid leaking
connections" when using this factory.

## Why it exists

`HttpClient client = HttpClient.create(url);` compiles fine and
returns a working client — but the official javadoc explicitly warns
this factory is for use *outside* a Micronaut application (a CLI
tool, a standalone script). Using it inside application code bypasses
dependency injection entirely, and now the caller is personally
responsible for closing the client — miss that, and it leaks
connections silently.

## Why built this way

- **100% static text/PSI analysis** — matches the class/method name by
  simple text, so it works whether the real Micronaut jar is on the
  classpath or not. Java and Kotlin.
- **Confirmed gap**: JetBrains' own bundled Micronaut plugin has 8 real
  inspections (Micronaut Data repository methods, cache annotations,
  EL, injection points, properties/YAML config) — confirmed by
  extracting and reading the plugin's own `plugin.xml` directly. None
  of them cover this specific factory-method misuse.

## v0.1 scope — stated honestly, not exhaustively

Matches by simple name, not real type resolution — an unrelated
`HttpClient.create(...)` from a different library sharing the same
simple class name is a possible (rare) false positive. Flags every
occurrence unconditionally, since the javadoc's own guidance is
unconditional for code living inside the app — no attempt to guess
which module a given file belongs to.

## Usage

Open any Java/Kotlin file using Micronaut. An `HttpClient.create(...)`
call anywhere shows a warning icon.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
