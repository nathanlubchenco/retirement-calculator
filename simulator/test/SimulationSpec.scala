

import org.specs2.mutable.Specification

import scala.util.Random

//import org.specs2.ScalaCheck
//import org.scalacheck.{Gen, Arbitrary}

class SimulationSpec extends Specification  {

  val simulator = new core.Simulator

  "getHistoricalMarketData" should {
    "return expected results for the first and last entry" in {
      val data = simulator.getHistoricalMarketData

      val earnings = data.map(_.earningsPerc)
      val yld = data.map(_.yieldPerc)

      println(earnings.max)
      println(earnings.min)
      println(earnings.sum / earnings.length)


      println(yld.max)
      println(yld.min)
      println(yld.sum / yld.length)

      data.head.earningsPerc must beCloseTo(0.0534, 0.0001)
      data.reverse.head.yieldPerc must beCloseTo(0.0196, 0.0001)
    }
  }

  "getHistoricalInflationData" should {
    "return expected results for the first and last entry" in {
      val data = simulator.getHistoricalInflationData
      data.head.inflation must beCloseTo(0.0147, 0.0001)

      val infl = data.map(_.inflation)

      println(infl.max)
      println(infl.min)
      println(infl.sum / infl.length)

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

  "genGaussian" should {
    "produce a list with a mean near the mean given for a large enough list" in {
      val range = 1 to 10000
      val stdDev = 10
      val mean = 100
      val rng = new Random

      val generated = range.toList.map(x => simulator.genGaussian(stdDev, mean, rng))

      val genMean = generated.sum / generated.size

      genMean must beCloseTo(mean, 3d)
    }
    "produce a list with a standard deviation near the std dev given for a large enough list" in {
      val range = 1 to 10000
      val stdDev = 10
      val mean = 100
      val rng = new Random

      val generated = range.toList.map(x => simulator.genGaussian(stdDev, mean, rng))

      val genStdDev = simulator.stdDev(generated)

      genStdDev must beCloseTo(stdDev, 1d)
    }
  }

  "standardDeviation" should {
    "return the correct std dev for  specific lists" in {
      val stdDev1 = simulator.stdDev(List(1,2,3,4,5,6))
      val stdDev2 = simulator.stdDev(List(10,70, 300))

      stdDev1 must beCloseTo(1.87, 0.01)
      stdDev2 must beCloseTo(153.07, 0.01)
    }
  }



}
