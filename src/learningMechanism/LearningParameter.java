package learningMechanism;

import network.LayerParameters.NetworkParameters;

public abstract class LearningParameter {

	NetworkParameters netParams;
	
	double val;
	
	
	public LearningParameter(NetworkParameters netParams) {
		this.netParams = netParams;
	}

	//	public abstract LearningParameter newLearningParameter();

	public void setValue(double val) {
		this.val = val;
	}
	
	public abstract double computeUpdate(double der);
	
	public String toString() {
		return ""+val;
	}
	
}
