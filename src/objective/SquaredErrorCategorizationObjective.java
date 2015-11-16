package objective;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import util.math.Math2;

public class SquaredErrorCategorizationObjective extends CategorizationObjective {

	public SquaredErrorCategorizationObjective(Network nw) { super(nw); }

	@Override
    double computeValue(NetworkState[] states) { 
		double totalError = 0;
		for (NetworkState nwState : states) 
		{
			CnnDoubleLayerState state = nwState.getLast(); 
            InputSample out = state.outputMaps;
			for (int f = 0; f<out.depth;f++) 
				for (int xO = 0; xO<out.width; xO++)
					for (int yO = 0; yO<out.height; yO++) 
                        totalError += Math2.square(((state.inputMaps.cat==f)? 1:0) - state.outputMaps.get(xO,yO,f));
		}
		
		return -totalError*.5;
	}

	@Override
    public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
        return ((state.inputMaps.cat==feature)? 1:0) - state.outputMaps.get(xOutput,yOutput,feature);
	}

	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
		return;
	}
	
    public String toString() { return "Classification distance = "+ lastValue +"\t(square-average distance to objective outputs = "+Math.sqrt(-lastValue/network.currentBatchSize)+")"; }

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
	}
}
