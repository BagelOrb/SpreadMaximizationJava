package learningMechanism;

import java.util.Arrays;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import network.LayerParameters;
import network.Network;
import network.LayerParameters.NetworkParameters;
import network.analysis.Debug;
import util.basics.DoubleArray3D;
import util.math.MathFunction1D;
import util.math.MathFunction2D;

public class Rprop extends LearningMechanism<Rprop.RpropState>{

	public class RpropState extends LearningMechanism.LearningState{

		DoubleArray3D[] weightDeltas;
		double[] biasDeltas;
		
		DoubleArray3D[] lastWeightDerivatives;
		double[] lastBiasDerivatives;
		
		public RpropState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
			
			weightDeltas = new DoubleArray3D[params.nFeatures];
			for (int f = 0 ; f < params.nFeatures ; f++)
			{
				weightDeltas[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
			}
			biasDeltas = new double[params.nFeatures];

			reset();
			
			lastWeightDerivatives = new DoubleArray3D[params.nFeatures];
			for (int f = 0 ; f < params.nFeatures ; f++)
				lastWeightDerivatives[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);
			
			lastBiasDerivatives = new double[params.nFeatures];
		}

		@Override
		public void reset() {
			for (DoubleArray3D v : weightDeltas)
				v.fill(params.netParams.delta_rProp_init);
			Arrays.fill(biasDeltas, params.netParams.delta_rProp_init);
		}

	}

	

	public Rprop() {
	}

	
	@Override
	public void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network) {
		if (cnnDoubleLayer.locked)
			return;
		final LayerParameters params = cnnDoubleLayer.params;
		
		RpropState rpropState = (RpropState) cnnDoubleLayer.learningState;
		
		DoubleArray3D[] weightDeltas = rpropState.weightDeltas;
		DoubleArray3D[] weightDerivatives = cnnDoubleLayer.weightsDerivatives;
		DoubleArray3D[] lastWeightDerivatives = rpropState.lastWeightDerivatives;

//		double biasDeltaTotal = 0;
//		for (int f = 0 ; f< params.nFeatures; f++)
//			biasDeltaTotal += rpropState.biasDeltas[f];
//		
//		final double etaPlus = 1+(params.netParams.eta_rPropPlus-1)/(1+biasDeltaTotal*1000);
//		final double etaMin = 1+(params.netParams.eta_rPropMin-1)/(1+biasDeltaTotal*1000);

		final double etaPlus = params.netParams.eta_rPropPlus;
		final double etaMin  = params.netParams.eta_rPropMin;

		for (int f = 0 ; f< params.nFeatures; f++)
		{
			
			// weights
			weightDeltas[f].multiply(weightDerivatives[f].zippedWith(
					new MathFunction2D() {
						@Override public double apply(double wD, double lD) {
							if (wD*lD>0) return etaPlus;
							if (wD*lD<0) return etaMin;
							return 1;
						}}
					, lastWeightDerivatives[f]));
	
			//weights.weightPooling += Math.signum(wDerWeightPool)*rPropValues.deltas.weightPooling;
			DoubleArray3D deltaWeights = weightDerivatives[f].mapped(new MathFunction1D(){
				@Override public double apply(double arg) { return Math.signum(arg);
				}}).times(weightDeltas[f]);
			
			cnnDoubleLayer.weights[f].add(deltaWeights);
			
			
			
			
			lastWeightDerivatives[f].set(weightDerivatives[f]);
//			rpropState.lastWeightDerivatives[f] = cnnDoubleLayer.weightsDerivatives[f];
			
		}
		
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		if (cnnDoubleLayer.locked)
			return;
		LayerParameters params = cnnDoubleLayer.params;
		
		RpropState rpropState = (RpropState) cnnDoubleLayer.learningState;
		
		final double etaPlus = params.netParams.eta_rPropPlus;
		final double etaMin = params.netParams.eta_rPropMin;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			double bDer = cnnDoubleLayer.biasesDerivatives[f];
			double bDerLast = rpropState.lastBiasDerivatives[f];
			
			if (bDer * bDerLast > 0)
				rpropState.biasDeltas[f] *= etaPlus;
			else if (bDer * bDerLast < 0)
				rpropState.biasDeltas[f] *= etaMin;
			
			cnnDoubleLayer.biases[f] += Math.signum( cnnDoubleLayer.biasesDerivatives[f] ) * rpropState.biasDeltas[f];
			
			
			rpropState.lastBiasDerivatives[f] = bDer;
		}
		
		
	}


	@Override
	public RpropState newState(CnnDoubleLayer cnnDoubleLayer) {
		return new RpropState(cnnDoubleLayer);
	}


	@Override
	public void updateBiases(ConvAutoEncoder objective) {
		if (objective.network.getLastLayer().locked)
			return;
		LayerParameters params = objective.network.getLastLayer().params;
		
		final double etaPlus = params.netParams.eta_rPropPlus;
		final double etaMin = params.netParams.eta_rPropMin;
		
		for (int f = 0 ; f< params.nInputFeatures; f++)
		{
			double bDer = objective.reconstructionBiasesDerivatives[f];
			double bDerLast = objective.reconstructionBiasDerivativesLast[f];
			
			if (bDer * bDerLast > 0)
				objective.reconstructionBiasVelocities[f] *= etaPlus;
			else if (bDer * bDerLast < 0)
				objective.reconstructionBiasVelocities[f] *= etaMin;
			
			objective.reconstructionBiases[f] += Math.signum( objective.reconstructionBiasesDerivatives[f] ) * objective.reconstructionBiasVelocities[f];
			
			
			objective.reconstructionBiasDerivativesLast[f] = objective.reconstructionBiasesDerivatives[f];
		}

		
	}
	

	
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		// TODO Auto-generated method stub
		Debug.err("newLearningParameter not implemented for "+this.getClass());
		return null;
	}
}
