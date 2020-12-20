package net.finmath.aadexperiments.randomvalue;

import net.finmath.aadexperiments.value.Value;

public interface ConvertableToValueArray {

	/**
	 * Returns the floating point array value of this object
	 *
	 * @return Floating point array value represented by this object
	 */
	Value[] asValueArray();

}
