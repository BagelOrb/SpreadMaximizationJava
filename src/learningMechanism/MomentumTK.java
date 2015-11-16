package learningMechanism;

import java.util.Arrays;

import objective.ConvAutoEncoder;
import layer.CnnDoubleLayer;
import network.LayerParameters;
import network.Network;
import network.LayerParameters.NetworkParameters;
import network.analysis.Debug;
import util.basics.DoubleArray3D;
import util.math.Math2;
import util.math.MathFunction1D;
import util.math.MathFunction2D;

public class MomentumTK extends LearningMechanism<MomentumTK.MomentumState>{

	public class MomentumState extends LearningMechanism.LearningState{

		DoubleArray3D[] weightVelocities;
		double[] biasVelocities;
		
		public MomentumState(CnnDoubleLayer cnnDoubleLayer) {
			super(cnnDoubleLayer);
			
			weightVelocities = new DoubleArray3D[params.nFeatures];
			for (int f = 0 ; f < params.nFeatures ; f++)
				weightVelocities[f] = new DoubleArray3D(params.widthConvolutionField, params.heightConvolutionField, params.nInputFeatures);

			biasVelocities = new double[params.nFeatures];
		}

		@Override
		public void reset() {
			for (DoubleArray3D v : weightVelocities)
				v.fill(0);
			Arrays.fill(biasVelocities, 0);
		}

	}

	
	public double alpha_momentum;
	private double etaWeights;

	public MomentumTK(double alpha_momentum, double etaWeights) {
		this.alpha_momentum = alpha_momentum;
		this.etaWeights = etaWeights;
	}

	
	@Override
	public void updateWeights(CnnDoubleLayer cnnDoubleLayer, Network network) {
		if (cnnDoubleLayer.locked)
			return;
		final LayerParameters params = cnnDoubleLayer.params;
		
		MomentumState momentumState = (MomentumState) cnnDoubleLayer.learningState;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			momentumState.weightVelocities[f].multiply(alpha_momentum);
			momentumState.weightVelocities[f].add(cnnDoubleLayer.weightsDerivatives[f].times(etaWeights));
			
			
			// extra slowdown when derivative and velocity are of opposite sign
			momentumState.weightVelocities[f].zippedWith(new MathFunction2D() {
				
				@Override
				public double apply(double v, double dw) {
					if (v*dw < 0 )
						return v + dw * etaWeights;
					return v;
				}
			}, cnnDoubleLayer.weightsDerivatives[f]);			

			Debug.checkNaN(momentumState.weightVelocities[f].get(0, 0, 0));

			momentumState.weightVelocities[f].map(new MathFunction1D() {
				
				@Override
				public double apply(double v) {
					if (params.netParams.rescaleWeights && Math.abs(v) > params.netParams.weightMax )
						return Math.signum(v) * params.netParams.weightMax;
					return v;
				}
			});
			
//			ObjectiveFunction obj = network.objective;
//			if (obj instanceof ChaObjective)
//			{
//				ChaObjective objective = (ChaObjective) network.objective;
//				
//				double m = objective.means[f];
//				
//				double decayTerm = 1/(10*m+1);
//				
//				cnnDoubleLayer.weights[f].add(momentumState.weightVelocities[f].times(decayTerm));
//			}
//			else
				cnnDoubleLayer.weights[f].add(momentumState.weightVelocities[f]);
		}
	}

	@Override
	public void updateBiases(CnnDoubleLayer cnnDoubleLayer) {
		if (cnnDoubleLayer.locked)
			return;
		LayerParameters params = cnnDoubleLayer.params;
		
		MomentumState momentumState = (MomentumState) cnnDoubleLayer.learningState;
		
		for (int f = 0 ; f< params.nFeatures; f++)
		{
			momentumState.biasVelocities[f] *= alpha_momentum; // .1 * TODO: implement this option?
			momentumState.biasVelocities[f] += cnnDoubleLayer.biasesDerivatives[f] * params.netParams.etaBiases;// ; // TODO: implement this option?
			
			// extra slowdown when derivative and velocity are of opposite sign
			if (momentumState.biasVelocities[f] * cnnDoubleLayer.biasesDerivatives[f] < 0) // TODO: move this to a new kind of Momentum2.java ?
				momentumState.biasVelocities[f] += cnnDoubleLayer.biasesDerivatives[f] * params.netParams.etaBiases; // ;// TODO: implement this option?
			
			Debug.checkNaN(momentumState.biasVelocities[f]);
			
			if (Math.abs(momentumState.biasVelocities[f]) > 10)
				momentumState.biasVelocities[f] = 10 * Math.signum(momentumState.biasVelocities[f]);
			
			cnnDoubleLayer.biases[f] += Math2.limit(-1E6, momentumState.biasVelocities[f], 1E6); 
//			cnnDoubleLayer.biases[f] += cnnDoubleLayer.biasesDerivatives[f] * etaWeights; // TODO: put this option somewhere?
		}
		
	}


	@Override
	public MomentumState newState(CnnDoubleLayer cnnDoubleLayer) {
		return new MomentumState(cnnDoubleLayer);
	}

	@Override
	public void updateBiases(ConvAutoEncoder objective) {
		Debug.err("ConvAutoEncoder learning not implemented for "+this.getClass()+"!!");
	}


	
	
	@Override
	public LearningParameter newLearningParameter(NetworkParameters netParams) {
		// TODO Auto-generated method stub
		Debug.err("newLearningParameter not implemented for "+this.getClass());
		return null;
	}
	
}
