package objective;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.analysis.Debug;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import util.basics.DoubleArray2D;
import util.math.DifferentiableFunction;
import util.math.Statistics;


public class GaussUniformizationObjective  extends CovarObjective {
	private RealMatrix s0;
	private RealMatrix s1inv;
	private RealVector m1;
	private RealMatrix meansD;
	

	
//	static final double optimalStDev = 1.4;
////	static final double optimalStDev = 1.7488009035459489;
//	static final double optimalVar = optimalStDev*optimalStDev;
	
	private double optimalStdDev_logisticSigmoid = 1.8137993369195464;
	private double optimalStdDev_tanh 			= optimalStdDev_logisticSigmoid/2;

	private void outputObjective() {
		Random r = new Random(124);
		int n = 10000;
		double[][] data = new double[n][getDimensionality()];
		for (int i = 0; i<n; i++) {
			double x = r.nextGaussian()*optimalVar;
			double y = r.nextGaussian()*optimalVar;
			data[i][0] = x; data[i][1] = y;
		}
		
		String out = "";
		for (double[] ds : data) 
			out += (ds[0]+" ;\t"+ds[1]+ "\r\n");
		
		try {
			FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataObjective1.7...csv"), out);
		} catch (IOException e) { e.printStackTrace(); }
		
		out = "";
		DifferentiableFunction s = DifferentiableFunction.logisticSigmoid;
		for (double[] ds : data) 
			out += (s.apply(ds[0])+" ;\t"+s.apply(ds[1])+ "\r\n");

		try {
			FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataObjectiveTransformed1.7...csv"), out);
		} catch (IOException e) { e.printStackTrace(); }

	}
	
	@SuppressWarnings("unused")
	private void test() {
		boolean outputResults = true;

		Random r = new Random(); // 124
		int n = 1000;
		
		
		boolean rProp = true;
		
		double d = 1E-0;
		
		final double etaPlus = 1.2;
		final double etaMin = .5;
		double rPropInit = 1E-3;
	
		int nIterations;
		if (rProp) nIterations = 100;
		else		nIterations = 100000;
		

		
		double[][] data = new double[n][getDimensionality()];
		double correlation = .5;
		double varx = 5;
		double vary = .5;
		MultivariateNormalDistribution dist = new MultivariateNormalDistribution(new double[]{4,-4}, new double[][]{{varx,correlation*varx*vary},{correlation*varx*vary,vary}});
		for (int i = 0; i<n; i++) {
			double x,y;
			double[] xy = dist.sample();
			x = xy[0];
			y=xy[1];
			
//			x = r.nextGaussian()*1 +0;
//			y = r.nextGaussian()*1*.5+.5*x -0;
			
//			if (i<n/2) {
//				x = r.nextGaussian()*.01 +.4;
//				y = r.nextGaussian()*.005+.5*x -.4;
//			} else {
//				x = r.nextGaussian()*.1 -.4;
//				y = r.nextGaussian()*.025-.75*x +.4;
//			}
			data[i][0] = x; data[i][1] = y;
		}
		covarMatrix = new BlockRealMatrix(Statistics.covarianceMatrix(data));
		
		if (outputResults) {
			
			String out = "";
			for (double[] ds : data) 
				out += (ds[0]+" ;\t"+ds[1]+ "\r\n");
			
			try {
				FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataInit.csv"), out);
			} catch (IOException e) { e.printStackTrace(); }
			
			out = "";
			DifferentiableFunction s = DifferentiableFunction.logisticSigmoid;
			for (double[] ds : data) 
				out += (s.apply(ds[0])+" ;\t"+s.apply(ds[1])+ "\r\n");

			try {
				FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataInitTransformed.csv"), out);
			} catch (IOException e) { e.printStackTrace(); }

		}
		
        Debug.out("s0:          \t"+ s0);
        Debug.out("covarMatrix: \t"+covarMatrix);
		totals = Statistics.means(data);
        Debug.out("means: "+ Arrays.toString(totals));
	
		double[][] deltas = new double[n][getDimensionality()];
		new DoubleArray2D(deltas).fill(rPropInit );
		double[][] lastDerivatives = new double[n][getDimensionality()];

		for (int it = 0; it<nIterations; it++) {
			this.covarMatrix = new BlockRealMatrix(Statistics.covarianceMatrix(data));
			RealMatrix s1 = covarMatrix;
			totals = Statistics.means(data);
			m1 = new ArrayRealVector(totals);
			s1inv = new LUDecomposition(s1).getSolver().getInverse();
			
			meansD = computeMuDerivatives();
			covarMatrixDerivative = computeCovarMatrixD();
			
			if (it%(Math.max(1, nIterations/10))==0) {
                Debug.out(">>>>> \t\t\t " +(it/Math.max(1, nIterations/100))+ "%");
                Debug.out("================ covarMatrix:");
                Debug.out(covarMatrixDerivative);
				double valBef = lastValue;
				covarMatrix.addToEntry(0, 0, d);
				double valAft = lastValue;
                Debug.out("estimatedDer="+(valAft-valBef)/d);
				covarMatrix.addToEntry(0, 0, -d); // = undoing
				
                Debug.out("================ means:");
                Debug.out(meansD);
				totals[0] += d;
				valAft = lastValue;
                Debug.out("estimatedDer="+(valAft-valBef)/d);
				totals[0] -= d;
			}
            if (it%(Math.max(1, nIterations/100))==0) Debug.out("val="+lastValue);
			
			
			for (int i = 0; i<n; i++) 
				for (int kk=0; kk<getDimensionality(); kk++) {
					double der = 0;
					for (int f = 0; f<getDimensionality(); f++) { 
							der += 2 * 	// rows and columns are equal -> symmetric.. The der of the var of i is 2*... , but is only present once in the covar matrix
								covarMatrixDerivative.getEntry(kk, f)
				            *  (data[i][f] - totals[f]); //((params.batchLearning)? means[f] : features[f].outputMean)); 
					}
					der = 1d/(n)* der; // as is code in CovarObjective
					

					der += 1d/(n)* meansD.getEntry(kk,0); // means derivative
					Debug.checkNaN(der);
//					if(it==0) deltas[i][kk] = der*d;
					
					if (der*lastDerivatives[i][kk] > 0) Math.min(deltas[i][kk] *= etaPlus, 1);
					else if (der*lastDerivatives[i][kk] < 0) deltas[i][kk] *= etaMin;
					lastDerivatives[i][kk] = der;
					if (rProp) 	data[i][kk] += deltas[i][kk] * Math.signum(der) ; 
					else 		data[i][kk] += der*d ; 
				}
		}
		
        Debug.out("s0:          \t"+ s0);
        Debug.out("covarMatrix: \t"+covarMatrix);
		
        Debug.out("means: "+ Arrays.toString(totals));
		
		if (outputResults) {
			String out = "";
			for (double[] ds : data) 
				out += (ds[0]+" ;\t"+ds[1]+ "\r\n");
			
			try {
				FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataAfter.csv"), out);
			} catch (IOException e) { e.printStackTrace(); }
			
			out = "";
			DifferentiableFunction s = DifferentiableFunction.logisticSigmoid;
			for (double[] ds : data) 
				out += (s.apply(ds[0])+" ;\t"+s.apply(ds[1])+ "\r\n");

			try {
				FileUtils.writeStringToFile(new File("C:\\Users\\TK\\Documents\\TAI\\Thesis\\Thesis Chapters\\Uniformification\\R\\dataAfterTransformed.csv"), out);
			} catch (IOException e) { e.printStackTrace(); }
			
		}

	}

