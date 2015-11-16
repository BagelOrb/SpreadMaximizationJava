package objective;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.Network;
import network.NetworkState;
import network.Static;
import network.Static.Objective;
import util.math.DifferentiableFunction;
import util.math.MathFunction2D;

public abstract class ObjectiveFunction {

	public Network network;

	public double lastValue = Double.NEGATIVE_INFINITY;
	
	public double getValue(NetworkState[] states) {
		lastValue = computeValue(states);
		return lastValue; 
	}
	abstract double computeValue(NetworkState[] states);
	public abstract double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) ;
	public abstract void addWeightDerivatives(NetworkState[] states) ;
	
	/**
	 * Compute stuff which is needed for all derivatives.
	 * @param states the signalling states of the network
	 */
	public abstract void computeCommonDerivativeStuff(NetworkState[] states);
	
	
	public ObjectiveFunction(Network nw) {
		network = nw;
	}
	public ObjectiveFunction(int dim) {
	}
	
	
	
	
	static ObjectiveFunction squared(final Network nw){ 
		ObjectiveFunction ret = new ObjectiveFunction(nw){
			
			@Override double computeValue(NetworkState[] states) {
				double total = 0;
				for (NetworkState nwState : states)
				{
					CnnDoubleLayerState state = nwState.getLast();
					InputSample out = state.outputMaps;
					total += out.mapped(DifferentiableFunction.square).foldr(MathFunction2D.addition, 0);
				}
				return .5*total;
			}
			
			public String toString() { return "Square total: "+lastValue; }

			@Override
			public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
				return state.outputMaps.get(xOutput,yOutput,feature);
			}

			@Override
			public void addWeightDerivatives(NetworkState[] states) {
				return;
			}

			@Override
			public void computeCommonDerivativeStuff(
					NetworkState[] states) {
			}
		};
		ret.network = nw;
		return ret;
	}
	static ObjectiveFunction linear(final Network nw){  
		ObjectiveFunction ret = new ObjectiveFunction(nw){
	
			public String toString() { return "Linear total: "+lastValue; }

			@Override
			double computeValue(NetworkState[] states) {
				double total = 0;
				for (NetworkState nwState : states) {
					CnnDoubleLayerState state = nwState.getLast();
					InputSample out = state.outputMaps;
					total += out.foldr(MathFunction2D.addition, 0);
				}
				return total;
			}

			@Override
			public double getDerivative(int feature, int xOutput, int yOutput, CnnDoubleLayerState state) {
				return 1;
			}

			@Override
			public void addWeightDerivatives(NetworkState[] states) {
				return;
			}

			@Override
			public void computeCommonDerivativeStuff(
					NetworkState[] states) {
				return;
			}
			};
		ret.network = nw;
		return ret;
	}
	
	public ObjectiveFunction compose(final double weight, final ObjectiveFunction second) {
		final ObjectiveFunction first = this;
		return new ObjectiveFunction(this.network) {
			
			
			public String toString() { return "Objective function value: "+lastValue+ "("+first+"+ "+weight+"*"+second+")"; }

			@Override
			double computeValue(NetworkState[] states) {
				return first.getValue(states) + weight*second.getValue(states);
			}

			@Override
			public double getDerivative(int feature, int xOutput, int yOutput,
					CnnDoubleLayerState state) {
				return first.getDerivative(feature, xOutput, yOutput, state) + weight*second.getDerivative(feature, xOutput, yOutput, state);
			}

			@Override
			public void addWeightDerivatives(NetworkState[] states) {
				first.addWeightDerivatives(states);
				second.addWeightDerivatives(states);
			}

			@Override
			public void computeCommonDerivativeStuff(
					NetworkState[] states) {
				first.computeCommonDerivativeStuff(states);
				second.computeCommonDerivativeStuff(states);
			}
		};
	}
	public static ObjectiveFunction getObjectiveFunction(Objective obj, Network network) {
		switch(obj) {
		case DECORRELATION: 		return  new DecorrelationObjective(network);  
		case SQUARED_ERROR_CLASSIFICATION: 	return new SquaredErrorCategorizationObjective(network); 
		case CROSS_ENTROPY_CLASSIFICATION: 	return new CrossEntropyCategorizationObjective(network); 
		case LINEAR_FUNCTION: 		return ObjectiveFunction.linear(network); 
		case SQUARED_TOTAL: 		return ObjectiveFunction.squared(network); 
		case GAUSS_UNIFORMIZATION:		return new GaussUniformizationObjective(network); 
		case CONV_HEBBIAN_ALG:		return new ChaObjective(network); 
		case CONV_AUTO_ENCODER:		return new ConvAutoEncoder(network);
		default: return null;
		}
	}
	public static String computeAllObjectivesValues(Network network, NetworkState[] states) {
		String valuesOfAllObjectives = "abr;val;xtra\r\n";
		network.signalBatch();
		for (Objective obj : Static.Objective.values()) {
			if (obj.equals(Static.Objective.CROSS_ENTROPY_CLASSIFICATION) ) continue;
			if ((obj.equals(Static.Objective.DECORRELATION) || obj.equals(Static.Objective.GAUSS_UNIFORMIZATION)) ) continue;
			ObjectiveFunction eachObjective = getObjectiveFunction(obj, network);
			eachObjective.computeCommonDerivativeStuff(states);
			
			valuesOfAllObjectives += obj.getAbbrev()+";"+eachObjective.getValue(states)+";"+eachObjective+"\r\n";
			if (obj.equals(Static.Objective.CROSS_ENTROPY_CLASSIFICATION))
				valuesOfAllObjectives += "ERR;"+((CategorizationObjective) eachObjective).getErrorRate(network, states)+";Actual error \r\n";
		}
		ObjectiveFunction weightDecayObjective = new WeightDecayObjective(network);
		weightDecayObjective.computeCommonDerivativeStuff(states);
		valuesOfAllObjectives += "WDC;"+weightDecayObjective.getValue(states)+";"+weightDecayObjective+"\r\n";
		
//		ObjectiveFunction squareVarPerInputMapObjective = new WeightUniformityObjective(network);
//		squareVarPerInputMapObjective.computeCommonDerivativeStuff(states);
//		valuesOfAllObjectives += "SWU;"+squareVarPerInputMapObjective.getValue(states)+";"+squareVarPerInputMapObjective+"\r\n";
		
		Network.output(valuesOfAllObjectives);
		return valuesOfAllObjectives;
	}
	public int getDimensionality() {
		return network.getLastLayer().params.nFeatures;
	}
	
}