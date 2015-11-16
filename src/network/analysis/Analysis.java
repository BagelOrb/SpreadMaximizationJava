package network.analysis;

import java.util.Random;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.Static.Objective;
import network.main.JFrameExperiment;
import objective.ChaObjective;

import org.apache.commons.lang3.StringUtils;

import util.basics.DoubleArray3D;
import util.basics.DoubleArray3Dext.Loc;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.MathFunction2D;

public class Analysis {

	
	
	private static final double dw  = 1E-9 ;
	private static boolean noUpdateBefore;
	private static double[] meansBefore;
	
	
	public static void checkDeriatives(Network network) {
		noUpdateBefore = Debug.noUpdate;
		
		

		
		NetworkState[] states = network.processBatch();

		DoubleArray3D[] computedDers = network.getLastLayer().weightsDerivatives;
		
		DoubleArray3D[] estimatedDers = Analysis.estimateWeightDerivatives(network, states);
		
		
		double totalDifference = 0;

		
		DoubleArray3D[] differences = new DoubleArray3D[network.getLastLayer().params.nFeatures];
		DoubleArray3D[] ratios = new DoubleArray3D[network.getLastLayer().params.nFeatures];
		for (int f = 0 ; f< network.getLastLayer().params.nFeatures ; f++)
		{
			try {
				ratios[f] = computedDers[f].zippedWith(new MathFunction2D() {
					
					@Override
					public double apply(double computed, double estimated) {
						if (Math2.liesWithin(-1E-7, computed , 1E-7 )
								&& Math2.liesWithin(-1E-7, estimated , 1E-7 ))
							return 1;
						double ratio = computed / estimated;
						return ratio;
					}
				}, estimatedDers[f]);
				differences[f] = computedDers[f].plus(estimatedDers[f].times(-1));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			totalDifference += ratios[f].plus(-1.).mapped(DifferentiableFunction.square).totalSum() / ratios[f].size();
			
			if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) {
				Network.output("=================================");
				Network.output("Stopped analysis! ");
				break;
			}
		}
		
		totalDifference /= network.getLastLayer().params.nFeatures; 
				
		if (totalDifference > Debug.significantChange && JFrameExperiment.keepRunning)
		{
			
//			states = network.processBatch();
			Random random = Network.random;
			CnnDoubleLayerState randomState = states[random.nextInt(states.length)].getLast();

			
			checkOutputDerivatives(network, randomState, states);
			
			checkPoolingDerivatives(network, randomState, states);

			
			Debug.out("computed  der weights = \n"+StringUtils.join(computedDers, "\n"));
			Debug.out("estimated der weights = \n"+ StringUtils.join(estimatedDers, "\n"));
			Debug.out("ratios    der weights = \n"+ StringUtils.join(ratios, "\n"));
			Debug.out("diffs     der weights = \n"+ StringUtils.join(differences, "\n"));

			
			network.significantChange = true;
			
		}
		
		
		Debug.noUpdate = noUpdateBefore;
		
	}

	/*
	 *   W E I G H T   D E R I V A T I V E S
	 */

	private static DoubleArray3D[] estimateWeightDerivatives(Network network, NetworkState[] states) {
		int nFeatures = network.getLastLayer().params.nFeatures;
		int w = network.getLastLayer().params.widthConvolutionField;
		int h = network.getLastLayer().params.heightConvolutionField;
		int d = network.getLastLayer().params.nInputFeatures;
		
		
		DoubleArray3D[] ret = new DoubleArray3D[nFeatures];
		
		for (int f = 0; f< nFeatures; f++){
			ret[f] = new DoubleArray3D(w, h, d);
			for (Loc l : ret[f])
				ret[f].set(l, estimateWeightDerivative(l.x, l.y, l.z, f, network, states));
		}
			
		
		return ret;
	}
	private static double estimateWeightDerivative(int xw, int yw, int zw, int f, Network network, NetworkState[] states) {
		Debug.noUpdate = true;

		
		double objBefore = getObjectiveValueBefore(network, states, f);		
		
		network.getLastLayer().weights[f].add(xw, yw, zw, dw);
		
//		NetworkState[] newStates = network.signalBatch();
		
		for (NetworkState nwState : states)
			network.signal(nwState);
		

		double objAfter = getObjectiveValueAfter(network, states, f);

		double estimatedDer = (objAfter- objBefore)/dw;
		
		
		network.getLastLayer().weights[f].add(xw, yw, zw, -dw);
		Debug.noUpdate = noUpdateBefore;

		return estimatedDer;
	}
	
