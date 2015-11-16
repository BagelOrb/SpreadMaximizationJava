package objective;

import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;

public class GeneralizedHebbianObjective extends ObjectiveFunction{

	public GeneralizedHebbianObjective(Network nw) {
		super(nw);
		// TODO Auto-generated constructor stub
	}

	public GeneralizedHebbianObjective(int dim) {
		super(dim);
		// TODO Auto-generated constructor stub
	}

	@Override
    double computeValue(NetworkState[] states) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		// TODO Auto-generated method stub
		
	}

}
