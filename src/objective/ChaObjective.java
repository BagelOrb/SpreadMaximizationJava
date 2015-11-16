package objective;

import io.Images.InputSample;

import java.util.Arrays;

import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;
import learningMechanism.LearningParameter;
import network.LayerParameters;
import network.Network;
import network.NetworkState;
import network.analysis.Debug;
import network.main.JFrameExperiment;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import util.basics.DoubleArray1D;
import util.basics.DoubleArray3D;
import util.basics.DoubleArray3Dext.Loc;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.MathFunction2D;
import util.math.MathFunction3D;

public class ChaObjective extends ObjectiveFunction {

	public double[] meansUsed;
	public double[] movingMeans;
	public double[] newMeans;
	public double[] lastMeans;
	
	private static final boolean useMeans 					= true;
	private static final boolean useConstraints 			= true;
	private static final boolean useNormalizationConstraint = true; 

	private static final boolean useMovingMeans				= false; 
	
	
	// TODO: make tanh conversion possible as well!
	private double optimalStdDev_logisticSigmoid = 1.8137993369195464;
	private double optimalStdDev_tanh 			= optimalStdDev_logisticSigmoid/2;
	
	public double[] featureObjectives;
	
	public ChaObjective(Network nw) {
		super(nw);

	}

	@Override
	double computeValue(NetworkState[] states) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		featureObjectives = new double[lastLayer.params.nFeatures];

		
		double gho = getGhoObjective(states);
		
		Debug.checkNaN(gho);
		
		double totWeightConstraints = 0;
		for (NetworkState nwState : states)
		{
			CnnDoubleLayerState state = nwState.getLast();
			totWeightConstraints += getWeightObjective(state);
		}
		Debug.checkNaN(totWeightConstraints);
		
		
//		double ff = 0;
//		for (int i = 0; i< this.featureObjectives.length; i++)
//			ff += featureObjectives[i];
//		double diff = ff - (gho+totWeightConstraints);
				
		
		double zeroMeaning = 0;
		for (double mean : meansUsed)
			zeroMeaning += network.netParams.etaMean *
				(-1/4 * Math2.pow(mean, 4) 
				- 1/2 * mean*mean );
		
