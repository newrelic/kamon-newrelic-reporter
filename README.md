# THIS VERSION OF THE REPORTER HAS BEEN DEPRECATED
Please use the official reporter located here:  https://github.com/kamon-io/kamon-newrelic

Note: New Relic still supports that official reporter; we have just moved it to the kamon-io organization to improve
visibility for kamon users.

# New Relic Kamon Reporter
Reports data generated by the Kamon Java agent to New Relic via the Telemetry SDK.

## Local Development

### Build

`sbt clean`  
`sbt compile`  

Change `scalaVersion` in the `build.sbt` to compile for a specific Scala version.

#### Automated builds 
These are handled by [circleci](https://circleci.com/gh/newrelic/kamon-newrelic-reporter)

### Publish

Our automated circleci builds handle publishing to maven-central. 
If you wish to publish locally, you can publish the `kamon-newrelic` using one of the following options.

#### Option 1: Local Maven Repo

Publish to [local Maven repo](https://www.scala-sbt.org/1.x/docs/Publishing.html#Publishing+locally):

`sbt publishM2`

#### Option 2: Local Ivy Repo

Publish to [local Ivy repo](https://www.scala-sbt.org/1.x/docs/Publishing.html#Publishing+locally):

`sbt publishLocal`

#### Option 3: Package Artifact

Generate an artifact at `kamon-newrelic/target/scala-2.13/kamon-newrelic_2.13-<version>.jar`

`sbt package`

## Usage

Add the `kamon-newrelic` reporter dependency to the project `build.sbt`:

```
libraryDependencies ++= Seq(
    "io.kamon" %% "kamon-bundle" % "2.0.2",
    "com.newrelic.telemetry" %% "kamon-newrelic-reporter" % "0.0.4",
    ...
)
```

Add the following config to the `application.conf` to configure the New Relic Span Reporter:

```
 # ======================================= #
 # kamon-newrelic reference configuration  #
 # ======================================= #

kamon.newrelic {
    # A New Relic Insights API Insert Key is required to send trace data to New Relic
    # https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#insert-key-create
    nr-insights-insert-key = "none"
}
```

## Viewing Data in New Relic

Span data can be:
* Queried from New Relic Insights (`SELECT * FROM Span`)
* Viewed in [New Relic One](https://one.newrelic.com/) distributed traces

## Span JSON Posted to New Relic

```json
[
  {
    "common": {
      "attributes": {
        "instrumentation.provider": "kamon-agent",
        "service.name": "Test Service A",
        "host": "host123.test.com"
      }
    },
    "spans": [
      {
        "id": "e2e6ee273c7fa481",
        "trace.id": "4c88d8227b52b1fe",
        "timestamp": 1572492529161,
        "attributes": {
          "duration.ms": 20,
          "http.status_code": "200",
          "component": "play.server.akka-http",
          "span.kind": "server",
          "http.response.ready": 1572492529182,
          "name": "http.server.request",
          "http.url": "http://localhost:9000/v1/posts",
          "error": "false",
          "http.method": "GET",
          "operation": "http.server.request"
        }
      },
      {
        "id": "e5ce67106ca9b40b",
        "trace.id": "215fb309db9ba548",
        "timestamp": 1572492524769,
        "attributes": {
          "duration.ms": 16,
          "http.status_code": "200",
          "component": "play.server.akka-http",
          "span.kind": "server",
          "http.response.ready": 1572492524786,
          "name": "http.server.request",
          "http.url": "http://localhost:9000/v1/posts",
          "error": "false",
          "http.method": "GET",
          "operation": "http.server.request"
        }
      }
    ]
  }
]
```

## Release Process

#### Publish to Staging Repo
The staging release job is managed by CircleCI.

To stage the release simply submit and merge a PR to update the [build.sbt](build.sbt) file with the version to be released (e.g. `version := "0.0.4"`).

Results of the job can be viewed here: https://app.circleci.com/github/newrelic/kamon-newrelic-reporter/pipelines

After the staging release job has run successfully it will publish the new artifact to a staging repository on Sonatype at: https://oss.sonatype.org/#stagingRepositories.

#### Manually Release Staging Repo
1. Find the staging repo on Sonatype, which should be named similar to `comnewrelic-nnnn`, and validate that the contents and version look correct.
2. If the contents look correct, select the staging repo and choose `close`, leaving a comment such as `releasing 0.0.4`.
3. When the staging repo is finished closing, select the staging repo and choose `release`, keeping the `Automatically Drop` checkbox checked, and leave a comment such as `releasing 0.0.4`.
4. Verify that the artifact was published on Maven Central at: https://repo1.maven.org/maven2/com/newrelic/telemetry/kamon-newrelic-reporter_2.13/

#### Post Release
Submit and merge a PR with the following:
* Update the [build.sbt](build.sbt) file with a snapshot of the next version to be released (e.g. `version  := "0.0.5-SNAPSHOT"`).
* Update the [CHANGELOG](CHANGELOG.md) with details of the new release:
  ```markdown
  ## [0.0.4]
  - Miscellaneous bug fixes and tweaks
  ```
* Update the [Usage](#usage) example in the [README](README.md) with the newly released version (e.g. `"com.newrelic.telemetry" %% "kamon-newrelic-reporter" % "0.0.4"`).
