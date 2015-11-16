package layer;

import io.Images.InputSample;
import io.JsonAble;
import io.JsonArrays;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;

import learningMechanism.LearningMechanism.LearningState;
import network.LayerParameters;
import network.Network;
import network.Static.Objective;
import network.Static.OutputFunctionType;
import network.analysis.Debug;
import outputFunction.LinearOutputFunction;
import outputFunction.OutputFunction;
import outputFunction.SoftMaxAct;
import pooling.ArgMaxPooling;
import pooling.PoolingFunction;
import pooling.SoftArgMaxPooling;
import util.basics.DoubleArray3D;
import util.basics.DoubleArray3Dext.Loc;
import util.math.DifferentiableFunction;
import util.math.MathFunction1D;

public class CnnDoubleLayer extends JsonAble<CnnDoubleLayer> {
	public LayerParameters params;

	public PoolingFunction<?> poolingFunction;
	
	public OutputFunction<?> outputFunction;
	
	
	public static Random random;
	
	
	/**
	 * For Locking a layer during learning
	 */
	public boolean locked = false;
	
	public DoubleArray3D[] weights;
	public DoubleArray3D[] weightsDerivatives;
	public DoubleArray3D[][] tempWeightsDerivatives;
	
	public double[] biases;
	public double[] biasesDerivatives;
	public double[][] tempBiasesDerivatives;
	
//	public CnnDoubleLayerState state;

	public LearningState learningState;

	public int position;

	private Network network;

