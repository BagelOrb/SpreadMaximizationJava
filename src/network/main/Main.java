package network.main;

import io.Folder;
import io.Images.InputSample;
import io.UniqueFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import layer.CnnDoubleLayer;
import network.CombisParameterConfigs;
import network.LayerParameters;
import network.LayerParameters.NetworkParameters;
import network.Network;
import network.NetworkIO;
import network.NetworkState;
import network.Static;
import network.Static.DataGeneration;
import network.Static.LearningMechanismType;
import network.Static.Objective;
import network.Static.OutputFunctionType;
import network.Static.Pooling;
import network.analysis.Analysis;
import network.analysis.Debug;
import objective.ChaObjective;
import objective.ConvAutoEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import util.basics.DoubleArray3Dext.Loc;
import util.math.DifferentiableFunction;
import data.DataFromNetwork;
import data.DataSet;
import data.GenerateData;
import data.MNIST;
import data.SyntheticDataset;
import data.SyntheticDataset2;



@SuppressWarnings("unused")
public class Main {

	public static void mainForJAR(String[] args) {

		System.out.println("Call: main> "+StringUtils.join(args, " "));
		
//		Defaults.getStandardDefaults();
		
		
		if (Debug.useWindowQuitExperiment) JFrameExperiment.run();
		
		try {
			if (args.length>0)
				switch (args[0].toLowerCase()) {
//				case "chaSequential":
//					NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\simpleNetwork");
//					chaSequential();
//					break;
				case "loadfromfileandconvertdata":
					loadFromFileAndConvertData();
					break;
				case "testsettings":
					int nRuns = Integer.parseInt(args[2]);
					switch(args[1].toLowerCase()) {
					case "comparecha":
						testSettings(ParameterConfigs.compareCha, nRuns);
						break;
					case "fromParams":
						String netParamsPath = args[3];
						String layerParamsPath = args[4];
						NetworkParameters netParams;
						try {
							netParams = new NetworkParameters().readFromFile(new File(netParamsPath));
							LayerParameters layerParams = LayerParameters.readFromFile(new File(layerParamsPath), netParams);
							testSettings(new CombisParameterConfigs(layerParams), nRuns);
						} catch (IllegalArgumentException | SecurityException
								| FileNotFoundException | InstantiationException
								| IllegalAccessException
								| InvocationTargetException | NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						Debug.out("not implemented "+args[1]);
					}
					break;
					
				default:
					Debug.out("not implemented "+args[0]);
				}
			else
				Debug.err("pass arguments!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		
		if (Debug.useWindowQuitExperiment) JFrameExperiment.frame.dispose();
		System.exit(0);
	}
	
	public static String[] callArgs;
	public static String additionalOptionRemarks = "";
	public static String mainFunction = "";
	public static void main(String[] args) {
		if (Debug.useWindowQuitExperiment) JFrameExperiment.run();
		callArgs = args;
		
//		testNetwork();
//		
//		
		wd = true; sp = true;
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
		wd = false; sp = true;
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
		wd = true; sp = false;
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
		wd = false; sp = false;
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
//		syntheticTest();
////		
//		eigenvolumeExpansion(args);
////
//		preSigmoidGaussApproximation(args);
////		
//		convAutoEncoderNetwork(args);
////		
////		
//		uniformization(args);
////		
//		
//		
//		continueLearning(args);
//		
//		continueLearningWholeNetwork(args, true);
//		
//		continueFinalization(args);
//		
//		
//		
//		
//		trainSecondLayerClassificationLayer(args);
//		
//		addClassificationLayerAndTrainWholeNetwork(args);
//		
//		binarifyAndTrainSecondLayerClassificationLayer(args);
//		
		
//		noPreLearning(args);
		
//		evaluateNetworkOnMNIST(args);

		
		
//		outputAllWeightImages();
		
		
		
		
//		MainHelperFunctions.processAllNetworks(new File(args[0]), MainHelperFunctions.saveConfig());
//		MainHelperFunctions.processAllNetworks(new File(args[0]), MainHelperFunctions.createAllEvals());
		
//		MainHelperFunctions.main(args);
		
		
		
//		trainLeNet1();
		
//		trainLeNet1CHASM(args);
		
		sp = true;
		trainLeNet1CAE(args);
		
//		trainLeNet1PSGU(args);
		
//		trainLeNet1EED(args);
		
		
		
		
//		convAutoEncoderNetworkTEST();
		
		
//		twoLayerClassification();
//		oneLayerClassification();
		
		
//		twoUnifLayersOnwClassificationLayer();
//		wholeNetworkLayerwiseMNISTsingleClassificationLayer();
//		oneUnifLayerOneClassificationLayerMNIST(args);
//		oneUnifLayerOneClassificationLayerMNISTfromWholeNetwork(args);
		if (Debug.useWindowQuitExperiment) JFrameExperiment.frame.dispose();
		System.exit(0);

	}
	
	static boolean wd = false;
	static boolean sp = false;
	private static void setStaticData(LayerParameters params) {
//		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize();
//		params.netParams.staticData = true;
//		params.netParams.nSamples = params .netParams.batchSize; 
		params.netParams.staticData = true;

	}
	public static int patternUsed;

	private static void useDataset(LayerParameters params1) {
//		patternUsed = 3;  additionalOptionRemarks += " syntheticPattern=3";
//		SyntheticDataset2 dataset = new SyntheticDataset2(true, -1, false, 10, patternUsed);
//		params1.setDataset(dataset);
//		params1.netParams.batchSize = dataset.getDataSetSize();
//		params1.netParams.nIterations = 10000;
//		params1.nFeatures = 2;
	}
	private static void setSpecialParameters(LayerParameters params1) {
//		params1.netParams.weightDecay = -.001; additionalOptionRemarks += " weightDecay-.001";
		if (sp)
			{ params1.pooling = Pooling.SOFT_ARG_MAX_ABS; additionalOptionRemarks += " SOFT_ARG_MAX_ABS-pooling"; }
		else
			{ params1.pooling = Pooling.ARG_MAX_ABS; additionalOptionRemarks += " ARG_MAX_ABS-pooling"; }
//		params1.netParams.etaBiases = params1.netParams.etaWeights = 1E-6; additionalOptionRemarks += " eta 1E-6";
//		params1.netParams.etaBiases = params1.netParams.etaWeights = 0; additionalOptionRemarks += " eta 0";
//		params1.netParams.etaBiases = params1.netParams.etaBiases * 10;  additionalOptionRemarks += " etaBiases "+params1.netParams.etaBiases;
//		params1.netParams.etaWeights= params1.netParams.etaWeights * 10;  additionalOptionRemarks += " etaWeights "+params1.netParams.etaBiases;
//		params1.setPoolingFieldAndStepSize(1);
//		params1.signallingFunction = DifferentiableFunction.signum; additionalOptionRemarks += " binarized with signum";
		
	}

	
	
	
	
	private static void syntheticTest() {
//		testSynthNetwork(eigenvolumeExpansion("-", "5", "6"));
//		
//		testSynthNetwork(preSigmoidGaussApproximation("-", "5", "6"));

		testSynthNetwork(convAutoEncoderNetwork("-", "5", "6"));
		
//		testSynthNetwork(uniformization("-", "5", "6"));
		
	}
	
	
	
	private static void testSynthNetwork(Network network) {
		LayerParameters params1 = network.getFirstLayer().params;
		
		
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);

		network.getFirstLayer().locked = false;
		
		
		LayerParameters params2 = params1.getNextLayerParams();

		
		
		if (wd)
			{ params1.netParams.weightDecay = -.001; additionalOptionRemarks += " weightDecay-.001"; }
		else 
			{ params1.netParams.weightDecay = 0 ; additionalOptionRemarks += " NO weightDecay "; }
		
//		params1.signallingFunction = DifferentiableFunction.signum;
		
		
		
		
		params2.netParams.absMaxInitWeights = .1;
		params2.netParams.randomizeBiases = true;
		
		
		
		params2.setConvolutionFieldSize(1);
		params2.setPoolingFieldAndStepSize(1);
		params2.nFeatures = 4;
		network.addLayer(params2);
		network.getLastLayer().initializeWeights();
		
		LayerParameters params3 = params2.getNextLayerParams();
		params3.setConvolutionFieldSize(1);
		params3.setPoolingFieldAndStepSize(1);
		params3.nFeatures = 2;
		params3.setClassificationLayer();
		network.addLayer(params3);
		network.getLastLayer().initializeWeights();
		
		params3.netParams.etaMean =
		params3.netParams.etaBiases =
				1E0;
		params3.netParams.objective = Objective.CROSS_ENTROPY_CLASSIFICATION;
		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);

		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.path.getParent()+"/layer1_0_layerClass2_");
		trainNetwork(network);
		evaluateNetwork(network, false, NetworkIO.path+"");
	}

