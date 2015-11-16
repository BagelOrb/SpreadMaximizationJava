package objective;

import java.util.Arrays;

import javax.swing.text.Position.Bias;

import io.Images.InputSample;
import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;
import learningMechanism.LearningMechanism.LearningState;
import network.LayerParameters;
import network.Network;
import network.NetworkState;
import network.analysis.Debug;
import pooling.ArgMaxPooling.ArgMaxState;
import pooling.SoftArgMaxPooling;
import pooling.SoftArgMaxPooling.SoftArgMaxState;
import util.basics.DoubleArray3D;
import util.basics.DoubleArray3Dext.Loc;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.MathFunction2D;
import util.math.MathFunction3D;

public class ConvAutoEncoder extends ObjectiveFunction {

	public ConvAutoEncoder(Network nw) {
		super(nw);
		
	}
	public void construct(CnnDoubleLayer lastLayer) {
		int nif = lastLayer.params.nInputFeatures;
		reconstructionBiases = new double[nif]; 		// "one bias per input channel"
		reconstructionBiasesDerivatives = new double[nif]; 		// "one bias per input channel"
		
		switch (network.netParams.learningMechanismType) {
		case MOMENTUM:
			reconstructionBiasVelocities = new double[nif];
			break;
		case RPROP:
			reconstructionBiasVelocities = new double[nif];
			reconstructionBiasDerivativesLast = new double[nif];
			Arrays.fill(reconstructionBiasVelocities, lastLayer.params.netParams.delta_rProp_init);
			break;
		default:
			// don't initialize learning state arrays!
			break;
		}
		
	}
	public LearningState biasLearningState;

	public double[] reconstructionBiases; 		// "one bias per input channel"
	public double[] reconstructionBiasesDerivatives; 		// "one bias per input channel"

	public double[] reconstructionBiasVelocities;
	
	public double[] reconstructionBiasDerivativesLast;
	
	private class DecodeState {
		final CnnDoubleLayerState encodeState; 
		
		InputSample inputReconstruction;
		final InputSample inputReconstructionActivation;
		final DoubleArray3D poolMaps;
		
		InputSample inputReconstructionActivationDerivatives;
		final DoubleArray3D poolMapsDerivatives;
		
		public DecodeState(CnnDoubleLayerState encodeState) {
			this.encodeState = encodeState;
			int wcm = encodeState.convolutionMaps.width;
			int hcm = encodeState.convolutionMaps.height;
			int nif = encodeState.params.nInputFeatures;
			int nf = encodeState.params.nFeatures;
			
			poolMaps = new DoubleArray3D(wcm, hcm, nf);
			poolMapsDerivatives = new DoubleArray3D(wcm, hcm, nf);
			
			int wi = encodeState.inputMaps.width;
			int hi = encodeState.inputMaps.height;
			int tag = encodeState.inputMaps.tag;
			int cat = encodeState.inputMaps.cat;
			
			inputReconstructionActivation = new InputSample(tag, wi, hi, nif, cat);
			inputReconstruction = new InputSample(tag, wi, hi, nif, cat);
			
			inputReconstructionActivationDerivatives = new InputSample(tag, wi, hi, nif, cat);
			
		}
	}
	
	@Override
	double computeValue(NetworkState[] states) {
		// TODO Auto-generated method stub
		return lastValue; // computation done in addWeightDerivatives
	}

