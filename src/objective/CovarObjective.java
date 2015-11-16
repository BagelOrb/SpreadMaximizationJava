package objective;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.analysis.Debug;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import util.basics.DoubleArray3Dext;

public abstract class CovarObjective extends ObjectiveFunction {

	final boolean usePreSigmoidActivations; 
	/**
	 * The covariance matrix of the outputs
	 */
	public BlockRealMatrix covarMatrix;
	/**
	 * The output means of the current batch, for each output neuron. 
	 * Mean across the whole image of every output image.  
	 */
	double[] totals;
	/**
	 * The derivative matrix of the determinant w.r.t. each box of the covariance matrix. 
	 */
	public RealMatrix covarMatrixDerivative;


	/**
	 * The number of outputs. The total of all output image sizes.
	 */
	double nOutputs;
	
	public CovarObjective(Network nw) {
		this(nw,false);
	}
	public CovarObjective(Network nw, boolean usePreSigmoidActivations) {
		super(nw); 
		this.usePreSigmoidActivations = usePreSigmoidActivations;
//		covarMatrix = new BlockRealMatrix(getDimensionality(), getDimensionality());
//		outputDataUsedGetter = (usePreSigmoidActivations)? getPreSigmoidOutputData : getNormalOutputData;
	}

	public CovarObjective(int i) {
		this(i,false);
	}
	public CovarObjective(int i, boolean usePreSigmoidActivations) {
		super(i);
		this.usePreSigmoidActivations = usePreSigmoidActivations;
//		outputDataUsedGetter = (usePreSigmoidActivations)? getPreSigmoidOutputData : getNormalOutputData;
	}

	
	




//	protected final GetOutputDataUsed outputDataUsedGetter;
	private DoubleArray3Dext<?> getOutputDataUsed(CnnDoubleLayerState state){
		return state.outputMaps;
//		return outputDataUsedGetter.getOutputDataUsed(state);
	}
//	interface GetOutputDataUsed {
//		DoubleArray3Dext<?> getOutputDataUsed(CnnDoubleLayerState state);
//	}
//	private static final GetOutputDataUsed getNormalOutputData = new GetOutputDataUsed(){
//		@Override public DoubleArray3Dext<?> getOutputDataUsed(CnnDoubleLayerState state) {
//			return state.output;
//		}};
////	private static final GetOutputDataUsed getPreSigmoidOutputData = new GetOutputDataUsed(){
////		@Override public DoubleArray3Dext<?> getOutputDataUsed(State state) {
////			DoubleArray3D[] act = ((SigmoidState) state).activations;
////			DoubleArray3D outputDataUsed = DoubleArray3D.connectDepthwise(ArraysTK.map(new Function1D<DoubleArray3D, DoubleArray2D>(){
////				@Override
////				public DoubleArray2D apply(DoubleArray3D a1) {
////					return a1.fold1D(2, MathFunction2D.addition, 0);
////				}}, act)); // : add up activations across input features for each neuron in the array and combine DA2D's into DA3D
////			return outputDataUsed;
////		}};
//	private final GetOutputDataUsed getPreSigmoidOutputData = new GetOutputDataUsed(){
//		@Override public DoubleArray3Dext<?> getOutputDataUsed(CnnDoubleLayerState state) {
//			DoubleArray3D[] act = ((SigmoidState) state).activations;
//			InputSample outputDataUsed = state.output.mapped(new MathFunction1D(){
//				@Override
//				public double apply(double in) {
//					return network.lastLayer.params.signallingFunction.applyInverse(in);
//				}});
//			outputDataUsed.map(new MathFunction1D(){
//				@Override public double apply(double in) {
//					return Math2.limit(-20, in, 20);
//				}});
//			return outputDataUsed;
//		}};
			
			
			
	public void updateCovarMatrixBatch(final Network network, NetworkState[] states) {
		CovarComputation comp = new CovarComputation(network);
		comp.updateCovarMatrixBatch(network, states);
		this.nOutputs = comp.nOutputs;
		this.totals = comp.totals;
		this.covarMatrix = comp.covarMatrix;
//
//		totals = new double[getDimensionality()];
//		nOutputs = 0;
//		for (int i =0; i<states.length; i++) {
//			CnnDoubleLayerState state = states[i].getLast();
//			DoubleArray3Dext<?> outputDataUsed = getOutputDataUsed(state); 
//			
//			
//			for (int xO = 0; xO<outputDataUsed.width ; xO++)
//				for (int yO = 0; yO<outputDataUsed.height; yO++) {
//					nOutputs++;
//					for (int f = 0; f<getDimensionality();f++) 
//						totals[f] += outputDataUsed.get(xO,yO,f);
//				}
//		}
//			
//		if (Debug.nanChecking && nOutputs==0)
//            Debug.out("nOuputs=0!!");
//	
//		final double[] means = totals;
//		for (int f = 0; f<getDimensionality();f++)
//			means[f] /= nOutputs;
//			
//		final double[][] covarTotals = new double[getDimensionality()][getDimensionality()];
//		
//		for (int i =0; i<states.length; i++) {
//			CnnDoubleLayerState state = states[i].getLast();
//			DoubleArray3Dext<?> outputDataUsed = getOutputDataUsed(state); 
//			
//			for (int xO = 0; xO<outputDataUsed.width ; xO++)
//				for (int yO = 0; yO<outputDataUsed.height; yO++)  
//					for (int f1 = 0; f1<getDimensionality();f1++) {
//						double f1var = outputDataUsed.get(xO,yO,f1) - means[f1];
//						if (Debug.nanChecking && (Double.isNaN(f1var)||Double.isInfinite(f1var)))
//                            Debug.out("f1Var is not finite! : f1var="+f1var+" in="+outputDataUsed.get(xO,yO,f1)+ " mean="+means[f1]);
//	
//						covarTotals[f1][f1] += f1var*f1var;
//						for (int f2 = f1+1; f2<getDimensionality();f2++) {
//							covarTotals[f1][f2] += f1var * (outputDataUsed.get(xO,yO,f2) - means[f2]);
//						}
//					}
//		}
//		
//		
//		double[][] covars = covarTotals;
//		for (int f1 = 0; f1<getDimensionality();f1++) { 
//			covars[f1][f1] /= (nOutputs-0d); // TODO: do sample covar correction -1 ??
//			for (int f2 = f1+1; f2<getDimensionality();f2++) {
//				double covar = covars[f1][f2]/(nOutputs-0d);// TODO: do sample covar correction -1 ??
//				//if (covar==0) covar = 1E-10;
//				covars[f1][f2] = covar;
//				covars[f2][f1] = covar; // matrix is symmetric
//			}
//		}
//		covarMatrix = new BlockRealMatrix(covars);
		
	}

	protected double getSampleCovarMatrixDerivativeForFeature(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		DoubleArray3Dext<?> outputDataUsed = getOutputDataUsed(state); 

		double ret = 0;
		for (int f = 0; f<getDimensionality(); f++) { 
				ret += 2 * 	// rows and columns are equal -> symmetric.. The der of the var of i is 2*... , but is only present once in the covar matrix
					covarMatrixDerivative.getEntry(feature, f)
	            *  (outputDataUsed.get(xOutput,yOutput,f) - totals[f]); //((params.batchLearning)? means[f] : features[f].outputMean)); 
		}
		if (Debug.nanChecking && (Double.isNaN(ret)||Double.isInfinite(ret)))
			Debug.err("isNan at getSampleDeterminantDerivativeForFeature");
		return 1d/(nOutputs)* ret; 
	}

}