	private static void trainLeNet1PSGU(String[] args) {
		mainFunction = "trainLeNet1PSGU";
		Network network = firstLenetLayer(Static.Objective.GAUSS_UNIFORMIZATION);
		
		
		network = secondLenetLayer(network);
		
		
		trainSecondLayerClassificationLayer(network, NetworkIO.path, 2, false, true);
	}
	private static void trainLeNet1EED(String[] args) {
		mainFunction = "trainLeNet1PSGU";
		Network network = firstLenetLayer(Static.Objective.DECORRELATION);
		
		
		network = secondLenetLayer(network);
		
		
		trainSecondLayerClassificationLayer(network, NetworkIO.path, 2, false, true);
	}
	private static void trainLeNet1CHASM(String[] args) {
		mainFunction = "trainLeNet1CHASM";
		Network network = firstLenetLayer(Static.Objective.CONV_HEBBIAN_ALG);
		
		
		network = secondLenetLayer(network);
		
		
		trainSecondLayerClassificationLayer(network, NetworkIO.path, 2, false, true);
	}
	
	private static void trainLeNet1CAE(String[] args) {
		mainFunction = "trainLeNet1CAE";
		Network network = firstLenetLayer(Static.Objective.CONV_AUTO_ENCODER);
		
		
		network = secondLenetLayer(network);
		
		
		trainSecondLayerClassificationLayer(network, NetworkIO.path, 2, false, true);
	}

	private static Network secondLenetLayer(Network network) {
		LayerParameters params1 = network.getLastLayer().params;
		
		network.getLastLayer().locked = true;
		
		params1.setPoolingFieldAndStepSize(2);
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);

		
		LayerParameters params2 = params1.getNextLayerParams();
		
		params2.setConvolutionFieldSize(5);
		params2.setPoolingFieldAndStepSize(4);
		
		params2.nFeatures = 12;
		
		network.addLayer(params2);
		network.getLastLayer().initializeWeights();
		
		
		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
		