		return gho + ((useConstraints)? totWeightConstraints :0)  + zeroMeaning ;
	}

	private double getGhoObjective(NetworkState[] states) {
		double total = 0;
		
		for (NetworkState nwState : states) {
			CnnDoubleLayerState state = nwState.getLast();
			InputSample out = state.outputMaps;
//			total += out.mapped(DifferentiableFunction.square).foldr(MathFunction2D.addition, 0);
			double totState = 0;
			for (int x = 0; x< out.width; x++)
				for (int y = 0; y< out.height; y++)
					for (int f = 0; f< out.depth; f++)
					{
						double ghoLocal = Math2.square(out.get(x, y, f)- meansUsed[f]);
						totState += ghoLocal;
						featureObjectives[f] += ghoLocal;
					}
			total += totState;
		}
		double gho =  .5*total; 
		for (int i = 0 ; i< featureObjectives.length; i++)
			featureObjectives[i] *= .5;
		return gho;
	}

	private double getWeightObjective(CnnDoubleLayerState state) {
		double tot = 0;
		for (int xf = 0; xf < state.outputMaps.width; xf++)
			for (int yf = 0; yf < state.outputMaps.height; yf++)
				tot += getWeightObjective(xf, yf, state);
		return tot;
	}
	
	private double getWeightObjective(int xf, int yf, CnnDoubleLayerState state) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		LayerParameters params = lastLayer.params;
		
		double tot = 0;
		for (int k = 0; k<params.nFeatures; k++)
		{
			double p = state.poolingMaps.get(xf, yf, k);
			for (int j = 0; j < k; j++)
			{
				double weightedActivationMaps = getWeightedActionvationMaps(xf, yf, k, j, state);
				
				double wjTwk = 
						state.cnnDoubleLayer.weights[j].zipFold(new MathFunction3D() {
							
							@Override
							public double apply(double arg1, double arg2, double result) {
								return result + arg1 * arg2;
							}
						}, 0, state.cnnDoubleLayer.weights[k]); // vector multiplication
				
//				DoubleArray3D q = state.cnnDoubleLayer.weights[j].times(state.cnnDoubleLayer.weights[k]);
//				
//				wjTwk = q.totalSum();
				
				double localOrthogonalizationObj = (p- meansUsed[k]) * wjTwk * weightedActivationMaps;
				
				tot -= localOrthogonalizationObj ;
				
				featureObjectives[k] -= localOrthogonalizationObj;
			}
			
			// for j=k
			if (useNormalizationConstraint)
			{
				double weightedActivationMaps = getWeightedActionvationMaps(xf, yf, k, k, state);
	
				double wkTwk = state.cnnDoubleLayer.weights[k].foldr(new MathFunction2D() {
					@Override public double apply(double arg1, double arg2) {
						return arg2+ arg1*arg1;
					}
				}, 0); // total squared sum
				
	//			wkTwk = Math2.limit(-1E10, wkTwk, 1E10);
				
				Debug.checkNaN(weightedActivationMaps);
				Debug.checkNaN(wkTwk);
				double localNormalizationObj = .5* (p-meansUsed[k]) * (1-wkTwk) * weightedActivationMaps;
				tot += localNormalizationObj ;
				
				featureObjectives[k] += localNormalizationObj;
			}
		}
		Debug.checkNaN(tot);
		return tot;
	}

	
	
	
	
	
	
	/*
	 * Derivatives
	 */
	

	@Override
	public void computeCommonDerivativeStuff(NetworkState[] states) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		
		int nf = lastLayer.params.nFeatures;
		
		lastMeans = meansUsed;
		meansUsed = new double[nf];
		newMeans = new double[nf];
		
		if (!useMeans )
			return;
			
		
		Mean[] meanComps = new Mean[nf];
		for (int f = 0 ; f < nf ; f++)
			meanComps[f] = new Mean();
		
		
		for (NetworkState state : states)
		{ 
			InputSample out = state.getLast().outputMaps;
			for (Loc l : out) {
				meanComps[l.z].increment(out.get(l));
			}
		}
		
		
		for (int f = 0 ; f < nf ; f++)
		{
			newMeans[f] = meanComps[f].getResult();
			
			Debug.checkNaN(newMeans[f]);
			
			if (Math.abs(newMeans[f])>1E5)
				Debug.out("Mean too high!!! : "+newMeans[f]);
		}
		
		// compute moving means:
