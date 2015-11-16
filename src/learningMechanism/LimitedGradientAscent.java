package learningMechanism;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import learningMechanism.GradientAscent.GAparameter;
import network.LayerParameters.NetworkParameters;
import network.Network;

public class LimitedGradientAscent extends LearningMechanism<LimitedGradientAscent.GradientAscentState>{

	public static class GradientAscentState extends LearningMechanism.LearningState {

		public GradientAscentState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
		}

		@Override
		public void reset() {
		}
		
	}

	static double max = 1;
	@Override
	public void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network) {
		if (cnnDoubleLayer.locked)
			return;
		for (int f = 0; f< cnnDoubleLayer.params.nFeatures ; f++)
		{
			double l = cnnDoubleLayer.weightsDerivatives[f].vectorLength();
			if (l > max) 
				cnnDoubleLayer.weights[f].add(cnnDoubleLayer.weightsDerivatives[f].times(max / l * cnnDoubleLayer.params.netParams.etaWeights));
			else
				cnnDoubleLayer.weights[f].add(cnnDoubleLayer.weightsDerivatives[f].times(cnnDoubleLayer.params.netParams.etaWeights));
		}
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		if (cnnDoubleLayer.locked)
			return;
		for (int f = 0; f< cnnDoubleLayer.params.nFeatures ; f++)
		{
			if (Math.abs(cnnDoubleLayer.biasesDerivatives[f]) > max)
				cnnDoubleLayer.biases[f] += cnnDoubleLayer.params.netParams.etaBiases * max * Math.signum(cnnDoubleLayer.biasesDerivatives[f]);
			else
				cnnDoubleLayer.biases[f] += cnnDoubleLayer.params.netParams.etaBiases * cnnDoubleLayer.biasesDerivatives[f];
		}
	}

	@Override
	public GradientAscentState newState(CnnDoubleLayer cnnDoubleLayer) {
		return new GradientAscentState(cnnDoubleLayer);
	}

	@Override
	public void updateBiases(ConvAutoEncoder objective) {
		for (int f = 0; f< objective.reconstructionBiases.length; f++)
			objective.reconstructionBiases[f] += objective.network.getLastLayer().params.netParams.etaBiases 
												* objective.reconstructionBiasesDerivatives[f];
	}

	
	
	
	
	public static class LGAparameter extends LearningParameter {

		public LGAparameter(NetworkParameters netParams) {
			super(netParams);
		}


		@Override
		public double computeUpdate(double der) {
			if (Math.abs(der) > max)
				return netParams.etaWeights * max * Math.signum(der);
			else
				return der * netParams.etaWeights;

		}
		
	}
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		return new LGAparameter(netParams);
	}


}
