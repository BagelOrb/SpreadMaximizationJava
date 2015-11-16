package objective;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.analysis.Debug;

import org.apache.commons.math3.linear.BlockRealMatrix;

import util.basics.DoubleArray3Dext;

public class CovarComputation {
	/**
	 * The output means of the current batch, for each output neuron. 
	 * Mean across the whole image of every output image.  
	 */
	public double[] totals;
	private Network network;
	public  BlockRealMatrix covarMatrix;
	public  int nOutputs;
	
	
	public CovarComputation(Network network) {
		this.network = network;
	}
	
	private int getDimensionality() {
		return network.getLastLayer().params.nFeatures;
	}
	
	private DoubleArray3Dext<?> getOutputDataUsed(CnnDoubleLayerState state){
		return state.outputMaps;
	}
	
	public void updateCovarMatrixBatch(final Network network, NetworkState[] states) {

		totals = new double[getDimensionality()];
		nOutputs = 0;
		for (int i =0; i<states.length; i++) {
			CnnDoubleLayerState state = states[i].getLast();
			DoubleArray3Dext<?> outputDataUsed = getOutputDataUsed(state); 
			
			
			for (int xO = 0; xO<outputDataUsed.width ; xO++)
				for (int yO = 0; yO<outputDataUsed.height; yO++) {
					nOutputs++;
					for (int f = 0; f<getDimensionality();f++) 
						totals[f] += outputDataUsed.get(xO,yO,f);
				}
		}
			
		if (Debug.nanChecking && nOutputs==0)
            Debug.out("nOuputs=0!!");
	
		final double[] means = totals;
		for (int f = 0; f<getDimensionality();f++)
			means[f] /= nOutputs;
			
		final double[][] covarTotals = new double[getDimensionality()][getDimensionality()];
		
		for (int i =0; i<states.length; i++) {
			CnnDoubleLayerState state = states[i].getLast();
			DoubleArray3Dext<?> outputDataUsed = getOutputDataUsed(state); 
			
			for (int xO = 0; xO<outputDataUsed.width ; xO++)
				for (int yO = 0; yO<outputDataUsed.height; yO++)  
					for (int f1 = 0; f1<getDimensionality();f1++) {
						double f1var = outputDataUsed.get(xO,yO,f1) - means[f1];
						if (Debug.nanChecking && (Double.isNaN(f1var)||Double.isInfinite(f1var)))
                            Debug.out("f1Var is not finite! : f1var="+f1var+" in="+outputDataUsed.get(xO,yO,f1)+ " mean="+means[f1]);
	
						covarTotals[f1][f1] += f1var*f1var;
						for (int f2 = f1+1; f2<getDimensionality();f2++) {
							covarTotals[f1][f2] += f1var * (outputDataUsed.get(xO,yO,f2) - means[f2]);
						}
					}
		}
		
		
		double[][] covars = covarTotals;
		for (int f1 = 0; f1<getDimensionality();f1++) { 
			covars[f1][f1] /= (nOutputs-0d); // TODO: do sample covar correction -1 ??
			for (int f2 = f1+1; f2<getDimensionality();f2++) {
				double covar = covars[f1][f2]/(nOutputs-0d);// TODO: do sample covar correction -1 ??
				//if (covar==0) covar = 1E-10;
				covars[f1][f2] = covar;
				covars[f2][f1] = covar; // matrix is symmetric
			}
		}
		covarMatrix = new BlockRealMatrix(covars);
		
	}


}