//		double ratio = ((double) network.currentBatchSize) / ((double) network.netParams.nSamples);
//		ratio = Math2.limit(1./((double) network.netParams.nSamples), ratio, 1);
//		ratio = 1 - .9 * (1-ratio); // depend a little more on the current batch!
//		ratio *= .5; // depend a little less on the current batch!
		double ratio = .1;
		if (lastMeans == null)
			movingMeans = newMeans;
		else
			for (int m = 0; m< meansUsed.length; m++)
			{
				movingMeans[m] = movingMeans[m] *(1-ratio) + ratio * newMeans[m];
			}
		
		
		if (!useMovingMeans)
			meansUsed = newMeans;
		else
			meansUsed = movingMeans;
	}
	/*
	public void computeCommonDerivativeStuff(NetworkState[] states, int f) {
		lastMeans = meansUsed;
		newMeans = Arrays.copyOf(meansUsed, lastMeans.length);
		newMeans[f] = 0;
		meansUsed = null;
		
		if (!useMeans )
			return;
		
		
		{
			double totF = 0;
			for (NetworkState nwState : states)
			{
				CnnDoubleLayerState state = nwState.getLast(); 
				double totState = 0;
				InputSample out = state.outputMaps;
				for (int x = 0; x < out.width; x++)
					for (int y = 0; y < out.height; y++)
						totState += out.get(x, y, f);
				totF += totState;
			}
			newMeans[f] = totF / ( states.length * states[0].getLast().outputMaps.width * states[0].getLast().outputMaps.height);
			
			Debug.checkNaN(newMeans[f]);
			
			if (Math.abs(newMeans[f])>1E5)
				Debug.out("Mean too high!!!");
		}
//		Debug.out("\nstate0 out = "+states[0].poolingMaps.get(0, 0, 0)+"\t\tbiases= " +Arrays.toString(network.cnnDoubleLayer.biases));
//		network.debugout("\nstate0 out = "+states[0].getLast().outputMaps+"means = "+ Arrays.toString(means)+"\t\tbiases= " +Arrays.toString(network.getLastLayer().biases));
//		network.debugout("means = "+ Arrays.toString(means)+"\t\tbiases= " +Arrays.toString(network.getLastLayer().biases));
		
		
		
		
		// compute moving means:
//		double ratio = ((double) network.currentBatchSize) / ((double) network.netParams.nSamples);
//		ratio = Math2.limit(1./((double) network.netParams.nSamples), ratio, 1);
////		ratio = 1 - .9 * (1-ratio); // depend a little more on the current batch!
//		ratio *= .5; // depend a little less on the current batch!
		double ratio = .1;
		if (lastMeans == null)
			movingMeans = newMeans;
		else
			movingMeans[f] = movingMeans[f] *(1-ratio) + ratio * newMeans[f];
		
		
		if (!useMovingMeans)
			meansUsed = newMeans;
		else
			meansUsed = movingMeans;
	}

	*/
	
	
	
	@Override
	public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		return state.outputMaps.get(xOutput,yOutput,feature) - meansUsed[feature];
	}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		if (!useConstraints)
			return;


		if (Debug.checkDerivatives && Debug.debugLevel >= 3)
		{
			DoubleArray3D[] totalResult = getGhoWeightsDerivatives(network, states);
			Debug.out("difference between backpropagated derivatives and computed derivatives:\n"
					+ totalResult[0].plus(network.getLastLayer().weightsDerivatives[0].times(-1)));
		}
		

		for (NetworkState nwState : states)
		{
			CnnDoubleLayerState state = nwState.getLast();
			
			LayerParameters params = network.getLastLayer().params;
			for (int k = 0; k<params.nFeatures; k++)
				addWeightDerivatives(state, k);
		}
		
		Debug.checkNaN(network.getLastLayer().weightsDerivatives[0].get(0, 0, 0));
			
		

		
		addBiasDerivatives(states);
		
	}
	public void addWeightDerivatives(NetworkState[] states, int f) {
		if (!useConstraints)
			return;
		
		
		if (Debug.checkDerivatives && Debug.debugLevel >= 3)
		{
			DoubleArray3D[] totalResult = getGhoWeightsDerivatives(network, states);
			Debug.out("difference between backpropagated derivatives and computed derivatives:\n"
					+ totalResult[0].plus(network.getLastLayer().weightsDerivatives[0].times(-1)));
		}
		
		
		for (NetworkState nwState : states)
		{
			CnnDoubleLayerState state = nwState.getLast();
			
			addWeightDerivatives(state, f);
		}
		
		Debug.checkNaN(network.getLastLayer().weightsDerivatives[0].get(0, 0, 0));
		
		
		
		
		addBiasDerivatives(states, f);
		
	}


	private void addBiasDerivatives(NetworkState[] states) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		for (int f = 0; f<lastLayer.params.nFeatures; f++) 
			addBiasDerivatives(states, f);
		
	}
	private void addBiasDerivatives(NetworkState[] states, int f) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		
		lastLayer.biasesDerivatives[f] = -1*
				network.netParams.etaMean *
//				( 	Math2.pow(meansUsed[f], 3) +  
//					states.length * 
					meansUsed[f]				
