package kamon.newrelic.metrics

import java.time.{Duration, Instant}

import kamon.metric.{Instrument, MeasurementUnit, Metric, MetricSnapshot}
import kamon.tag.TagSet

class TestMetricHelper {
}

object TestMetricHelper {
  val end: Long = System.currentTimeMillis()
  val endInstant: Instant = Instant.ofEpochMilli(end)
  val start: Long = end - 101
  val startInstant: Instant = Instant.ofEpochMilli(start)
  val value1: Long = 55L
  val value2: Long = 66L

  def buildCounter = {
    val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
    val settings = Metric.Settings.ForValueInstrument(MeasurementUnit.percentage, Duration.ofMillis(12))
    val inst1 = new Instrument.Snapshot[Long](tagSet, value1)
    val inst2 = new Instrument.Snapshot[Long](tagSet, value2)
    MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst1, inst2))
  }
}
