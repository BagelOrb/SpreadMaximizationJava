package objective;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;

public abstract class CategorizationObjective extends ObjectiveFunction {

	public CategorizationObjective(Network nw) {
		super(nw);
	}

	public CategorizationObjective(int dim) {
		super(dim);
	}

	public double getErrorRate(Network network, NetworkState[] states) { 
		double totalError = 0;
		for (NetworkState nwState : states) {
			CnnDoubleLayerState state = nwState.getLast();
            InputSample out = state.outputMaps;
			double sampleBestOut = Double.NEGATIVE_INFINITY;
			int bestFeat = -1;
			for (int f = 0; f<out.depth;f++) {
				double featBestOut = Double.NEGATIVE_INFINITY;
				for (int xO = 0; xO<out.width; xO++)
					for (int yO = 0; yO<out.height; yO++) 
						if (out.get(xO,yO,f) > featBestOut) featBestOut = out.get(xO,yO,f);
				if (featBestOut>sampleBestOut) {
					sampleBestOut = featBestOut;
					bestFeat = f;
				}
			}
            int actualFeat = state.inputMaps.cat;
			totalError += (actualFeat==bestFeat)? 0 : 1 ;
		}
		
		return totalError/states.length;
	
	}

}