		if (network.originalObjective instanceof ConvAutoEncoder)
			((ConvAutoEncoder) network.originalObjective).construct(network.getLastLayer());

		network.setDataGenerator();
		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.path.getParentFile()+"/layer2_");
		trainNetwork(network);// second layer
		return network;
	}

	private static Network firstLenetLayer(Static.Objective obj) {
		
		Network network;
		LayerParameters params1;
		Folder outPath;

		String netName;
		switch (obj) {
		case CONV_HEBBIAN_ALG:
			params1 = ParameterConfigs.cha();
			netName = "Chasm";
			break;
		case CONV_AUTO_ENCODER: 
			params1 = ParameterConfigs.convAutoEncoder();
			netName = "Cae";
			break;
		case GAUSS_UNIFORMIZATION:
			params1 = ParameterConfigs.preSigmoidGaussApproximation();
			netName = "Psgu";
			break;
		case DECORRELATION:
			params1 = ParameterConfigs.eigenvolumeExpansion();
			netName = "Eed";
			break;
		default:
			Debug.err("Lenet not implemented for the objective "+obj);
			netName = "";
			params1 = null;
			System.exit(0);
		}
		outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\lenet"+netName+"Network");
		
		params1.setConvolutionFieldSize(5);
		params1.setPoolingFieldAndStepSize(4);
		
		params1.nFeatures = 4;
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);
		
		
		
		
		
		
//		params1.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize();
//		params1.netParams.staticData = true;

		
		
		
		
		
		
		network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
		trainNetwork(network);// first layer
		
		return network;
	}
	
	
	

	private static void trainLeNet1() {
		Network net = LeNet1();
		
		initializeClassRandoms();
		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\leNet1");

		trainNetwork(net);
	}

	private static Network LeNet1() {
		mainFunction = "LeNet1";

		LayerParameters params1 = ParameterConfigs.firstLayerForClassification();
		params1.nFeatures = 4;
		params1.setConvolutionFieldSize(5);
		params1.setPoolingFieldAndStepSize(2);
		params1.pooling = Pooling.MAX;
		
		setSpecialParameters(params1); 
		
		LayerParameters params2 = params1.getNextLayerParams();
		params2.nFeatures = 12;
		params2.setConvolutionFieldSize(5);
		params2.setPoolingFieldAndStepSize(2);

		LayerParameters params3 = params2.getNextLayerParams();
		params3.setConvolutionFieldSize(4);
		params3.setPoolingFieldAndStepSize(1);
		params3.nFeatures = 10;
		params3.signallingFunction = DifferentiableFunction.linear;
		params3.outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		
		
		
		Network network = new Network(params1);
		network.addLayer(params2);
		network.addLayer(params3);
		
		
		network.initializeData(); 
		network.initializeWeights();

		return network;
	}
	
	
	


	private static void outputAllWeightImages() {
		File base = new File("C:/Users/TK/Documents/Thesis/ThesisSimple/imageOutput/test");
		Stack<File[]> dfs = new Stack<File[]>();
		dfs.add(base.listFiles());
		
		while ( ! dfs.isEmpty())
		{
			File[] subFiles = dfs.pop();
			for (File subFolder : subFiles)
			{
				if ( ! subFolder.isDirectory())
					continue;
				
				dfs.add(subFolder.listFiles());
				
				if (new File(subFolder+"/output.txt").exists())
				{
					try {
						Folder nwFolder = new Folder(subFolder);
						Network network = NetworkIO.recover(nwFolder);
						NetworkIO.outputWeightImages(network, nwFolder);
						
					} catch (Exception e) { e.printStackTrace(); }
				}
			}
		}
	}



	public static void evaluateNetworkOnMNIST(String... args) {
		mainFunction = "evaluateNetworkOnMNIST";
		String path = args[0];
		Folder fromFolder = new Folder(path);
		
		Boolean training = Boolean.parseBoolean(args[1]);
		
		try {
			FileUtils.writeStringToFile(UniqueFile.newUniqueFile(path+"\\", "test", ".txt"), "test");
		} catch (IOException e) { e.printStackTrace(); }
		
		Network network = null;
		try {
			network = NetworkIO.recover(fromFolder);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
		}	
		evaluateNetwork(network, training, path);
	}
	
	public static void evaluateNetwork(Network network, boolean training, String path) {
		
		DataSet dataset;
		switch (network.netParams.dataGeneration) {
		case MNIST:
			dataset = new MNIST(training, 10000, false);
			break;
		case SYNTHETIC2:
			dataset = new SyntheticDataset2(training, -1, false, 10, patternUsed);
			break;
		default:
			Debug.err("dataset not supported yet: "+network.netParams.dataGeneration);
			dataset = null;
		}
		
		
		evaluateNetwork(network, training, path, dataset);
	}
	
	static void evaluateNetwork(Network network, boolean training, String path, DataSet dataset) {
		
		{ // check settings of the network
			boolean incorrectSettings = false;
			if (network.getLastLayer().params.outputFunction != OutputFunctionType.SOFT_MAX_ACT) incorrectSettings = true;
			try {
				NetworkState state = new NetworkState(network, dataset.readImage(0));
				if (!(state.getLast().outputMaps.width==1 && state.getLast().outputMaps.height==1)) incorrectSettings = true;
			} catch (IOException e) { e.printStackTrace(); }
			
			
			if (incorrectSettings)
				Debug.err("Network doesn't have the correct settings to work as classification network!");
		}		
		
		
		double entropy = 0;
		int nCorrect = 0;
		
		
		Debug.out("Signalling and evaluating all test samples...");
		int counter = 0;
		for (InputSample in : dataset)
		{
			NetworkState state = new NetworkState(network, in);
			network.signal(state);
			InputSample out = state.getLast().outputMaps;
			
			
			int t = in.cat;
			
			int p = -1;
			double highestOutVal = Double.NEGATIVE_INFINITY;
			
			for (Loc l : out)
				if (out.get(l) > highestOutVal)
				{
					p = l.z;
					highestOutVal = out.get(l);
				}
			
			if (t==p)
				nCorrect++;
			
			entropy -= Math.log(out.get(0, 0, t));
			
			
			
			if ((counter+1) % Math.max(1, dataset.getLimit()/10) == 0)
				Debug.out(((counter+1) *100 / dataset.getLimit())+"%");
			counter++;
		}
		
		
		double percentage = (double)nCorrect/dataset.getLimit() *100.;
		double logMeanTrueOut = Math.exp(-entropy/dataset.getLimit());
		
		String evalString = "nCorrect: \t "+nCorrect+"\r\n"+
							"percentage: \t "+percentage+"%\r\n"+
							"entropy: \t "+entropy+"\r\n"+
							"logMeanTrueOut: \t "+logMeanTrueOut+"\r\n";
		
		try {
			FileUtils.writeStringToFile(UniqueFile.newUniqueFile(path+"\\", "eval " +((training)? "training" : "test"), ".txt"), evalString);
		} catch (IOException e) { e.printStackTrace(); }
		
		Debug.out(evalString);
		
	
	}
	
	public static void testNetwork() {
		mainFunction = "test";
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\testNetwork");
		NetworkIO.path.mkdirs();
		trainNetwork(ParameterConfigs.testConvAutoEncoder());
	}
	private static Network convAutoEncoderNetwork(String... args) {
		mainFunction = "convAutoEncoderNetwork";
		
//		LayerParameters params1 = ParameterConfigs.testConvAutoEncoder();
//		Network network = trainNetwork(params1);
		
		String firstLayerLocation = args[0];
		
		
		Network network;
		LayerParameters params1;
		Folder outPath;
		
		int fieldSize;
		int firstLayerPoolSize;
		try {
			fieldSize = Integer.parseInt(args[1]);
			firstLayerPoolSize = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fieldSize = 0; 
			firstLayerPoolSize = 0;
			System.exit(0);
		}
		
//		if (firstLayerLocation.length() < 4)
//		{
			outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\caeNetwork");
			params1 = ParameterConfigs.convAutoEncoder();
			
			params1.setPoolingFieldAndStepSize(firstLayerPoolSize);
			params1.setConvolutionFieldSize(fieldSize);
			
			setStaticData(params1);
			useDataset(params1);
			setSpecialParameters(params1);

			network = initializeNetwork(params1);
			
			NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
			trainNetwork(network);// first layer
//		} else
//		{
//			try {
//				outPath = new Folder(firstLayerLocation);
//				
//				network = NetworkIO.recover(outPath);
//				initializeClassRandoms();
//				
//				params1 = network.getFirstLayer().params;
//				
////				network.cnnDoubleLayers.remove(1);
//			} catch (SecurityException | InstantiationException
//					| IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException | NoSuchMethodException
//					| IOException e) {
//				e.printStackTrace();
//				network = null;
//				params1 = null;
//				outPath = null;
//				Debug.exit();
//			}
//		}

		
//		trainSecondLayerClassificationLayer(network, NetworkIO.path, firstLayerPoolSize, false, true);
		return network;
	}
	private static Network convAutoEncoderNetworkTEST() {
		mainFunction = "convAutoEncoderNetwork";
		
//		LayerParameters params1 = ParameterConfigs.testConvAutoEncoder();
//		Network network = trainNetwork(params1);
		
		
		
		Network network;
		LayerParameters params1;
		Folder outPath;
		
		int fieldSize;
		int firstLayerPoolSize;
		try {
			fieldSize = 7;
			firstLayerPoolSize = 7;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fieldSize = 0; 
			firstLayerPoolSize = 0;
			System.exit(0);
		}
		
//		if (firstLayerLocation.length() < 4)
//		{
		outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\caeNetwork");
		params1 = ParameterConfigs.convAutoEncoder();
		
		params1.setPoolingFieldAndStepSize(firstLayerPoolSize);
		params1.setConvolutionFieldSize(fieldSize);
		
//		setStaticData(params1);
//		useDataset(params1);
//		setSpecialParameters(params1);
		
		params1.netParams.nSamples = params1.netParams.batchSize; //new MNIST(true, -1, false).getDataSetSize();
		params1.netParams.staticData = true;
		params1.pooling = Pooling.SOFT_ARG_MAX_ABS; additionalOptionRemarks += " SOFT_ARG_MAX_ABS-pooling";

		params1.nFeatures = 4;
		
		params1.netParams.nIterations = 100;
		
		network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
		trainNetwork(network);// first layer
//		} else
//		{
//			try {
//				outPath = new Folder(firstLayerLocation);
//				
//				network = NetworkIO.recover(outPath);
//				initializeClassRandoms();
//				
//				params1 = network.getFirstLayer().params;
//				
////				network.cnnDoubleLayers.remove(1);
//			} catch (SecurityException | InstantiationException
//					| IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException | NoSuchMethodException
//					| IOException e) {
//				e.printStackTrace();
//				network = null;
//				params1 = null;
//				outPath = null;
//				Debug.exit();
//			}
//		}
		
		
		trainSecondLayerClassificationLayer(network, NetworkIO.path, firstLayerPoolSize, false, true);
		return network;
	}
	private static Network eigenvolumeExpansion(String... args) {
		mainFunction = "eigenvolumeExpansion";
		
		
//		String firstLayerLocation = args[0];
		
		
		Network network;
		LayerParameters params1;
		Folder outPath;
		
		int fieldSize;
		int firstLayerPoolSize;
		try {
			fieldSize = Integer.parseInt(args[1]);
			firstLayerPoolSize = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fieldSize = 0; 
			firstLayerPoolSize = 0;
			System.exit(0);
		}
		
		outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\eedNetwork");
		params1 = ParameterConfigs.eigenvolumeExpansion();
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);


		params1.setPoolingFieldAndStepSize(firstLayerPoolSize);
		params1.setConvolutionFieldSize(fieldSize);
		
		
		network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
		trainNetwork(network);// first layer
		
		
