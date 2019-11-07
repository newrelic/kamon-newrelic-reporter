package kamon.newrelic.metrics

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.metrics.{Count, Metric, MetricBatch, MetricBatchSender}
import com.typesafe.config.{Config, ConfigValue, ConfigValueFactory}
import kamon.Kamon
import kamon.metric.Metric.Settings.ForValueInstrument
import kamon.metric.{Instrument, MeasurementUnit, MetricSnapshot, PeriodSnapshot}
import kamon.tag.TagSet
import org.mockito.Mockito._
import org.scalatest.{Matchers, WordSpec}

import scala.jdk.CollectionConverters._

class NewRelicMetricsReporterSpec extends WordSpec with Matchers {

  "The metrics reporter" should {
    "send some metrics" in {
      val from: Instant = Instant.now()
      val to = from.plus(100, ChronoUnit.MILLIS)
      val settings: ForValueInstrument = new ForValueInstrument(MeasurementUnit.percentage, Duration.ZERO)
      val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
      val inst1 = new Instrument.Snapshot[Long](tagSet, 55L)
      val inst2 = new Instrument.Snapshot[Long](tagSet, 66L)
      val counter: MetricSnapshot.Values[Long] = MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst1, inst2))
      val periodSnapshot = new PeriodSnapshot(from, to, Seq(counter), Seq(), Seq(), Seq(), Seq())

      val expectedAttributes = new Attributes()
        .put("description", "flam")
        .put("dimensionName", "percentage")
        .put("magnitudeName", "percentage")
        .put("scaleFactor", 1.0d)
        .put("foo", "bar")
      val flib1: Metric = new Count("flib", 55.0, from.toEpochMilli, to.toEpochMilli, expectedAttributes)
      val flib2: Metric = new Count("flib", 66.0, from.toEpochMilli, to.toEpochMilli, expectedAttributes)
      val expectedCommonAttributes: Attributes = new Attributes()
        .put("service.name", "kamon-application")
        .put("instrumentation.source", "kamon-agent")
      val expectedBatch: MetricBatch = new MetricBatch(Seq(flib1, flib2).asJava, expectedCommonAttributes)

      val sender = mock(classOf[MetricBatchSender])

      val reporter = new NewRelicMetricsReporter(sender)
      reporter.reportPeriodSnapshot(periodSnapshot)

      verify(sender).sendBatch(expectedBatch)
    }

    "be reconfigurable" in {
      val from: Instant = Instant.now()
      val to = from.plus(100, ChronoUnit.MILLIS)
      val settings: ForValueInstrument = new ForValueInstrument(MeasurementUnit.percentage, Duration.ZERO)
      val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
      val inst2 = new Instrument.Snapshot[Long](tagSet, 66L)
      val counter: MetricSnapshot.Values[Long] = MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst2))
      val periodSnapshot = new PeriodSnapshot(from, to, Seq(counter), Seq(), Seq(), Seq(), Seq())

      val expectedAttributes = new Attributes()
        .put("description", "flam")
        .put("dimensionName", "percentage")
        .put("magnitudeName", "percentage")
        .put("scaleFactor", 1.0d)
        .put("foo", "bar")
      val flib2: Metric = new Count("flib", 66.0, from.toEpochMilli, to.toEpochMilli, expectedAttributes)

      val expectedCommonAttributes: Attributes = new Attributes()
        .put("service.name", "cheese-whiz")
        .put("instrumentation.source", "kamon-agent")
      val expectedBatch: MetricBatch = new MetricBatch(Seq(flib2).asJava, expectedCommonAttributes)

      val configObject: ConfigValue = ConfigValueFactory.fromMap(Map("service" -> "cheese-whiz").asJava)
      val config: Config = Kamon.config().withValue("kamon.environment", configObject)

      val sender = mock(classOf[MetricBatchSender])
      val reporter = new NewRelicMetricsReporter(sender)

      reporter.reconfigure(config)
      reporter.reportPeriodSnapshot(periodSnapshot)

      verify(sender).sendBatch(expectedBatch)
    }
  }

}
