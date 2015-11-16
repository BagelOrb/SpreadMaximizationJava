package network;

import io.Images.InputSample;
import io.JsonAble;
import io.JsonArrays;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonObject;

import network.Static.DataGeneration;
import network.Static.LearningMechanismType;
import network.Static.ObjectiveCorrectionTerm;
import network.Static.OutputFunctionType;
import network.Static.Pooling;
import network.Static.WCwrapper;
import network.Static.WeightConstraint;
import network.main.ParameterConfigs;
import util.math.DifferentiableFunction;
import data.DataSet;
import data.MNIST;
import data.SyntheticDataset;
import data.TestCases;



public class LayerParameters extends JsonAble<LayerParameters> implements Cloneable{

	public LayerParameters(NetworkParameters netParams2) {
		signallingFunction = DifferentiableFunction.tanh;
		this.netParams = netParams2;
	}
	
	
	// layer parameters:
	public boolean useBias;
	public DifferentiableFunction signallingFunction;

	public Static.Pooling pooling;
	public Static.OutputFunctionType outputFunction;
	

	
	
	
	public int heightPoolingStep;
	public int widthPoolingStep;
	public int heightConvolutionField;
	public int widthConvolutionField;
	public int heightPoolingField;
	public int widthPoolingField;
	
	public int nInputFeatures; 
	public int widthLocalInput;
	public int heightLocalInput;


	public int nFeatures ;
	
	public int rbmMiniBatchSize;


