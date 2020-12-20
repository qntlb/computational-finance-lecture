/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 08.11.2020
 */
package net.finmath.aadexperiments.randomvalue;

public interface RandomValue {

	/**
	 * Returns a random variable factory that allows to generate new object of the same type as the one implementing this interface.
	 *
	 * @return An object implementing RandomValueFactory
	 */
	RandomValueFactory getFactory();

	/**
	 * Returns the random variable representing the expectation of this random variable.
	 *
	 * @return New object representing the result.
	 */
	RandomValue expectation();

	/**
	 * Returns the random variable representing the variance of this random variable.
	 *
	 * @return New object representing the result.
	 */
	default RandomValue variance() {
		return this.sub(this.expectation()).squared().expectation();
	}

	/**
	 * Returns the random variable representing the constant 1/sqrt(n) sigma with sigma = x.sub(x.expectation()).squared().expectation().sqrt()
	 *
	 * @return New object representing the result.
	 */
	RandomValue sampleError();

	/**
	 * Applies x*x to this object x.
	 *
	 * @return New object representing the result.
	 */
	RandomValue squared();

	/**
	 * Applies sqrt(x) to this object x.
	 *
	 * @return New object representing the result.
	 */
	RandomValue sqrt();

	/**
	 * Applies exp(x) to this object x.
	 *
	 * @return New object representing the result.
	 */
	RandomValue exp();

	/**
	 * Applies log(x) to this object x.
	 *
	 * @return New object representing the result.
	 */
	RandomValue log();

	/**
	 * Applies a+x to this object a.
	 *
	 * @param x The value x - the value to add.
	 * @return New object representing the result.
	 */
	RandomValue add(double x);

	/**
	 * Applies a+x to this object a.
	 *
	 * @param x The value x - the value to add.
	 * @return New object representing the result.
	 */
	RandomValue add(RandomValue x);

	/**
	 * Applies a-x to this object a.
	 *
	 * @param x The value x - the value to substract.
	 * @return New object representing the result.
	 */
	RandomValue sub(RandomValue x);

	/**
	 * Applies a*x to this object a.
	 *
	 * @param x The value x - the factor of this multiplication.
	 * @return New object representing the result.
	 */
	RandomValue mult(double x);

	/**
	 * Applies a*x to this object a.
	 *
	 * @param x The value x - the factor of this multiplication.
	 * @return New object representing the result.
	 */
	RandomValue mult(RandomValue x);

	/**
	 * Applies a/x to this object a.
	 *
	 * @param x The value x - the denominator of the division.
	 * @return New object representing the result.
	 */
	RandomValue div(RandomValue x);

	/**
	 * Applies trigger >= 0 ? valueIfNonNegative : valueIfNegative to this object, where trigger = this object.
	 * 
	 * @param valueIfNonNegative The value if this value is positive or zero.
	 * @param valueIfNegative The vlaue is this value is negative.
	 * @return New object representing the result.
	 */
	RandomValue choose(RandomValue valueIfNonNegative, RandomValue valueIfNegative);
}