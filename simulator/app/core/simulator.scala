package core

import scala.util.Random
import scala.io.Source

import Math._

import scalaz._


class Simulator {
  def simulateMarketReturns(n: Int, data: List[HistoricalMarketReturn]): List[SimulatedMarketReturn] = {
    val range = 1 to n

    val earnings = data.map(_.earningsPerc)
    val yields = data.map(_.yieldPerc)

    val earningsStdDev = stdDev(earnings)
    val yieldsStdDev = stdDev(yields)
    val earningsMean = earnings.sum / earnings.size.toDouble
    val yieldsMean = yields.sum / yields.size.toDouble

    println("market \n")
    println(earningsStdDev)
    println(earningsMean)

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

    println("inflation \n")
    println(inflationStdDev)
    println(inflationMean)

    val rng = new Random

    range.toList map { x =>
      SimulatedInflation(genGaussian(inflationStdDev, inflationMean, rng))
    }
  }

  def simulateYear(capital: Double, monthlyExpenses: Double, marketReturn: SimulatedMarketReturn, inflationRate: SimulatedInflation): Double = {
    val earnings = capital * marketReturn.earningsPerc
    val yielded = capital * marketReturn.yieldPerc
    val newCapital = capital + earnings + yielded
    // capital minus expenses then adjusted for inflation
    // need to investigate inflation adjustment
    (newCapital - monthlyExpenses * 12) / (1 + inflationRate.inflation)
  }


  def simulateEarlyRetirement(params: RetirementParameters): SimulatedRetirement = {
    val yearsInEarlyRetirement = params.yearsUntilRetirementAge.i - params.yearsUntilEarlyRetirement.i

    val market: List[SimulatedMarketReturn] = simulateMarketReturns(yearsInEarlyRetirement, getHistoricalMarketData)
    val inflationUntilEarlyRetirement: List[SimulatedInflation] = simulateInflation(params.yearsUntilEarlyRetirement.i, getHistoricalInflationData)
    val inflationDuringEarlyRetirement: List[SimulatedInflation] = simulateInflation(yearsInEarlyRetirement, getHistoricalInflationData)

    // should do a sanity check here to make sure these are reasonable and the calculation is correct
    val inflationAdjustedMonthlyExpenses = inflationUntilEarlyRetirement.foldLeft(params.estimatedMonthlyExpenses)((a,b) => EstimatedMonthlyExpenses(a.d * (1 + b.inflation)))


    case class Data(market: List[SimulatedMarketReturn], inflation: List[SimulatedInflation], capitalRemaining: Double, expenses: EstimatedMonthlyExpenses)
    val data = Data(market, inflationDuringEarlyRetirement, params.initialCapital.d, inflationAdjustedMonthlyExpenses )
    println(data)

    val state = State { data: Data =>
      ( Data(data.market.tail,
        data.inflation.tail,
        simulateYear(data.capitalRemaining, data.expenses.d, data.market.head, data.inflation.head), data.expenses ),
        data.market.size)
    }

    def runUntil(stateResult: Data): Data =  {
      if( stateResult.market.size == 0) {
        println(stateResult)
        stateResult
      }
      else {
        val nextResult = state.run(stateResult)
        println(nextResult)
        runUntil(nextResult._1)
      }
    }

    val result = runUntil(data)

    val failure = if(result.capitalRemaining < 0) true else false

    SimulatedRetirement( SimulatedCapital(result.capitalRemaining), Failure(failure))

  }


  def aggregatedSimulatedRetirements(params: RetirementParameters, runs: Int): AggregatedSimulatedRetirements = {
    val range = 1 until runs
    val data = range.toList.map(x => simulateEarlyRetirement(params))

    val failurePerc = data.map(_.failure.b).foldLeft(0.0)((a,b) => if(b) 1 + a else a) / runs
    val avgRemainingCaptial = data.map(_.simulatedRemainingCapital.d).sum / data.size
    val maxRemainingCapital = data.map(_.simulatedRemainingCapital.d).max
    val minRemainingCapital = data.map(_.simulatedRemainingCapital.d).min

    AggregatedSimulatedRetirements(failurePerc, avgRemainingCaptial, maxRemainingCapital, minRemainingCapital, data)

  }

  def stdDev(data: List[Double]) = {
    val mean = data.sum / data.length
    sqrt(data.map(x => pow((x - mean),2)).sum / ( data.length -1 ))
  }

  def genGaussian(stdDev: Double, mean: Double, rng: Random ): Double = {
    if(stdDev < 1 | mean < 1) {

      val variance = pow(stdDev * 100,2)
      val range = 0 to variance.floor.toInt
      val scaledResult = range.toList.map(x => rng.nextGaussian()).sum + (mean * 100)
      scaledResult / 100

    } else {
      val variance = pow(stdDev, 2)
      val range = 0 to variance.floor.toInt
      range.toList.map(x => rng.nextGaussian()).sum + mean
      //println(stdDev)
      //println(mean)
      //println(result)
    }
  }

  def getHistoricalMarketData: List[HistoricalMarketReturn] = {
    Source.fromFile("project/resources/market.tsv").getLines().drop(1).map(x => x.split("\t")).toList.map(y => HistoricalMarketReturn(y(1).toDouble / 100d ,y(2).toDouble / 100d))
  }

  def getHistoricalInflationData: List[HistoricalInflation] = {
    Source.fromFile("project/resources/inflation.tsv").getLines().drop(2).map(x => x.split("\t")).toList.map(y => HistoricalInflation(y(13).toDouble / 100d))
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

case class SimulatedRetirement(simulatedRemainingCapital: SimulatedCapital, failure: Failure) // find a way to recover an entire list, but final value Ok for now
case class AggregatedSimulatedRetirements(failurePerc: Double, averageRemainingCapital: Double, maxRemainingCapital: Double, minRemainingCapital: Double, rawData: List[SimulatedRetirement])