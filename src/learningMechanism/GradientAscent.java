package learningMechanism;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import network.Network;
import network.LayerParameters.NetworkParameters;

public class GradientAscent extends LearningMechanism<GradientAscent.GradientAscentState>{

	public static class GradientAscentState extends LearningMechanism.LearningState {

		public GradientAscentState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
		}

		@Override
		public void reset() {
		}
		
	}

	@Override
	public void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network) {
		if (cnnDoubleLayer.locked)
			return;
		for (int f = 0; f< cnnDoubleLayer.params.nFeatures ; f++)
		{
			cnnDoubleLayer.weights[f].add(cnnDoubleLayer.weightsDerivatives[f].times(cnnDoubleLayer.params.netParams.etaWeights));
		}
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		if (cnnDoubleLayer.locked)
			return;
		for (int f = 0; f< cnnDoubleLayer.params.nFeatures ; f++)
		{
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

	
	public static class GAparameter extends LearningParameter {

		public GAparameter(NetworkParameters netParams) {
			super(netParams);
		}


		@Override
		public double computeUpdate(double der) {
			return der * netParams.etaWeights;
		}
		
	}
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		return new GAparameter(netParams);
	}


}
