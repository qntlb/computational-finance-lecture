package net.finmath.experiments.montecarlo.interestrates;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariableFactory;
import net.finmath.montecarlo.RandomVariableFromArrayFactory;
import net.finmath.montecarlo.interestrate.TermStructureMonteCarloSimulationModel;
import net.finmath.montecarlo.interestrate.products.Bond;
import net.finmath.montecarlo.interestrate.products.TermStructureMonteCarloProduct;

public class TermStructureMonteCarloSimulationExperiments {

	public static void main(String[] args) throws CalculationException {

		testBond();
	}

	private static void testBond() throws CalculationException {

		RandomVariableFactory randomVariableFactory = new RandomVariableFromArrayFactory();
		String measure = "terminal";
		double forwardRate = 0.05;
		double periodLength = 0.5;
		boolean useDiscountCurve = false;
		double volatility = 0.30;
		double localVolNormalityBlend = 0.0;
		double correlationDecayParam = 0.0;
		int numberOfFactors = 1;
		int numberOfPaths = 50000;
		int seed = 3141;

		TermStructureMonteCarloSimulationModel model = ModelFactory.createTermStuctureModel(randomVariableFactory, measure, forwardRate, periodLength, useDiscountCurve, volatility, localVolNormalityBlend, correlationDecayParam, numberOfFactors, numberOfPaths, seed);

		for(double maturity = 0.5; maturity < 20; maturity += 0.5) {
			TermStructureMonteCarloProduct product = new Bond(maturity);

			double value = product.getValue(model);
			double valueAnalytic = 1.0/ Math.pow(1+forwardRate*periodLength, maturity/periodLength);

			double yield = -Math.log(value)/maturity;
			double yieldAnalytic = -Math.log(valueAnalytic)/maturity;
			double error = yield - yieldAnalytic;

			System.out.println(maturity + "\t" + error);
		}
	}

}