	public void deriveOtherParams() {
//		if (Debug.checkDerivatives) {
//			netParams.learningMechanismType = Static.LearningMechanismType.GRADIENT_ASCENT;
//			Debug.warn("!!>> Switching to Normal Backprop for checkDerivatives!! <<!!");
//		}
		
		
		InputSample[] testSamples = null;
		switch(netParams.dataGeneration) {
			case TEST_XOR:
				testSamples = TestCases.xor; break;
			case TEST_XOR_ADD:
				testSamples = TestCases.xorAdd; break;
			case TEST_ALL_DIRS_24_CONTRAST:
				testSamples = TestCases.allDirections24Contrast; break;
			case TEST_ALL_DIRS_24_CONTRAST_NOISE: 
				testSamples = TestCases.allDirections24ContrastWithNoise; break;
			case TEST_ALL_DIRS_360_CONTRAST_NOISE: 
				testSamples = TestCases.allDirections360ContrastWithNoise; break;
			case TEST_HV_CONTRAST:
				testSamples = TestCases.hvContrast; break;
			case TEST_HV_LINES:
				testSamples = TestCases.hvLines; break;
			default:
				break;
		}
		switch(netParams.dataGeneration) {
			case DEBUG_CASES:
	//			this.widthPoolingField = this.heightPoolingField = this.widthActivationMap(new DoubleArray3D(TestCases.input3[0]).width); // TODO: do it more directly
				this.widthConvolutionField = this.heightConvolutionField = TestCases.weights2[0][0].length;
				this.widthPoolingField = this.heightPoolingField = 1;
				
				this.nFeatures = TestCases.weights2.length;
				this.netParams.batchSize = this.netParams.nSamples = TestCases.input3.length; // TODO: match with TestCases in Network.java
				this.netParams.batchLearning = this.netParams.staticData = true;
				break;
			case TEST_XOR:
			case TEST_ALL_DIRS_24_CONTRAST:
			case TEST_ALL_DIRS_24_CONTRAST_NOISE:
			case TEST_ALL_DIRS_360_CONTRAST_NOISE:
			case TEST_HV_CONTRAST:
			case TEST_HV_LINES:
				this.nInputFeatures = testSamples[0].depth;
				widthConvolutionField = testSamples[0].width;
				heightConvolutionField = testSamples[0].height;
				this.widthPoolingField = this.heightPoolingField = 1;
				this.netParams.batchSize = this.netParams.nSamples = testSamples.length;
				this.netParams.batchLearning = this.netParams.staticData = true;
				break;
			case READ_FROM_MAT_FILE:
				try {
//					byte[] ba = FileUtils.readFileToByteArray(netParams.matFileForInputs);
					InputSample firstImage = new InputSample().fromByteArray(new BufferedInputStream(new FileInputStream(netParams.matFileForInputs)));
					int imageLength = firstImage.toByteArray().length;
					
					this.nInputFeatures = firstImage.depth;
					widthConvolutionField = firstImage.width;
					heightConvolutionField = firstImage.height;
					this.netParams.batchSize = this.netParams.nSamples = (int) (netParams.matFileForInputs.length() / imageLength);
					this.netParams.batchLearning = this.netParams.staticData = true;
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				
		}
//		netParams.widthInput = this.widthPoolingField   + this.widthConvolutionField -1;
//		netParams.heightInput = this.heightPoolingField + this.heightConvolutionField -1;
		
		
		switch(netParams.dataGeneration) {
		case MNIST:
			netParams.widthInput = netParams.heightInput = 28;
			break;
		default:
			break;
		}

		
		if (widthLocalInput == 0)
			widthLocalInput = netParams.widthInput;
		if (heightLocalInput == 0)
			heightLocalInput = netParams.heightInput;
	}
	public int widthConvolutionMap(int w) {
		int ret = w - widthConvolutionField +1;
		if  (ret <= 0)
			try { throw new Exception("Bad activation map width: "+ret);
			} catch (Exception e) { e.printStackTrace(); 	}
		return ret; }
	public int heightConvolutionMap(int h) { 
		int ret = h-heightConvolutionField+1;
		if  (ret <= 0)
			try { throw new Exception("Bad activation map height: "+ret);
			} catch (Exception e) { e.printStackTrace(); 	}
		return ret; }
	public int widthPoolingMap(int w) { 
		int ret = widthConvolutionMap( w)/ widthPoolingField
				;//+ Integer.signum(widthActivationMap(i)%widthPoolingField);// for leftover pixels at the right and bottom
		if  (ret <= 0)
			try { throw new Exception("Bad output map width: "+ret);
			} catch (Exception e) { e.printStackTrace(); 	}
		return ret; }   
	public int heightPoolingMap(int h) { 
		int ret = heightConvolutionMap(h)/heightPoolingField 
				;//+ Integer.signum(heightActivationMap(i)%heightPoolingField);  // for leftover pixels at the right and bottom
		if  (ret <= 0)
			try { throw new Exception("Bad output map height: "+ret);
			} catch (Exception e) { e.printStackTrace(); 	}
		return ret; }   


//	public int xActivation2xPooling(int xI) { return xI/widthPoolingField; }
//	public int yActivation2yPooling(int yI) { return yI/heightPoolingField; }
	
//	public int xOutput2xActivationLU(int xO) { return xO*widthPoolingField; }
//	public int yOutput2yActivationLU(int yO) { return yO*heightPoolingField; }

//	public int widthOutput2widthInput(DoubleArray3Dext<?> out) { 	return widthPoolingField  * out.width + widthLocalField - 1; }
//	public int heightOutput2heightInput(DoubleArray3Dext<?> out) {return heightPoolingField * out.height+ heightLocalField - 1; }
	
	public static void testJson() {
		LayerParameters ret = ParameterConfigs.smallTry();
		try {
			ret.writeToFile(new File("parameters.json"));
			LayerParameters params = LayerParameters.readFromFile(new File(""), new NetworkParameters());
			params.writeToFile(new File("parameters2.json"));
		} catch (Exception e) {
			e.printStackTrace(); }
	}
	
	
	
	
	
	
	
	public final NetworkParameters netParams; // = new NetworkParameters();


	public static class NetworkParameters extends JsonAble<NetworkParameters>{
		public double etaMean;
		public double etaWeights;
		public double etaBiases;
		public double weightDecay;
		public LearningMechanismType learningMechanismType;
		public double eta_rPropPlus;
		public double eta_rPropMin;
		public double delta_rProp_init;
		public double convergedIfBelow;
		public Static.Objective objective;
		public double offset;
		public boolean staticData;
		public DataGeneration dataGeneration;
		public boolean batchLearning;
		public int batchSize;
		public int nIterations;
		public int nSamples;
		public double absMaxInitWeights;
		public boolean randomizeBiases;
		public int nItersTrainInputs;
		public int widthInput;
		public int heightInput;
		public boolean rescaleWeights;
		
		public ObjectiveCorrectionTerm objectiveCorrectionTerm;
		public double etaObjectiveCorrection;
		public File matFileForInputs;
		
		public double alpha_momentum;
		public WeightConstraint[] weightConstraints;
		public double weightMax; 
		
		public boolean tanhDataVsBinary;
		public boolean standardizedInput;
		public double caeNoise;

		
		@Override
		public JsonObject toJsonObject() {
			return Json.createObjectBuilder()
		        .add("dataGeneration", dataGeneration.toString())
		        .add("etaMean", Double.toString(etaMean))
		        .add("etaWeights", etaWeights+"")
		        .add("etaBiases", etaBiases+"")
		        .add("weightDecay", weightDecay+"")
	            .add("learningMechanismType", learningMechanismType.toString())
		        .add("eta_rPropPlus", eta_rPropPlus+"")
		        .add("eta_rPropMin", eta_rPropMin+"")
		        .add("delta_rProp_init", delta_rProp_init+"")
		        .add("offset", offset+"")
		        .add("staticData", staticData)
		        .add("batchLearning", batchLearning)
		        .add("batchSize", batchSize)
		        .add("nIterations", nIterations)
		        .add("nSamples", nSamples)
		        .add("absMaxInitWeights", absMaxInitWeights+"")
		        .add("randomizeBiases", randomizeBiases)
		        .add("nItersTrainInputs", nItersTrainInputs)
		        .add("convergedIfBelow", convergedIfBelow+"")
		        .add("widthPreferredInput", widthInput)
		        .add("heightPreferredInput", heightInput)
		        .add("rescaleWeights", rescaleWeights)
		        .add("objective", objective.toString())
		        .add("objectiveCorrectionTerm", objectiveCorrectionTerm.toString())
		        .add("etaObjectiveCorrection", Double.toString(etaObjectiveCorrection))
		        .add("alpha_momentum", Double.toString(alpha_momentum))
		        .add("matFileForInputs", matFileForInputs+"")
		        .add("weightConstraints", JsonArrays.toJsonArray(WCwrapper.getWrappers(weightConstraints)))
		        .add("weightMax", Double.toString(weightMax))
		        .add("caeNoise", Double.toString(caeNoise))
		        .add("tanhDataVsBinary", tanhDataVsBinary)
		        .add("standardizedInput", standardizedInput)
		     .build(); 
		}

		@Override
		public NetworkParameters fromJsonObject(JsonObject o)
				throws IllegalArgumentException, SecurityException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			dataGeneration = DataGeneration.valueOf(o.getString("dataGeneration") );
			etaMean = Double.parseDouble(o.getString("etaMean")) ;
			etaWeights = Double.parseDouble(o.getString("etaWeights")) ;
			etaBiases = Double.parseDouble(o.getString("etaBiases", etaWeights+"")) ;
			weightDecay = Double.parseDouble(o.getString("weightDecay")) ;
	        learningMechanismType = LearningMechanismType.valueOf(o.getString("learningMechanismType") );
			eta_rPropPlus = Double.parseDouble(o.getString("eta_rPropPlus")) ;
			eta_rPropMin =Double.parseDouble( o.getString("eta_rPropMin")) ;
			delta_rProp_init = Double.parseDouble(o.getString("delta_rProp_init")) ;
			convergedIfBelow = Double.parseDouble(o.getString("convergedIfBelow")) ;
			offset = Double.parseDouble(o.getString("offset")) ;
			staticData = o.getBoolean("staticData") ;
			batchLearning = o.getBoolean("batchLearning") ;
			batchSize = o.getInt("batchSize") ;
			nIterations = o.getInt("nIterations") ;
			nSamples = o.getInt("nSamples") ;
			absMaxInitWeights = Double.parseDouble(o.getString("absMaxInitWeights"));
			randomizeBiases = o.getBoolean("randomizeBiases") ;
			nItersTrainInputs = o.getInt("nItersTrainInputs") ;
			widthInput = o.getInt("widthPreferredInput") ;
			heightInput = o.getInt("heightPreferredInput") ;
			rescaleWeights = o.getBoolean("rescaleWeights") ;
			objective = Static.Objective.valueOf(o.getString("objective") );
			objectiveCorrectionTerm = ObjectiveCorrectionTerm.valueOf(o.getString("objectiveCorrectionTerm") );
			etaObjectiveCorrection = Double.parseDouble(o.getString("etaObjectiveCorrection")) ;
			alpha_momentum = Double.parseDouble(o.getString("alpha_momentum")) ;
			matFileForInputs = new File(o.getString("matFileForInputs"));
			weightConstraints = WCwrapper.getConstraints(JsonArrays.fromJsonArray1D(WCwrapper.class, o.getJsonArray("weightConstraints")));
			weightMax = Double.parseDouble(o.getString("weightMax")) ;
			caeNoise = Double.parseDouble(o.getString("caeNoise", ".3")) ;
			tanhDataVsBinary = o.getBoolean("tanhDataVsBinary") ;
			standardizedInput = o.getBoolean("standardizedInput") ;
			
			return this;
		}
		
		@Override
		public NetworkParameters clone() {
			NetworkParameters c = new NetworkParameters();
			c.dataGeneration = this.dataGeneration ;
			c.etaMean = this.etaMean ;
			c.etaWeights = this.etaWeights ;
			c.etaBiases = this.etaBiases ;
			c.weightDecay = this.weightDecay ;
	        c.learningMechanismType = this.learningMechanismType ;
			c.eta_rPropPlus = this.eta_rPropPlus ;
			c.eta_rPropMin =this.eta_rPropMin ;
			c.delta_rProp_init = this.delta_rProp_init ;
			c.convergedIfBelow = this.convergedIfBelow ;
			c.offset = this.offset ;
			c.staticData = this.staticData ;
			c.batchLearning = this.batchLearning ;
			c.batchSize = this.batchSize ;
			c.nIterations = this.nIterations ;
			c.nSamples = this.nSamples ;
			c.absMaxInitWeights = this.absMaxInitWeights ;
			c.randomizeBiases = this.randomizeBiases ;
			c.nItersTrainInputs = this.nItersTrainInputs ;
			c.widthInput = this.widthInput ;
			c.heightInput = this.heightInput ;
			c.rescaleWeights = this.rescaleWeights ;
			c.objective = this.objective ;
			c.objectiveCorrectionTerm = this.objectiveCorrectionTerm;
			c.etaObjectiveCorrection = this.etaObjectiveCorrection;
			c.matFileForInputs = this.matFileForInputs;
			c.alpha_momentum = this.alpha_momentum;
			c.weightConstraints = Arrays.copyOf(weightConstraints, weightConstraints.length);
			c.weightMax = this.weightMax;
			c.caeNoise = this.caeNoise;
			c.tanhDataVsBinary = this.tanhDataVsBinary;
			c.standardizedInput = this.standardizedInput;
			return c;
		}
		
		public static NetworkParameters readFromFileStatic(File dir) throws IllegalArgumentException, SecurityException, FileNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			return new NetworkParameters().readFromFile(dir);
		}

