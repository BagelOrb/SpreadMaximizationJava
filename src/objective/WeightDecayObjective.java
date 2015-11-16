package objective;

import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import util.math.Math2;
import util.math.MathFunction2D;

public class WeightDecayObjective extends ObjectiveFunction {


	public WeightDecayObjective(Network nw) {
		super(nw);
	}

	@Override
	public double computeValue(NetworkState[] states) {
		double tot = 0;
		for (CnnDoubleLayer layer : network.cnnDoubleLayers)
			for (int f = 0 ; f< layer.params.nFeatures; f++)
			{
				tot += layer.weights[f].foldr(new MathFunction2D() {
					
					@Override
					public double apply(double arg1, double arg2) {
						return arg2 + arg1*arg1;
					}
				}, 0);
				tot += Math2.square(layer.biases[f]);
			}
		return tot;
	}


	@Override
    public void computeCommonDerivativeStuff(NetworkState[] states) {
	}

	@Override
	public String toString() {
		return "Square weight sum: "+lastValue;
	}

	@Override
	public double getDerivative(int feature, int xOutput, int yOutput,
			CnnDoubleLayerState state) {
		return 0;
	}

	@Override
	public void addWeightDerivatives(NetworkState[] states) {
		for (CnnDoubleLayer layer : network.cnnDoubleLayers)
		{
			double weightDecay = network.netParams.weightDecay;
			for (int f = 0 ; f< layer.params.nFeatures; f++)
			{
				layer.weightsDerivatives[f].add(layer.weights[f].times(weightDecay * layer.weights[f].size()));
				layer.biasesDerivatives[f] += layer.biases[f] * weightDecay;
			}
		}
	}
}
