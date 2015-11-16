package learningMechanism;

import java.util.Arrays;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import network.LayerParameters;
import network.LayerParameters.NetworkParameters;
import network.analysis.Debug;
import network.Network;
import util.basics.DoubleArray3D;

public class TKMomentumAtNeuron extends LearningMechanism<TKMomentumAtNeuron.TKrpropMomentumUnitState> {

	public static class TKrpropMomentumUnitState extends LearningMechanism.LearningState {

		double[] weightVelocities;
		double[] biasVelocities;
		
		DoubleArray3D[] lastWeightDerivatives;
		double[] lastWeightDerivativesLengths;
		double[] lastBiasDerivatives;

		public TKrpropMomentumUnitState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
			
			weightVelocities = new double[params.nFeatures];
			biasVelocities = new double[params.nFeatures];
			
			
			lastWeightDerivatives = new DoubleArray3D[params.nFeatures];
			for (int f = 0 ; f < params.nFeatures ; f++)
				lastWeightDerivatives[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
			
			lastBiasDerivatives = new double[params.nFeatures];
			lastWeightDerivativesLengths = new double[params.nFeatures];

			reset();
		}

		@Override
		public void reset() {
			Arrays.fill(weightVelocities, params.netParams.delta_rProp_init);
			Arrays.fill(biasVelocities, params.netParams.delta_rProp_init);
			Arrays.fill(lastWeightDerivativesLengths, 0);
		}
		
	}

	@Override
	public void updateWeights(CnnDoubleLayer layer, Network network) {
		LayerParameters params = layer.params;
		NetworkParameters netParams = params.netParams;
		
		TKrpropMomentumUnitState learningState = (TKrpropMomentumUnitState) layer.learningState;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			double weightDerivativesLength = layer.weightsDerivatives[f].vectorLength();
			
			double dotProduct = layer.weightsDerivatives[f].dotProduct(learningState.lastWeightDerivatives[f]);
			
			// angle between 0 and 1
			double angle = Math.acos(dotProduct / (weightDerivativesLength * learningState.lastWeightDerivativesLengths[f])) / Math.PI;
			if (Double.isInfinite(angle) || Double.isNaN(angle))
				angle = 0;
			
			double eta = netParams.eta_rPropMin + (netParams.eta_rPropPlus-netParams.eta_rPropMin ) * angle;
			
			learningState.weightVelocities[f] *= eta;
			
//			learningState.weightVelocities[f] = Math2.limit(.0001, learningState.weightVelocities[f], 10000);
			
			
			
			// the update itself!
			if (weightDerivativesLength==0)
				layer.weights[f].add(layer.weightsDerivatives[f]);
			else
			{
				layer.weightsDerivatives[f].multiply(learningState.weightVelocities[f]/weightDerivativesLength);
//				layer.weightsDerivatives[f].multiply(learningState.weightVelocities[f]);
				layer.weights[f].add(layer.weightsDerivatives[f]); // changes derivatives!
			}
			
			
			
			
			learningState.lastWeightDerivatives[f].set(layer.weightsDerivatives[f]);

			learningState.lastWeightDerivativesLengths[f] = weightDerivativesLength;
		}
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		LayerParameters params = cnnDoubleLayer.params;
			
		TKrpropMomentumUnitState learningState = (TKrpropMomentumUnitState) cnnDoubleLayer.learningState;
		
		final double etaPlus = params.netParams.eta_rPropPlus;
		final double etaMin = params.netParams.eta_rPropMin;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			double bDer = cnnDoubleLayer.biasesDerivatives[f];
			double bDerLast = learningState.lastBiasDerivatives[f];
			if (bDer * bDerLast > 0)
				learningState.biasVelocities[f] *= etaPlus;
			else if (bDer * bDerLast < 0)
				learningState.biasVelocities[f] *= etaMin;
			
//			learningState.biasVelocities[f] = Math2.limit(.00001, learningState.biasVelocities[f], 10000);
			
//			cnnDoubleLayer.biases[f] +=  cnnDoubleLayer.biasesDerivatives[f]  * learningState.biasVelocities[f];
			cnnDoubleLayer.biases[f] += 
					Math.signum( cnnDoubleLayer.biasesDerivatives[f] ) 
					* learningState.biasVelocities[f] ;
//					* params.netParams.etaWeights;
			
			
			learningState.lastBiasDerivatives[f] = bDer;
		}
			
			

	}

	@Override
	public TKrpropMomentumUnitState newState(CnnDoubleLayer cnnDoubleLayer) {
		return new TKrpropMomentumUnitState(cnnDoubleLayer);
	}
	
	@Override
	public void updateBiases(ConvAutoEncoder objective) {
		Debug.err("ConvAutoEncoder learning not implemented for "+this.getClass()+"!!");
	}
	
	
	
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		// TODO Auto-generated method stub
		Debug.err("newLearningParameter not implemented for "+this.getClass());
		return null;
	}
}