		public void set(NetworkParameters n) {
			this.dataGeneration = n.dataGeneration ;
			this.etaMean = n.etaMean ;
			this.etaWeights = n.etaWeights ;
			this.etaBiases = n.etaBiases ;
			this.weightDecay = n.weightDecay ;
	        this.learningMechanismType = n.learningMechanismType ;
			this.eta_rPropPlus = n.eta_rPropPlus ;
			this.eta_rPropMin =n.eta_rPropMin ;
			this.delta_rProp_init = n.delta_rProp_init ;
			this.convergedIfBelow = n.convergedIfBelow ;
			this.offset = n.offset ;
			this.staticData = n.staticData ;
			this.batchLearning = n.batchLearning ;
			this.batchSize = n.batchSize ;
			this.nIterations = n.nIterations ;
			this.nSamples = n.nSamples ;
			this.absMaxInitWeights = n.absMaxInitWeights ;
			this.randomizeBiases = n.randomizeBiases ;
			this.nItersTrainInputs = n.nItersTrainInputs ;
			this.widthInput = n.widthInput ;
			this.heightInput = n.heightInput ;
			this.rescaleWeights = n.rescaleWeights ;
			this.objective = n.objective ;
			this.objectiveCorrectionTerm = n.objectiveCorrectionTerm;
			this.etaObjectiveCorrection = n.etaObjectiveCorrection;
			this.matFileForInputs = n.matFileForInputs;
			this.alpha_momentum = n.alpha_momentum;
			this.weightConstraints = Arrays.copyOf(weightConstraints, weightConstraints.length);
			this.weightMax = n.weightMax;
			this.caeNoise = n.caeNoise;
			this.tanhDataVsBinary = n.tanhDataVsBinary;
			this.standardizedInput = n.standardizedInput;
		}
		
