package pooling;

import io.Images.InputSample;
import layer.CnnDoubleLayerState;
import network.LayerParameters;
import network.analysis.Debug;
import util.basics.DoubleArray3D;
import util.math.DifferentiableFunction;
import util.math.Math2;

public class SoftArgMaxPooling extends PoolingFunction<SoftArgMaxPooling.SoftArgMaxState> {

	public class SoftArgMaxState extends PoolingState {

		public DoubleArray3D totalContributions;
		private CnnDoubleLayerState state;
		
		
		
		public SoftArgMaxState(CnnDoubleLayerState state) {
			this.state = state;
			
			InputSample poolingMaps = state.poolingMaps;
			totalContributions = new DoubleArray3D(poolingMaps.width, poolingMaps.height, poolingMaps.depth);
		}


		@Override
		public PoolingState copy() {
			SoftArgMaxState ret = new SoftArgMaxState(state);
			ret.totalContributions = this.totalContributions.clone();
			return ret;
		}


		@Override
		public void reset() {
			totalContributions.fill(0);
		}
		
	}

	public DifferentiableFunction innerFunction;
	public double hardness;
	
	
	
	/**
	 * used for reflection only!
	 */
	@Deprecated public SoftArgMaxPooling() { }
	
	public SoftArgMaxPooling(DifferentiableFunction innerFunction, double hardness) {
		this.innerFunction = innerFunction;
		this.hardness = hardness;
	}




	@Override
	public SoftArgMaxState newPoolingState(CnnDoubleLayerState state) {
		return new SoftArgMaxState(state);
	}

	@Override
	public double computeOutput(int xp, int yp, int f, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
        int wps = params.widthPoolingStep;
        int hps = params.heightPoolingStep;
		
		SoftArgMaxState poolingState = ((SoftArgMaxState) state.poolingState);
		
		
		double totalContribution = 0;
		double ret = 0;
		for (int xc = 0; xc < wpf; xc++)
			for (int yc = 0; yc < hpf; yc++)
			{
				
				double in = state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f);
				double fin = innerFunction.apply(in);
                double contribution = Math.exp(hardness * fin);
//                contribution = Math2.limit(-1E10, contribution, 1E10);
//                if (!Math2.liesWithin(-1E-3, contribution, 1E3))
//                	contr
//                Debug.checkNaN(contribution);
				totalContribution += contribution;
                ret += contribution * in;
			}
		
		poolingState.totalContributions.set(xp, yp, f, totalContribution);
		if (totalContribution==0)
		{
			return 0;
		}
		else
		{
			if (Double.isNaN(ret) || !Math2.liesWithin(-1E20, ret, 1E20))
			{
				if (!warnedSoftMax) {
					Debug.warn("Nan at soft max pooling! (or too large!) switching to hard max! (warning occurs only once!)\r\n ret = "+ret);
					warnedSoftMax = true;
				}
				return computeHardArgMax(xp,yp,f, state);
			}
			return ret/totalContribution;
		}
	}
	private boolean warnedSoftMax = false;

	private double computeHardArgMax(int xp, int yp, int f, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
        int wps = params.widthPoolingStep;
        int hps = params.heightPoolingStep;
		
		
		int xArgMax = 0;
		int yArgMax = 0;
		double argMaxIn = state.convolutionMaps.get(xp*wps + xArgMax, yp*hps + yArgMax, f);
		double argMax = innerFunction.apply(argMaxIn);
		
		for (int xc = 0; xc < wpf; xc++)
			for (int yc = 0; yc < hpf; yc++)
			{
				
				double in = state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f);
				double fin = innerFunction.apply(in);
				if (fin > argMax)
				{
					xArgMax = xc; yArgMax = yc;
					argMax = fin;
				}
			}
		
		double best = state.convolutionMaps.get(xp*wps + xArgMax, yp*hps + yArgMax, f);
		return best;
	}

	@Override
	public double computeDerivative(int xp, int yp, int f, CnnDoubleLayerState state, int xc, int yc) {
        int wps = state.params.widthPoolingStep;
        int hps = state.params.heightPoolingStep;

		
		SoftArgMaxState poolingState = ((SoftArgMaxState) state.poolingState);
		
        double in = state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f);
		
        double totalContribution = poolingState.totalContributions.get(xp, yp, f);
        
		double contribution = ((totalContribution ==0)? 0 :  
				 Math.exp(hardness * innerFunction.apply(in))
								/ totalContribution 		);
		
		
		double fDer = hardness * innerFunction.applyD(in);
		
		double out = state.outputMaps.get(xp, yp, f);
		
		double ret = contribution * (1+ fDer *(in - out ) );
		
//		ret = Math2.limit(-1000, ret, 1000);
		
//		if (Double.isNaN(ret))
//			ret = Math.signum(in);
		
//		ret = Math2.limit(-1E10, ret, 1E10);
		Debug.check(Math2.liesWithin(-1E2, ret, 1E2), "derivative too large!");
		Debug.checkNaN(ret);
//		if (ret==0)
//			Debug.out(in, totalContribution, contribution, fDer, out);
		
		return ret;
	}

	
	
	
	
	@Override
	public double[][][] getBestInput(int xp, int yp, int f, CnnDoubleLayerState state) {
		LayerParameters params = state.params;
		int wpf = params.widthPoolingField;
		int hpf = params.heightPoolingField;
		int wcf = params.widthConvolutionField;
		int hcf = params.heightConvolutionField;
		int wps = params.widthPoolingStep;
		int hps = params.heightPoolingStep;
		double highestVal = Double.NEGATIVE_INFINITY;
		int bestx = 0;
		int besty = 0;
		for (int xc = 0; xc < wpf ; xc++)
			for (int yc = 0; yc < hpf ; yc++)
                if (innerFunction.apply(state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f)) > highestVal)
				{
					bestx = xc; besty = yc;
                    highestVal = innerFunction.apply(state.convolutionMaps.get(xp*wps + xc, yp*hps + yc, f));
				}
		return state.inputMaps.subArray(
                xp*wps + bestx, xp*wps + bestx + wcf-1, 
                yp*hps + besty, yp*hps + besty + hcf-1, f, f);
	}
	
	
	
	@Override
	public String toString() {
		return "SoftArgMax-"+innerFunction.toString()+"-"+hardness;
	}
	
	
	
	
}
