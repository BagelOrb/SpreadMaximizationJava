package pooling;

import layer.CnnDoubleLayerState;
import util.math.DifferentiableFunction;

public abstract class PoolingFunction<PoolingStateExt extends PoolingState> {

	public abstract double computeOutput(int xp, int yp, int f, CnnDoubleLayerState state);
	
	/**
     * compute derivative of pool (xp,yp,f) w.r.t. (xp*wps + xc,yp*hps + yc,f)
	 * @param in
	 * @param out
	 * @param state TODO
	 * @return
	 */
	public abstract double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state, int xc, int yc);
	
	
	public static PoolingFunction<?> fromString(String string) {
		String[] identifiers = string.split("-");
		if (identifiers[0].equals("SoftArgMax"))
			return new SoftArgMaxPooling(DifferentiableFunction.fromString(identifiers[1]), Double.parseDouble(identifiers[2]));
		if (identifiers[0].equals("ArgMax"))
			return new ArgMaxPooling(DifferentiableFunction.fromString(identifiers[1]));
		return null;
	}
	
	public abstract String toString();

	public abstract PoolingStateExt newPoolingState(CnnDoubleLayerState state);

	public abstract double[][][] getBestInput(int xPool, int yPool, int feature, CnnDoubleLayerState state);


	
}
