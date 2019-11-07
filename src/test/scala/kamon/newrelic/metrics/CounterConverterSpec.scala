package kamon.newrelic.metrics

import java.time.Duration

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.metrics.{Count, Metric => NewRelicMetric}
import kamon.metric.{Instrument, MeasurementUnit, Metric, MetricSnapshot}
import kamon.tag.TagSet
import org.scalatest.{Matchers, WordSpec}

class CounterConverterSpec extends WordSpec with Matchers {

  "the counter converter" should {
    "convert a counter with multiple instruments" in {
      val end = System.currentTimeMillis()
      val start = end - 101
      val value1 = 14444444L
      val value2 = -3L
      val tagSet: TagSet = TagSet.from(Map("foo" -> "bar"))
      val settings = Metric.Settings.ForValueInstrument(MeasurementUnit.percentage, Duration.ofMillis(12))
      val inst1 = new Instrument.Snapshot[Long](tagSet, value1)
      val inst2 = new Instrument.Snapshot[Long](tagSet, value2)
      val counter: MetricSnapshot.Values[Long] = MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst1, inst2))
      val expectedAttrs = new Attributes()
        .put("description", counter.description)
        .put("dimensionName", "percentage")
        .put("magnitudeName", "percentage")
        .put("scaleFactor", 1.0)
        .put("foo", "bar")

      val expected1: NewRelicMetric = new Count("flib", value1, start, end, expectedAttrs);
      val expected2: NewRelicMetric = new Count("flib", value2, start, end, expectedAttrs);

      val expectedResult: Seq[NewRelicMetric] = Seq(expected1, expected2)

      val result = new CounterConverter().convert(start, end, counter)
      result shouldBe expectedResult
    }
  }

}
