

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

  "simulateYear" should {
    "produce a remaining capital greater than initial capital when expenses are 0 AND market returns are greater than inflation" in {
      // TODO refactor with scalacheck

      val market = core.SimulatedMarketReturn(0.10, 0.05)
      val inflation = core.SimulatedInflation(0.03)
      val initialCapital = 100000d

      val remainingCapital = simulator.simulateYear(initialCapital, 0, market, inflation)
      println(remainingCapital)
      remainingCapital must beGreaterThan(initialCapital)
    }
  }

  "simulateEarlyRetirement" should {
    "do something sensible" in {
      // TODO refactor with scalacheck

      val capital = core.InitialCapital(400000d)
      val expenses = core.EstimatedMonthlyExpenses(4000d)
      val untilEarRet = core.YearsUntilEarlyRetirement(10)
      val untilRetAge = core.YearsUntilRetirementAge(20)

      val params = core.RetirementParameters(capital, expenses, untilEarRet, untilRetAge)

      val er = simulator.simulateEarlyRetirement(params)

      println(er)

      // this is just a filler test, mainly want to run to see printlns
      // replace with something better
      er.simulatedRemainingCapital.d must beGreaterThan(-10000000d)

    }
  }



}
