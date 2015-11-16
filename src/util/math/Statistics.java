package util.math;

import generics.Tuple;

import java.util.Random;

import network.analysis.Debug;

import org.apache.commons.math3.special.Beta;

import util.basics.DoubleArray2D;

public class Statistics {

	public static int total(int[] re) {
		int ret = 0;
		for (int r : re)
			ret += r;
		return ret;
	}
	
	
	
	/*
	 * Doubles :
	 */
	
	public static double mean(double[] re) {
		return total(re)/re.length;
	}
	public static double total(double[] re) {
		double ret = 0;
		for (double r : re)
			ret += r;
		return ret;
	}
	public static double[] means(double[][] datapoints) {
		double ret[] = new double[datapoints[0].length];
		for (double[] re : datapoints)
			for (int i = 0; i< ret.length; i++)
				ret[i] += re[i];
		for (int i = 0; i<ret.length; i++)
			ret[i] = ret[i] / datapoints.length;
		return ret;
	}
	public static double variance(double[] qs) {
		double ret = 0;
		double mean = mean(qs);
		for (double q:qs) 
			ret += (q-mean)*(q-mean);
		return ret/qs.length;
	}
	public static Tuple<Double, Double> meanVariance(double[] qs) {
		double ret = 0;
		double mean = mean(qs);
		for (double q:qs) 
			ret += (q-mean)*(q-mean);
		return new Tuple<Double, Double>(mean,ret/qs.length);
	}
	@Deprecated public static Tuple<Double, Double> meanVarSinglePass(double[] x) {// is actually slower!
		double mean = 0;
		double var = 0;
		for (int i =0; i<x.length; i++) {
			double delta = x[i] - mean;
			mean += delta/(i+1.);
			var += (x[i]-mean)*delta;
			
		}
		var /= x.length;
		return new Tuple<Double, Double>(mean,var);
	}
	public static double varianceSample(double[] qs) {
		double ret = 0;
		double mean = mean(qs);
		for (double q:qs) 
			ret += (q-mean)*(q-mean);
		return ret/(qs.length-1);
	}
	public static double covariance(double[] qs, double[] ws) {
		double ret = 0;
		double meanQs = mean(qs);
		double meanWs = mean(ws);
		for (int i = 0; i < qs.length; i++) 
			ret += (qs[i]-meanQs)*(ws[i]-meanWs);
		return ret/qs.length;		
	}
	
	public static double[][] covarianceMatrix(double[][] data) {
		DoubleArray2D data2 = new DoubleArray2D(data);
		int k = data2.width;
		int n = data2.height;
		double[] means = means(data);
		double[][] covarMatrix = new double[k][k];
		for (int i = 0; i<k; i++)
			for (int j = i; j<k; j++) {
				for (int q = 0; q<n; q++)
					covarMatrix[j][i] += (data[q][i]-means[i])*(data[q][j]-means[j]);
				covarMatrix[i][j] = covarMatrix[j][i] = covarMatrix[j][i] / n;
			}
		return covarMatrix;
	}

	@SuppressWarnings("unused")
	private static void testcovarianceMatrix() {
        Debug.out(new DoubleArray2D(covarianceMatrix(new double[][] {{0,0},{1,1},{.8,.5}})));
        Debug.out(variance(new double[]{0,1,.8}));
        Debug.out(variance(new double[]{0,1,.5}));
        Debug.out(covariance(new double[]{0,1,.8}, new double[]{0,1,.5}));
	}
	
	public static double correlation(double[] qs, double[] ws) {
		return covariance(qs,ws)/Math.sqrt(variance(qs)*variance(ws));
	}
	
//	public static double[][] covarMatrix(double[][] samples) {
//		int k = Arrays2D.width(samples);
//		double[][] ret = new double[k][k];
//		int n = Arrays2D.height(samples);
////		
////		for (int i = 0; i<samples.length; i++) {
////			
////		}
//		// TODO: efficient computation
//		
//		for (int x = 0; x<k; x++)
//			for (int y = x; y<k; y++) {
//				double cov = 
//			}
//		
//		return ret;
//	}
	
	public static double distance(double one, double two) { return (one-two)*(one-two); }
	
	
	
	
	
	
	
