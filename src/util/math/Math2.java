package util.math;

import java.lang.reflect.Method;
import java.util.Random;

import network.analysis.Debug;

public class Math2 {
	
	
	
	public static Method tanh;
	static{
		try {
			tanh = Math2.class.getDeclaredMethod("tanh", double.class);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double sigmoid(double in) {
		double ret = 1d/(1d+Math.exp(-in));
		if (Double.isNaN(ret)) ret = 0;
		return ret;
	}
	public static double sigmoidDO(double out) { // given the output of the sigmoid
		return out*(1-out);
	}
	public static double sigmoidD(double d) {
		double eterm = Math.exp(d);
		double ret = eterm / square(1+eterm);
		if (Double.isNaN(ret)) return 0;
		return ret;
//		double out = sigmoid(d);
//		return out*(1-out);
	}

	public static double square(double n) { return n*n; }
	
	public static double heartBeat(double x) {			//  ___  |^\___
		return powNeg(x*5, 1d/3d)*Math.exp(-25*x*x); 	//     \_|
	}		
	public static double powNeg(double x, double y) { return Math.signum(x) *Math.pow(Math.abs(x),y); } // preserves the sign of the base
	public static int limit(int min, int in, int max) { return Math.min(Math.max(min, in),max); }
	public static double limit(double min, double in, double max) { return Math.min(Math.max(min, in),max); }
	public static float limit(float min, float in, float max) { return Math.min(Math.max(min, in),max); }
	
	public static double tanh(double x) {
		double eterm = Math.exp(-2*x);
		double ret = (1-eterm)/(1+eterm);
		if (eterm == -1) return 1;
		if (Double.isNaN(ret)) return -1;
		return ret;
	}
	
	public static double tanhD(double x) {
		double eterm = Math.exp(2*x);
		double ret = (4*eterm)/ ( (1+eterm)*(1+eterm) );
		if (Double.isNaN(ret)) return 0;
		return ret;
	}
	public static double tanhDO(double o) { // output based
		return 1-o*o;
	}
	
	public static double softmax(double out, double[] allOuts) {
		return softmax(out, allOuts, 1);
	}
	public static double softmax(double out, double[] allOuts, double steepness) {
		double denom = 0;
		for (double o : allOuts)
			denom += Math.exp(o*steepness);
		return Math.exp(out*steepness)/ denom;
	}
	
	public static double sech(double x) {
		return 2d/(Math.exp(x)+Math.exp(-x));
	}
	
	public static double plus(double a, double b) {return a+b;}
	
	static final Random rand = new Random();
	public static void main(String[] args) {
		for (int i = 0; i<10000; i++)
            Debug.out(tanh(rand.nextDouble()*1E10-.5E10));
	}
	public static double cosh(double in) {
		return .5*(Math.exp(in) - Math.exp(-in));
	}
	public static double pow(double b, int exp) {
		boolean neg = exp< 0;
		double ret = 1;
		for (int e = Math.abs(exp); e>0; e--)
			ret *=b;
		
		if (neg) 	return 1/ret;
		else 		return ret;
	}
	public static boolean liesWithin(int min, int i, int max) {
		return i<=max && i>= min;
	}
	public static boolean liesWithin(double min, double i, double max) {
		return i<=max && i>= min;
	}
}
