/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 08.11.2020
 */
package net.finmath.aadexperiments.randomvalue;

public interface RandomValueDifferentiable extends RandomValue {

	/**
	 * Returns the floating point value of the derivative of this object z with respect to the argument object x.
	 *
	 * @return New object representing the result.
	 */
	RandomValue getDerivativeWithRespectTo(RandomValueDifferentiable x);

}