		public String toString() {
			String ret = "\r\nNetworkParameters |\r\n------------------+\r\n";
			
			try {
				for (Field q : this.getClass().getDeclaredFields()) 
					if (!q.isSynthetic())
							ret+=(String.format("%-35s", q.getName())+
									" \t"+q.get(this)+"\r\n");
				
			} catch (Exception e) { 
				e.printStackTrace(); }
			
			return ret;
		}
	}

	@Override
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
	        .add("useBias", useBias)
	        .add("signallingFunction", signallingFunction.toString())
//	        .add("signallingFunctionAfterPooling", signallingFunctionAfterPooling.toString())
	        .add("pooling", pooling.toString())
	        .add("outputFunction", outputFunction.toString())
//	        .add("poolingFunction", poolingFunction.toString())
//	        .add("widthPoolingField", widthPoolingField)
//	        .add("heightPoolingField", heightPoolingField)
	        .add("heightConvolutionField", heightConvolutionField)
	        .add("widthConvolutionField", widthConvolutionField)
	        .add("heightPoolingField", heightPoolingField)
	        .add("widthPoolingField", widthPoolingField)
	        .add("heightPoolingStep", heightPoolingStep)
	        .add("widthPoolingStep", widthPoolingStep)
	        .add("nInputFeatures", nInputFeatures)
	        .add("widthLocalInput", widthLocalInput)
	        .add("heightLocalInput", heightLocalInput)
	        .add("nFeatures", nFeatures)
	        .add("rbmMiniBatchSize", rbmMiniBatchSize)
	     .build();
		// netParams not included! read and write them seperately..
	}

	@Override
	public LayerParameters fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		useBias = o.getBoolean("useBias");
		signallingFunction = DifferentiableFunction.fromString(o.getString("signallingFunction") );