//							)
				; //  (discard backpropagated derivatives)
		Debug.checkNaN(lastLayer.biasesDerivatives[f]);
	}

	private void addWeightDerivatives(CnnDoubleLayerState state, int k) {
		for (int xf = 0; xf < state.outputMaps.width; xf++)
			for (int yf = 0; yf < state.outputMaps.height; yf++)
				addWeightDerivatives(k, xf, yf, state);
	}

	private void addWeightDerivatives(int k, int xf, int yf, CnnDoubleLayerState state) {
		
//		int wcf = params.widthConvolutionField;
//		int hcf = params.heightConvolutionField;
//		int wps = params.widthPoolingStep;
//		int hps = params.heightPoolingStep;
		
		
		
		double p = state.poolingMaps.get(xf, yf, k);
		int kk = (useNormalizationConstraint)? k : k-1;
		for (int j = 0; j <= kk; j++)
		{
			double weightedActivationMaps = getWeightedActionvationMaps(xf, yf, k, j, state);
			
			DoubleArray3D dw = state.cnnDoubleLayer.weights[j].times( -(p-meansUsed[k]) * weightedActivationMaps );
			state.cnnDoubleLayer.weightsDerivatives[k].add(dw);
			
//			for (int xi = 0 ; xi < wcf; xi++)
//				for (int yi = 0 ; yi < hcf; yi++) 
//					for (int z = 0 ; z < params.nInputFeatures; z++) 
//					{
//							double oDer = -p * weightedActivationMaps
//									* state.cnnDoubleLayer.weights[j].get(xi, yi, z);
//							Debug.checkNaN(oDer);
//							state.cnnDoubleLayer.weightsDerivatives[k].add(xi, yi, z, oDer);
//					}
			
		}
		Debug.checkNaN(network.getLastLayer().weightsDerivatives[0].get(0, 0, 0));
	}

	private double getWeightedActionvationMaps(int xf, int yf, int k, int j, CnnDoubleLayerState state) {
		
		// TODO: assumes linear transfer function!
		
		CnnDoubleLayer lastLayer = network.getLastLayer();
		LayerParameters params = lastLayer.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		double weightedActivationMaps = 0;
		for (int xp = 0 ; xp < wpf; xp++)
			for (int yp = 0 ; yp < hpf; yp++) 
			{
				weightedActivationMaps += state.cnnDoubleLayer.poolingFunction.computeDerivative(xf, yf, k, state, xp, yp)
						* state.convolutionMaps.get(xf*wps + xp, yf*hps + yp, j);
			}
		return weightedActivationMaps;
	}
	
	
	
	
	
	
	
	
	/*
	 *  evaluation of derivatives without backprop
	 */
	
	private DoubleArray3D[] getGhoWeightsDerivatives(Network network,
			NetworkState[] states) {
		CnnDoubleLayer lastLayer = network.getLastLayer();
		LayerParameters params = lastLayer.params;
		DoubleArray3D[] ret = new DoubleArray3D[params.nFeatures];
		for (int k = 0; k<params.nFeatures; k++)
		{
			DoubleArray3D totalResult = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
			{ // compute original derivatives (debug)
		
				for (NetworkState nwState : states)
				{
					CnnDoubleLayerState state = nwState.getLast();
					for (int xf = 0; xf < state.outputMaps.width; xf++)
						for (int yf = 0; yf < state.outputMaps.height; yf++)
								totalResult.add(getGhoDerivative(xf, yf, k, state));
				}
			}
			ret[k] = totalResult;
		}
		return ret;
	}
	private DoubleArray3D getGhoDerivative(int xf, int yf, int k, CnnDoubleLayerState state) {
		
		// TODO: assumes linear transfer function!

		double p = state.poolingMaps.get(xf, yf, k);

		CnnDoubleLayer lastLayer = network.getLastLayer();
		LayerParameters params = lastLayer.params;
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		
		DoubleArray3D weightedInputMaps = new DoubleArray3D(wcf, hcf, params.nInputFeatures); 
		
		for (int xp = 0 ; xp < wpf; xp++)
			for (int yp = 0 ; yp < hpf; yp++)
			{
				double pDer = state.cnnDoubleLayer.poolingFunction.computeDerivative(xf, yf, k, state, xp, yp);
				for (int xi = 0 ; xi < wcf; xi++)
					for (int yi = 0 ; yi < hcf; yi++) 
						for (int fi = 0 ; fi < params.nInputFeatures; fi++) 
						{
							double d =  pDer * state.inputMaps.get(xf*wps + xp + xi, yf*hps + yp + yi, fi);
							weightedInputMaps.add(xi, yi, fi, d);
						}
			}
		
		weightedInputMaps.multiply(p-meansUsed[k]);
		return weightedInputMaps;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 *   weight transformation
	 */
	
	public void orthonormalizeWeights() {
		CnnDoubleLayer layer = network.getLastLayer();
		LayerParameters params = layer.params;
		for (int f = 0 ; f< params.nFeatures; f++)
			orthonormalizeWeights(f);
		
//		network.outputConstraints(network.getLastLayer());
		
	}
	
	public void orthonormalizeWeights(int k) {
		CnnDoubleLayer layer = network.getLastLayer();
		DoubleArray3D w_k = layer.weights[k];
		// orthogonalize
		DoubleArray3D projections = new DoubleArray3D(w_k.width, w_k.height, w_k.depth);
		for (int j = 0 ; j< k; j++)
		{
			DoubleArray3D w_j = layer.weights[j];
			projections.add(w_j.times(w_k.dotProduct(w_j)));
		}
		
		w_k.add(projections.times(-1));
			
		// normalize
		double weightLength = Math.sqrt(w_k.foldr(new MathFunction2D() {
			
			@Override
			public double apply(double arg1, double arg2) {
				return arg1*arg1+arg2;
			}
		}, 0) 
				);
		w_k.multiply(1. / weightLength );
//		layer.biases[f] *= 1.  ;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 *   F I N A L I Z E   N E T W O R K
	 */
	
	
	
	public void finalizeNetwork() {
		orthonormalizeWeights();

		transformWeights();
		
		learnBiases();
		
//		transformWeightsAndBiasesIteratively();
		
		transformNetwork();
	}
	
	private void transformWeightsAndBiasesIteratively() {
		Debug.out("transformWeightsAndBiasesIteratively...");
		int nGoodEstimate = 500;
		int nBatches = Math.max(1, nGoodEstimate / network.netParams.batchSize);
		
		int nf = network.getLastLayer().params.nFeatures;
		
		// 										network.netParams.nIterations/2
		int totalIterations = 100;
		for (int i = network.jumpToIteration; i< totalIterations; i++)
		{
			
			SummaryStatistics[] stats = new SummaryStatistics[nf];
			for (int f = 0; f< nf; f++)
				stats[f] = new SummaryStatistics();
			
			for (int b = 0; b< nBatches; b++)
			{
				NetworkState[] batch = network.signalBatch(network.netParams.batchSize);
				for (NetworkState state : batch)
					for (Loc l : state.getLast().outputMaps)
					{
						Debug.checkNaN(state.getLast().outputMaps.get(l));
						stats[l.z].addValue(state.getLast().outputMaps.get(l));
					}
			}
			
			if (transformationHasConverged(stats))
			{
				Debug.out("Converged after "+i+" iterations!");
				break;
			}
			
			
			if (monitorWeightAndBiasLearning(i, totalIterations, stats))
			{
				break;
			}
			
			transformBiasesAndWeights(stats);
			
		}
		
		Debug.out("Finished transformWeightsAndBiasesIteratively...");
	}
	


	private boolean monitorWeightAndBiasLearning(int i, int totalIterations, SummaryStatistics[] stats) {
		int nf = network.getLastLayer().params.nFeatures;
//		if (i%Math.max(1, totalIterations / 10) == 0)
//		{
			Network.output((int) ((i*100.) / (totalIterations)) + "%");

			
			double[] means = new double[nf];
			for (int f = 0; f<nf; f++)
				means[f] = Math.sqrt(stats[f].getMean());
			
			Debug.out("  means = "+Arrays.toString(means));
			
			
			double[] stdDevs = new double[nf];
			for (int f = 0; f<nf; f++)
				stdDevs[f] = Math.sqrt(stats[f].getVariance());
			
			Debug.out("  standard deviations = "+Arrays.toString(stdDevs));
//			
//			String varsStr = "";
//			for (Variance var : vars)
//				varsStr += var.getResult()+", ";
//			Debug.out("  vars = "+varsStr);
//		}			
		
		if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) {
			Network.output("=================================");
			Network.output("Stopped after "+i+" iterations");
			JFrameExperiment.keepRunning = true;
			return true;
		}
		
		return false;
	}

	private void transformBiasesAndWeights(SummaryStatistics[] stats) {
		int nf = network.getLastLayer().params.nFeatures;
		
		
		CnnDoubleLayer layer = network.getLastLayer();
		
		for (int f = 0 ; f< nf; f++)
		{
			layer.biases[f] -= stats[f].getMean();
			
			
			double var = stats[f].getVariance();
			layer.weights[f].multiply(optimalStdDev_tanh / Math.sqrt(var));
			layer.biases[f] *= optimalStdDev_tanh / Math.sqrt(var) ;
		}
		
	}
	
	private boolean transformationHasConverged(SummaryStatistics[] stats) {
		int nf = network.getLastLayer().params.nFeatures;
		for (int f = 0 ; f< nf; f++)
			if ( ! (stats[f].getMean() < 1E-3  
					&& Math.sqrt(stats[f].getVariance()-Math2.square(optimalStdDev_tanh))<1E-3))
					return false;
		return true;
	}
	
	/*
	 * T R A N S F O R M   W E I G H T S
	 */
	
	

	public void transformWeights() {
		
		
		transformWeightsByOneGoodEstimate(10000);
		transformWeightsIteratively();
//		transformWeightsByOneGoodEstimate();
		

	}
	



	private void transformWeightsIteratively() {
		lastMeans = null;
		network.learningMechanism.resetState(network.getLastLayer());
		JFrameExperiment.keepRunning = true; 
		Network.output("=================================");
		Network.output("==      Learning scalings      ==");
	
		double etaWeightsBefore = network.netParams.etaWeights;
		
		network.netParams.etaWeights = 2E-4;
		Network.output("Setting etaWeights to " + network.netParams.etaWeights);
		
		
		
		int nf = network.getLastLayer().params.nFeatures;
		
		LearningParameter[] scalingParams = new LearningParameter[nf];
		for (int f = 0; f<nf; f++)
			scalingParams[f] = network.learningMechanism.newLearningParameter(network.netParams);
		
		
		for (int i = network.jumpToIteration; i<network.netParams.nIterations/2; i++) {

			Variance[] vars = processBatchForScalings(scalingParams);
			
			boolean stop = monitorScalingLearningProgress(i, scalingParams, vars);
			if (stop)
				break;
		}
		
		
		
		
		network.jumpToIteration = 0;
		
		network.netParams.etaWeights = etaWeightsBefore;

		Network.output("\r\n== Finished learning scalings ==\r\n");
	}



	private Variance[] processBatchForScalings(LearningParameter[] scalingParams) {
		NetworkState[] states = network.signalBatch(Math.min(100, network.netParams.nSamples));  // .nSamples); //
		
		Variance[] vars = computeVariances(states);
		
		CnnDoubleLayer layer = network.getLastLayer();
		int nf = layer.params.nFeatures;
		
		for (int f = 0; f<nf; f++)
		{
			double stdDev = Math.sqrt(vars[f].getResult());
			scalingParams[f].setValue(stdDev);
			double update = scalingParams[f].computeUpdate(optimalStdDev_tanh - stdDev);
			double ratio = (stdDev + update) / stdDev;
			
//			double sqVecL = Math2.square(layer.weights[f].vectorLength());
//			double totalVectorLength = Math.sqrt(sqVecL + Math2.square(layer.biases[f]));
			
			layer.weights[f].multiply(ratio);
			layer.biases[f] *= ratio;
			
		}
		
		return vars;
	}

	private Variance[] computeVariances(NetworkState[] states) {
		int nf = network.getLastLayer().params.nFeatures;
		
		Variance[] vars = new Variance[nf];
		for (int f = 0 ; f < nf; f++)
			vars[f] = new Variance();
		

		for (NetworkState state : states)
		{ 
			InputSample out = state.getLast().outputMaps;
			for (Loc l : out)
				vars[l.z].increment(out.get(l));
		}
		
		return vars;
	}
	
	
	private boolean monitorScalingLearningProgress(int i, LearningParameter[] scalingParams, Variance[] vars) {
		if (i%Math.max(1, network.netParams.nIterations  / 2 / 10) == 0)
		{
			Network.output((int) ((i*100.) / (network.netParams.nIterations  / 2.)) + "%");
			Debug.out("  scaling params = "+Arrays.toString(scalingParams));
//			
//			String varsStr = "";
//			for (Variance var : vars)
//				varsStr += var.getResult()+", ";
//			Debug.out("  vars = "+varsStr);
		}

		
		if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) {
			Network.output("=================================");
			Network.output("Stopped after "+i+" iterations");
			JFrameExperiment.keepRunning = true;
			return true;
		}
		
		return false;
	}
	
	
	
	
	/*
	 * non-iterative variance update
	 */
	
	
	private void transformWeightsByOneGoodEstimate(int nGoodEstimates) {
		Network.output("Estimating variances...");
		
		int nBatches = nGoodEstimates / network.netParams.batchSize;
		if (nBatches<1)
			nBatches = 1;
		
		
		int nFeatures = network.getLastLayer().params.nFeatures;
		
		Variance[] vars = new Variance[nFeatures];
		for (int f = 0 ; f < nFeatures; f++)
			vars[f] = new Variance();
		
		
		for (int b = 0; b< nBatches; b++)
		{
			NetworkState[] states = network.signalBatch(network.netParams.batchSize);
			for (NetworkState state : states)
			{ 
				InputSample out = state.getLast().outputMaps;
				for (Loc l : out)
					vars[l.z].increment(out.get(l));
			}
				
		}
		
		
		
		
		Network.output("Variances estimated! Transforming weights..");
		
		
		
		CnnDoubleLayer layer = network.getLastLayer();
		LayerParameters params = layer.params;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			double var = vars[f].getResult();
			layer.weights[f].multiply(optimalStdDev_tanh / Math.sqrt(var));
			layer.biases[f] *= optimalStdDev_tanh / Math.sqrt(var) ;
		}
		
		Network.output("Weights transformed!");
	}

	/*
	 * ==========================================================================================
	 */
	
	
	
	
	
	
	
	private BlockRealMatrix getCovarMatrixbatch() {
		NetworkState[] states = network.signalBatch(Math.min(100, network.netParams.nSamples));
		return getCovarMatrix(states);
	}
	private BlockRealMatrix getCovarMatrix(NetworkState[] states) {
		CovarComputation comp = new CovarComputation(network);
		comp.updateCovarMatrixBatch(network, states);
		return comp.covarMatrix;
	}

	public void transformNetwork() {
		network.getLastLayer().params.signallingFunction = DifferentiableFunction.tanh;
	}
	
	
	
	
	
	/*
	 * L E A R N   B I A S E S
	 * 
	 */
	public void learnBiases() {
		lastMeans = null;
		network.learningMechanism.resetState(network.getLastLayer());
		JFrameExperiment.keepRunning = true; 
		Network.output("=================================");
		Network.output("==       Learning biases       ==");
	
		Network.output("Setting etaBias to " + 1E-5);
		double etaBiasesBefore = network.netParams.etaBiases;
		
		network.netParams.etaBiases = 1E-5;
		
		for (int i = network.jumpToIteration; i<network.netParams.nIterations/2; i++) {

			processBatchForBiases();
			
			boolean stop = monitorBiasLearningProgress(i);
			
			if (stop || new DoubleArray1D(movingMeans).vectorLength() < 1E-4)
				break;
		}
		network.jumpToIteration = 0;
		
		network.netParams.etaBiases = etaBiasesBefore;

		Network.output("\r\n== Finished learning biases ==\r\n");
	}
	/*
	public void learnBiases(int f) {
		double eta_rProp_min_before = network.netParams.eta_rPropMin;
		network.netParams.eta_rPropMin = .25;
		double eta_Weights_before = network.netParams.etaWeights;
		network.netParams.etaWeights = .25;
		double alpha_momentum = network.netParams.alpha_momentum ;
		network.netParams.alpha_momentum = .9;

		lastMeans = null;
		
		network.learningMechanism.resetState(network.getLastLayer());
		JFrameExperiment.keepRunning = true; 
		Network.output("=================================");
		Network.output("==       Learning biases       ==");
		
		for (int i = 0; i<network.netParams.nIterations/2; i++) {
			
			processBatchForBiases(f);
			
			boolean stop = monitorBiasLearningProgress(i);
			
			
			if (stop || Math.abs(movingMeans[f]) < 1E-4)
				break;
			
		}
		
		network.netParams.etaWeights = eta_Weights_before; 
		network.netParams.eta_rPropMin = eta_rProp_min_before;
		network.netParams.alpha_momentum = alpha_momentum;
		Network.output("\r\n== Finished learning biases ==\r\n");
	}
	*/

	public boolean monitorBiasLearningProgress(int i) {
		if (i%Math.max(1, network.netParams.nIterations  / 2 / 10) == 0)
		{
			Network.output((int) ((i*100.) / (network.netParams.nIterations  / 2.)) + "%");
			Debug.out("  moving means =\n "+Arrays.toString(movingMeans));
			Debug.out("  means =\n "+Arrays.toString(newMeans));
		}

		
		{ // rendering
			
//			network.valuesOverTime.add(Arrays.copyOf(newMeans, getDimensionality()));
//			
//			if (System.currentTimeMillis() - network.lastRenderUpdate > 5000)
//			{
//				network.lastRenderUpdate = System.currentTimeMillis();
//				
//				try {
//					JFrameExperiment.frame.graphValues(network.valuesOverTime);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}
		
		if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) {
			Network.output("=================================");
			Network.output("Stopped after "+i+" iterations");
			JFrameExperiment.keepRunning = true;
			return true;
		}
		
		return false;
	}

	public NetworkState[] processBatchForBiases() {
		NetworkState[] states = network.signalBatch(Math.min(100, network.netParams.nSamples));  // .nSamples); // 
		computeCommonDerivativeStuff(states);
		addBiasDerivatives(states);
		network.learningMechanism.updateBiases(network.getLastLayer());
		return states;
	}
	/*
	private NetworkState[] processBatchForBiases(int f) {
		if (!network.netParams.staticData)
			network.inputNewImages(Math.min(100, network.netParams.nSamples));

		NetworkState[] states = network.signalBatchCHA(Math.min(100, network.netParams.nSamples), f);  // .nSamples); // 
		computeCommonDerivativeStuff(states, f);
		addBiasDerivatives(states, f);
		network.learningMechanism.updateBiases(network.getLastLayer());
		return states;
	}
	
	*/
	
	public String toString() {
		return "ChaObjective: "+lastValue;
	}


	
}
