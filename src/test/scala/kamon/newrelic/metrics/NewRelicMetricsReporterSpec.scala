package kamon.newrelic.metrics

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.metrics.{Count, Metric, MetricBatch, MetricBatchSender}
import kamon.metric.Metric.Settings.ForValueInstrument
import kamon.metric.{Instrument, MeasurementUnit, MetricSnapshot, PeriodSnapshot}
import kamon.tag.TagSet
import org.mockito.Mockito._
import org.scalatest.{Matchers, WordSpec}

import scala.jdk.CollectionConverters._

class NewRelicMetricsReporterSpec extends WordSpec with Matchers {

  val expectedCommonAttributes: Attributes = new Attributes()
    .put("service.name", "kamon.service")
    .put("instrumentation.source", "kamon-agent")


  "The metrics reporter" should {
    "send some metrics" in {
      val from: Instant = Instant.now()
      val to = from.plus(100, ChronoUnit.MILLIS)
      val settings: ForValueInstrument = new ForValueInstrument(MeasurementUnit.percentage, Duration.ZERO)
      val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
      val inst1 = new Instrument.Snapshot[Long](tagSet, 55L)
      val inst2 = new Instrument.Snapshot[Long](tagSet, 66L)

      val expectedAttributes = new Attributes()
        .put("foo", "bar")
        .put("description", "flam")
        .put("dimensionName", "percentage")
        .put("magnitudeName", "percentage")
        .put("scaleFactor", 1.0)
      val flib1: Metric = new Count("flib", 55L, from.toEpochMilli, to.toEpochMilli, expectedAttributes)
      val flib2: Metric = new Count("flib", 66L, from.toEpochMilli, to.toEpochMilli, expectedAttributes)
      val expectedBatch: MetricBatch = new MetricBatch(Seq(flib1, flib2).asJavaCollection, expectedCommonAttributes)

      val sender = mock(classOf[MetricBatchSender])

      val counter: MetricSnapshot.Values[Long] = MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst1, inst2))
      val periodSnapshot = new PeriodSnapshot(from, to, Seq(counter), Seq(), Seq(), Seq(), Seq());

      val reporter = new NewRelicMetricsReporter(sender)
      reporter.reportPeriodSnapshot(periodSnapshot)

      verify(sender).sendBatch(expectedBatch)
    }

    "be reconfigurable" in {
      fail("build me")
    }
  }

}
