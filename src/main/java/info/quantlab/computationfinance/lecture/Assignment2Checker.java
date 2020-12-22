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
		case 3:
			return testAssigmentDigitalCaplet(solution);

		default:
			return new Result(false, "Your solution may or may not be correct. Tests are not implemented yet.");
		}
	}

	public Result testAssigmentDigitalCaplet(Assignment2 solution) {

		/*
		 * Create a normal distributed random sample vector
		 */
		// Create normal distributed random variable
		Random random = new Random(3413);
		int numberOfPath = 100000;
		double[] samples = new double[numberOfPath];
		for(int pathIndex=0; pathIndex<numberOfPath; pathIndex++) {
			samples[pathIndex] = random.nextGaussian();
		}

		RandomValue normal = solution.getRandomValueFromArray(samples);
		RandomValueFactory randomValueFactory = normal.getFactory();

		RandomValue forwardRate = randomValueFactory.fromConstant(0.05);
		RandomValue payoffUnit = randomValueFactory.fromConstant(0.9);
		RandomValue volatility = randomValueFactory.fromConstant(0.3);
		RandomValue strike = randomValueFactory.fromConstant(0.06);
		RandomValue maturity = randomValueFactory.fromConstant(2.0);
		RandomValue periodLength = randomValueFactory.fromConstant(0.5);

		RandomValue brownianMotionUponMaturity = normal.mult(maturity.sqrt());

		RandomValue value = solution.getMonteCarloBlackModelValueOfDigitalCaplet(forwardRate, payoffUnit, volatility, brownianMotionUponMaturity, strike, maturity, periodLength);

		double valueMonteCarlo = ((ConvertableToFloatingPoint)value).asFloatingPoint();
		double valueAnalytic = AnalyticFormulas.blackScholesDigitalOptionValue(0.05, 0.0, 0.3, 2.0, 0.06) * 0.9 * 0.5;

		boolean success = Math.abs(valueAnalytic-valueMonteCarlo) < 1E-3;
		String message;
		if(success) message = "Congratulation! The valuation of the digital caplet appears to be correct.";
		else message = "Sorry, the valuation of the digital caplet appears to be not correct.";

		return new Result(success,message);
	}
}
