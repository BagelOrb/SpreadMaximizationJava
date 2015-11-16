package learningMechanism;

import java.util.Arrays;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import learningMechanism.LimitedGradientAscent.LGAparameter;
import network.LayerParameters;
import network.Network;
import network.LayerParameters.NetworkParameters;
import network.analysis.Debug;
import util.basics.DoubleArray3D;
import util.math.Math2;
import util.math.MathFunction1D;

public class Momentum extends LearningMechanism<Momentum.MomentumState>{

	public class MomentumState extends LearningMechanism.LearningState{

		DoubleArray3D[] weightVelocities;
		double[] biasVelocities;
		
		public MomentumState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
			
			weightVelocities = new DoubleArray3D[params.nFeatures];
			for (int f = 0 ; f < params.nFeatures ; f++)
				weightVelocities[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);

			biasVelocities = new double[params.nFeatures];
		}

		@Override
		public void reset() {
			for (DoubleArray3D v : weightVelocities)
				v.fill(0);
			Arrays.fill(biasVelocities, 0);
		}

	}

	

	public Momentum() {
		
	}

	
	@Override
	public void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network) {
		if (cnnDoubleLayer.locked)
			return;
		
		final LayerParameters params = cnnDoubleLayer.params;
		
		MomentumState momentumState = (MomentumState) cnnDoubleLayer.learningState;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			momentumState.weightVelocities[f].multiply(network.netParams.alpha_momentum);
			momentumState.weightVelocities[f].add(cnnDoubleLayer.weightsDerivatives[f].times(network.netParams.etaWeights));
			
			

			Debug.checkNaN(momentumState.weightVelocities[f].get(0, 0, 0));

			if (params.netParams.rescaleWeights)
				momentumState.weightVelocities[f].map(new MathFunction1D() {
					
					@Override
					public double apply(double v) {
						if (Math.abs(v) > params.netParams.weightMax )
							return Math.signum(v) * params.netParams.weightMax;
						return v;
					}
				});
			
			cnnDoubleLayer.weights[f].add(momentumState.weightVelocities[f]);
		}
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		if (cnnDoubleLayer.locked)
			return;

		LayerParameters params = cnnDoubleLayer.params;
		
		MomentumState momentumState = (MomentumState) cnnDoubleLayer.learningState;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			momentumState.biasVelocities[f] *= params.netParams.alpha_momentum; // .1 * TODO: implement this option?
			momentumState.biasVelocities[f] += cnnDoubleLayer.biasesDerivatives[f] * params.netParams.etaBiases;// ; // TODO: implement this option?
			
			
			Debug.checkNaN(momentumState.biasVelocities[f]);
			
			if (Math.abs(momentumState.biasVelocities[f]) > 10)
				momentumState.biasVelocities[f] = 10 * Math.signum(momentumState.biasVelocities[f]);
			
			cnnDoubleLayer.biases[f] += Math2.limit(-1E6, momentumState.biasVelocities[f], 1E6); 
//			cnnDoubleLayer.biases[f] += cnnDoubleLayer.biasesDerivatives[f] * etaWeights; // TODO: put this option somewhere?
		}
		
	}

	@Override
	public void updateBiases(ConvAutoEncoder objective) {
		if (objective.network.getLastLayer().locked)
			return;

		LayerParameters params = objective.network.getLastLayer().params;
		
		
		for (int f = 0 ; f< params.nInputFeatures; f++)
		{
			objective.reconstructionBiasVelocities[f] *= params.netParams.alpha_momentum; // .1 * TODO: implement this option?
			objective.reconstructionBiasVelocities[f] += objective.reconstructionBiasesDerivatives[f] * params.netParams.etaBiases;// ; // TODO: implement this option?
			
			
			Debug.checkNaN(objective.reconstructionBiasVelocities[f]);
			
			if (Math.abs(objective.reconstructionBiasVelocities[f]) > 10)
				objective.reconstructionBiasVelocities[f] = 10 * Math.signum(objective.reconstructionBiasVelocities[f]);
			
			objective.reconstructionBiases[f] += Math2.limit(-1E6, objective.reconstructionBiasVelocities[f], 1E6); 
//			cnnDoubleLayer.biases[f] += cnnDoubleLayer.biasesDerivatives[f] * etaWeights; // TODO: put this option somewhere?
		}
		
	}

	@Override
	public MomentumState newState(CnnDoubleLayer cnnDoubleLayer) {
		return new MomentumState(cnnDoubleLayer);
	}

	
	
	
	
	


	
	public static class MomentumParameter extends LearningParameter {

		public MomentumParameter(NetworkParameters netParams) {
			super(netParams);
		}

		double velocity = 0;

		@Override
		public double computeUpdate(double der) {
			
			velocity *= netParams.alpha_momentum;
			
			velocity += der * netParams.etaWeights;
			
			return velocity;

		}
		
	}
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		return new MomentumParameter(netParams);
	}

	
	
	
}
