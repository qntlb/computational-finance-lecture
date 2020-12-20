package net.finmath.aadexperiments.randomvalue;

public interface RandomValueDifferentiableFactory extends RandomValueFactory {
	
	RandomValueDifferentiable zero();
	
	RandomValueDifferentiable one();
	
	RandomValueDifferentiable fromConstant(double constant);
	
	RandomValueDifferentiable fromArray(double[] values);

}
