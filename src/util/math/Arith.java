package util.math;

import network.analysis.Debug;

public class Arith {

	
	public static double min(double... args) {
		double min = Double.POSITIVE_INFINITY;
		for (double a : args)
			min = Math.min(min, a);
		return min;
	}
	
	public static double max(double... args) {
		double max = Double.NEGATIVE_INFINITY;
		for (double a : args)
			max = Math.max(max, a);
		return max;
	}
	
	public static double argMax(MathFunction f, double[]... args) {
		double max = Double.NEGATIVE_INFINITY;
		double arg_max = Double.NaN;
		for (double[] a : args)
			if (f.apply(a)>max) {max = f.apply(a); arg_max=a[0]; }
		return arg_max;
		
	}
	
	public static double boven(int m, int n) { return (factorial(m))/((double)factorial(n)*factorial(m));}
	
	public static int factorial(int x) { 
		int ret = 1;
		for (int i = 2; i<x; i++)
			ret *=i;
		return ret*x;
	} 
	public static double factorial(double x) { 
		double ret = 1;
		for (double i = 2; i<x; i++)
			ret *=i;
		return ret*x;
	} 

	public static void main(String[] args) {
        Debug.out(factorial(20.));
	}

	public static double factorialApprox(double n) {
		//if (n<=1) return 1;
		return Math.sqrt(2*Math.PI*n)*Math.pow(n/Math.E, n);
	}
}
