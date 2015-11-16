package outputFunction;

import layer.CnnDoubleLayerState;
import network.analysis.Debug;

public abstract class OutputFunction<OutputFunctionStateExt extends OutputFunctionState> {

	public abstract void computeOutputs(int xp, int yp, CnnDoubleLayerState state);
	
	/**
     * compute derivative of pool (xp,yp,f) w.r.t. (xp*wps + xc,yp*hps + yc,f)
	 * @param in
	 * @param out
	 * @param state 
	 * @return
	 */
	public abstract double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state);
	
	
	public static OutputFunction<?> fromString(String string) {
		if (string.equals("SoftMaxAct"))
			return new SoftMaxAct();
		if (string.equals("Linear"))
			return new LinearOutputFunction();
		
		Debug.err("Cannot parse output function! \""+string+"\"");
		return null;
	}
	
	public abstract String toString();

	public abstract OutputFunctionStateExt newOutputFunctionState(CnnDoubleLayerState state);

	public abstract void setDerivatives(CnnDoubleLayerState state);

	
}
