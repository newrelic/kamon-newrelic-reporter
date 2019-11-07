package kamon.newrelic.metrics

import com.newrelic.telemetry.metrics.MetricBatchSender
import kamon.metric.{MetricSnapshot, PeriodSnapshot}
import org.mockito.Mockito._
import org.scalatest.{Matchers, WordSpec}

class NewRelicMetricsReporterSpec extends WordSpec with Matchers {

  "The metrics reporter" should {
    "should send some metrics" in {

      val sender = mock(classOf[MetricBatchSender])

      val counter: MetricSnapshot.Values[Long] = MetricSnapshot.ofValues("flib", "flam", settings, Seq(inst1, inst2))
      val periodSnapshot = new PeriodSnapshot(from, to, Seq(counter), Seq(), Seq(), Seq(), Seq());

      val reporter = new NewRelicMetricsReporter(sender)
      reporter.reportPeriodSnapshot(periodSnapshot)

    }

    "can be reconfigured" in {
      fail("build me")
    }
  }

}
