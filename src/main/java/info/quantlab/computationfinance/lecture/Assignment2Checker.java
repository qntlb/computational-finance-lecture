package info.quantlab.computationfinance.lecture;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueFactory;
import net.finmath.aadexperiments.value.ConvertableToFloatingPoint;
import net.finmath.functions.AnalyticFormulas;

public class Assignment2Checker {

	public class Result {
		public Result(boolean success, String message) {
			super();
			this.success = success;
			this.message = message;
		}
		public boolean success;
		public String message;
	}

	public Result check(Assignment2 solution, int level) {
		switch(level) {
		case 1:
			return new Result(false, "Test of getRandomValueFromArray: Test is not implemented yet. Your solution may or may not be correct.\n");
		case 2:
			return new Result(false, "Test of getRandomDifferentiableValueFromArray: Test is not implemented yet. Your solution may or may not be correct.\n");
		case 3:
			return testAssigmentDigitalCaplet(solution);
		case 4:
			return testAssigmentForwardInArrears(solution);
		case 5:
			return new Result(false, "Test of getMonteCarloBlackModelDeltaXxx: Tests are not implemented yet. Your solution may or may not be correct.\n");

		default:
			return new Result(false, "Your solution may or may not be correct. Tests are not implemented yet.\n");
		}
	}

	public Result testAssigmentDigitalCaplet(Assignment2 solution) {

		final double modelForwardRate = 0.05;
		final double modelPayoffUnit = 0.9;
		final double modelVolatility = 0.3;
		
		final double productStrike = 0.06;
		final double productMaturity = 2.0;
		final double productPeriodLength = 0.5;
		
		final int numberOfPaths = 100000;
		
		/*
		 * Create a normal distributed random sample vector
		 */
		// Create normal distributed random variable
		Random random = new Random(3413);
		
		double[] samples = new double[numberOfPaths];
		for(int pathIndex=0; pathIndex<numberOfPaths; pathIndex++) {
			samples[pathIndex] = random.nextGaussian();
		}

		RandomValue normal = solution.getRandomValueFromArray(samples);
		RandomValueFactory randomValueFactory = normal.getFactory();

		RandomValue forwardRate = randomValueFactory.fromConstant(modelForwardRate);
		RandomValue payoffUnit = randomValueFactory.fromConstant(modelPayoffUnit);
		RandomValue volatility = randomValueFactory.fromConstant(modelVolatility);
		RandomValue strike = randomValueFactory.fromConstant(productStrike);
		RandomValue maturity = randomValueFactory.fromConstant(productMaturity);
		RandomValue periodLength = randomValueFactory.fromConstant(productPeriodLength);

		RandomValue brownianMotionUponMaturity = normal.mult(maturity.sqrt());

		RandomValue value = solution.getMonteCarloBlackModelValueOfDigitalCaplet(forwardRate, payoffUnit, volatility, brownianMotionUponMaturity, strike, maturity, periodLength);

		double valueMonteCarlo = ((ConvertableToFloatingPoint)value).asFloatingPoint();
		double valueAnalytic = AnalyticFormulas.blackScholesDigitalOptionValue(modelForwardRate, 0.0, modelVolatility, productMaturity, productStrike) * modelPayoffUnit * productPeriodLength;

		boolean success = Math.abs(valueAnalytic-valueMonteCarlo) < 1E-3;
		String message = "Test of getMonteCarloBlackModelValueOfDigitalCaplet: ";
		if(success) message += "Congratulation! The valuation of the digital caplet appears to be correct.";
		else message += "Sorry, the valuation of the digital caplet appears to be not correct.";

		message += "\n";

		return new Result(success,message);
	}

	private Result testAssigmentForwardInArrears(Assignment2 solution) {
		final double modelForwardRate = 0.05;
		final double modelPayoffUnit = 0.9;
		final double modelVolatility = 0.3;
		
		final double productMaturity = 2.0;
		final double productPeriodLength = 0.5;
		
		final int numberOfPaths = 100000;
		
		/*
		 * Create a normal distributed random sample vector
		 */
		// Create normal distributed random variable
		Random random = new Random(3413);
		
		double[] samples = new double[numberOfPaths];
		for(int pathIndex=0; pathIndex<numberOfPaths; pathIndex++) {
			samples[pathIndex] = random.nextGaussian();
		}

		RandomValue normal = solution.getRandomValueFromArray(samples);
		RandomValueFactory randomValueFactory = normal.getFactory();

		RandomValue forwardRate = randomValueFactory.fromConstant(modelForwardRate);
		RandomValue payoffUnit = randomValueFactory.fromConstant(modelPayoffUnit);
		RandomValue volatility = randomValueFactory.fromConstant(modelVolatility);
		RandomValue maturity = randomValueFactory.fromConstant(productMaturity);
		RandomValue periodLength = randomValueFactory.fromConstant(productPeriodLength);

		RandomValue brownianMotionUponMaturity = normal.mult(maturity.sqrt());

		RandomValue value = solution.getMonteCarloBlackModelValueOfForwardRateInArrears(forwardRate, payoffUnit, volatility, brownianMotionUponMaturity, maturity, periodLength);

		double valueMonteCarlo = ((ConvertableToFloatingPoint)value).asFloatingPoint();
		double valueAnalytic = modelForwardRate * productPeriodLength * modelPayoffUnit * (1 + modelForwardRate * productPeriodLength * Math.exp(modelVolatility*modelVolatility*(productMaturity+productPeriodLength)));

		boolean success = Math.abs(valueAnalytic-valueMonteCarlo) < 1E-3;
		String message = "Test of getMonteCarloBlackModelValueOfForwardRateInArrears: ";
		if(success) message += "Congratulation! The valuation of the digital caplet appears to be correct.";
		else message += "Sorry, the valuation of the digital caplet appears to be not correct.";

		message += "\n";

		return new Result(success,message);
	}

}
