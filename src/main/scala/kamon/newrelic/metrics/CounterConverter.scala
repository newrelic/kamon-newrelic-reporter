package kamon.newrelic.metrics

import com.newrelic.telemetry.metrics.{Count, Metric}
import kamon.metric.{Instrument, MetricSnapshot}
import kamon.newrelic.TagSetToAttributes.addTags
import kamon.newrelic.metrics.ConversionSupport.buildAttributes
import org.slf4j.LoggerFactory

class CounterConverter {
  private val logger = LoggerFactory.getLogger(classOf[CounterConverter])

  def convert(start: Long, end: Long, counter: MetricSnapshot.Values[Long]): Seq[Metric] = {
    val attributes = buildAttributes(counter)
    logger.debug("name: {} ; numberOfInstruments: {}", counter.name, counter.instruments.size)
    counter.instruments.map { inst: Instrument.Snapshot[Long] =>
      new Count(counter.name, inst.value, start, end, addTags(Seq(inst.tags), attributes.copy()))
    }
  }


}
