package objective;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.Static;
import network.analysis.Debug;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import util.basics.DoubleArrays;
import util.basics.DoubleArrays2D;

public class DecorrelationObjective extends CovarObjective {
	
	public DecorrelationObjective(Network nw) { 
		super(nw);
		}
	/**
	 * The determinant of the covariance matrix
	 */
	public double matrixDeterminant;
	/**
	 * @return the determinant of the {@link Network#objective.covarMatrix}
	 */
	double getDeterminant() {
		return new LUDecomposition(covarMatrix).getDeterminant();
	}

	/**
	 * @return The derivative of the determinant of the covariance matrix of the outputs.
	 */
	private RealMatrix getCovarMatrixD() {
//	    if (network.netParams.learningMechanismType == Static.LearningMechanismType.RPROP_AT_WEIGHTS && Debug.higherRpropForQuickness) 
//			return new LUDecomposition(covarMatrix).getSolver().getInverse(); //.transpose() not necessary; // scalar mult not neccesary
		if (matrixDeterminant==0) {
			Debug.out(">>>>>>>>>>>>>>> Determinant = "+getDeterminant()+" = 0!");
//			for (State state : (ArrayList<? extends State>) ((NeuronLayer) network.firstLayer).states)
//				Debug.out(state.input+"");
//            Debug.out("=================================\r\n================================");
//			for (State state : (ArrayList<? extends State>) ((NeuronLayer) network.lastLayer).states)
//				Debug.out(state.input+"");
//            Debug.out("=================================\r\n================================");
//			for (State state : (ArrayList<? extends State>) ((NeuronLayer) network.lastLayer).states)
//				Debug.out(state.output+"");
			try { throw new Exception("Determinant = 0!!! Matrix: \r\n" +DoubleArrays2D.toString(covarMatrix.getData()));
			} catch (Exception e) { e.printStackTrace(); 	}
			double[][] re = new double[getDimensionality()][getDimensionality()];
			DoubleArrays2D.fill(re, -.01);
			System.exit(0);
			return new BlockRealMatrix(re);
		}
		RealMatrix detD = new LUDecomposition(covarMatrix).getSolver().getInverse(); // .scalarMultiply(matrixDeterminant); // det(X)*X^-1 = det(X)*X^-1^T
//		return detD.scalarMultiply(.5 * Math.sqrt(matrixDeterminant)); // der of  sqrt(det)
		return detD;
	}

	@Override
    double computeValue(NetworkState[] states) {
		return getDeterminant();
	}

	@Override
	public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		return getSampleCovarMatrixDerivativeForFeature(feature, xOutput, yOutput, state);
	}

	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
		updateCovarMatrixBatch(network, states);
		matrixDeterminant = getDeterminant();
		if (Debug.nanChecking && (Double.isNaN(matrixDeterminant)||Double.isInfinite(matrixDeterminant)))
            Debug.out("isNan at update: det="+DoubleArrays.toString(covarMatrix.getData()));
		
		covarMatrixDerivative = getCovarMatrixD();
	}
	
	public String toString() {
		return "Determinant = "+matrixDeterminant;
	}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		return;
	}
}
