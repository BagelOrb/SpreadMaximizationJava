package objective;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.Static.OutputFunctionType;
import network.analysis.Debug;

public class CrossEntropyCategorizationObjective extends CategorizationObjective {

	private double averageOutForTrue;

	public CrossEntropyCategorizationObjective(Network nw) { super(nw); }

	@Override
    double computeValue(NetworkState[] states) { 
		if (network.getLastLayer().params.outputFunction != OutputFunctionType.SOFT_MAX_ACT)
			Debug.err("CrossEntropyCategorizationObjective expects softmax output function!!");

		double totalEntropy = 0;
		averageOutForTrue = 0;
		for (NetworkState nwState: states)
		{
			CnnDoubleLayerState state = nwState.getLast(); 
            InputSample out = state.outputMaps;
			for (int xO = 0; xO<out.width; xO++)
				for (int yO = 0; yO<out.height; yO++) { 
//					for (int f = 0; f<out.depth;f++)
                    int t = state.outputMaps.cat;
                    
                    if (t==-1)
                    	Debug.err("unclassified image! : "+state.inputMaps.tag);
                    
					if (t >= out.depth) 
						continue;
					
					averageOutForTrue += out.get(xO,yO,t);
					
					if (out.get(xO,yO,t)==0) 
					{ 
						totalEntropy += -20; 
						continue; 
					} 
					Debug.checkNaN(totalEntropy);
					
					double dE = Math.log(out.get(xO,yO,t));
					
					Debug.checkNaN(dE);
					
					totalEntropy += dE;
				}
			}
		
		averageOutForTrue /= states.length;
		return totalEntropy; // entropy is -1*totalEntropy, but we want to minimize it, so we output -1*real_entropy = totalEntropy
	}
	
	public double computeClassificationRate(NetworkState[] states) { 
		if (network.getLastLayer().params.outputFunction != OutputFunctionType.SOFT_MAX_ACT)
			Debug.err("CrossEntropyCategorizationObjective expects softmax output function!!");
		
		int nOuts = 0;
		int totalCorrect = 0;
		for (NetworkState nwState: states)
		{
			CnnDoubleLayerState state = nwState.getLast(); 
			InputSample out = state.outputMaps;
			for (int xO = 0; xO<out.width; xO++)
				for (int yO = 0; yO<out.height; yO++) { 
					nOuts++;
//					for (int f = 0; f<out.depth;f++)
					int t = state.outputMaps.cat;
					
					if (t==-1)
						Debug.err("unclassified image! : "+state.inputMaps.tag);
					
					if (t >= out.depth) 
						Debug.warn("not enough neurons to classify image with cat="+t);;
					
					if (out.get(xO,yO,t)==0) 
					{ 
						continue; 
					} 
					
					int max = -1;
					double maxVal = Double.NEGATIVE_INFINITY;
					for (int f = 0; f<out.depth; f++)
					{
						if (out.get(xO, yO, f) > maxVal)
						{
							max = f;
							maxVal = out.get(xO, yO, f);
						}
					}
					
					if (max == t)
						totalCorrect++;
					
				}
		}
		return ((double)totalCorrect)/nOuts;
	}

	@Override
    public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
		if (network.getLastLayer().params.outputFunction != OutputFunctionType.SOFT_MAX_ACT)
			Debug.err("CrossEntropyCategorizationObjective expects softmax output function!!");
		
        int t = state.outputMaps.cat;
        double o = state.outputMaps.get(xOutput,yOutput,feature);

        Debug.check(t<10, "true class is more than 9! = "+t);

        
        if (t==feature)
        	return 1-o;
        else
        	return -o;
        
        
//        if (o == 0)
//        	return ((feature==t)? 1.:0.) * 20;		// entropy is -1*totalEntropy, but we want to minimize it, so we output -1*real_entropy = totalEntropy
//        	
//        double der =  ((feature==t)? 1.:0.) / o;  	// entropy is -1*totalEntropy, but we want to minimize it, so we output -1*real_entropy = totalEntropy
//        return der;
	}

	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
		return;
	}
	
    public String toString() { return "Cross Entropy for classification = "+ lastValue + "\r\n(log-average output for true class = "+Math.exp(lastValue/network.currentBatchSize)+") ( average output for true class: "+averageOutForTrue+")";}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		return;
	}
}