	public GaussUniformizationObjective(Network nw) {
		super(nw, true);

	}
	
//	public static void main(String[] args) {
//		new GaussUniformizationObjective().outputObjective();
////		new GaussUniformizationObjective().test();
//	}
//	@Deprecated private GaussUniformizationObjective() { // for testing only!
//		super(2, true);
////		setDimensionality(2);
//		covarMatrix = new BlockRealMatrix(getDimensionality(), getDimensionality());
//		
//		double[][] s0re = new double[getDimensionality()][getDimensionality()];
//		for (int i = 0; i<getDimensionality(); i++)
//			s0re[i][i] = optimalVar;
//		s0 = new BlockRealMatrix(s0re);
//	}

	@Override
    double computeValue(NetworkState[] states) {
		RealMatrix s1 = covarMatrix;
		m1 = new ArrayRealVector(totals);
		
		setS0();
		
		int k = s0.getColumnDimension();
		RealMatrix m = asColumnMatrix(m1);
		RealMatrix s1inv = new LUDecomposition(s1).getSolver().getInverse();
		double first = s1inv.multiply(s0).getTrace() ;
		double mid = m.transpose().multiply(s1inv).multiply(m).getEntry(0, 0);
		double last = Math.log(new LUDecomposition(s0).getDeterminant() / new LUDecomposition(s1).getDeterminant() );
		return - .5* (first + mid - k - last   );
	}

	private void setS0() {
		if (s0 != null)
			return;
		
		double[][] s0re = new double[getDimensionality()][getDimensionality()];
		for (int i = 0; i<getDimensionality(); i++)
			s0re[i][i] = optimalVar;
		s0 = new BlockRealMatrix(s0re);
	}

	static RealMatrix asColumnMatrix(RealVector v) {
		RealMatrix ret = new BlockRealMatrix(v.getDimension(), 1);
		ret.setColumn(0, v.toArray());
		return ret;
	}
	
	@Override
	public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		double covarD = getSampleCovarMatrixDerivativeForFeature(feature, xOutput, yOutput, state);
		double meansDsample = 1. / nOutputs * meansD.getEntry(feature,0) ;
		return covarD+meansDsample;
	}
	
	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
		updateCovarMatrixBatch(network, states);
		RealMatrix s1 = covarMatrix;
		m1 = new ArrayRealVector(totals);
		s1inv = new LUDecomposition(s1).getSolver().getInverse();
		
		meansD = computeMuDerivatives();
		covarMatrixDerivative = computeCovarMatrixD();
		
		
	}
	public RealMatrix computeMuDerivatives() {
		// 2 S m
		RealMatrix ret = s1inv.multiply(asColumnMatrix(m1));
		
//		// 2 (S m)^T = m^T S
//		RealMatrix ret = asColumnMatrix(m1).transpose().multiply(s1inv);
		
		return ret.scalarMultiply(-1); // times -1 for minimization instead of maximization
	}
	public RealMatrix computeCovarMatrixD() {
		setS0();
		return s1inv.multiply(	MatrixUtils.createRealIdentityMatrix(getDimensionality())
				.subtract(s0.add(m1.outerProduct(m1))
						.multiply(s1inv)))				.scalarMultiply(-.5); // .5 times -1 for minimization instead of maximization

	}
	
	public String toString() {
		return "-(Kullback-Leibler Divergence) = "+lastValue;
	}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		return;
	}
}
