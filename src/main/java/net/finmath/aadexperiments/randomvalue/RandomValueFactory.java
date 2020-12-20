package net.finmath.aadexperiments.randomvalue;

public interface RandomValueFactory {
	
	RandomValue zero();
	
	RandomValue one();
	
	RandomValue fromConstant(double constant);

	RandomValue fromArray(double[] values);

}