	/*
	 * Floats : 
	 */
	public static double mean(float[] re) {
		return total(re)/re.length;
	}
	public static double total(float[] re) {
		double ret = 0;
		for (double r : re)
			ret += r;
		return ret;
	}
	public static double[] means(float[][] datapoints) {
		double ret[] = new double[datapoints[0].length];
		for (float[] re : datapoints)
			for (int i = 0; i< ret.length; i++)
				ret[i] += re[i];
		for (int i = 0; i<ret.length; i++)
			ret[i] = ret[i] / datapoints.length;
		return ret;
	}
	public static double variance(float[] qs) {
		double ret = 0;
		double mean = mean(qs);
		for (double q:qs) 
			ret += (q-mean)*(q-mean);
		return ret/qs.length;
	}
	public static double covariance(float[] qs, float[] ws) {
		double ret = 0;
		double meanQs = mean(qs);
		double meanWs = mean(ws);
		for (int i = 0; i < qs.length; i++) 
			ret += (qs[i]-meanQs)*(ws[i]-meanWs);
		return ret/qs.length;		
	}
	public static double correlation(float[] qs, float[] ws) {
		return covariance(qs,ws)/Math.sqrt(variance(qs)*variance(ws));
	}
	
//	public static float[][] covarMatrix(float[][] samples) {
//		int k = Arrays2D.width(samples);
//		float[][] ret = new float[k][k];
//		int n = Arrays2D.height(samples);
////		
////		for (int i = 0; i<samples.length; i++) {
////			
////		}
//		// TODO: efficient computation
//		
//		for (int x = 0; x<k; x++)
//			for (int y = x; y<k; y++) {
//				float cov = 
//			}
//		
//		return ret;
//	}
	
	public static float distance(float one, float two) { return (one-two)*(one-two); }
	
	
	
//	@SuppressWarnings("serial")
//	@Deprecated // crappish shite!
//	public static class Random extends java.util.Random{
//		public Boolean nextBoolean(double p) { return nextDouble()<p;}
//
//		public int nextBinary(double d) {
//			// TODO Auto-generated method stub
//			return nextBoolean(d)? 1 : 0;
//		} 
//	}

	
	
	public static MathFunction1D betaDistribution(final double a, final double b) {
		double logBeta = Beta.logBeta(a, b);
		final double beta = Math.exp(-logBeta);
//        Debug.out("logBeta: "+logBeta);
//        Debug.out("beta: "+beta);
		return new MathFunction1D() {
			@Override public double apply(double x) {
				return beta * Math.pow(x, a-1) * Math.pow(1-x, b-1);
			}
		};
		
	}
	
	/**
	 * @param m the mean of the distribution
	 * @param stdDev the standard deviation of the distribution
	 * @return the pdf for the logistic distribution
	 */
	public static DerivingFunction1D logisticDistribution(final double m, final double stdDev) {
		final double s = Math.sqrt(3)/Math.PI*stdDev;
		final double ms = m/s/2;
		final double is = 1/s;
		return new DerivingFunction1D() {
			@Override public double apply(double x) {
				return .25*is*Math2.sech(x*is-ms);
			}

			@Override
			public String toString() {
				return "logisticDistribution";
			}

			@Override @Deprecated
			public MathFunction1D getDerivative() {
				return null; // 1/(4s^2) * tanh((m-x)/(2s)) * sech((m-x)/(2s))^2
			}

			@Override
			public MathFunction1D getIntegral() {
				final DerivingFunction1D thiss = this;
				return new DifferentiableFunction() {
					
					@Override
					public double apply(double arg) {
						return 1/(1+Math.exp(-(arg-m)*is));
					}
					
					@Override
					public String toString() {
						return "f{"+thiss.toString()+"}";
					}
					
					@Override
					public double applyInverse(double in) {
						return m-s * Math.log(1/in-1);
					}
					
					@Override  @Deprecated
					public double applyIntegral(double in) {
						return Double.NaN;
					}
					
					@Override @Deprecated
					public double applyDO(double in) {
						return Double.NaN;
					}
					
					@Override
					public double applyD(double in) {
						return thiss.apply(in);
					}

					@Override
					public DifferentiableFunction getInverse() {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}


			@Override @Deprecated
			public double applyDO(double in) {
				return Double.NaN;
			}

			@Override
			public DifferentiableFunction getInverse() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	public static double sampleFromDistribution(DerivingFunction1D distr, Random... r) {
		Random rand;
		if (r.length>0) rand = r[0];
		else rand = new Random();
		
		double rd = rand.nextDouble();
		
		return ((DifferentiableFunction) distr.getIntegral()).applyInverse(rd);
		
	}
	
	public static void testlogisticDistribution() {
		
		DerivingFunction1D dist = logisticDistribution(0,1);
		
		for (int i =0; i<50; i++)
            Debug.out(sampleFromDistribution(dist));
	}
//	public double beta(double x, double y) {
//		double n = 0;
//		double answer = 0;
//		while() {
//			answer +=  
//			n++;
//		}
//	}





	
	
}
