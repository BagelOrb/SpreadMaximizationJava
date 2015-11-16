package outputFunction;

import layer.CnnDoubleLayerState;

public class LinearOutputFunction extends OutputFunction<LinearOutputFunction.LinearOutputFunctionState>{

	public static class LinearOutputFunctionState extends OutputFunctionState {

		@Override
		public OutputFunctionState copy() {
			return new LinearOutputFunctionState();
		}

		@Override
		public void reset() {
		}
		
	}


	@Override
	public void computeOutputs(int xp, int yp, CnnDoubleLayerState state) {
//		state.outputMaps.set(state.poolingMaps);
		state.outputMaps = state.poolingMaps;
	}

	@Override
	public double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state) {
		return state.outputMapsDerivatives.get(xp, yp, f);
	}
	@Override
	public void setDerivatives(CnnDoubleLayerState state) {
		state.poolingMapsDerivatives = state.outputMapsDerivatives;
	}

	@Override
	public String toString() {
		return "Linear";
	}

	@Override
	public LinearOutputFunctionState newOutputFunctionState( CnnDoubleLayerState state) {
		return new LinearOutputFunctionState();
	}


	
	
	
}
