package core

import scala.util.Random
import Math._

class simulator {
  def simulateMarketReturns(n: Int, data: List[HistoricalMarketReturn]): List[SimulatedMarketReturn] = ???

  def simulateInflation(n: Int, data: List[HistoricalInflation]): List[SimulatedInflation] = ???

  def simulateEarlyRetirement(params: RetirementParameters, market: List[SimulatedMarketReturn], inflation: List[SimulatedInflation]): SimulatedRetirement = ???
}

class utils {
  def stdDev(data: List[Double]) = {
    val mean = data.sum / data.length
    sqrt(data.map(x => pow((mean - x),2)).sum / data.length)
  }

  def getHistoricalMarketData: List[HistoricalMarketReturn] = ???

  def getHistoricalInflationData: List[HistoricalInflation] = ???
}

case class HistoricalMarketReturn(d: Double) extends AnyVal
case class SimulatedMarketReturn(d: Double) extends AnyVal
case class HistoricalInflation(d: Double) extends AnyVal
case class SimulatedInflation(d: Double) extends AnyVal

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