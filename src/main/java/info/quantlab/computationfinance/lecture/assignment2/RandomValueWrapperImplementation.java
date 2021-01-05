package info.quantlab.computationfinance.lecture.assignment2;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import net.finmath.aadexperiments.randomvalue.ConvertableToFloatingPointArray;
import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueFactory;
import net.finmath.aadexperiments.value.ConvertableToFloatingPoint;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.stochastic.RandomVariable;

public class RandomValueWrapperImplementation implements RandomValue, ConvertableToFloatingPoint, ConvertableToFloatingPointArray {

	private RandomVariable value;

	RandomValueWrapperImplementation(double constant) {
		value = new RandomVariableFromDoubleArray(0.0, constant);
	}

	RandomValueWrapperImplementation(double[] samples) {
		if(samples.length == 1) {
			value = new RandomVariableFromDoubleArray(0.0, samples[0]);
		}
		else {
			value = new RandomVariableFromDoubleArray(0.0, samples);
		}
	}
	
	RandomValueWrapperImplementation(RandomVariable value) {
		this.value = value;
	}

	@Override
	public RandomValueFactory getFactory() {
		return new RandomValueFactory() {
			
			@Override
			public RandomValue zero() {
				return new RandomValueWrapperImplementation(0.0);
			}
			
			@Override
			public RandomValue one() {
				return new RandomValueWrapperImplementation(1.0);
			}
			
			@Override
			public RandomValue fromConstant(double constant) {
				return new RandomValueWrapperImplementation(constant);
			}
			
			@Override
			public RandomValue fromArray(double[] values) {
				return new RandomValueWrapperImplementation(values);
			}
		};
	}

	private RandomVariable apply(UnaryOperator<RandomVariable> function, RandomVariable arg1) {
		return function.apply(arg1);
	}

	private RandomVariable apply(BinaryOperator<RandomVariable> function, RandomVariable arg1, RandomVariable arg2) {
		return function.apply(arg1, arg2);
	}

	@Override
	public RandomValue expectation() {
		return new RandomValueWrapperImplementation(apply(x -> x.average(), value));
	}


	@Override
	public RandomValue sampleError() {
		return new RandomValueWrapperImplementation(apply(x -> x.variance().sqrt().mult(Math.sqrt(1.0/x.size())), value));
	}

	@Override
	public RandomValue squared() {
		return new RandomValueWrapperImplementation(apply(x -> x.squared(), value));
	}

	@Override
	public RandomValue sqrt() {
		return new RandomValueWrapperImplementation(apply(x -> x.sqrt(), value));
	}

	@Override
	public RandomValue exp() {
		return new RandomValueWrapperImplementation(apply(x -> x.exp(), value));
	}

	@Override
	public RandomValue log() {
		return new RandomValueWrapperImplementation(apply(x -> x.log(), value));
	}

	@Override
	public RandomValue add(double x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.add(b), value, new RandomVariableFromDoubleArray(x)));
	}

	@Override
	public RandomValue add(RandomValue x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.add(b), value, ((RandomValueWrapperImplementation)x).value));
	}

	@Override
	public RandomValue sub(RandomValue x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.sub(b), value, ((RandomValueWrapperImplementation)x).value));
	}

	@Override
	public RandomValue mult(double x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.mult(b), value, new RandomVariableFromDoubleArray(x)));
	}

	@Override
	public RandomValue mult(RandomValue x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.mult(b), value, ((RandomValueWrapperImplementation)x).value));
	}

	@Override
	public RandomValue div(RandomValue x) {
		return new RandomValueWrapperImplementation(apply((a,b) -> a.div(b), value, ((RandomValueWrapperImplementation)x).value));
	}

	@Override
	public RandomValue choose(RandomValue valueIfNonNegative, RandomValue valueIfNegative) {
		return new RandomValueWrapperImplementation(this.value.choose(((RandomValueWrapperImplementation)valueIfNonNegative).value, ((RandomValueWrapperImplementation)valueIfNegative).value));
	}

	@Override
	public Double asFloatingPoint() {
		return value.doubleValue();
	}

	@Override
	public double[] asFloatingPointArray() {
		return value.getRealizations();
	}
}
