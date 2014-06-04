package core

import scala.util.Random
import scala.io.Source

import Math._


class simulator {
  def simulateMarketReturns(n: Int, data: List[HistoricalMarketReturn]): List[SimulatedMarketReturn] = {
    val range = 1 to n

    val earnings = data.map(_.earningsPerc)
    val yields = data.map(_.yieldPerc)

    val earningsStdDev = stdDev(earnings)
    val yieldsStdDev = stdDev(yields)
    val earningsMean = earnings.sum / earnings.size.toDouble
    val yieldsMean = yields.sum / yields.size.toDouble

    val rng = new Random

    range.toList map { x =>
      SimulatedMarketReturn(genGaussian(earningsStdDev, earningsMean, rng), genGaussian(yieldsStdDev, yieldsMean, rng))
    }
  }

  def simulateInflation(n: Int, data: List[HistoricalInflation]): List[SimulatedInflation] = {
    val range = 1 to n

    val inflation = data.map(_.inflation)
    val inflationStdDev = stdDev(inflation)
    val inflationMean = inflation.sum / inflation.size.toDouble

    val rng = new Random

    range.toList map { x =>
      SimulatedInflation(genGaussian(inflationStdDev, inflationMean, rng))
    }
  }

  def simulateEarlyRetirement(params: RetirementParameters, market: List[SimulatedMarketReturn], inflation: List[SimulatedInflation]): SimulatedRetirement = ???

  def aggregatedSimulatedRetirements(sims: List[SimulatedRetirement], runs: Int): AggregatedSimulatedRetirements = ???

  def stdDev(data: List[Double]) = {
    val mean = data.sum / data.length
    sqrt(data.map(x => pow((mean - x),2)).sum / data.length)
  }

  def genGaussian(stdDev: Double, mean: Double, rng: Random ): Double = {
    val variance = sqrt(stdDev)
    val range = 0 to variance.floor.toInt
    range.toList.map(x => rng.nextGaussian()).sum + mean
  }

  def getHistoricalMarketData: List[HistoricalMarketReturn] = {
    Source.fromFile("project/resources/market.tsv").getLines().drop(1).map(x => x.split("\t")).toList.map(y => HistoricalMarketReturn(y(1).toDouble ,y(2).toDouble))
  }

  def getHistoricalInflationData: List[HistoricalInflation] = {
    Source.fromFile("project/resources/inflation.tsv").getLines().drop(2).map(x => x.split("\t")).toList.map(y => HistoricalInflation(y(14).toDouble))
  }
}

case class HistoricalMarketReturn(earningsPerc: Double, yieldPerc: Double)
case class SimulatedMarketReturn(earningsPerc: Double, yieldPerc: Double)
case class HistoricalInflation(inflation: Double) extends AnyVal
case class SimulatedInflation(inflation: Double) extends AnyVal

case class InitialCapital(d: Double) extends AnyVal
case class EstimatedMonthlyExpenses(d: Double) extends AnyVal
case class YearsUntilEarlyRetirement(i: Int) extends AnyVal
case class YearsUntilRetirementAge(i: Int) extends AnyVal

case class RetirementParameters(initialCapital: InitialCapital,
                                estimatedMonthlyExpenses: EstimatedMonthlyExpenses,
                                yearsUntilEarlyRetirement: YearsUntilEarlyRetirement,
                                yearsUntilRetirementAge: YearsUntilRetirementAge)

case class SimulatedCapital(d: Double) extends AnyVal
case class Failure(b: Boolean) extends AnyVal

case class SimulatedRetirement(simulatedCapital: List[SimulatedCapital], failure: Failure)
case class AggregatedSimulatedRetirements(failurePerc: Double, averageRemainingCapital: Double, maxRemainingCapital: Double, minRemainingCapital: Double, rawData: List[SimulatedRetirement])