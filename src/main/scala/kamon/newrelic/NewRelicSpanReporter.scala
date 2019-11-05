package kamon.newrelic


import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.spans.SpanBatch
import com.typesafe.config.Config
import kamon.Kamon
import kamon.module.{Module, ModuleFactory, SpanReporter}
import kamon.trace.Span
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters._

class NewRelicSpanReporter(spanBatchSenderBuilder: SpanBatchSenderBuilder =
                           new SimpleSpanBatchSenderBuilder("kamon.newrelic")) extends SpanReporter {

  // TODO figure out how to handle exceptions when the optional config settings don't exist
  //  com.typesafe.config.ConfigException$Missing: No configuration setting found for key 'service-name'

  private val logger = LoggerFactory.getLogger(classOf[NewRelicSpanReporter])
  private var spanBatchSender = spanBatchSenderBuilder.build(Kamon.config())
  private var commonAttributes = buildCommonAttributes(Kamon.config())

  private def buildCommonAttributes(config: Config) = {
    new Attributes()
      .put("instrumentation.source", "kamon-agent")
      .put("service.name", config.getConfig("kamon.newrelic").getString("service-name"))
  }

  checkJoinParameter()
  logger.info("Started the New Relic Span reporter")

  //   TODO is this actually needed with NR Telemetry SDK? research exactly what this does
  def checkJoinParameter(): Unit = {
    val joinRemoteParentsWithSameID = Kamon.config().getBoolean("kamon.trace.join-remote-parents-with-same-span-id")
    if (!joinRemoteParentsWithSameID) {
      logger.warn("For full distributed trace compatibility enable `kamon.trace.join-remote-parents-with-same-span-id` to " +
        "preserve span id across client/server sides of a Span.")
    }
  }

  /**
   * Sends batches of Spans to New Relic using the Telemetry SDK
   *
   * Modules implementing the SpanReporter trait will get registered for periodically receiving span batches. The frequency of the
   * span batches is controlled by the kamon.trace.tick-interval setting.
   *
   * @param spans - spans to report to New Relic
   */
  override def reportSpans(spans: Seq[Span.Finished]): Unit = {
    logger.warn("NewRelicSpanReporter reportSpans...")
    val newRelicSpans = spans.map(NewRelicSpanConverter.convertSpan).asJava
    spanBatchSender.sendBatch(new SpanBatch(newRelicSpans, commonAttributes))
  }

  override def reconfigure(newConfig: Config): Unit = {
    logger.warn("NewRelicSpanReporter reconfigure...")
    spanBatchSender = spanBatchSenderBuilder.build(newConfig)
    commonAttributes = buildCommonAttributes(newConfig)
    checkJoinParameter()
  }

  override def stop(): Unit =
    logger.info("Stopped the New Relic Span reporter")
}

object NewRelicSpanReporter {

  class Factory extends ModuleFactory {
    override def create(settings: ModuleFactory.Settings): Module =
      new NewRelicSpanReporter()
  }

}