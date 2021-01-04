package info.quantlab.computationfinance.lecture;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueDifferentiable;
import net.finmath.aadexperiments.randomvalue.RandomValueFactory;
import net.finmath.aadexperiments.value.ConvertableToFloatingPoint;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.automaticdifferentiation.RandomVariableDifferentiable;

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
			return testRandomvalue(solution);
		case 2:
			return testRandomDifferentiableValue(solution);
		case 3:
			return testAssigmentDigitalCaplet(solution);
		case 4:
			return testAssigmentForwardInArrears(solution);
		case 5:
		{
			Result result1 = testAssigmentDigitalCapletDelta(solution);
			Result result2 = testAssigmentForwardInArrearsDelta(solution);
			return new Result(result1.success && result2.success, result1.message + "\n" + result2.message);
		}
		default:
			return new Result(false, "Your solution may or may not be correct. Tests are not implemented yet.\n");
		}
	}

	public Result testRandomvalue(Assignment2 solution) {
		double[] values = new double[] { 5.0, 1.0, 3.0, 4.0, 1.0, 0.0, -1.0, 2.0, -3.0 };
		
		var randomValue = solution.getRandomValueFromArray(values);
		
		var randomValueAbs = randomValue.squared().sqrt().expectation();
		
		if(!(randomValueAbs instanceof ConvertableToFloatingPoint)) {
			return new Result(false, "Test of getRandomValueFromArray: Object does not implement ConvertableToFloatingPoint. Your implementation should also implement ConvertableToFloatingPoint, "
					+ "at least for object that are the result of expectation(). We need this to check"
					+ "your results.");
		}

		if(Math.abs(((ConvertableToFloatingPoint)randomValueAbs).asFloatingPoint() - (new RandomVariableFromDoubleArray(0.0, values).squared().sqrt().average().doubleValue())) > 1E-10) {
			return new Result(false, "Test of getRandomValueFromArray: Implementation of .squared().sqrt().expecation() appears to be inaccurate.");
		}
		
		return new Result(true, "Test of getRandomValueFromArray: We have checked some implementation, but not everything. Looks OK so far.");
	}

	public Result testRandomDifferentiableValue(Assignment2 solution) {
		double[] values = new double[] { 5.0, 1.0, 3.0, 4.0, 1.0, 0.0, -1.0, 2.0, -3.0 };
		
		var randomValue = solution.getRandomDifferentiableValueFromArray(values);

		if(!(randomValue instanceof RandomValueDifferentiable)) {
			return new Result(false, "Test of getRandomDifferentiableValueFromArray: Object does not implement RandomValueDifferentiable.");
		}

		var y = randomValue.squared();
		
		if(!(y instanceof RandomValueDifferentiable)) {
			return new Result(false, "Test of getRandomDifferentiableValueFromArray: Object returned by squared() does not implement RandomValueDifferentiable.");
		}

		var dydx = ((RandomValueDifferentiable)y).getDerivativeWithRespectTo((RandomValueDifferentiable)randomValue);
		
		if(Math.abs(
				((ConvertableToFloatingPoint)(dydx.expectation())).asFloatingPoint() - 
				((ConvertableToFloatingPoint)(randomValue.mult(2.0).expectation())).asFloatingPoint()) > 1E-10) {
			return new Result(false, "Test of getRandomDifferentiableValueFromArray: Derivative of squared() look not correct.");
		}
		
		return new Result(true, "Test of getRandomDifferentiableValueFromArray: We have checked some implementation, but not everything. Looks OK so far.");
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

	public Result testAssigmentDigitalCapletDelta(Assignment2 solution) {

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

		RandomValue normal = solution.getRandomDifferentiableValueFromArray(samples);
		RandomValueFactory randomValueFactory = normal.getFactory();

		RandomValue forwardRate = randomValueFactory.fromConstant(modelForwardRate);
		RandomValue payoffUnit = randomValueFactory.fromConstant(modelPayoffUnit);
		RandomValue volatility = randomValueFactory.fromConstant(modelVolatility);
		RandomValue strike = randomValueFactory.fromConstant(productStrike);
		RandomValue maturity = randomValueFactory.fromConstant(productMaturity);
		RandomValue periodLength = randomValueFactory.fromConstant(productPeriodLength);

		RandomValue brownianMotionUponMaturity = normal.mult(maturity.sqrt());

		RandomValue delta = solution.getMonteCarloBlackModelDeltaOfDigitalCaplet(forwardRate, payoffUnit, volatility, brownianMotionUponMaturity, strike, maturity, periodLength);

		double deltaMonteCarlo = ((ConvertableToFloatingPoint)delta).asFloatingPoint();
		double delataAnalytic = AnalyticFormulas.blackModelDigitalCapletDelta(modelForwardRate, modelVolatility, productPeriodLength, modelPayoffUnit, productMaturity, productStrike);

		boolean success = Math.abs(delataAnalytic-deltaMonteCarlo) < 1E-1;
		String message = "Test of getMonteCarloBlackModelDeltaOfDigitalCaplet: ";
		if(success) message += "Congratulation! The delta of the digital caplet appears to be correct.";
		else {
			message += "Sorry, the delta of the digital caplet appears to be not correct.\n";
			message += "  Expected: " + delataAnalytic + "\n";
			message += "  Actual..: " + deltaMonteCarlo + "\n";
		}

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
		if(success) message += "Congratulation! The valuation of the Forward Rate In Arrears appears to be correct.";
		else {
			message += "Sorry, the valuation of the Forward Rate In Arrears appears to be not correct.\n";
			message += "  Expected: " + valueAnalytic + "\n";
			message += "  Actual..: " + valueMonteCarlo + "\n";
		}

		message += "\n";

		return new Result(success,message);
	}

	private Result testAssigmentForwardInArrearsDelta(Assignment2 solution) {
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

		RandomValue normal = solution.getRandomDifferentiableValueFromArray(samples);
		RandomValueFactory randomValueFactory = normal.getFactory();

		RandomValue forwardRate = randomValueFactory.fromConstant(modelForwardRate);
		RandomValue payoffUnit = randomValueFactory.fromConstant(modelPayoffUnit);
		RandomValue volatility = randomValueFactory.fromConstant(modelVolatility);
		RandomValue maturity = randomValueFactory.fromConstant(productMaturity);
		RandomValue periodLength = randomValueFactory.fromConstant(productPeriodLength);

		RandomValue brownianMotionUponMaturity = normal.mult(maturity.sqrt());

		RandomValue delta = solution.getMonteCarloBlackModelDeltaOfForwardRateInArrears(forwardRate, payoffUnit, volatility, brownianMotionUponMaturity, maturity, periodLength);

		double deltaMonteCarlo = ((ConvertableToFloatingPoint)delta).asFloatingPoint();
		double delataAnalytic = productPeriodLength * modelPayoffUnit * (1 + 2*modelForwardRate * productPeriodLength * Math.exp(modelVolatility*modelVolatility*(productMaturity+productPeriodLength)));

		boolean success = Math.abs(delataAnalytic-deltaMonteCarlo) < 1E-1;
		String message = "Test of getMonteCarloBlackModelDeltaOfForwardRateInArrears: ";
		if(success) message += "Congratulation! The delta of the Forward Rate In Arrears appears to be correct.";
		else {
			message += "Sorry, the delta of the Forward Rate In Arrears appears to be not correct.\n";
			message += "  Expected: " + delataAnalytic + "\n";
			message += "  Actual..: " + deltaMonteCarlo + "\n";
		}

		message += "\n";

		return new Result(success,message);
	}
}
