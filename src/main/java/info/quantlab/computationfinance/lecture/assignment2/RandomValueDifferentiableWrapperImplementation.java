package info.quantlab.computationfinance.lecture.assignment2;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import net.finmath.aadexperiments.randomvalue.ConvertableToFloatingPointArray;
import net.finmath.aadexperiments.randomvalue.RandomValue;
import net.finmath.aadexperiments.randomvalue.RandomValueDifferentiable;
import net.finmath.aadexperiments.randomvalue.RandomValueFactory;
import net.finmath.aadexperiments.value.ConvertableToFloatingPoint;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.automaticdifferentiation.RandomVariableDifferentiable;
import net.finmath.montecarlo.automaticdifferentiation.backward.RandomVariableDifferentiableAAD;
import net.finmath.stochastic.RandomVariable;

public class RandomValueDifferentiableWrapperImplementation implements RandomValueDifferentiable, ConvertableToFloatingPoint, ConvertableToFloatingPointArray {

	private RandomVariable value;

	RandomValueDifferentiableWrapperImplementation(double constant) {
		value = new RandomVariableDifferentiableAAD(new RandomVariableFromDoubleArray(0.0, constant));
	}

	RandomValueDifferentiableWrapperImplementation(double[] samples) {
		if(samples.length == 1) {
			value = new RandomVariableDifferentiableAAD(new RandomVariableFromDoubleArray(0.0, samples[0]));
		}
		else {
			value = new RandomVariableDifferentiableAAD(new RandomVariableFromDoubleArray(0.0, samples));
		}
	}

	private RandomValueDifferentiableWrapperImplementation(RandomVariable value) {
		this.value = value;
	}

	public RandomValueDifferentiableWrapperImplementation(RandomValue value) {
		if(value instanceof ConvertableToFloatingPointArray) {
			double[] samples = ((ConvertableToFloatingPointArray) value).asFloatingPointArray();
			if(samples.length == 1) {
				this.value = new RandomVariableDifferentiableAAD(new RandomVariableFromDoubleArray(0.0, samples[0]));
			}
			else {
				this.value = new RandomVariableDifferentiableAAD(new RandomVariableFromDoubleArray(0.0, samples));
			}
		}
		else {
			throw new IllegalArgumentException("Unable to wrap value in a differentiable object. (Another implementation would maybe allow it).");
		}
	}

	@Override
	public RandomValueFactory getFactory() {
		return new RandomValueFactory() {

			@Override
			public RandomValue zero() {
				return new RandomValueDifferentiableWrapperImplementation(0.0);
			}

			@Override
			public RandomValue one() {
				return new RandomValueDifferentiableWrapperImplementation(1.0);
			}

			@Override
			public RandomValue fromConstant(double constant) {
				return new RandomValueDifferentiableWrapperImplementation(constant);
			}

			@Override
			public RandomValue fromArray(double[] values) {
				return new RandomValueDifferentiableWrapperImplementation(values);
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
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.average(), value));
	}


	@Override
	public RandomValue sampleError() {
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.variance().sqrt().mult(Math.sqrt(1.0/x.size())), value));
	}

	@Override
	public RandomValue squared() {
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.squared(), value));
	}

	@Override
	public RandomValue sqrt() {
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.sqrt(), value));
	}

	@Override
	public RandomValue exp() {
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.exp(), value));
	}

	@Override
	public RandomValue log() {
		return new RandomValueDifferentiableWrapperImplementation(apply(x -> x.log(), value));
	}

	@Override
	public RandomValue add(double x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.add(b), value, new RandomVariableFromDoubleArray(x)));
	}

	@Override
	public RandomValue add(RandomValue x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.add(b), value, ((RandomValueDifferentiableWrapperImplementation)x).value));
	}

	@Override
	public RandomValue sub(RandomValue x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.sub(b), value, ((RandomValueDifferentiableWrapperImplementation)x).value));
	}

	@Override
	public RandomValue mult(double x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.mult(b), value, new RandomVariableFromDoubleArray(x)));
	}

	@Override
	public RandomValue mult(RandomValue x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.mult(b), value, ((RandomValueDifferentiableWrapperImplementation)x).value));
	}

	@Override
	public RandomValue div(RandomValue x) {
		return new RandomValueDifferentiableWrapperImplementation(apply((a,b) -> a.div(b), value, ((RandomValueDifferentiableWrapperImplementation)x).value));
	}

	@Override
	public RandomValue choose(RandomValue valueIfNonNegative, RandomValue valueIfNegative) {
		return new RandomValueDifferentiableWrapperImplementation(this.value.choose(((RandomValueDifferentiableWrapperImplementation)valueIfNonNegative).value, ((RandomValueDifferentiableWrapperImplementation)valueIfNegative).value));
	}

	@Override
	public Double asFloatingPoint() {
		return value.doubleValue();
	}

	@Override
	public double[] asFloatingPointArray() {
		return value.getRealizations();
	}

	@Override
	public RandomValue getDerivativeWithRespectTo(RandomValueDifferentiable x) {
		return new RandomValueWrapperImplementation(((RandomVariableDifferentiable)value).getGradient().get(
				((RandomVariableDifferentiable)
						((RandomValueDifferentiableWrapperImplementation)x).value).getID()));
	}
}
