package outputFunction;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.LayerParameters;
import network.analysis.Debug;
import util.basics.DoubleArray2D;
import util.math.Math2;

public class SoftMaxAct extends OutputFunction<SoftMaxAct.SoftMaxActState> {

	public class SoftMaxActState extends OutputFunctionState {

		DoubleArray2D totalContributions;
		private CnnDoubleLayerState state;
		
		
		
		public SoftMaxActState(CnnDoubleLayerState state) {
			this.state = state;
			
			InputSample poolingMaps = state.poolingMaps;
			totalContributions = new DoubleArray2D(poolingMaps.width, poolingMaps.height);
		}


		@Override
		public SoftMaxActState copy() {
			SoftMaxActState ret = new SoftMaxActState(state);
			ret.totalContributions = this.totalContributions.clone();
			return ret;
		}


		@Override
		public void reset() {
			totalContributions.fill(0);
		}
		
	}

	
	
	
//	/**
//	 * used for reflection only!
//	 */
//	@Deprecated public SoftMaxAct() { }
	
	public SoftMaxAct() {
	}




	@Override
	public SoftMaxActState newOutputFunctionState(CnnDoubleLayerState state) {
		return new SoftMaxActState(state);
	}

	@Override
	public void computeOutputs(int xp, int yp, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		
		SoftMaxActState ouputFunctionState = ((SoftMaxActState) state.outputFunctionState);
		
		
		double totalContribution = 0;
		for (int f = 0; f<params.nFeatures; f++)
		{
			
			double in = state.poolingMaps.get(xp, yp, f);
			double contribution = Math.exp(in);
//                contribution = Math2.limit(-1E10, contribution, 1E10);
//                if (!Math2.liesWithin(-1E-3, contribution, 1E3))
//                	contr
//                Debug.checkNaN(contribution);
			totalContribution += contribution;
			state.outputMaps.set(xp,yp,f, contribution);
		}
		ouputFunctionState.totalContributions.set(xp, yp, totalContribution);
		for (int f = 0; f<params.nFeatures; f++)
		{
			if (ouputFunctionState.totalContributions.get(xp, yp) == 0 )
				state.outputMaps.set(xp, yp, f, 0);
			else if (Double.isInfinite(ouputFunctionState.totalContributions.get(xp, yp)))
				state.outputMaps.set(xp, yp, f, (isMax(xp,yp,f, state))? 1 : 0);
			else
				state.outputMaps.set(xp, yp, f, state.outputMaps.get(xp, yp, f) / ouputFunctionState.totalContributions.get(xp, yp));
		}
	}
	
	

	private boolean isMax(int xp, int yp, int f2, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		double here = state.poolingMaps.get(xp, yp, f2);
		
		for (int f = 0; f< params.nFeatures; f++)
		{
			if (state.poolingMaps.get(xp, yp, f) > here)
				return false;
		}
		
		return true;
	}


	@Override
	public double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state) {
//		if (state.cnnDoubleLayer.params.netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION)
//		{
//			double der = state.outputMapsDerivatives.get(xp, yp, f);
//			Debug.checkNaN(der);
//			return der;
//		}
		
		LayerParameters params = state.params;
		
		double on = state.outputMaps.get(xp, yp, f);

		double don = state.outputMapsDerivatives.get(xp, yp, f);
		
		double dokok = 0;
		for (int f2 = 0; f2<params.nFeatures; f2++)
			dokok += state.outputMapsDerivatives.get(xp, yp, f2) * state.outputMaps.get(xp, yp, f2);
			
		double ret = on * (don - dokok);
		
//		ret = Math2.limit(-1000, ret, 1000);
		
//		if (Double.isNaN(ret))
//			ret = Math.signum(state.);
		
//		ret = Math2.limit(-1E10, ret, 1E10);
		Debug.check(Math2.liesWithin(-1E5, ret, 1E5), "derivative too large!");
		Debug.checkNaN(ret);
//		if (ret==0)
//			Debug.out(in, totalContribution, contribution, fDer, out);
		
		return ret;
	}

	@Override
	public void setDerivatives(CnnDoubleLayerState state) {
		for (int f = 0 ; f < state.poolingMaps.depth ; f++)
			for (int xp = 0 ; xp < state.poolingMaps.width ; xp++)
				for (int yp = 0 ; yp < state.poolingMaps.height ; yp++)
				{
					double der = computeDerivative(xp, yp, f, state);
					state.poolingMapsDerivatives.set(xp, yp, f, der);
				}

	}
	
	
	
	
	
	@Override
	public String toString() {
		return "SoftMaxAct";
	}






	
	
	
}
