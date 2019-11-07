package kamon.newrelic.metrics

import java.time.Duration

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.metrics.Gauge
import kamon.metric.MeasurementUnit.Dimension
import kamon.metric.{Instrument, MeasurementUnit, Metric, MetricSnapshot}
import kamon.tag.TagSet
import org.scalatest.{Matchers, WordSpec}

class GaugeConverterSpec extends WordSpec with Matchers {


  "gauge converter" should {
    "convert a gauge" in {
      val timestamp: Long = System.currentTimeMillis()
      val converter = new GaugeConverter();
      val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
      val settings = Metric.Settings.ForValueInstrument(
        new MeasurementUnit(Dimension.Information, new MeasurementUnit.Magnitude("finch", 11.0d)), Duration.ofMillis(12))
      val inst = new Instrument.Snapshot[Double](tagSet, 15.6d)
      val kamonGauge = new MetricSnapshot.Values[Double]("shirley", "another one", settings, Seq(inst))
      val result = converter.convert(timestamp, kamonGauge)
      val attributes = new Attributes()
        .put("description", "another one")
        .put("magnitudeName", "finch")
        .put("dimensionName", "information")
        .put("scaleFactor", 11.0)
        .put("foo", "bar")
      val expectedGauge = new Gauge("shirley", 15.6d, timestamp, attributes)
      result shouldBe Seq(expectedGauge)
    }
  }

}