	/*
	 *  O U T P U T   D E R I V A T I V E S
	 */

	

	private static void checkOutputDerivatives(Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		
		DoubleArray3D computedDers = randomState.outputMapsDerivatives;
		
		DoubleArray3D estimatedDers = Analysis.estimateOutputDerivatives(network, randomState, states); 
		Debug.out("computed  der out = \n"+computedDers);
		Debug.out("estimated der out = \n"+ estimatedDers);
	}
	
	
	private static DoubleArray3D estimateOutputDerivatives(Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		int w = randomState.outputMaps.width; 
		int h = randomState.outputMaps.height; 
		int d = network.getLastLayer().params.nFeatures; 
		DoubleArray3D ret = new DoubleArray3D(w, h, d);
		
		for (Loc l : ret)
			ret.set(l, estimateOutputDerivative(l.x, l.y, l.z, network, randomState, states));
		
		return ret;
	}
	private static double estimateOutputDerivative(int xw, int yw, int f, Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		Debug.noUpdate = true;
		
		double objBefore = getObjectiveValueBefore(network, states, f);
		
		
		randomState.outputMaps.add(xw, yw, f, dw);
		
//		NetworkState[] newStates = network.signalBatch();
		
		double objAfter = getObjectiveValueAfter(network, states, f);
		
		Debug.noUpdate = noUpdateBefore;
		
		return (objAfter- objBefore)/dw;
	}
	
	
	
	
	
	
	/*
	 *  P O O L I N G   D E R I V A T I V E S
	 */
	
	private static void checkPoolingDerivatives(Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		
		
		
		DoubleArray3D computedDers = randomState.convolutionMapsDerivatives;
		
		DoubleArray3D estimatedDers = Analysis.estimatePoolingDerivatives(network, randomState, states); 
		Debug.out("computed  der pooling = \n"+computedDers);
		Debug.out("estimated der pooling = \n"+ estimatedDers);
					
	}
	
	private static DoubleArray3D estimatePoolingDerivatives(Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		int w = randomState.convolutionMaps.width; 
		int h = randomState.convolutionMaps.height; 
		int d = network.getLastLayer().params.nFeatures; 
		DoubleArray3D ret = new DoubleArray3D(w, h, d);
		
		for (Loc l : ret)
			ret.set(l, estimatePoolingDerivative(l.x, l.y, l.z, network, randomState, states));
		
		return ret;
	}
	private static double estimatePoolingDerivative(int xw, int yw, int f, Network network, CnnDoubleLayerState randomState, NetworkState[] states) {
		Debug.noUpdate = true;


		double objBefore = getObjectiveValueBefore(network, states, f); 
		
		
		randomState.convolutionMaps.add(xw, yw, f, dw);
		
		network.getLastLayer().signalPoolingLayer(randomState);
		network.getLastLayer().signalOutputFunction(randomState);
		// take the same states, but with the altered randomState
		
		
		double objAfter = getObjectiveValueAfter(network, states, f);  
		
		
		Debug.noUpdate = noUpdateBefore;

		return (objAfter- objBefore)/dw;
	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	 *   H E L P E R   F U N C T I O N S 
	 */


	private static double getObjectiveValueBefore(Network network, NetworkState[] states, int f) {
		for (NetworkState nwState : states)
			network.signal(nwState);

		network.objective.computeCommonDerivativeStuff(states);
		if (network.netParams.objective == Objective.CONV_AUTO_ENCODER)
			network.processBatch(); // note that here noUptade == true

		
		double ret = network.objective.getValue(states);

		meansBefore = null;
		if (network.originalObjective instanceof ChaObjective)
		{
			ChaObjective cha = ((ChaObjective) network.originalObjective);
			meansBefore = cha.meansUsed;
			ret = cha.featureObjectives[f];
			
		}
		
		return ret;
	}

	private static double getObjectiveValueAfter(Network network, NetworkState[] states, int f) {
		network.objective.computeCommonDerivativeStuff(states);
		if (network.netParams.objective == Objective.CONV_AUTO_ENCODER)
			network.processBatch(); // note that here noUptade == true
		
		if (network.originalObjective instanceof ChaObjective)
			((ChaObjective) network.originalObjective).meansUsed = meansBefore;

		double ret = network.objective.getValue(states);
		
		if (network.originalObjective instanceof ChaObjective)
		{
			ChaObjective cha = ((ChaObjective) network.originalObjective);
			ret = cha.featureObjectives[f];
			
		}
		return ret;
	}


}
