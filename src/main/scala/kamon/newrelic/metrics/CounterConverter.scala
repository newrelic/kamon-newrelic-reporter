package kamon.newrelic.metrics

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.metrics.{Count, Metric}
import kamon.metric.{Instrument, MetricSnapshot}
import kamon.newrelic.TagSetToAttributes.addTags

class CounterConverter {

  def convert(start: Long, end: Long, counter: MetricSnapshot.Values[Long]): Seq[Metric] = {
    val name: String = counter.name
    val dimensionName = counter.settings.unit.dimension.name
    val magnitudeName = counter.settings.unit.magnitude.name
    val scaleFactor = counter.settings.unit.magnitude.scaleFactor
    val attributes = new Attributes()
      .put("description", counter.description)
      .put("dimensionName", dimensionName)
      .put("magnitudeName", magnitudeName)
      .put("scaleFactor", scaleFactor)

    counter.instruments.map { inst: Instrument.Snapshot[Long] =>
      new Count(name, inst.value, start, end, addTags(Seq(inst.tags), attributes.copy()))
    }
  }

}
