package info.quantlab.computationfinance.lecture;

import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueDifferentiable;

public interface Assignment2 {

	RandomValue getRandomValueFromArray(double[] values);
	
	RandomValueDifferentiable getRandomDifferentiableValueFromArray(double[] values);

	RandomValue getMonteCarloBlackModelValueOfDigitalCaplet(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue strike, RandomValue maturity);
	
	RandomValue getMonteCarloBlackModelDeltaOfDigitalCaplet(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue strike, RandomValue maturity);

	RandomValue getMonteCarloBlackModelValueOfForwardRateInArrears(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue paymentTime);

	RandomValue getMonteCarloBlackModelDeltaOfForwardRateInArrears(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue paymentTime);
}