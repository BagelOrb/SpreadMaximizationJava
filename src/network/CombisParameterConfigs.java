package network;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import network.LayerParameters.NetworkParameters;
import network.Static.DataGeneration;
import network.Static.LearningMechanismType;
import network.Static.Objective;
import network.Static.ObjectiveCorrectionTerm;
import network.Static.Pooling;
import network.Static.WeightConstraint;
import network.analysis.Debug;
import network.main.ParameterConfigs;
import util.basics.StringUtilsTK;
import util.math.DifferentiableFunction;



public class CombisParameterConfigs 
//extends JsonAble<CombisParameterConfigs> 
implements Iterable<LayerParameters>{


	
	
	
	public static final int nParams = 47;
	public static int nLayerParameters;




	
	

	
	// layer parameters:
	public Boolean[] useBias;
	public DifferentiableFunction[] signallingFunction;
	public Static.Pooling[] pooling;
	public Static.OutputFunctionType[] outputFunction;
	public Integer[] heightPoolingStep;
	public Integer[] widthPoolingStep;
	public Integer[] heightConvolutionField;
	public Integer[] widthConvolutionField;
	public Integer[] heightPoolingField;
	public Integer[] widthPoolingField;
	public Integer[] nInputFeatures; 
	public Integer[] heightLocalInput; 
	public Integer[] widthLocalInput; 
	public Integer[] nFeatures ;
	public Integer[] rbmMiniBatchSize;
	

	
	
	public CombisParameterConfigs(LayerParameters lp) {
		netParams = new CombisNetworkParameters(lp.netParams);
		
		useBias = new Boolean[]{lp.useBias};
		signallingFunction = new DifferentiableFunction[]{lp.signallingFunction };
		pooling = new Pooling[]{lp.pooling };
		outputFunction = new Static.OutputFunctionType[]{lp.outputFunction };
		heightPoolingStep = new Integer[]{lp.heightPoolingStep };
		widthPoolingStep = new Integer[]{lp.widthPoolingStep };
		heightConvolutionField = new Integer[]{lp.heightConvolutionField };
		widthConvolutionField = new Integer[]{lp.widthConvolutionField };
		heightPoolingField = new Integer[]{lp.heightPoolingField };
		widthPoolingField = new Integer[]{lp.widthPoolingField };
		nInputFeatures = new Integer[]{lp.nInputFeatures };
		heightLocalInput = new Integer[]{lp.heightLocalInput };
		widthLocalInput = new Integer[]{lp.widthLocalInput };
		nFeatures = new Integer[]{lp.nFeatures };
		rbmMiniBatchSize = new Integer[]{lp.rbmMiniBatchSize };
	}
	

	public CombisNetworkParameters netParams; // = new NetworkParameters();


	public static class CombisNetworkParameters {
		public Double[] etaMean;
		public Double[] etaWeights;
		public Double[] etaBiases;
		public Double[] weightDecay;
		public LearningMechanismType[] learningMechanismType;
		public Double[] eta_rPropPlus;
		public Double[] eta_rPropMin;
		public Double[] delta_rProp_init;
		public Double[] convergedIfBelow;
		public Static.Objective[] objective;
		public Double[] offset;
		public Boolean[] staticData;
		public DataGeneration[] dataGeneration;
		public Boolean[] batchLearning;
		public Integer[] batchSize;
		public Integer[] nIterations;
		public Integer[] nSamples;
		public Double[] absMaxInitWeights;
		public Boolean[] randomizeBiases;
		public Integer[] nItersTrainInputs;
		public Integer[] widthInput;
		public Integer[] heightInput;
		public Boolean[] rescaleWeights;
		public ObjectiveCorrectionTerm[] objectiveCorrectionTerm;
		public Double[] etaObjectiveCorrection;
		public File[] matFileForInputs;
		public Double[] alpha_momentum;
		public WeightConstraint[][] weightConstraints;
		public Double[] weightMax;
		public Double[] caeNoise;
		public Boolean[] tanhDataVsBinary;
		public Boolean[] standardizedInput;
		
		
		
		public CombisNetworkParameters(NetworkParameters np) {
			dataGeneration = new DataGeneration[]{np.dataGeneration};
			etaMean = new Double[]{np.etaMean};
			etaWeights = new Double[]{np.etaWeights};
			etaBiases = new Double[]{np.etaBiases};
			weightDecay = new Double[]{np.weightDecay};
			learningMechanismType = new LearningMechanismType[]{np.learningMechanismType};
			eta_rPropPlus = new Double[]{np.eta_rPropPlus};
			eta_rPropMin =new Double[]{np.eta_rPropMin};
			delta_rProp_init = new Double[]{np.delta_rProp_init};
			convergedIfBelow = new Double[]{np.convergedIfBelow};
			offset = new Double[]{np.offset};
			staticData = new Boolean[]{np.staticData};
			batchLearning = new Boolean[]{np.batchLearning};
			batchSize = new Integer[]{np.batchSize};
			nIterations = new Integer[]{np.nIterations};
			nSamples = new Integer[]{np.nSamples};
			absMaxInitWeights = new Double[]{np.absMaxInitWeights};
			randomizeBiases = new Boolean[]{np.randomizeBiases};
			nItersTrainInputs = new Integer[]{np.nItersTrainInputs};
			widthInput = new Integer[]{np.widthInput};
			heightInput = new Integer[]{np.heightInput};
			rescaleWeights = new Boolean[]{np.rescaleWeights};
			objective = new Objective[]{np.objective};
			objectiveCorrectionTerm = new ObjectiveCorrectionTerm[]{np.objectiveCorrectionTerm};
			etaObjectiveCorrection = new Double[]{np.etaObjectiveCorrection};
			matFileForInputs = new File[]{np.matFileForInputs};
			alpha_momentum = new Double[]{np.alpha_momentum};
			weightConstraints = new WeightConstraint[][]{np.weightConstraints};
			weightMax = new Double[]{np.weightMax};
			caeNoise = new Double[]{np.caeNoise};
			tanhDataVsBinary = new Boolean[]{np.tanhDataVsBinary};
			standardizedInput = new Boolean[]{np.standardizedInput};
		}


		public String getSeparateParams(boolean unchanging,
				LayerParameters paramsNow) {
			String ret = "\r\n";
			if (unchanging) ret += "Common ";
			else 			ret += "Config specific ";
			ret += "\r\nNetworkParameters |\r\n------------------+\r\n";
			
				for (Field q : this.getClass().getDeclaredFields()) 
					if (!q.isSynthetic())
						try {
							if (((Object[]) q.get(this)).length>1 ^ unchanging)
								ret+=(String.format("%-35s", q.getName())+
										" \t= "+NetworkParameters.class.getField(q.getName()).get(paramsNow.netParams)+"\r\n");
							
						} catch (Exception e) { 
                            Debug.out(">>>>>"+q.getName());
							e.printStackTrace(); }
			
			return ret;
		}
		public String toString() {
			return "\r\n"+StringUtilsTK.toString(this);
		}
	}

	
	
	@Override
	public Iterator<LayerParameters> iterator() {
		return new Iterator<LayerParameters>(){
			int[] currentPositions = new int[nParams];
			LayerParameters currentParams = getFirstParams();
			boolean isLast = false;
			@Override
			public boolean hasNext() {
				for (int p = 0; p<nParams; p++)
					if (currentPositions[p] > 0) return true;
				return !isLast;
			}
//			public boolean hasNext() {
//				if (isLast) return false;
//				for (int p = 0; p<nParams; p++)
//					if (currentPositions[p] < positionToParams(p).length-1) return true;
////				isLast = true;
//				return false;
//			}

			@Override
			public LayerParameters next() {
				LayerParameters ret = currentParams.clone();
				updateParamBy1(0);
				return ret;
			}
			private void updateParamBy1(int p) {
				currentPositions[p] += 1;
				if (currentPositions[p] == positionToParams(p).length) {
					currentPositions[p] = 0;
					if (p+1 == nParams) isLast = true; // if (currentPositions[p] +1 == positionToParams(p).length) 
					else updateParamBy1(p+1);
				}
				setParam(currentParams, p, positionToParams(p)[currentPositions[p]]);
			}

			@Override public void remove() { }};
	}


	public LayerParameters getFirstParams() {
		LayerParameters lp = new LayerParameters(new NetworkParameters());
		for (int p = 0; p<nParams; p++)
			setParam(lp, p, positionToParams(p)[0]);
		return lp;
	}
	
	public static void test() {
		
		CombisParameterConfigs c = new CombisParameterConfigs(ParameterConfigs.testCase());
//		c.getAllCombisFields();
        Debug.out(Arrays.toString(c.positionToParams(14)));
		Debug.out(c);
		LayerParameters lp = c.getFirstParams();
		setParam(lp, 14, 11);
        Debug.out(lp);
	}

	
	public static void setParam(LayerParameters lp, int p, Object val) {
		try {
			String fieldName = getAllCombisFields().get(p).getName();
			if (p >= nLayerParameters)
				NetworkParameters.class.getField(fieldName).set(lp.netParams, val);
			else
				LayerParameters.class.getField(fieldName).set(lp, val);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		
	public Object[] positionToParams(int p) {
		Field f = getAllCombisFields().get(p);
		try {
			if (f.getDeclaringClass().equals(CombisParameterConfigs.class))
					return (Object[]) f.get(this);
			else return (Object[]) f.get(this.netParams);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static LinkedList<Field> getAllCombisFields() {
		LinkedList<Field> lps = new LinkedList<Field>();
		for (Field q : CombisParameterConfigs.class.getDeclaredFields()) 
			if (!  (q.getName().equals("netParams") || q.getName().equals("nParams")  || q.getName().equals("nLayerParameters") )) {
				lps.add(q);
			}
		
		nLayerParameters = lps.size();
		for (Field q : CombisParameterConfigs.CombisNetworkParameters.class.getDeclaredFields()) {
			lps.add(q);
		}
//		Debug.out("n parameters= "+lps.size());
		return lps;
	}
/*
	@Deprecated public Object[] positionToParamsOLD(int p) {
		CombisNetworkParameters np = netParams;
		switch(p) {
		case 0: return useBias; 
		case 1: return tanhWeights; 
		case 2: return tanhBias; 
		case 3: return weightConversion; 
		case 4: return biasConversion; 
		case 5: return localContrastEnhancementLimitation; 
		case 6: return localContrastEnhancement; 
		case 7: return contrastEnhancementLocation; 
		case 8: return signallingFunction; 
		case 9: return pooling; 
		case 10: return heightConvolutionStep; 
		case 11: return widthConvolutionStep; 
		case 12: return heightLocalField; 
		case 13: return widthLocalField; 
		case 14: return nInputFeatures; 
		case 15: return nFeatures; 
		case 16: return VarVsCovar;  //////
		case 17: return np.dataGeneration; 
		case 18: return np.etaMean; 
		case 19: return np.etaWeights; 
		case 20: return np.weightDecay; 
        case 21: return np.backprop; 
		case 22: return np.eta_rPropPlus; 
		case 23: return np.eta_rPropMin; 
		case 24: return np.delta_rProp_init; 
		case 25: return np.deltaNormalizingObjective; 
		case 26: return np.etaDeltaNormalizingObjective; 
		case 27: return np.convergedIfBelow; 
		case 28: return np.offset; 
		case 29: return np.staticData; 
		case 30: return np.batchLearning; 
		case 31: return np.batchSize; 
		case 32: return np.nIterations; 
		case 33: return np.nSamples; 
		case 34: return np.absMaxInitWeights; 
		case 35: return np.randomizeBiases; 
		case 36: return np.nItersTrainInputs; 
		case 37: return np.widthPreferredInput; 
		case 38: return np.heightPreferredInput; 
		case 39: return np.rescaleWeights; 
		case 40: return np.objective; 
		case 41: return np.objectiveCorrectionTerm; 
		case 42: return np.etaObjectiveCorrection; 
		case 43: return np.matFileForInputs; 
		case 44: return np.networkStructureFactory; 
		case 45: return np.alpha_momentum; 
		case 46: return np.weightConstraints;
		case 47: return np.weightMax;
		case 48: return rbmMiniBatchSize;
		case 49: return np.tanhDataVsBinary;
		default: 
			Debug.err("Unknown parameter "+p);
			return null;
		}
		
	}
	@Deprecated public static void setParamOLD(LayerParameters lp, int p, Object val) {
		NetworkParameters np = lp.netParams;
		switch(p) {
		case 0: lp.useBias = (Boolean) val; break; 
		case 1: lp.tanhWeights = (Boolean) val; break; 
		case 2: lp.tanhBias = (Boolean) val; break; 
		case 3: lp.weightConversion = (DifferentiableFunction) val; break; 
		case 4: lp.biasConversion = (DifferentiableFunction) val; break; 
		case 5: lp.localContrastEnhancementLimitation = (DifferentiableFunction) val; break; 
		case 6: lp.localContrastEnhancement = (ContrastEnhancement) val; break; 
		case 7: lp.contrastEnhancementLocation = (ContrastEnhancementLocation) val; break; 
		case 8: lp.signallingFunction = (DifferentiableFunction) val; break; 
		case 9: lp.pooling = (Pooling) val; break; 
		case 10: lp.heightConvolutionStep = (Integer) val; break; 
		case 11: lp.widthConvolutionStep = (Integer) val; break; 
		case 12: lp.heightLocalField = (Integer) val; break; 
		case 13: lp.widthLocalField = (Integer) val; break; 
		case 14: lp.nInputFeatures = (Integer) val; break; 
		case 15: lp.nFeatures = (Integer) val; break; 
		case 16: lp.VarVsCovar = (Double) val; break;  //////
		case 17: np.dataGeneration = (DataGeneration) val; break; 
		case 18: np.etaMean = (Double) val; break; 
		case 19: np.etaWeights = (Double) val; break; 
		case 20: np.weightDecay = (Double) val; break; 
	    case 21: np.backprop = (Backprop) val; break; 
		case 22: np.eta_rPropPlus = (Double) val; break; 
		case 23: np.eta_rPropMin = (Double) val; break; 
		case 24: np.delta_rProp_init = (Double) val; break; 
		case 25: np.deltaNormalizingObjective = (Double) val; break; 
		case 26: np.etaDeltaNormalizingObjective = (Double) val; break; 
		case 27: np.convergedIfBelow = (Double) val; break; 
		case 28: np.offset = (Double) val; break; 
		case 29: np.staticData = (Boolean) val; break; 
		case 30: np.batchLearning = (Boolean) val; break; 
		case 31: np.batchSize = (Integer) val; break; 
		case 32: np.nIterations = (Integer) val; break; 
		case 33: np.nSamples = (Integer) val; break; 
		case 34: np.absMaxInitWeights = (Double) val; break; 
		case 35: np.randomizeBiases = (Boolean) val; break; 
		case 36: np.nItersTrainInputs = (Integer) val; break; 
		case 37: np.widthPreferredInput = (Integer) val; break; 
		case 38: np.heightPreferredInput = (Integer) val; break; 
		case 39: np.rescaleWeights = (Boolean) val; break; 
		case 40: np.objective = (Objective) val; break; 
		case 41: np.objectiveCorrectionTerm = (ObjectiveCorrectionTerm) val; break; 
		case 42: np.etaObjectiveCorrection = (Double) val; break; 
		case 43: np.matFileForInputs = (File) val; break; 
		case 44: np.networkStructureFactory = (NetworkStructureFactory) val; break;
		case 45: np.alpha_momentum = (Double) val; break;
		case 46: np.weightConstraints =  (WeightConstraint[]) val; break;
		case 47: np.weightMax = (Double) val; break;
		case 48: lp.rbmMiniBatchSize = (Integer) val; break;
		case 49: np.tanhDataVsBinary = (Boolean) val; break;
		}
	}
	*/
	static {
		testNparams();
	}
	public static boolean testNparams() {
		int npLP = LayerParameters.class.getDeclaredFields().length - 2; // one param for netParams, and one for ?! $SWITCH_TABLE$network$Static$DataGeneration  ?!
		int npNP = NetworkParameters.class.getDeclaredFields().length;
		LinkedList<String> lps = new LinkedList<String>();
		for (Field q : LayerParameters.class.getDeclaredFields()) {
			if (!q.isSynthetic()) 
				lps.add(q.getName());
		}
		for (Field q : NetworkParameters.class.getDeclaredFields()) {
			if (!q.isSynthetic()) 
				lps.add(q.getName());
		}
		boolean passed = npLP+npNP == nParams;
		if (!passed) {
			for (Field q : CombisParameterConfigs.class.getDeclaredFields()) {
				if (!q.isSynthetic()) {
					lps.remove(q.getName());
				}
			}
			for (Field q : CombisNetworkParameters.class.getDeclaredFields()) {
				if (!q.isSynthetic()) {
					lps.remove(q.getName());
				}
			}
			
			
			Debug.err("Parameters from LayerParameters ("+(npLP+npNP)+" params) are missing from CombisParameterConfigs ("+nParams+" params)\r\n : "+ 
						lps+" ( :> if empty then adjust CombisParameterConfigs.nParams to "+(npLP+npNP)+")");
		}
		return passed;
	}
	
	public String getSeparateParams(boolean unchanging, LayerParameters paramsNow) {
		String ret = "";
		if (unchanging) ret += "Common ";
		else 			ret += "Config specific ";
		ret += "\r\nLayerParameters |\r\n----------------+\r\n";
		
			for (Field q : this.getClass().getDeclaredFields()) 
				if (!q.isSynthetic() 
						&& !q.getName().equals("netParams") 
						&& !q.getName().equals("nParams") 
						&& !q.getName().equals("nLayerParameters"))
					try {
						if (((Object[]) q.get(this)).length>1 ^ unchanging)
							ret+=(String.format("%-35s", q.getName())+
									" \t= "+LayerParameters.class.getField(q.getName()).get(paramsNow)+"\r\n");
						
					} catch (Exception e) { 
                        Debug.out(">>>>>"+q.getName());
						e.printStackTrace(); }
		
		ret += netParams.getSeparateParams(unchanging, paramsNow);
		
		return ret;
	}
	
	public String toString() {
		return StringUtilsTK.toString(this);
	}
	public static void mainTest() {
		CombisParameterConfigs pp = new CombisParameterConfigs(ParameterConfigs.testCase());
		pp.useBias = new Boolean[]{true,false};
		pp.nInputFeatures = new Integer[]{1,2,3};
		for (LayerParameters q : pp) {
            Debug.out(pp.getSeparateParams(true, q));
            Debug.out("--------------");
			break;
		}
//            Debug.out(q.useBias +",\t"+q.tanhWeights+",\t"+q.nInputFeatures);
		
		
//        Debug.out(getParamsLength(new CombisParameterConfigs(ParameterConfigs.testCase()).positionToParam(6)));
	}


}
