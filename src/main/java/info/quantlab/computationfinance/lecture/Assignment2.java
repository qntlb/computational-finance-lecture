package info.quantlab.computationfinance.lecture;

import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueDifferentiable;

public interface Assignment2 {

	/**
	 * Returns an object implementing the interface <code>RandomValue</code> representing a random variable.
	 * Important: Objects that represent random variables that are deterministic, e.g. those that are the result of
	 * expectation, variance or sampleError also implement <code>ConvertableToFloatingPoint</code>.
	 * 
	 * Remark: You may create one implementation and let it implement both interfaces, and then throw a suitable exception
	 * if the method asFloatingPoint is called on a non-deterministic random variable.
	 * 
	 * @param values A sample vector.
	 * @return A RandomValue
	 */
	RandomValue getRandomValueFromArray(double[] values);
	
	/**
	 * Returns an object implementing the interface <code>RandomValueDifferentiable</code> representing a random variable.
	 * Important: Object that represent random variables that are deterministic, e.g. those that are the result of
	 * expectation, variance or sampleError also implement <code>ConvertableToFloatingPoint</code>.
	 * 
	 * Remark: You may create one implementation and let it implement both interfaces, and then throw a suitable exception
	 * if the method asFloatingPoint is called on a non-deterministic random variable.
	 * 
	 * @param values A sample vector.
	 * @return A RandomValueDifferentiable
	 */
	RandomValueDifferentiable getRandomDifferentiableValueFromArray(double[] values);

	/**
	 * Returns the RandomValue representing the value of a digital caplet.
	 * 
	 * @param forwardRate The rate L(T1,T2;T0)
	 * @param payoffUnit The zero bond P(T2;T0)
	 * @param volatility The parameter sigma of the Black model
	 * @param brownianMotionUponMaturity The Brownian motion at W(T1) (where W(T0)=0)
	 * @param strike The strike K
	 * @param periodLength The period length T2-T1
	 * @return The expectation representing V(T0)
	 */
	RandomValue getMonteCarloBlackModelValueOfDigitalCaplet(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue strike, RandomValue maturity, RandomValue periodLength);
	
	/**
	 * Returns the RandomValue representing the delta of a digital caplet (using your AAD framework)
	 * 
	 * @param forwardRate The rate L(T1,T2;T0)
	 * @param payoffUnit The zero bond P(T2;T0)
	 * @param volatility The parameter sigma of the Black model
	 * @param brownianMotionUponMaturity The Brownian motion at W(T1) (where W(T0)=0)
	 * @param strike The strike K
	 * @param maturity The maturity T1
	 * @param periodLength The period length T2-T1
	 * @return The expectation representing d V(T0) / d L(T0) 
	 */
	RandomValue getMonteCarloBlackModelDeltaOfDigitalCaplet(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue strike, RandomValue maturity, RandomValue periodLength);

	/**
	 * Returns the RandomValue representing the value of a forward rate in arrears paying L(T1,T2) (T2-T1) in T1.
	 * 
	 * Hint: the value of paying The payoff is L(T1) * (1 + L(T1) periodLength) paid in T2.
	 * 
	 * @param forwardRate The rate L(T1,T2;T0)
	 * @param payoffUnit The zero bond P(T2;T0)
	 * @param volatility The parameter sigma of the Black model
	 * @param brownianMotionUponMaturity The Brownian motion at W(T1) (where W(T0)=0)
	 * @param maturity The maturity T1
	 * @param periodLength T2-T1
	 * @return The expectation representing V(0)
	 */
	RandomValue getMonteCarloBlackModelValueOfForwardRateInArrears(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue maturity, RandomValue periodLength);

	/**
	 * Returns the RandomValue representing the value of a forward rate in arrears paying L(T1,T2) (T2-T1) in T1.
	 * 
	 * Hint: the value of paying The payoff is L(T1) * (1 + L(T1) periodLength) paid in T2.
	 * 
	 * @param forwardRate The rate L(T1,T2;T0)
	 * @param payoffUnit The zero bond P(T2;T0)
	 * @param volatility The parameter sigma of the Black model
	 * @param brownianMotionUponMaturity The Brownian motion at W(T1) (where W(T0)=0)
	 * @param maturity The maturity T1
	 * @param periodLength T2-T1
	 * @return The expectation representing d V(T0) / d L(T0) 
	 */
	RandomValue getMonteCarloBlackModelDeltaOfForwardRateInArrears(RandomValue forwardRate, RandomValue payoffUnit, RandomValue volatility, RandomValue brownianMotionUponMaturity, RandomValue maturity, RandomValue periodLength);
}