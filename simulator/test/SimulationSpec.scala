

import org.specs2.mutable.Specification
//import org.specs2.ScalaCheck
//import org.scalacheck.{Gen, Arbitrary}

class SimulationSpec extends Specification  {

  val simulator = new core.Simulator

  "getHistoricalMarketData" should {
    "return expected results for the first and last entry" in {
      val data = simulator.getHistoricalMarketData
      data.head.earningsPerc must beCloseTo(0.0534, 0.0001)
      data.reverse.head.yieldPerc must beCloseTo(0.0196, 0.0001)
    }
  }

  "getHistoricalInflationData" should {
    "return expected results for the first and last entry" in {
      val data = simulator.getHistoricalInflationData
      data.head.inflation must beCloseTo(0.0147, 0.0001)
      data.reverse.head.inflation must beCloseTo(0.0135, 0.0001)
    }
  }

  "simulateMarketReturns" should {
    "almost never produce average values greater than 1" in {
      val data = simulator.simulateMarketReturns(10, simulator.getHistoricalMarketData)
      val avgEarning = data.map(_.earningsPerc).sum / 10
      val avgYield = data.map(_.yieldPerc).sum / 10

      avgEarning must beLessThan(1.0)
      avgYield must beLessThan(1.0)

    }
  }



}
