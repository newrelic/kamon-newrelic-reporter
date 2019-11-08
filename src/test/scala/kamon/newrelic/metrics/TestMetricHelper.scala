package kamon.newrelic.metrics

import java.time.{Duration, Instant}

import kamon.metric.MeasurementUnit.Dimension
import kamon.metric.{Instrument, MeasurementUnit, Metric, MetricSnapshot}
import kamon.tag.TagSet

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
    val instrument1 = new Instrument.Snapshot[Long](tagSet, value1)
    val instrument2 = new Instrument.Snapshot[Long](tagSet, value2)
    MetricSnapshot.ofValues("flib", "flam", settings, Seq(instrument1, instrument2))
  }

  def buildGauge = {
    val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
    val settings = Metric.Settings.ForValueInstrument(
      new MeasurementUnit(Dimension.Information, new MeasurementUnit.Magnitude("finch", 11.0d)), Duration.ofMillis(12))
    val inst = new Instrument.Snapshot[Double](tagSet, 15.6d)
    new MetricSnapshot.Values[Double]("shirley", "another one", settings, Seq(inst))
  }
}
