package probeersels;

import java.util.Arrays;

import network.analysis.Debug;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import util.math.Statistics;

public class MatrixTry {

	public static void main2() {
		double[][] q = new double[][] 
		      { {0,0},
				{0,1},
				{0,1},
				{1,1},
				{1,1},
				{1,0},
	          	{1,0},					
				{0,1},
				{0,1},
				{1,1},
				{1,1},
				{1,0},
	          	{1,0} };

		Covariance w = new Covariance(q);
		RealMatrix x = w.getCovarianceMatrix();
        Debug.out(x);
        Debug.out( new LUDecomposition(x).getSolver().getInverse().scalarMultiply(new LUDecomposition(x).getDeterminant()));
        Debug.out(Arrays.toString(Statistics.means(q)));
        Debug.out(new BlockRealMatrix(q).scalarMultiply(.5).add(new BlockRealMatrix(q).scalarMultiply(.5)));
        Debug.out(new BlockRealMatrix(2,2));
	}
	         
	public static void main(String[] args) {
		main2();

			
	}
}