//		signallingFunctionAfterPooling = DifferentiableFunction.fromString(o.getString("signallingFunctionAfterPooling") );
//		poolingFunction = PoolingFunction.fromString(o.getString("poolingFunction") );
		pooling = Pooling.valueOf(o.getString("pooling") );
		outputFunction = Static.OutputFunctionType.valueOf(o.getString("outputFunction") );
//		widthPoolingField = o.getInt("widthPoolingField") ;
//		heightPoolingField = o.getInt("heightPoolingField") ;
		heightPoolingStep = o.getInt("heightPoolingStep") ;
		widthPoolingStep = o.getInt("widthPoolingStep") ;
		heightConvolutionField = o.getInt("heightConvolutionField") ;
		widthConvolutionField = o.getInt("widthConvolutionField") ;
		heightPoolingField = o.getInt("heightPoolingField") ;
		widthPoolingField = o.getInt("widthPoolingField") ;
		nInputFeatures = o.getInt("nInputFeatures") ;
		widthLocalInput = o.getInt("widthLocalInput") ;
		heightLocalInput = o.getInt("heightLocalInput") ;
		nFeatures = o.getInt("nFeatures") ;
		rbmMiniBatchSize = o.getInt("rbmMiniBatchSize") ;

		// netParams not included! read and write them seperately..
		return this;
	}
	@Override
	public LayerParameters clone() {
		LayerParameters c = new LayerParameters(this.netParams);  // !! no clone!!! same reference!

		c.useBias = this.useBias;
		c.signallingFunction = this.signallingFunction ;
//		c.signallingFunctionAfterPooling = this.signallingFunctionAfterPooling ;
//		c.poolingFunction = this.poolingFunction ;
		c.pooling = this.pooling ;
		c.outputFunction = this.outputFunction ;
//		c.widthPoolingField = this.widthPoolingField ;
//		c.heightPoolingField = this.heightPoolingField ;
		c.heightPoolingStep = this.heightPoolingStep ;
		c.widthPoolingStep = this.widthPoolingStep ;
		c.heightConvolutionField = this.heightConvolutionField ;
		c.widthConvolutionField = this.widthConvolutionField ;
		c.heightPoolingField = this.heightPoolingField ;
		c.widthPoolingField = this.widthPoolingField ;
		c.nInputFeatures = this.nInputFeatures ;
		c.widthLocalInput = this.widthLocalInput ;
		c.heightLocalInput = this.heightLocalInput ;
		c.nFeatures = this.nFeatures ;
		c.rbmMiniBatchSize = this.rbmMiniBatchSize ;
		
		return c;
	}
	public void set(LayerParameters from) {
		this.useBias = from.useBias;
		this.signallingFunction = from.signallingFunction ;
//		this.signallingFunctionAfterPooling = from.signallingFunctionAfterPooling ;
//		this.poolingFunction = from.poolingFunction ;
		this.pooling = from.pooling ;
		this.outputFunction = from.outputFunction ;
//		this.widthPoolingField = from.widthPoolingField ;
//		this.heightPoolingField = from.heightPoolingField ;
		this.heightPoolingStep = from.heightPoolingStep ;
		this.widthPoolingStep = from.widthPoolingStep ;
		this.heightConvolutionField = from.heightConvolutionField ;
		this.widthConvolutionField = from.widthConvolutionField ;
		this.heightPoolingField = from.heightPoolingField ;
		this.widthPoolingField = from.widthPoolingField ;
		this.nInputFeatures = from.nInputFeatures ;
		this.widthLocalInput = from.widthLocalInput ;
		this.heightLocalInput = from.heightLocalInput ;
		this.nFeatures = from.nFeatures ;
		this.rbmMiniBatchSize = from.rbmMiniBatchSize ;

	}	

	public static LayerParameters readFromFile(File dir, NetworkParameters netParams) throws IllegalArgumentException, SecurityException, FileNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return new LayerParameters(netParams).readFromFile(dir);
	}

