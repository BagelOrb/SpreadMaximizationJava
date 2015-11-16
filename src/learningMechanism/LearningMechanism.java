package learningMechanism;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import learningMechanism.LearningMechanism.LearningState;
import network.LayerParameters;
import network.LayerParameters.NetworkParameters;
import network.Network;

public abstract class LearningMechanism<LearningStateExt extends LearningState> {

	
	public static abstract class LearningState {
		
		CnnDoubleLayer cnnDoubleLayer;
		LayerParameters params;

		public LearningState(CnnDoubleLayer cnnDoubleLayer) {
			this.cnnDoubleLayer = cnnDoubleLayer; 
			this.params = cnnDoubleLayer.params;
		}
		
		public abstract void reset();
	}
	
	public abstract void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network);
	public abstract void updateBiases(CnnDoubleLayer cnnDoubleLayer);
	public abstract LearningStateExt newState(CnnDoubleLayer cnnDoubleLayer);
	
//	public abstract void resetState(CnnDoubleLayer cnnDoubleLayer);
	public void resetState(CnnDoubleLayer cnnDoubleLayer) {
		cnnDoubleLayer.learningState.reset();
	}
	public abstract void updateBiases(ConvAutoEncoder objective);
	
	public abstract LearningParameter newLearningParameter(NetworkParameters netParams);
}