//		trainSecondLayerClassificationLayer(params1, network, outPath, firstLayerPoolSize);
		
		return network;
	}
	private static Network preSigmoidGaussApproximation(String... args) {
		mainFunction = "preSigmoidGaussApproximation";
		
		
		String firstLayerLocation = args[0];
		
		
		Network network;
		LayerParameters params1;
		Folder outPath;
		
		int fieldSize;
		int firstLayerPoolSize;
		try {
			fieldSize = Integer.parseInt(args[1]);
			firstLayerPoolSize = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fieldSize = 0; 
			firstLayerPoolSize = 0;
			System.exit(0);
		}
		
		outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\psgNetwork");
		params1 = ParameterConfigs.preSigmoidGaussApproximation();
		
		params1.setPoolingFieldAndStepSize(firstLayerPoolSize);
		params1.setConvolutionFieldSize(fieldSize);
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);


		network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
		trainNetwork(network);// first layer
		
		
//		trainSecondLayerClassificationLayer(params1, network, outPath, firstLayerPoolSize);
		
		return network;
	}
	
	private static Network uniformization(String... args) {
		mainFunction = "uniformization";
		
		Network network;
		LayerParameters params1;
		Folder outPath;

		int fieldSize;
		int poolSize;
		try {
			fieldSize = Integer.parseInt(args[1]);
			poolSize = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fieldSize = 0; 
			poolSize = 0;
			System.exit(0);
		}
		
		outPath = Folder.newUniqueFolder(NetworkIO.basePath+"\\smNetwork");
		params1 = ParameterConfigs.cha();
		
		
		params1.setPoolingFieldAndStepSize(poolSize);
		params1.setConvolutionFieldSize(fieldSize);
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);

		
		
		
//		params1.netParams.etaWeights= 1E-4;
//		params1.netParams.etaBiases = 1E-3;
//		params1.nFeatures = 12;
//		
//		params1.netParams.nIterations = 1000;
		
		
		network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layer1_");
		trainNetwork(network);// first layer
		
		return network;
	}
	

	private static Network noPreLearning(String... args) {
		mainFunction = "noPreLearning";
		
		LayerParameters params1 = ParameterConfigs.cha();
		
		params1.setPoolingFieldAndStepSize(2);
		
		LayerParameters params2 = params1.getNextLayerParams();
		params2.netParams.set(ParameterConfigs.classify().netParams);
		
		params2.signallingFunction = DifferentiableFunction.linear;
		params2.outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		
		setStaticData(params2);
		useDataset(params1);
		setSpecialParameters(params1);

		
//		params1.pooling = params2.pooling = Pooling.ARG_MAX_ABS;
		
		params2.setPoolingFieldAndStepSize(2);
		params2.setConvolutionFieldSize(params2.widthLocalInput - 1);
		
		params2.netParams.absMaxInitWeights = .01;
		params2.netParams.nIterations = 10000;
//		params2.netParams.weightDecay = -.001;
		
		Network network = new Network(params1);
		network.addLayer(params2);
		
		initializeNetwork(network);
		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath +"\\noPreLearning");
		trainNetwork(network);
		
		return network;
				
	}

	
	
	public static void continueFinalization(String... args) {
		mainFunction = "continueFinalization";
		
		Folder fromFolder = new Folder(args[0]);
		
		Network network = null;
		try {
			network = NetworkIO.recover(fromFolder);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
		}
		initializeClassRandoms();
		
		if (network.netParams.objective != Static.Objective.CONV_HEBBIAN_ALG)
			Debug.err("continueFinalization is only for CHA! not for "+network.netParams.objective);
		
//		network.netParams.weightDecay = -1E0;
//		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
//		network.netParams.nIterations = 10000;
//		network.netParams.etaWeights = 1E-4;
//		network.netParams.etaBiases= 1E-4;
//		network.netParams.nSamples = 240;
//		network.netParams.batchSize = 120;
//		network.netParams.staticData = true;
		
		try {
			network.jumpToIteration = Integer.parseInt(args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		network.initializeData();
		
		for (int l = 0; l<network.cnnDoubleLayers.size()-1; l++)
			network.cnnDoubleLayers.get(l).locked = true;
		
//		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\simpleNetwork");
		if (fromFolder.getName().toLowerCase().startsWith("backup"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder.getParent()+"\\continued");
		else if (fromFolder.getName().toLowerCase().startsWith("layer1"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"_continued");
		else
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"\\continued");
//		trainNetwork(network);
		
		network.getLastLayer().params.signallingFunction = DifferentiableFunction.linear;
		
		network.finalizeNetwork(null);
		
		try {
			NetworkIO.document(network);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void continueLearning(String[] args) {
		mainFunction = "continueLearning";

		Folder fromFolder = new Folder(args[0]);
		
		Network network = null;
		try {
			network = NetworkIO.recover(fromFolder);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
		}
		initializeClassRandoms();
		
//		network.netParams.weightDecay = -1E0;
//		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
//		network.netParams.nIterations = 10000;
//		network.netParams.etaWeights = 1E-4;
//		network.netParams.etaBiases= 1E-4;
//		network.netParams.nSamples = 240;
//		network.netParams.batchSize = 120;
//		network.netParams.staticData = true;
		
		try {
			network.jumpToIteration = Integer.parseInt(args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		network.initializeData();
		
		for (int l = 0; l<network.cnnDoubleLayers.size()-1; l++)
			network.cnnDoubleLayers.get(l).locked = true;
		
		if (fromFolder.getName().toLowerCase().startsWith("backup"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder.getParent()+"\\continued");
		else if (fromFolder.getName().toLowerCase().startsWith("layer1"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"_continued");
		else
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"\\continued");
		
		
		trainNetwork(network);
	}
	public static void continueLearningWholeNetwork(String[] args, boolean useWeightDecay) {
		mainFunction = "continueLearningWholeNetwork";
		
		Folder fromFolder = new Folder(args[0]);
		
		Network network = null;
		try {
			network = NetworkIO.recover(fromFolder);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
		}
		initializeClassRandoms();
		
//		network.netParams.weightDecay = -1E0;
//		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
//		network.netParams.nIterations = 10000;
//		network.netParams.etaWeights = 1E-4;
//		network.netParams.etaBiases= 1E-4;
//		network.netParams.nSamples = 240;
//		network.netParams.batchSize = 120;
//		network.netParams.staticData = true;
		
		try {
			network.jumpToIteration = Integer.parseInt(args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if (useWeightDecay)
		{
			network.netParams.weightDecay = -.001;
			network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
			
			if (network.originalObjective instanceof ConvAutoEncoder)
				((ConvAutoEncoder) network.originalObjective).construct(network.getLastLayer());

		}
		
		network.initializeData();
		
		
		if (fromFolder.getName().toLowerCase().startsWith("backup"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder.getParent()+"\\continuedWHOLE"+((useWeightDecay)? "wWD" : ""));
		else if (fromFolder.getName().toLowerCase().startsWith("layer1"))
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"_continuedWHOLE"+((useWeightDecay)? "wWD" : ""));
		else
			NetworkIO.path = Folder.newUniqueFolder(fromFolder+"\\continuedWHOLE"+((useWeightDecay)? "wWD" : ""));
		
		
		
		trainNetwork(network);
	}
	public static void oneLayerClassification() {
		
		LayerParameters params1 = ParameterConfigs.classify();
		
		params1.outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);


		
		params1.nFeatures = 10;
		
		Network network = initializeNetwork(params1);
		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\classificationNetwork");
		trainNetwork(network);
		
	}


	public static void twoLayerClassification() {

		LayerParameters params1 = ParameterConfigs.classify();
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);

		
		
		params1.signallingFunction = DifferentiableFunction.tanh;
		params1.outputFunction = OutputFunctionType.LINEAR;
		
		Network network = initializeNetwork(params1);
		
		LayerParameters params2 = params1.getNextLayerParams();
		
		params2.setConvolutionFieldSize(1);
		params2.setPoolingFieldAndStepSize(1);
		
		params2.signallingFunction = DifferentiableFunction.linear;
		params2.outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		
		params2.nFeatures = 10;

		network.addLayer(params2);
		network.getLastLayer().initializeWeights();
		
		NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\simpleNetwork");
		trainNetwork(network);
		
	}
	
	
	
	
	private static void binarifyAndTrainSecondLayerClassificationLayer(String[] args) {
		mainFunction = "binarifyAndTrainSecondLayerClassificationLayer";
		
		trainSecondLayerClassificationLayer(args, true, true);
	}
	
	private static void trainSecondLayerClassificationLayer(String... args) {
		mainFunction = "trainSecondLayerClassificationLayer";
		
		trainSecondLayerClassificationLayer(args, false, true);
	}
	private static void addClassificationLayerAndTrainWholeNetwork(String... args) {
		mainFunction = "addClassificationLayerAndTrainWholeNetwork";
		
		trainSecondLayerClassificationLayer(args, false, false);
	}
	private static void trainSecondLayerClassificationLayer(String[] args, boolean binarify, boolean lockFirstLayers) {
		
		String firstLayerLocation = args[0];
		
		int poolSize;
		try {
			poolSize = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			poolSize = 0;
			System.exit(0);
		}
		
		
		Network network;
		LayerParameters params1;
		Folder outPath;
		
		try {
			outPath = new Folder(firstLayerLocation);
			network = NetworkIO.recover(outPath);
			network.initializeData();
			
			Debug.out("staticData="+network.netParams.staticData  );
			Debug.out("nSamples="+network.netParams.nSamples);

		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
			network = null;
			outPath = null;
			System.exit(0);
		}
		initializeClassRandoms();
		
		
		trainSecondLayerClassificationLayer(network, outPath, poolSize, binarify, lockFirstLayers);
	}

	private static void trainSecondLayerClassificationLayer(Network network, Folder outPath, int firstlayerPoolSize, boolean binarify, boolean lockFirstLayers) {

		if (lockFirstLayers)
			for (int l = 0; l<network.cnnDoubleLayers.size()-1; l++)
				network.cnnDoubleLayers.get(l).locked = true;
		
		LayerParameters params1 = network.getLastLayer().params;
		
		params1.setPoolingFieldAndStepSize(2);
		
		if (binarify)
			params1.signallingFunction = DifferentiableFunction.signum;
		else
			params1.signallingFunction = DifferentiableFunction.tanh;
		
		
		setStaticData(params1);
		useDataset(params1);
		setSpecialParameters(params1);
		
		
		LayerParameters params2 = params1.getNextLayerParams();
		params2.setConvolutionFieldSize(params2.widthLocalInput - 1);
		params2.setPoolingFieldAndStepSize(2);
		params2.pooling = Pooling.ARG_MAX_ABS;
		
		
		params2.netParams.objective = Objective.CROSS_ENTROPY_CLASSIFICATION;
		params2.outputFunction = OutputFunctionType.SOFT_MAX_ACT;
		params2.signallingFunction = DifferentiableFunction.linear;
		if (params1.netParams.dataGeneration == DataGeneration.SYNTHETIC2)
			params2.nFeatures = 2;
		else
			params2.nFeatures = 10;
		
		params2.netParams.absMaxInitWeights = 0.0001;
		
		network.netParams.etaBiases = 1E-3;
		network.netParams.etaWeights = 1E-3;
		
		network.netParams.nIterations = 20000;
		
		network.netParams.learningMechanismType = LearningMechanismType.MOMENTUM;
		network.netParams.alpha_momentum = .9;
		
//		network.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize();
//		network.netParams.staticData = true;
//		network.setDataGenerator();
//		network.initializeData();
//		network.netParams.nSamples = 100;
//		network.netParams.staticData = false;
		network.netParams.batchSize = 36;
		
//		network.netParams.weightMax = 20;
		
		network.addLayer(params2);
		network.getLastLayer().initializeWeights();
		
		network.objective = network.getObjectiveFunction(network.netParams.objective, network.netParams.etaObjectiveCorrection, network.netParams.objectiveCorrectionTerm);
		network.setDataGenerator();
		
		String appender = ((binarify)? "binarified_" : "") + ((lockFirstLayers)? "" : "unlocked_");
		
		if (outPath.getName().toLowerCase().startsWith("backup"))
			NetworkIO.path = Folder.newUniqueFolder(outPath.getParent()+"\\layerClass_"+appender);
		else if (outPath.getName().toLowerCase().startsWith("layer1"))
			NetworkIO.path = Folder.newUniqueFolder(outPath+"_layerClass_"+appender);
		else
			NetworkIO.path = Folder.newUniqueFolder(outPath+"\\layerClass_"+appender);

		trainNetwork(network); // classification layers
		
		
	}
	

/*
	public static void chaSequential() {
		 *
		 * process time = 752971
		 * 
		 * vs simultaneous (= 306915)
		 * 
		 *
		
		
		LayerParameters params = ParameterConfigs.cha();
		
		
		
		Network network = initializeNetwork(params);
		
		
		
		
		
		for (int f = 0 ; f < params.nFeatures; f++)
		{
			for (CnnDoubleLayer cnnDoubleLayer : network.cnnDoubleLayers)
				network.learningMechanism.resetState(cnnDoubleLayer);
			network.batchLearnCHA(params.netParams.nSamples, f);
			try {
				NetworkIO.outputWeightImages(network, Folder.newUniqueFolder(NetworkIO.path + "\\intermediateOutput feat "+f), "", Static.ColoringType.TYPE_COLOR_BEYOND_LIMITS);
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		if (network.originalObjective instanceof ChaObjective)
		{
			ChaObjective cha = (ChaObjective) network.originalObjective;

			cha.transformWeights();
			cha.transformNetwork();
			
			cha.learnBiases();
		}
		
		
		// check constraints
		network.outputConstraints(network.getLastLayer());
		
		
		
		try {
			NetworkIO.path = Folder.newUniqueFolder(NetworkIO.basePath+"\\simpleNetwork");
			NetworkIO.document(network);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		ForThreader.threadPool.shutdown();
	}
*/

	private static void loadFromFileAndConvertData() {
		String[] paths = new String[]{
//				"final tests\\MNIST\\config0\\network0" ,
//				"final tests\\MNIST\\config0\\network1" ,
//				"final tests\\MNIST\\config0\\network2" ,
//				"final tests\\MNIST\\config0\\network3" ,
//				"final tests\\MNIST\\config0\\network4" ,
//				
//				"final tests\\NORB1\\config0\\network0" ,
//				"final tests\\NORB1\\config0\\network1" ,
//				"final tests\\NORB1\\config0\\network2" ,
//				"final tests\\NORB2\\config0\\network0" ,
//				"final tests\\NORB2\\config0\\network1" ,
				"final tests\\NORB2\\config0\\network2" ,
				
				"final tests\\CIFAR1\\config0\\network0" ,
				"final tests\\CIFAR1\\config0\\network1" ,
				"final tests\\CIFAR2\\config0\\network0" ,
				"final tests\\CIFAR2\\config0\\network1" ,
				"final tests\\CIFAR2\\config0\\network2" 
		};
		Network network = null;
			try {
				for (String path : paths)
				{
					Debug.out("converting "+path);
					
					NetworkIO.path = new Folder(path);
					network = NetworkIO.recover(NetworkIO.path);
					
					LayerParameters params = network.getLastLayer().params; 
					params.widthPoolingField 		= params.widthPoolingStep
					= params.heightPoolingField		= params.heightPoolingStep
												= 2;
					
					NetworkIO.convertData(network, "2x2");
				}
			} catch (SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void testSettings(CombisParameterConfigs paramConfigs, int nReruns) {
		
		StringBuilder errors = new StringBuilder();
		
		Debug.documentProgram = false;
		
		Folder outputPath = Folder.newUniqueFolder("imageOutput\\run");
		try {
			FileUtils.writeStringToFile(new File(outputPath+"\\paramConfigs.txt"), paramConfigs+"");
			FileUtils.writeStringToFile(new File(outputPath+"\\commonParams.txt"), paramConfigs.getSeparateParams(true, paramConfigs.getFirstParams()));
		} catch (IOException e) { e.printStackTrace(); }
		
		if (Debug.documentProgram)
			try {
				Debug.out("Documenting program java files...");
				NetworkIO.documentProgram(new Folder(outputPath+"\\program\\"));
				Debug.out("Done.");
			} catch (IOException e) { e.printStackTrace(); }
		
		for (LayerParameters params : paramConfigs) {
			NetworkIO.basePath = Folder.newUniqueFolder(outputPath+"\\config");
			try {
				FileUtils.writeStringToFile(new File(NetworkIO.basePath+"\\params.txt"), paramConfigs.getSeparateParams(false, params));
			} catch (IOException e) { e.printStackTrace(); }
			
			
			try {
				
				
				testSingleSetting(params, nReruns);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				errors.append("for param setting "+NetworkIO.basePath);
				errors.append(StringUtils.join(e.getStackTrace(), "\r\n"));
				errors.append("\r\n\r\n\r\n\r\n\r\n");
			}
		}
	}
	public static void testSingleSetting(LayerParameters params, int nRestarts) {

		for (int x = 0; x<nRestarts; x++) 
			try {
				NetworkIO.path = Folder.newUniqueFolder(""+NetworkIO.basePath+"\\network");
				trainNetwork(params.clone());
//		Network network = NetworkIO.recover(new Folder("imageOutput\\simpleNetwork53"));
//		NetworkIO.path = Folder.newUniqueFolder("imageOutput\\simpleNetwork53_rec");
//		NetworkIO.document(network);
//        Debug.out("Documented to "+NetworkIO.path);

			} catch(Exception e) { e.printStackTrace(); }
//			if (!QuitExperiment.keepRunning) break;
//		}
//		
//		retrainNetwork();
//		trainInputImages();
		
	}
	
	static void initializeClassRandoms() { initializeClassRandoms(new Random().nextInt(1000)); }
	static void initializeClassRandoms(int seed) {
		Network.output("Seed: "+seed);
		Random staticRandom = new Random(seed);
		
		GenerateData.random = new Random(staticRandom.nextInt());
		Network.random = new Random(staticRandom.nextInt());
		NetworkIO.random = new Random(staticRandom.nextInt());
		CnnDoubleLayer.random = new Random(staticRandom.nextInt());
		DataSet.random = new Random(staticRandom.nextInt());
		
	}
	

	public static Network initializeNetwork(LayerParameters params) {
		if (params.netParams.batchSize > params.netParams.nSamples) 
			params.netParams.batchSize = params.netParams.nSamples; 
		
		
//		if (!params.netParams.staticData) 
//			params.netParams.nSamples = params.netParams.nIterations*params.netParams.batchSize;

		Network network = new Network(params);

		
		initializeNetwork(network);
		return network;
	}

	
	
	private static void initializeNetwork(Network network) {
		network.initializeData(); 
		network.initializeWeights();

		if (network.originalObjective instanceof ConvAutoEncoder)
			((ConvAutoEncoder) network.originalObjective).construct(network.getLastLayer());

		if (Debug.checkHeap) {
			Debug.out("Heap space available: "+ (Runtime.getRuntime().freeMemory()/1048576) +" mb ..");
			Debug.out("Max heap space: "+ (Runtime.getRuntime().maxMemory()/1048576) +" mb ..");
		}
	}

	public static Network trainNetwork(LayerParameters params) {
		Network network = initializeNetwork(params);
		trainNetwork(network);
		return network;
	}
		
	public static void trainNetwork(Network network ) {
		

		Network.output("\r\n\r\n\r\nStarting learning process...\r\n\r\n\r\n");
		network.batchLearn();
		
		
		
		if (Debug.trainingPhaseOnly) System.exit(0);
		
		if (Debug.useWindowQuitExperiment) JFrameExperiment.keepRunning = true;
		Network.output("============================\r\n============================\r");
		Network.output("\r\n\r\n\r\nStarting output/analysis process...\r\n\r\n\r\n");
	
		
		// check weight derivative
		if (Debug.checkDerivatives)
			Analysis.checkDeriatives(network);

		Network.output("============================\r\n============================\r");
		Network.output("\r\n\r\n\r\nAnalysis complete! Starting save...\r\n\r\n\r\n");
		
		Network.output(network.toString());
		
		try {
			NetworkIO.document(network);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Network.output("============================\r\n============================\r");
		Network.output("\r\n\r\n\r\nSave complete! \r\n\r\n\r\n");

	}
	
	
}
