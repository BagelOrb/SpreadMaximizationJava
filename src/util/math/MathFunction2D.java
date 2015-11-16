package util.math;


public abstract class MathFunction2D {

	public abstract double apply(double arg1, double arg2);
	
	public static final MathFunction2D addition = new MathFunction2D() {
		@Override public double apply(double arg1, double arg2) {
			return arg1+arg2;
		}} ; 
		public static final MathFunction2D subtraction = new MathFunction2D() {
			@Override public double apply(double arg1, double arg2) {
				return arg1-arg2;
			}} ; 
	public static final MathFunction2D multiplication = new MathFunction2D() {
		@Override public double apply(double arg1, double arg2) {
			return arg1*arg2;
		}} ; 
	public static final MathFunction2D max = new MathFunction2D() {
		@Override public double apply(double arg1, double arg2) {
			return Math.max(arg1, arg2);
		}} ; 
	public static final MathFunction2D min = new MathFunction2D() {
		@Override public double apply(double arg1, double arg2) {
			return Math.min(arg1, arg2);
		}} ;
	public static final MathFunction2D squaredAddition = new MathFunction2D() {
		
		@Override
		public double apply(double arg1, double arg2) {
			return arg1*arg1+arg2;
		}
	};
		
	public MathFunction2D compose(final MathFunction1D applyFirst) {
		final MathFunction2D thiss = this;
		return new MathFunction2D(){
			@Override
			public double apply(double arg1, double arg2) {
				return thiss.apply(applyFirst.apply(arg1), applyFirst.apply(arg2));
			}};
	}
}