//	public LayerParameters getCombiLayerParamsWithPoolingParams(
//			LayerParameters pool) {
//		LayerParameters ret = this.clone();
//		ret.nFeatures = pool.nFeatures;
//		
//		ret.widthConvolutionField = pool.widthConvolutionField * this.widthPoolingStep + (this.widthConvolutionField - this.widthPoolingStep);
//		ret.widthPoolingStep = pool.widthPoolingStep * this.widthPoolingStep;
//		ret.heightConvolutionField = pool.heightConvolutionField * this.heightPoolingStep + (this.heightConvolutionField - this.heightPoolingStep);
//		ret.heightPoolingStep = pool.heightPoolingStep * this.heightPoolingStep;
//		return ret;
//	}

	public LayerParameters getDoubleLayerParamsWithRightLayerBeing(
			LayerParameters right) {
		LayerParameters ret = this.clone();
		LayerParameters left = this;
		ret.nFeatures = left.nFeatures + right.nFeatures;
		return ret;
	}
	public LayerParameters getNextLayerParams() {
		LayerParameters ret = this.clone();
		ret.nInputFeatures = this.nFeatures;
		ret.widthLocalInput = this.widthPoolingMap(this.widthLocalInput);
		ret.heightLocalInput = this.heightPoolingMap(this.heightLocalInput);
		return ret;
	}


	public String toString() {
		String ret = "\r\nLayerParameters |\r\n----------------+\r\n";
		
		try {
			for (Field q : this.getClass().getDeclaredFields()) 
				if (!q.isSynthetic())
						ret+=(String.format("%-35s", q.getName())+
								" \t"+q.get(this)+"\r\n");
			
		} catch (Exception e) { 
			e.printStackTrace(); }
		
		return ret;
	}
	public void setPoolingFieldAndStepSize(int w) {
		widthPoolingField =
				widthPoolingStep = 
				heightPoolingField = 
				heightPoolingStep =
				w;
	}
	public void setConvolutionFieldSize(int w) {
		widthConvolutionField = heightConvolutionField = w;
	}
	public void setDataset(DataSet dataset) {
		netParams.dataGeneration = dataset.getDatasetType();
		netParams.widthInput =
				widthLocalInput = dataset.getWidth();
		netParams.heightInput = 
				heightLocalInput = dataset.getHeight();
		nInputFeatures = dataset.getDepth();

		if (netParams.staticData)
			netParams.nSamples = dataset.getDataSetSize();
		
	}
	public void setClassificationLayer() {
		outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		signallingFunction = DifferentiableFunction.linear;
	}
	
}
