package net.finmath.aadexperiments.randomvalue;

public interface ConvertableToFloatingPointArray {

	/**
	 * Returns the floating point array value of this object
	 *
	 * @return Floating point array value represented by this object
	 */
	double[] asFloatingPointArray();
}