	public CnnDoubleLayer(LayerParameters params, Network network, int position) {
		this.position = position;
		this.network = network;
		
		
		weights = new DoubleArray3D[params.nFeatures];
		for (int f = 0 ; f < params.nFeatures ; f++)
			weights[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
		biases = new double[params.nFeatures];

		construct(params);
	}
	
	/**
	 * for parsing!
	 * @param network
	 * @param p
	 */
	public CnnDoubleLayer(Network network, int p) {
		this.position = p;
		this.network = network;
	}
	
	private void construct(LayerParameters params2) {
		this.params = params2;
		switch(params.pooling)
		{
		case SOFT_ARG_MAX_SQUARE:
			this.poolingFunction = new SoftArgMaxPooling(DifferentiableFunction.square, 1);
			break;
		case ARG_MAX_SQUARE:
			this.poolingFunction = new ArgMaxPooling(DifferentiableFunction.square);
			break;
		case SOFT_ARG_MAX_ABS:
			this.poolingFunction = new SoftArgMaxPooling(DifferentiableFunction.abs, 1);
			break;
		case ARG_MAX_ABS:
			this.poolingFunction = new ArgMaxPooling(DifferentiableFunction.abs);
			break;
		case MAX:
			this.poolingFunction = new ArgMaxPooling(DifferentiableFunction.linear);
			break;
		default:
			Debug.err("other pooling functions not implemented yet!");
		}
//		this.poolingFunction = params.poolingFunction;

		
		switch(params.outputFunction)
		{
		case LINEAR:
			this.outputFunction = new LinearOutputFunction();
			break;
		case SOFT_MAX_ACT:
			this.outputFunction = new SoftMaxAct();
			break;
		default:
			Debug.err("other output functions not implemented yet!");
		}
		
		

		weightsDerivatives = new DoubleArray3D[params.nFeatures];
		for (int f = 0 ; f < params.nFeatures ; f++)
			weightsDerivatives[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
		biasesDerivatives = new double[params.nFeatures];

		//		inputMaps =  set these via Network.java!
		
		
		
		learningState = network.learningMechanism.newState(this);
	}


	/*
	 * INITIALIZATION
	 */
	


	public void initializeWeights() {
		for (int b = 0 ; b<biases.length ; b++)
			biases[b] = randomBias();
		for (int f = 0; f<params.nFeatures; f++)
			weights[f].map(new MathFunction1D() {
				@Override
				public double apply(double arg) {
					return randomWeight();
				}
			});
	}
	
	private double randomBias() {
		if (params.netParams.randomizeBiases)
			return randomWeight();
		else
			return 0;
	}
	
	private double randomWeight() {
		return random.nextGaussian()*params.netParams.absMaxInitWeights;
	}

	
	
	
	
	
	
	
	/*
	 * SIGNALLING
	 */
	


//	public CnnDoubleLayerState signal(InputSample imageSample) {
//		CnnDoubleLayerState state = new CnnDoubleLayerState(this, imageSample);
//		return signal(state);
//	}

	public void signalUpTo(CnnDoubleLayerState state, int lastF) {
		for (int f = 0; f<= lastF ; f++)
			signalConvolutionLayer(state, f);
		
		if (params.signallingFunction.equals(DifferentiableFunction.linear))
			state.convolutionMaps = state.convolutionActivationMaps;
		else
			state.convolutionMaps = state.convolutionActivationMaps.mapped(params.signallingFunction);	
		
		state.poolingMaps.fill(0);
		state.poolingState.reset();
		for (int f = 0; f<= lastF ; f++)
			signalPoolingLayer(state, f);
		
		signalOutputFunction(state);
	}
	public void signal(CnnDoubleLayerState state) {
		signalConvolutionLayer(state);
		signalPoolingLayer(state);
		signalOutputFunction(state);
	}

	private  void signalConvolutionLayer(CnnDoubleLayerState state) {
		for (int toFeature = 0; toFeature< params.nFeatures ; toFeature++)
			signalConvolutionLayer(state, toFeature);
		if (params.signallingFunction.equals(DifferentiableFunction.linear))
			state.convolutionMaps = state.convolutionActivationMaps;
		else
			state.convolutionMaps = state.convolutionActivationMaps.mapped(params.signallingFunction);
	}

	private  void signalConvolutionLayer(CnnDoubleLayerState state, int toFeature) {
		InputSample input = state.inputMaps;
		
		
		if (state.cnnDoubleLayer.params.netParams.objective == Objective.CONV_AUTO_ENCODER && params.netParams.caeNoise > 0)
		{
			input = input.clone();
			for (Loc l : input)
				if (random.nextDouble() < params.netParams.caeNoise)
					input.set(l, random.nextBoolean() ? 
							((params.signallingFunction == DifferentiableFunction.tanh)? -1. : 0.)
							: 1.);
					// += random.nextGaussian()*.2;
//									in += random.nextDouble()*.2;
		}
		
		
		
		
		for (int xc = 0 ; xc < state.convolutionMaps.width ; xc ++)
			for (int yc = 0 ; yc < state.convolutionMaps.height ; yc ++)
			{
//				state.convolutionMaps.set(xc, yc, toFeature, 0);
				state.convolutionActivationMaps.set(xc, yc, toFeature, 0);
				
				for (int fromFeature = 0; fromFeature< params.nInputFeatures; fromFeature++)
					for (int xi = 0 ; xi < params.widthConvolutionField ; xi++)
						for (int yi = 0 ; yi < params.heightConvolutionField ; yi++)
						{
							try{
								double in = input.get(xc +xi, yc+yi, fromFeature);
								
								double w = weights[toFeature].get(xi, yi, fromFeature);
								Debug.checkNaN(w);
								state.convolutionActivationMaps.add(xc , yc, toFeature, in * w);
							} catch (Exception e) {
								e.printStackTrace();
								Debug.out("ERROR: out of map evaluation!?");
								Debug.out("\nxc ",xc," xi",xi, " yc",yc, " yi",yi);
								Debug.out("widthInput="+params.netParams.widthInput);

								System.exit(0);
                                Debug.out(state.inputMaps);
							}
						}
				state.convolutionActivationMaps.add(xc , yc, toFeature, biases[toFeature]);
//				Debug.breakPoint(Math.abs(state.convolutionActivationMaps.get(xc , yc, toFeature))>10, "convolution map too large?  > 50 !");
			}
	}

	// TODO: make private again!
	public void signalPoolingLayer(CnnDoubleLayerState state) {
		state.poolingMaps.fill(0);
		state.poolingState.reset();
		for (int f = 0; f< params.nFeatures ; f++)
			signalPoolingLayer(state, f);
	}
	public void signalPoolingLayer(CnnDoubleLayerState state, int f) {
		for (int xp = 0 ; xp < state.poolingMaps.width ; xp++)
			for (int yp = 0 ; yp < state.poolingMaps.height ; yp++)
			{
				double out = poolingFunction.computeOutput(xp, yp, f, state);
				Debug.checkNaN(out);
//				Debug.out(f+", "+xp+", "+yp+", "+ ":"+out);
				state.poolingMaps.set(xp , yp, f, out);
			}
//		convolutionMaps.map(params.signallingFunctionAfterPooling);
	}
	
	

	// TODO: make private again!
	public void signalOutputFunction(CnnDoubleLayerState state) {
		for (int xp = 0 ; xp < state.poolingMaps.width ; xp++)
			for (int yp = 0 ; yp < state.poolingMaps.height ; yp++)
				outputFunction.computeOutputs(xp, yp, state);
	}
	
	
	
	
	
	
	
	
	
	/*
	 *  BACKPROP
	 */
	
	
	/**
	 * only backpropagates through the given feature! doesn't compute any derivative of other neurons!
	 * OutputFunction not properly considered!
	 * @param state
	 * @param feature
	 * @param threadTag
	 */
	public void addBackpropagatedDerivatives(CnnDoubleLayerState state, int feature, int threadTag) {
		if (locked)
			return;
		
		setOutputDerivatives(state, feature);
		state.convolutionMapsDerivatives.fill(0);
		backpropagatePoolingDerivatives(state, feature);
		backpropagateConvolutionDerivativesToWeights(state, feature, threadTag);
		
	}
	public void addBackpropagatedDerivatives(CnnDoubleLayerState state, int threadTag) {
		if (!(state.params.netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION && state.params.outputFunction == OutputFunctionType.SOFT_MAX_ACT))
			outputFunction.setDerivatives(state);
//		setOutputDerivatives(state);
		backpropagatePoolingDerivatives(state);
		backpropagateConvolutionDerivativesToWeights(state, threadTag);
	}
	private void setOutputDerivatives(CnnDoubleLayerState state) {
		for (int f = 0 ; f < state.poolingMaps.depth ; f++)
			setOutputDerivatives(state, f);
	}
	private void setOutputDerivatives(CnnDoubleLayerState state, int f) {
		
		for (int xp = 0 ; xp < state.poolingMaps.width ; xp++)
			for (int yp = 0 ; yp < state.poolingMaps.height ; yp++)
			{
				double der = outputFunction.computeDerivative(xp, yp, f, state);
				state.poolingMapsDerivatives.set(xp, yp, f, der);
			}
	}

	private void backpropagatePoolingDerivatives(CnnDoubleLayerState state) {
//		state.convolutionMapsDerivatives.fill(0);
		for (int f = 0; f< params.nFeatures ; f++)
			backpropagatePoolingDerivatives(state, f);
	}
	private void backpropagatePoolingDerivatives(CnnDoubleLayerState state, int f) {
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
        int wps = params.widthPoolingStep;
        int hps = params.heightPoolingStep;

		for (int xp = 0 ; xp < state.poolingMaps.width ; xp++)
			for (int yp = 0 ; yp < state.poolingMaps.height ; yp++)
			{
				for (int xc = 0 ; xc < wpf ; xc++)
					for (int yc = 0 ; yc < hpf ; yc++)
					{
						double pDer = poolingFunction.computeDerivative(xp, yp, f, state, xc, yc);
						double pmDer = state.poolingMapsDerivatives.get(xp, yp, f); 
						Debug.checkNaN(pDer);
						Debug.checkNaN(pmDer);
                        state.convolutionMapsDerivatives.add(xp*wps + xc, yp*hps + yc, f, pDer * pmDer);
					}
			}
		
		
	}
	public void backpropagateConvolutionDerivativesToWeights(CnnDoubleLayerState state, int threadTag) {
		for (int toFeature = 0; toFeature< params.nFeatures ; toFeature++)
			backpropagateConvolutionDerivativesToWeights(state, toFeature, threadTag);
		
	}
	private void backpropagateConvolutionDerivativesToWeights(CnnDoubleLayerState state, int toFeature, int threadTag) {
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
		
		
    	DoubleArray3D deltaWeightsDerivatives = new DoubleArray3D(wcf, hcf, params.nInputFeatures); 
		for (int xc = 0 ; xc < state.convolutionMaps.width ; xc ++)
			for (int yc = 0 ; yc < state.convolutionMaps.height ; yc ++)
			{
				double aDer = params.signallingFunction.applyDO(state.convolutionMaps.get(xc, yc, toFeature));
				Debug.checkNaN(aDer);
				double cDer = state.convolutionMapsDerivatives.get(xc, yc, toFeature);
				Debug.checkNaN(cDer);
				double actDer = aDer * cDer;
				
				if (threadTag == -1)
					biasesDerivatives[toFeature] += actDer;
				else 
					tempBiasesDerivatives[threadTag][toFeature] += actDer;
				
				
				
//					Debug.out(actDer);
				for (int fromFeature = 0; fromFeature< params.nInputFeatures; fromFeature++)
					for (int xi = 0 ; xi < wcf ; xi++)
						for (int yi = 0 ; yi < hcf ; yi++)
						{
							double in = state.inputMaps.get(xc +xi, yc+yi, fromFeature);
							double wDer = actDer * in;
							Debug.checkNaN(wDer);
							deltaWeightsDerivatives.add(xi, yi, fromFeature, wDer );
							Debug.checkNaN(deltaWeightsDerivatives.get(xi, yi, fromFeature)
									);
							if (state.inputMapsDerivatives != null) 
							{
								double w = weights[toFeature].get(xi, yi, fromFeature);
								double inDer = actDer * w;
								state.inputMapsDerivatives.add(xc+xi, yc+yi, fromFeature, inDer);
							}
						}
			}
		if (threadTag == -1)
			weightsDerivatives[toFeature].add(deltaWeightsDerivatives);
		else 
			tempWeightsDerivatives[threadTag][toFeature].add(deltaWeightsDerivatives);
	}


	public void resetDerivatives() {
		Arrays.fill(biasesDerivatives, 0);
		for (DoubleArray3D weightDersFeature : weightsDerivatives)
			weightDersFeature.fill(0);
//		state.convolutionMapsDerivatives.fill(0);
		
	}
	
	
	
	
	/*
	 *  I / O
	 */

	@Override
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
				.add("biases", JsonArrays.toJsonArray(biases))
				.add("weights", JsonArrays.toJsonArray(weights))
				.add("poolingFunction", poolingFunction.toString())
				.add("params", params.toJsonObject())
				.build();
	}

	@Override
	public CnnDoubleLayer fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		
		biases = JsonArrays.fromJsonArray1D(o.getJsonArray("biases"));
		weights= JsonArrays.fromJsonArray1D(DoubleArray3D.class , o.getJsonArray("weights"));
		poolingFunction = PoolingFunction.fromString(o.getString("poolingFunction"));
		params = new LayerParameters(network.netParams).fromJsonObject(o.getJsonObject("params"));
		construct(params);
		return this;
	}

	public void initializeTemporaryDerivatives(int nThreads) {
		tempBiasesDerivatives = new double[nThreads][biasesDerivatives.length];
		tempWeightsDerivatives = new DoubleArray3D[nThreads][weightsDerivatives.length];
		for (int t = 0; t< nThreads; t++)
			for (int f = 0 ; f < params.nFeatures ; f++)
				tempWeightsDerivatives[t][f] 
						= new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);

	}

	public void mergeTemporaryDerivatives(int nThreads) {
		Arrays.fill(biasesDerivatives, 0); 
		for (int t = 0; t< nThreads; t++)
			for (int f = 0 ; f < params.nFeatures ; f++)
				biasesDerivatives[f] += tempBiasesDerivatives[t][f];
		
		
		for (int f = 0 ; f < params.nFeatures ; f++)
		{
			weightsDerivatives[f].fill(0);
			for (int t = 0; t< nThreads; t++)
				weightsDerivatives[f].add(tempWeightsDerivatives[t][f]);
		}
	}



	
	
}
