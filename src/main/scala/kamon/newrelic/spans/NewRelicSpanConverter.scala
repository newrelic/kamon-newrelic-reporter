package kamon.newrelic.spans

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.spans.{Span => NewRelicSpan}
import kamon.newrelic.TagSetToAttributes
import kamon.tag.Lookups.{longOption, option}
import kamon.trace.Span
import kamon.trace.Span.Mark
import kamon.util.Clock
import zipkin2.Endpoint

/**
 * Converts a Kamon span to a New Relic span
 */
object NewRelicSpanConverter {

  /**
   * Converts a Kamon Span$Finished instance into a New Relic Span
   *
   * @param kamonSpan Kamon Span$Finished
   * @return New Relic Span
   */
  def convertSpan(kamonSpan: Span.Finished): NewRelicSpan = {
    val durationMs = Math.floorDiv(Clock.nanosBetween(kamonSpan.from, kamonSpan.to), 1000000)
    val parentId = if (kamonSpan.parentId.isEmpty) null else kamonSpan.parentId.string
    NewRelicSpan.builder(kamonSpan.id.string)
      .traceId(kamonSpan.trace.id.string)
      .parentId(parentId)
      .name(kamonSpan.operationName)
      .timestamp(Clock.toEpochMicros(kamonSpan.from) / 1000) // convert to milliseconds
      .durationMs(durationMs)
      .attributes(buildAttributes(kamonSpan))
      .build()
  }

  private def buildAttributes(kamonSpan: Span.Finished) = {
    val attributes = new Attributes().put("span.kind", kamonSpan.kind.toString)

    // Span is a client span
    if (kamonSpan.kind.toString == Span.Kind.Client.toString()) {
      val remoteEndpoint = Endpoint.newBuilder()
        .ip(getStringTag(kamonSpan, PeerKeys.IPv4))
        .ip(getStringTag(kamonSpan, PeerKeys.IPv6))
        .port(getLongTag(kamonSpan, PeerKeys.Port).toInt)
        .build()

      if (hasAnyData(remoteEndpoint))
        attributes.put("remoteEndpoint", remoteEndpoint.toString)
    }

    kamonSpan.marks.foreach {
      case Mark(instant, key) => attributes.put(key, Clock.toEpochMicros(instant) / 1000) // convert to milliseconds
    }

    TagSetToAttributes.addTags( Seq(kamonSpan.tags, kamonSpan.metricTags), attributes)
  }

  private def getStringTag(span: Span.Finished, tagName: String): String =
    span.tags.get(option(tagName)).orElse(span.metricTags.get(option(tagName))).orNull

  private def getLongTag(span: Span.Finished, tagName: String): Long =
    span.tags.get(longOption(tagName)).orElse(span.metricTags.get(longOption(tagName))).getOrElse(0L)


  private def hasAnyData(endpoint: Endpoint): Boolean =
    endpoint.ipv4() != null || endpoint.ipv6() != null || endpoint.port() != null || endpoint.serviceName() != null


  private object PeerKeys {
    val Host = "peer.host"
    val Port = "peer.port"
    val IPv4 = "peer.ipv4"
    val IPv6 = "peer.ipv6"
  }

}