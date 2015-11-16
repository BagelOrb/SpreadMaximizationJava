package util.math;

public abstract class DerivingFunction1D extends DifferentiableFunction {

	@Override public abstract double apply(double arg);


	@Override public double applyD(double in) {
		return getDerivative().apply(in);
	}

	@Override public double applyInverse(double in) {
		return getInverse().apply(in);
	}

	@Override public double applyIntegral(double in) {
		return getIntegral().apply(in);
	}

	@Override public abstract String toString();
	
//	public static DifferentiableFunction fromString(String in) {
//		if (in.equals("tanh")) return tanh;
//		else if (in.equals("tanhAlmost")) return tanhAlmost;
//		else if (in.equals("logisticSigmoid")) return logisticSigmoid;
//		else if (in.equals("none")) return none;
//		else if (in.equals("square")) return square;
//		else return none;
//	}
	
	public abstract MathFunction1D getDerivative();
	public abstract MathFunction1D getIntegral();
//	public abstract MathFunction1D getInverse();
	
	
}