	@Override
	public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		return 0;
	}

	
	private static final double log2 = Math.log(2);
	@Override
	public void addWeightDerivatives(NetworkState[] states) {
//		done in Network.updateBatch!
//		Arrays.fill(reconstructionBiasesDerivatives, 0);
//		for (DoubleArray3D wDers : weightsDerivatives)
//			wDers.fill(0);
		
		lastValue = 0;
		
		for (NetworkState nwState : states)
		{
			CnnDoubleLayerState encodeState = nwState.getLast();
			DecodeState decodeState = reconstruct(encodeState);

			
			decodeState.inputReconstructionActivationDerivatives = encodeState.inputMaps.zippedWith(MathFunction2D.subtraction, decodeState.inputReconstruction);
			
			double entropyHere = encodeState.inputMaps.zipFold(
					new MathFunction3D() {
						
						@Override
						public double apply(double t, double y, double entropy) {
							double de = Math2.limit(-100, .5*((1+t)*Math.log(1+y) + (1-t)*Math.log(1-y)) - log2 , 100);
//							Debug.check(de>0);
							if (Double.isNaN(de))
								return entropy;
							
							double ret = entropy + de;
							return ret;
						}
					}
					, 0, decodeState.inputReconstruction);
			
			lastValue += entropyHere;
			
//			Debug.check(entropyHere >=0 );
//			lastValue -= decodeState.inputReconstructionActivationDerivatives.vectorLength();// /decodeState.inputReconstructionActivationDerivatives.size(); // TODO: this is not really correct! its not cross entropy!
			//                 								 = square difference!  /\
			
			Debug.checkNaN(lastValue);
			backpropagateInputReconstructionActivationDerivatives(decodeState);
			
			backpropagatePoolDerivatives(decodeState);
			
			encodeState.cnnDoubleLayer.backpropagateConvolutionDerivativesToWeights(encodeState, -1);
		}
		
//		Debug.out(lastValue);
//		int i = 1;
//		lastValue /= states.length;
	}



	@Override
	public void computeCommonDerivativeStuff(NetworkState[] states) {
	}
	
	
	
	
	@Override 
	public String toString() {
		return "tanh entropy error = "+lastValue;
//		int wp = network.getLastLayer().params.widthPoolingMap(network.getLastLayer().params.widthLocalInput);
//		return "square-mean reconstruction activation difference: " +Math.sqrt(-lastValue/network.netParams.batchSize/wp/wp);
	}
	
	
	
	
	/*
	 *    D E C O D E
	 */
	
	public DecodeState reconstruct(CnnDoubleLayerState state) {
		CnnDoubleLayer layer = state.cnnDoubleLayer;
		LayerParameters params = layer.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
        int wps = params.widthPoolingStep;
        int hps = params.heightPoolingStep;

        DecodeState decodeState = new DecodeState(state);
		
		for (int f = 0; f< params.nFeatures; f++)
			for (int xp = 0; xp < state.poolingMaps.width; xp++)
				for (int yp = 0; yp < state.poolingMaps.height; yp++)
				{
					if (state.poolingState instanceof ArgMaxState)
					{
						ArgMaxState pState = (ArgMaxState) state.poolingState;
						int xc = xp*wps + pState.xcArgMax.get(xp, yp, f);
						int yc = yp*hps + pState.ycArgMax.get(xp, yp, f);
						decodeState.poolMaps.add(xc, yc, f, state.convolutionMaps.get(xc, yc, f));
//						addToReconstruction(reconstructionActivation, xc, yc, f, state.convolutionMaps.get(xc, yc, f), state);
						
					} else if (state.poolingState instanceof SoftArgMaxState)
					{
						SoftArgMaxPooling pLayer = (SoftArgMaxPooling) layer.poolingFunction;
						SoftArgMaxState pState = (SoftArgMaxState) state.poolingState;
						
						double totalContribution = pState.totalContributions.get(xp, yp, f);
						if (totalContribution == 0) 
							continue;
						
						for (int xcf = 0; xcf < wpf; xcf++)
							for (int ycf = 0; ycf < hpf; ycf++)
							{
								int xc = xp*wps + xcf;
								int yc = yp*hps + ycf;
								
								
								double inV = state.convolutionMaps.get(xc, yc, f);
								double fin = pLayer.innerFunction.apply(inV);
				                double contribution = Math.exp(pLayer.hardness * fin);

								double localVal = fin * contribution / totalContribution;
								decodeState.poolMaps.add(xc, yc, f, localVal);
//								addToReconstruction(reconstructionActivation, xc, yc, f, localVal , state);
							}
					} else Debug.err("Unknown pooling state type: "+state.poolingState.getClass());
				}
		
		for (Loc l : decodeState.poolMaps)
			signalPoolval(decodeState, l.x, l.y, l.z);
		
		for (Loc l : decodeState.inputReconstructionActivation)
			decodeState.inputReconstructionActivation.add(l.x, l.y, l.z, reconstructionBiases[l.z]);
		// "one bias per input channel"
		
		decodeState.inputReconstruction = decodeState.inputReconstructionActivation.mapped(network.getLastLayer().params.signallingFunction);

		return decodeState;
	}

	private void signalPoolval(DecodeState decodeState, int xc, int yc, int f) {
		CnnDoubleLayer layer = decodeState.encodeState.cnnDoubleLayer;
		LayerParameters params = layer.params;
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
        
		double poolVal = decodeState.poolMaps.get(xc, yc, f);
		
		for (int iF = 0; iF< params.nInputFeatures; iF++)
			for (int xi = 0; xi < wcf; xi++)
				for (int yi = 0; yi < hcf; yi++)
				{
					decodeState.inputReconstructionActivation.add(xc + xi, yc + yi, iF, layer.weights[f].get(xi, yi, iF) * poolVal);
				}
		
        
	}
	
	
	
	
	
	/*
	 *    D E R I V A T I V E S
	 */
	public void resetDerivatives() {
		Arrays.fill(reconstructionBiasesDerivatives, 0);
	}
	
	private void backpropagateInputReconstructionActivationDerivatives(DecodeState decodeState) {
//		poolMapsDerivatives
		
		LayerParameters params = decodeState.encodeState.params;
		
		DoubleArray3D poolMapsDerivatives = decodeState.poolMapsDerivatives;
		
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
		
		for (int f = 0; f< params.nFeatures ; f++)
		{
	    	DoubleArray3D deltaWeightsDerivatives = new DoubleArray3D(wcf, hcf, params.nInputFeatures); 
			for (int xc = 0 ; xc < poolMapsDerivatives.width ; xc ++)
				for (int yc = 0 ; yc < poolMapsDerivatives.height ; yc ++)
				{
					
					double inPool = decodeState.poolMaps.get(xc, yc, f);
					for (int iF = 0; iF< params.nInputFeatures; iF++)
						for (int xcf = 0 ; xcf < wcf ; xcf++)
							for (int ycf = 0 ; ycf < hcf ; ycf++)
							{
								double actDer = decodeState.inputReconstructionActivationDerivatives.get(xc + xcf, yc + ycf, iF);
								
								double wDer = actDer * inPool;
								
								
								Debug.checkNaN(wDer);
								decodeState.encodeState.cnnDoubleLayer.weightsDerivatives[f].add(xcf, ycf, iF, wDer);
								
								
								double w = decodeState.encodeState.cnnDoubleLayer.weights[f].get(xcf, ycf, iF);
								double poolDer = actDer * w;
								decodeState.poolMapsDerivatives.add(xc, yc, f, poolDer);
							}
				}
			
			for (Loc l : decodeState.inputReconstructionActivationDerivatives)
				reconstructionBiasesDerivatives[l.z] +=  decodeState.inputReconstructionActivationDerivatives.get(l);
			
	//		if (threadTag == -1)
			decodeState.encodeState.cnnDoubleLayer.weightsDerivatives[f].add(deltaWeightsDerivatives);
		}

	}

	
	

	private void backpropagatePoolDerivatives(DecodeState decodeState) {
		if (decodeState.encodeState.poolingState instanceof ArgMaxState)
			backpropagatePoolDerivatives(decodeState, (ArgMaxState) decodeState.encodeState.poolingState);
		else if (decodeState.encodeState.poolingState instanceof SoftArgMaxState)
			backpropagatePoolDerivatives(decodeState, (SoftArgMaxState) decodeState.encodeState.poolingState);
	}


	private void backpropagatePoolDerivatives(DecodeState decodeState, ArgMaxState poolingState) {
		LayerParameters params = decodeState.encodeState.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		
		CnnDoubleLayerState encodeState = decodeState.encodeState;
		DoubleArray3D convolutionMapsDerivatives = decodeState.encodeState.convolutionMapsDerivatives;
		DoubleArray3D poolMapsDerivatives = decodeState.poolMapsDerivatives;
		
		InputSample poolingMaps = encodeState.poolingMaps; // the pooled values
		DoubleArray3D poolMaps = decodeState.poolMaps; // the contribution values

		for (Loc lp : poolingMaps) 
		{
			int xpf = poolingState.xcArgMax.get(lp.x, lp.y, lp.z);
			int ypf = poolingState.ycArgMax.get(lp.x, lp.y, lp.z);
			int x = lp.x*wps + xpf;
			int y = lp.y*hps + ypf;
			int z = lp.z;
			
			double out = poolMaps.get(x, y, z);

			double in = encodeState.convolutionMaps.get(x, y, z);

			Debug.check(in==out);
			
			double der = poolMapsDerivatives.get(x, y, z);
			
			convolutionMapsDerivatives.set(x, y, z, der); 
		}
		//		decodeState.encodeState.convolutionMapsDerivatives = decodeState.poolMapsDerivatives;
	}

	private void backpropagatePoolDerivatives(DecodeState decodeState, SoftArgMaxState poolingState) {
		LayerParameters params = decodeState.encodeState.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		
		CnnDoubleLayerState encodeState = decodeState.encodeState;
		DoubleArray3D convolutionMapsDerivatives = decodeState.encodeState.convolutionMapsDerivatives;
		DoubleArray3D poolMapsDerivatives = decodeState.poolMapsDerivatives;
		
		
		InputSample poolingMaps = encodeState.poolingMaps; // the pooled values
		DoubleArray3D poolMaps = decodeState.poolMaps; // the contribution values
		
		double hardness = ((SoftArgMaxPooling) encodeState.cnnDoubleLayer.poolingFunction).hardness;
		DifferentiableFunction innerFunction = ((SoftArgMaxPooling) encodeState.cnnDoubleLayer.poolingFunction).innerFunction;
		
		
		for( Loc lp : poolingMaps)
		{
			double poolDerTotal = 0;
			for (int xpf = 0; xpf < wpf; xpf++ )
				for (int ypf = 0; ypf < hpf; ypf++ )
				{
					int x = lp.x*wps + xpf;
					int y = lp.y*hps + ypf;
					int z = lp.z;
					poolDerTotal += poolMaps.get(x, y, z) * poolMapsDerivatives.get(x, y, z);

				}

			for (int xpf = 0; xpf < wpf; xpf++ )
				for (int ypf = 0; ypf < hpf; ypf++ )
				{
					int x = lp.x*wps + xpf;
					int y = lp.y*hps + ypf;
					int z = lp.z;

					double outDer = poolMapsDerivatives.get(x, y, z);
					double out = poolMaps.get(x, y, z);

					double in = encodeState.convolutionMaps.get(x, y, z);

			        double totalContribution = poolingState.totalContributions.get(lp.x, lp.y, lp.z);
			        
			        double ff = Math.exp(hardness * innerFunction.apply(in));
			        
					double contribution = ((totalContribution ==0)? 0 :  
							 				ff
											/ totalContribution 		);
					double fDer = hardness * innerFunction.applyD(in);

					Debug.check(contribution < 1E8);
					Debug.check(totalContribution < 1E10);
					
					double der = outDer * ( contribution + fDer * out) - fDer * contribution * poolDerTotal;
					
//					Debug.check(der *der < outDer*outDer);
					
					Debug.checkNaN(der);
					convolutionMapsDerivatives.add(x, y, z, der);
				}
			
			
		}
			
	}


	
	
	
	
	
}
