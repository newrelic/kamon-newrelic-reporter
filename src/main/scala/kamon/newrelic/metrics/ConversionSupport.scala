package kamon.newrelic.metrics

import com.newrelic.telemetry.Attributes
import kamon.metric.MetricSnapshot.Values

class ConversionSupport {
}

object ConversionSupport {

  def buildAttributes(metric: Values[_]) = {
    val dimensionName = metric.settings.unit.dimension.name
    val magnitudeName = metric.settings.unit.magnitude.name
    val scaleFactor = metric.settings.unit.magnitude.scaleFactor
    new Attributes()
      .put("description", metric.description)
      .put("dimensionName", dimensionName)
      .put("magnitudeName", magnitudeName)
      .put("scaleFactor", scaleFactor)
  }
}
