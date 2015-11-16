package pooling;

import layer.CnnDoubleLayerState;
import network.LayerParameters;
import util.basics.IntegerArray3D;
import util.math.DifferentiableFunction;

public class ArgMaxPooling extends PoolingFunction<ArgMaxPooling.ArgMaxState> {

	public class ArgMaxState extends PoolingState {

		private CnnDoubleLayerState state;
		
		public IntegerArray3D xcArgMax;
		public IntegerArray3D ycArgMax;
		
		
		
		public ArgMaxState(CnnDoubleLayerState state) {
			this.state = state;
			
			xcArgMax = new IntegerArray3D(state.poolingMaps.width, state.poolingMaps.height, state.poolingMaps.depth);
			ycArgMax = new IntegerArray3D(state.poolingMaps.width, state.poolingMaps.height, state.poolingMaps.depth);
		}


		@Override
		public PoolingState copy() {
			ArgMaxState ret = new ArgMaxState(state);
			return ret;
		}


		@Override
		public void reset() {
			xcArgMax.fill(0);
			ycArgMax.fill(0);
		}
		
	}

	private DifferentiableFunction innerFunction;
	
	
	
	/**
	 * used for reflection only!
	 */
	@Deprecated public ArgMaxPooling() { }
	
	public ArgMaxPooling(DifferentiableFunction innerFunction) {
		this.innerFunction = innerFunction;
	}




	@Override
	public ArgMaxState newPoolingState(CnnDoubleLayerState state) {
		return new ArgMaxState(state);
	}

	@Override
	public double computeOutput(int xp, int yp, int f, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
        int wps = params.widthPoolingStep;
        int hps = params.heightPoolingStep;

		ArgMaxState poolingState = ((ArgMaxState) state.poolingState);

		
		poolingState.xcArgMax.set(xp, yp, f, 0);
		poolingState.ycArgMax.set(xp, yp, f, 0);
		double argMax = state.convolutionMaps.get(
				xp*wps + 0, 
				yp*hps + 0, f);
		double max = innerFunction.apply(argMax);
		
		for (int xc = 0; xc < wpf; xc++)
			for (int yc = 0; yc < hpf; yc++)
			{
				
				double in = state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f);
				double fin = innerFunction.apply(in);
				if (fin > max)
				{
					poolingState.xcArgMax.set(xp, yp, f, xc);
					poolingState.ycArgMax.set(xp, yp, f, yc);
					argMax = in;
					max = fin;
				}
			}
		
//		double best = state.convolutionMaps.get(
//				xp*wps + poolingState.xcArgMax.get(xp, yp, f), 
//				yp*hps + poolingState.ycArgMax.get(xp, yp, f), f);
		return argMax;
	}

	@Override
	public double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state, int xc, int yc) {
		ArgMaxState poolingState = ((ArgMaxState) state.poolingState);
		
//		if (xc == 0 && yc == 1 && f ==0)
//			Debug.out("at xc= "+xc+" : "+poolingState.xcArgMax.get(xp, yp, f) );
		
		double ret;
		if (xc == poolingState.xcArgMax.get(xp, yp, f) && yc == poolingState.ycArgMax.get(xp, yp, f))
			ret = 1;
		else
			ret = 0;
		
		return ret;
	}

	
	
	
	
	@Override
	public double[][][] getBestInput(int xp, int yp, int f, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		
		ArgMaxState poolingState = ((ArgMaxState) state.poolingState);
		int bestx = poolingState.xcArgMax.get(xp, yp, f);
		int besty = poolingState.ycArgMax.get(xp, yp, f);
		return state.inputMaps.subArray(
                xp*wps + bestx, xp*wps + bestx + wcf-1, 
                yp*hps + besty, yp*hps + besty + hcf-1, f, f);
	}
	
	
	
	@Override
	public String toString() {
		return "ArgMax-"+innerFunction.toString();
	}
	
	
	
	
}
