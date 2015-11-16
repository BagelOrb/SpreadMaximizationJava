package network;

import io.Folder;
import io.Images.InputSample;
import io.JsonAble;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;
import learningMechanism.GradientAscent;
import learningMechanism.LearningMechanism;
import learningMechanism.LimitedGradientAscent;
import learningMechanism.Momentum;
import learningMechanism.MomentumTK;
import learningMechanism.Rprop;
import learningMechanism.TKrpropMomentumUnit;
import network.CombisParameterConfigs.CombisNetworkParameters;
import network.LayerParameters.NetworkParameters;
import network.Static.LearningMechanismType;
import network.Static.Objective;
import network.Static.ObjectiveCorrectionTerm;
import network.Static.OutputFunctionType;
import network.analysis.Debug;
import network.main.ImagePanel;
import network.main.JFrameExperiment;
import network.main.Main;
import network.main.WeightRenderFrame;
import objective.ChaObjective;
import objective.ConvAutoEncoder;
import objective.CrossEntropyCategorizationObjective;
import objective.ObjectiveFunction;
import objective.WeightDecayObjective;
import util.basics.DoubleArray1D;
import util.basics.DoubleArray3Dext;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.MathFunction2D;
import data.CIFAR;
import data.GenerateData;
import data.MNIST;
import data.NORB;
import data.SyntheticDataset;
import data.SyntheticDataset2;
import data.TestCases;

public class Network extends JsonAble<Network> {

	
	
	public LinkedList<CnnDoubleLayer> cnnDoubleLayers = new LinkedList<CnnDoubleLayer>();
	
	
	public CnnDoubleLayer getLastLayer() {
		return cnnDoubleLayers.getLast();
	}
	public CnnDoubleLayer getFirstLayer() {
		return cnnDoubleLayers.getFirst();
	}
	
	/**
	 * The parameters of this network
	 */
	public NetworkParameters netParams;
//	public LayerParameters params;
	/**
	 * The input sample images used
	 */
	public InputSample[] imageSamples;
	/**
	 * The random used
	 */
	public static Random random;
	/**
	 * A counter used when the batchsize is smaller than the dataset.
	 * It keeps track of what place in line the first image of the current batch has.  
	 */
	int dataCounter = 0;
	/**
	 * Retrieving input sample images from file or generated at random.
	 */
	public DataGenerator dataGenerator;
	
	/**
	 * The objective function used by the network
	 */
	public ObjectiveFunction objective;
	public ObjectiveFunction originalObjective;


	public LearningMechanism<?> learningMechanism;
	
	

	
	public Network(LayerParameters params) { // Class<?>[] classes) {
		this(params.netParams);

		cnnDoubleLayers.add(new CnnDoubleLayer(params, this, 0));

	}

	public void addLayer(LayerParameters params) {
		int position = cnnDoubleLayers.size();
		cnnDoubleLayers.add(new CnnDoubleLayer(params, this, position));
	}

	public Network(NetworkParameters params2) {
		this.netParams = params2;
		
		Network.output = "";

		learningMechanism = getLearningMechanism(netParams.learningMechanismType);
		
		objective = getObjectiveFunction(netParams.objective, netParams.etaObjectiveCorrection, netParams.objectiveCorrectionTerm);
		
	}

	
	
	
	public LearningMechanism<?> getLearningMechanism(LearningMechanismType learningMechanismType) {
		switch (learningMechanismType) {
		case GRADIENT_ASCENT:
			return new GradientAscent();
		case LIMITED_GRADIENT_ASCENT:
			return new LimitedGradientAscent();
		case MOMENTUM:
			return new Momentum();
		case MOMENTUM2:
			return new MomentumTK(netParams.alpha_momentum, netParams.etaWeights);
		case RPROP:
			return new Rprop();
		case TK_LEARNING:
			return new TKrpropMomentumUnit();
		default:
			Debug.err("learningMechanismType not implemented yet! : "+netParams.learningMechanismType);
		}
		return null;
	}
	
	public void setLearningMechanism(LearningMechanismType rprop) {
		learningMechanism = getLearningMechanism(LearningMechanismType.RPROP);
		for (CnnDoubleLayer layer : cnnDoubleLayers)
			layer.learningState = learningMechanism.newState(layer);
	}

	
	/**
	 * Reset the derivatives and all values used by rProp
	 */
	public void resetBackpropValues() {
		for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
			cnnDoubleLayer.resetDerivatives();
	}
	
	/**
	 * Initialize all weights in the network
	 */
	public void initializeWeights() {
		for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
			cnnDoubleLayer.initializeWeights();
	}
	

	/**
	 * Adds the standard weight decay term and additional correction term if non-NONE
	 * @param obj The objective function as given by name 
	 * @param eta TODO
	 * @param objCorr The correction term (something like weight decay)
	 * @return The (combined) objective function itself
	 */
	public ObjectiveFunction getObjectiveFunction(Objective obj, double eta, ObjectiveCorrectionTerm objCorr) {
		ObjectiveFunction of = ObjectiveFunction.getObjectiveFunction(obj, this);
		originalObjective = of;
		if (netParams.weightDecay == 0)
			return of;
		
		of = of.compose(netParams.weightDecay, new WeightDecayObjective(this));

		switch(objCorr) {
		case NONE: break;
		}
		return of;
	}



	/**
	 * Use the {@link Network#dataGenerator} to initialize {@link Network#imageSamples}.
	 */
	public void initializeData() {
		setDataGenerator();
		if (netParams.staticData) {
			imageSamples = new InputSample[netParams.nSamples];
            Debug.out("Initializing Data...");
//			for (int f =0; f<params.nInputFeatures; f++) 
                for (int i =0; i<netParams.nSamples; i++) {
					imageSamples[i] = dataGenerator.generateData(netParams, i); // TODO: generate data with similar attributes in different color channels?
					if (!netParams.tanhDataVsBinary) {
						imageSamples[i].multiply(.5);
						imageSamples[i].add(.5);
					}
                    if (((i+1)*100d/(netParams.nSamples))%20==0) Debug.out((i+1)*100/netParams.nSamples+"%");
//                    ((State) firstLayer.states.get(i)).input = imageSamples[i];
				}
		}
		else
			imageSamples = new InputSample[currentBatchSize]; 
	}

	/**
	 * Set the {@link dataGenerator} in accordance with the network parameter {@link CombisNetworkParameters.NetworkParameters#dataGeneration}.
	 */
	public void setDataGenerator() {
		final InputSample[] testSamples;
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
			case READ_FROM_MAT_FILE:
				final File inputsFile = netParams.matFileForInputs;
				InputSample[] testSamplesFF = null; // for the case below gives an error (this intermediate ref is neccesary cause testSamples is final)
				try {
					testSamplesFF = InputSample.readArray(new BufferedInputStream(new FileInputStream(inputsFile)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				testSamples = testSamplesFF;
				break;
			default:
				testSamples = null;
				break;
		}
		
		switch (netParams.dataGeneration) {
		case GRADIENT_CURVE:
			dataGenerator = new DataGenerator(){
				@Override public InputSample generateData(NetworkParameters params, int i) {
					return Network.generateGradientCurveData(i, Network.this); 	} 	};
			break;
		case DEBUG_CASES:
			dataGenerator =  new DataGenerator() {
				@Override public InputSample generateData(NetworkParameters params, int i) {
					return new InputSample(i, TestCases.input3[i], -1); 	} 	};
			break;
		case TEST_XOR:
		case TEST_XOR_ADD:
		case TEST_ALL_DIRS_24_CONTRAST:
		case TEST_ALL_DIRS_24_CONTRAST_NOISE:
		case TEST_ALL_DIRS_360_CONTRAST_NOISE:
		case TEST_HV_CONTRAST:
		case TEST_HV_LINES:
		case READ_FROM_MAT_FILE:
			dataGenerator =  new DataGenerator() {
				@Override public InputSample generateData(NetworkParameters params, int i) {
					return  testSamples[i]; } 	};
			break;
		case MNIST:
			{
				final MNIST dataset = new MNIST(true, -1, netParams.standardizedInput);
				if (netParams.staticData && netParams.nSamples == dataset.getDataSetSize())
				{
					Debug.out("whole MNIST data set loading...");
					dataGenerator = new DataGenerator() {
						Iterator<InputSample> imageIterator = dataset.iterator();
						@Override
						public InputSample generateData(NetworkParameters params, int i) {
							return imageIterator.next();
						}
					};
				}
				else
					dataGenerator = dataset;
				break;
			}
		case SYNTHETIC:
		{
			final SyntheticDataset dataset = new SyntheticDataset(true, -1, netParams.standardizedInput);
			if (netParams.staticData && netParams.nSamples == dataset.getDataSetSize())
			{
				Debug.out("whole SyntheticDataset data set loading...");
				dataGenerator = new DataGenerator() {
					Iterator<InputSample> imageIterator = dataset.iterator();
					@Override
					public InputSample generateData(NetworkParameters params, int i) {
						return imageIterator.next();
					}
				};
			}
			else
				dataGenerator = dataset;
			break;
		}
		case SYNTHETIC2:
		{
			final SyntheticDataset2 dataset = new SyntheticDataset2(true, -1, netParams.standardizedInput, 10, Main.patternUsed);
			if (netParams.staticData && netParams.nSamples == dataset.getDataSetSize())
			{
				Debug.out("whole SyntheticDataset data set loading...");
				dataGenerator = new DataGenerator() {
					Iterator<InputSample> imageIterator = dataset.iterator();
					@Override
					public InputSample generateData(NetworkParameters params, int i) {
						return imageIterator.next();
					}
				};
			}
			else
				dataGenerator = dataset;
			break;
		}
		case NORB:
		{
			Debug.out("NORB data loading...");
			dataGenerator = new NORB(true, -1, netParams.standardizedInput);
			break;
		}
		case CIFAR:
			{
				final CIFAR dataset = new CIFAR(true, -1, netParams.standardizedInput, true);
				if (netParams.staticData && netParams.nSamples == dataset.getDataSetSize())
				{
					Debug.out("whole CIFAR data set loading...");
					dataGenerator = new DataGenerator() {
						Iterator<InputSample> imageIterator = dataset.iterator();
						@Override
						public InputSample generateData(NetworkParameters params, int i) {
							return imageIterator.next();
						}
					};
				}
				else
					dataGenerator = dataset;
				break;
			}
		case CIFAR_TRANSFORMED:
		{
			Debug.out("CIFAR data loading...");
			dataGenerator = new CIFAR(true, -1, false, false);
			break;
		}
		case CIFAR_TRANSFORMED_STANDARDIZED:
		{
			Debug.out("transformed and standardized CIFAR data loading...");
			DataGenerator dataGen = null;
			try {
				dataGen = new DataGenerator() {
					String path = CIFAR.path+"\\standardized\\";
					File file = new File(path+"imagesTRANSFORMED.mat");
					BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

					@Override
					public InputSample generateData(NetworkParameters params, int i) {
						try {
							return new InputSample().fromByteArray(stream);
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(0);
						}
						return null;
					}
					};
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
				dataGenerator =  dataGen; 
				break;
		}
			
		default: 
			Debug.err("Not implemented "+netParams.dataGeneration+" datageneration yet!");
		}
	}
	
	/** Generate a new input sample image by the {@link Network#dataGenerator}.
	 * @param i The number of the sample
	 * @param network TODO
	 * @return A new input sample image
	 */
	static InputSample generateGradientCurveData(int i, Network network) {
//		Tuple<Integer, Integer> wh;
//		if (Debug.outputSize > 0) 
//			wh = getInputSizeFromutputSize(Debug.outputSize, Debug.outputSize);
//		else {
//			wh = getInputSizeFromutputSize(random.nextInt(4) +1,  random.nextInt(4) +1);
//		}
//        int width = wh.fst;
//        int height = wh.snd; 
        int width = network.netParams.widthInput;
        int height = network.netParams.heightInput; 
			
		double[][][] ret = new double[network.getFirstLayer().params.nInputFeatures][][];
		for (int iF = 0; iF<network.getFirstLayer().params.nInputFeatures; iF++) 			
			ret[iF] = GenerateData.generateGradientContrastCurve(width, height, GenerateData.random.nextDouble()*2*Math.PI, 5d, 0);//(width+height)*.1);
		
		return new InputSample(i, ret, GenerateData.random.nextInt(3)); // TODO: no random tag!
	}
	

	
	

	/*
	 *  covar matrix determinant derivative stuff:
	 */




	/**
     * Objective values over time
     */
    public LinkedList<Double> objectives  = new LinkedList<Double> (); 
    
    /**
     * Objective values for all objective functions (in csv format)
     */
    String valuesOfAllObjectives;


	private int iteration;
    
    public void debugout(Object... out) {
    	if (iteration %50 == 0)
    		Debug.out(out);
    }
    
    
    private double eventualMomentum;
	private int eventualBatchSize;
	public int currentBatchSize;
	
	
	public long lastRenderUpdate;
	public LinkedList<double[]> valuesOverTime = new LinkedList<double[]>();
	
	private double movingMeanDeterminantDiff;
	private double lastObjectiveValue;
   
    
    
	/*
	 * Batch learning
	 */
	/*
	public void batchLearnCHA(int nSamples, int f) {
		eventualMomentum = netParams.alpha_momentum;
		eventualBatchSize = netParams.batchSize;
		currentBatchSize = eventualBatchSize;

		
		movingMeanDeterminantDiff = 0;
		lastObjectiveValue = 0;
		
		
		
		lastRenderUpdate = System.currentTimeMillis();
		
		NetworkState[] states = null;
		
		for (int i = 0; i<netParams.nIterations; i++) {
			iteration = i;
			
			changeLearningParameters();
			
			states = processBatch(f);
			
			
			boolean doneLearning = monitorLearningProgress(states);
			
			if (doneLearning)
				break;
		}
		monitorLearningProgress(states);
		
		finalizeNetwork(states, f);
	}
	*/
	public int jumpToIteration = 0;	

	/*
	 * Batch learning
	 */
	
	public void batchLearn() {
		long lastBackupTime = System.currentTimeMillis();
		
		eventualMomentum = netParams.alpha_momentum;
		eventualBatchSize = netParams.batchSize;
		currentBatchSize = eventualBatchSize;

		
		movingMeanDeterminantDiff = 0;
		lastObjectiveValue = 0;
		
		
		valuesOverTime = new LinkedList<double[]>();
		objectives = new LinkedList<Double> ();
		
		
		lastRenderUpdate = 0; // System.currentTimeMillis();
		
		NetworkState[] states = null;
		
		for (int i = jumpToIteration; i<netParams.nIterations; i++) {
			iteration = i;
;
			
			changeLearningParameters();
			
			states = processBatch() ;
			
			
			if (System.currentTimeMillis() - lastBackupTime > 1000*60*60*1) // 1 uur
			{
				lastBackupTime = System.currentTimeMillis();
				try {
					Thread.sleep(180000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				JFrameExperiment.intermediateOutput = true;
			}
			
			
			boolean doneLearning = monitorLearningProgress(states);
			
			while (JFrameExperiment.isPauzed)
			{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

				
			if (doneLearning)
				break;
			
		}
		jumpToIteration = 0;
		
		monitorLearningProgress(states);
		
		finalizeNetwork(states);
	}
	

	private void changeLearningParameters() {
//		gradualBatchSize();
		
//		gradualMomentumAlpha();
		
//		if (iteration == netParams.nIterations / 20  && originalObjective instanceof ConvAutoEncoder)
//		{
//			ChaObjective cha = new ChaObjective(this);
//			cha.orthonormalizeWeights();
//			learningMechanism.resetState(getLastLayer());
//		}
		
		// orthonormalization
		if (this.originalObjective instanceof ChaObjective)
			if ((iteration % (netParams.nIterations / 2) == 1 && iteration > 20) || JFrameExperiment.orthonormalize)
				if (originalObjective instanceof ChaObjective)
				{
					ChaObjective cha = (ChaObjective) originalObjective;
					cha.orthonormalizeWeights();
					Debug.out("orthonormalized!");
					learningMechanism.resetState(getLastLayer());
					
					for (int i = 0; i<netParams.nIterations/10; i++) {

						cha.processBatchForBiases();
						
						boolean stop = cha.monitorBiasLearningProgress(i);
						
						if (stop || new DoubleArray1D(cha.movingMeans).vectorLength() < 1E-1)
							break;
					}
					
					JFrameExperiment.orthonormalize = false;
				}
		
//		if (this.originalObjective instanceof ChaObjective)
//			if (iteration == netParams.nIterations / 4)
//			{
//				rescaleWeightVectorsToUnitLength(getLastLayer());
//				learningMechanism.resetState(getLastLayer());
//			}
		
	}
	private void gradualBatchSize() {
		int finalIteration = Math.max(1, netParams.nIterations / 2);
		
		if (iteration > finalIteration )
		{
			currentBatchSize = eventualBatchSize;
			return;
		}
		
		double progress = ((double) iteration+1) / finalIteration; 
//		progress = 1- .9*(1-progress);
		currentBatchSize = (int) Math2.limit(2., progress * eventualBatchSize, (double) eventualBatchSize);
//		Debug.out(currentBatchSize);
	}
	
//	private void gradualMomentumAlpha() {
//		
//		if (learningMechanism instanceof Momentum)
//			((Momentum) learningMechanism).alpha_momentum = Math.min(iteration/(Math.max(1, netParams.nIterations / 20)), eventualMomentum);
//		if (learningMechanism instanceof MomentumNormal)
//			netParams.alpha_momentum = Math.min(iteration/(Math.max(1, netParams.nIterations / 20)), eventualMomentum);
//
//	}

	/**
	 * @param states
	 * @return whether we should stop learning
	 */
	private boolean monitorLearningProgress(NetworkState[] states) {
		
		objectives.add(objective.getValue(states));
		if (Debug.debug && Debug.debugLevel>2) output(""+objective);
		Debug.checkNaN(objective.lastValue);
		
		
		
		{ // values over time
			if (originalObjective instanceof ChaObjective)
			{
	//			Debug.out("means and bias derivatives:\r\n"+Arrays.toString(((ChaObjective)objective).means)+"\r\n"+Arrays.toString(getLastLayer().biasesDerivatives));
				
				valuesOverTime.add(Arrays.copyOf(((ChaObjective)originalObjective).movingMeans, getLastLayer().params.nFeatures));
			}
			else
				valuesOverTime.add(new double[]{objective.lastValue});
			
		}
		
		{ // graph features and values over time
			if (System.currentTimeMillis() - lastRenderUpdate > 10000)
			{
				lastRenderUpdate = System.currentTimeMillis();
				
				try {
					JFrameExperiment.frame.network = this;
//					JFrameExperiment.frame.graphValues(valuesOverTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (getLastLayer().params.nInputFeatures < 4)
				{
					if (WeightRenderFrame.frame == null)
					{
						WeightRenderFrame.run(this);
					} else
					{
						WeightRenderFrame.frame.resetImages();
//						for (ImagePanel i : WeightRenderFrame.frame.imgPanels)
//							i.repaint();
						WeightRenderFrame.frame.repaint();
					}
				}
			}
			
		}
		
			
		// show objective
		if (iteration%Math.max(1, netParams.nIterations/100)==0)  { 
			output("\r\n"+(iteration*100/netParams.nIterations)+ "% : step: "+iteration);
			output(""+objective);
			
			try {
				Debug.out("some weightsDerivative : "+getFirstLayer().weightsDerivatives[0].get(0, 0, 0));
				Debug.out("some weight : "+getFirstLayer().weights[0].get(0, 0, 0));
				Debug.out("some biasDerivative : "+getFirstLayer().biasesDerivatives[0]);
				Debug.out("some bias : "+getFirstLayer().biases[0]);
				Debug.out("some weight vector length : "+getFirstLayer().weights[0].vectorLength());
			} catch (Exception e ) { e.printStackTrace(); }
			
			if (this.originalObjective instanceof ChaObjective)
				outputConstraints(getLastLayer());
		}
		
		
		if (iteration%Math.max(1, netParams.nIterations/10)==0)  { 
			
			// TODO: implement weight diff shite?
//			if (i==0) 
//				lastAllWeights = getAllWeights();
//			else output("Weight diff: "+meanWeightDiff(getAllWeights()));
			
			if (netParams.objective==Static.Objective.DECORRELATION) 
				output("left upper derivative for feat 0: "+objective.getDerivative(0, 0, 0, states[0].getLast()));

			output("-------------");
			
			
		// output signalling and learning times
			
			if (netParams.objective==Static.Objective.DECORRELATION) { 
//				output("Covar Matrix: \n"+DoubleArrays2D.toString(objective.covarMatrix.getData()));
				output(""+objective.lastValue);
			}
			output("Time learn/signal: " + learnTime/signalTime);
			output("Mean learn time: " + learnTime/(iteration+1)+" ms");
			output("Mean signal time: " + signalTime/(iteration+1)+" ms");
		
		}
		
		
		// intermediate output
		if (JFrameExperiment.intermediateOutput)
		{
			Network.output("Intermediate output at itration "+iteration);
			Folder pathBefore = NetworkIO.path;
			NetworkIO.path = Folder.newUniqueFolder(pathBefore.getPath()+"\\backup");

			try {
				NetworkIO.document(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			NetworkIO.path = pathBefore;
			JFrameExperiment.intermediateOutput = false;
		}
		
		// check convergence
//		if (netParams.convergedIfBelow>0 ) { // 
//			movingMeanDeterminantDiff = movingMeanDeterminantDiff*.95 + .05*Math.log(Math.abs(objective.lastValue-lastObjectiveValue));
//			lastObjectiveValue = objective.lastValue;
//			if (movingMeanDeterminantDiff < Math.log(netParams.convergedIfBelow) && iteration > 25) {
//				output("=================================");
//				output("==> Converged!");
//				return true;
//			}
//		}
		
		
		
		if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) {
			output("=================================");
			output("Stopped after "+iteration+" iterations");
			return true;
		}
		
		return false;
	}
	
	
	
	
	
	public void finalizeNetwork(NetworkState[] states) {
		output("\\/ \\/ \\/ \\/ \\/ \\/ \\/ \r\nBatch Learning Phase finished! \r\n/\\ /\\ /\\ /\\ /\\ /\\ /\\ ");
		
		
		
		output("");
		output("Objectives over time:");
		for (int i = 0; i<objectives.size(); i+=Math.max(1,objectives.size()/20))
			output(objectives.get(i).toString());
		
		if (originalObjective instanceof ChaObjective)
		{
			ChaObjective cha = (ChaObjective) originalObjective;
			cha.finalizeNetwork();
		} 
//		if (originalObjective instanceof CrossEntropyCategorizationObjective)
		if (netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION)
		{
			int nSamples;
			if (netParams.staticData)
				nSamples = Math.min(100, netParams.nSamples-1);
			else
				nSamples = 100;
			states = signalBatch(nSamples);
			try {
				double classificationRate = new CrossEntropyCategorizationObjective(this).computeClassificationRate(states);
				output("classificationRate = "+classificationRate );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (netParams.dataGeneration.isTest() && states != null &&
				( netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION  || netParams.objective == Objective.SQUARED_ERROR_CLASSIFICATION ))
		{
			for (int n = 0 ; n < states.length; n++)
			{
				CnnDoubleLayerState state = states[n].getLast();
//				Debug.out("output = "+state.outputMaps+"\t(cat="+state.outputMaps.cat+")");
				Debug.out("output of correct feature = "+state.outputMaps.get(0, 0, state.outputMaps.cat)+"(cat="+state.outputMaps.cat+")");
			}
		}
		
		
		if (states == null)
			Debug.warn("no batch learning performed!");
		else
			valuesOfAllObjectives = ObjectiveFunction.computeAllObjectivesValues(this, states);
	}
	/*
	private void finalizeNetwork(NetworkState[] states, int f) {
		output("\\/ \\/ \\/ \\/ \\/ \\/ \\/ \r\nBatch Learning Phase finished! \r\n/\\ /\\ /\\ /\\ /\\ /\\ /\\ ");
		
		
		
		output("");
		output("Objectives over time:");
		for (int i = 0; i<objectives.size(); i+=Math.max(1,objectives.size()/20))
			output(objectives.get(i).toString());
		
		if (originalObjective instanceof ChaObjective)
		{
			ChaObjective cha = (ChaObjective) originalObjective;
			cha.orthonormalizeWeights(f);
			
//			cha.transformWeights();
//			cha.transformNetwork();
			
			cha.learnBiases(f);
		}
		
		if (netParams.dataGeneration.isTest() && 
				( netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION  || netParams.objective == Objective.SQUARED_ERROR_CLASSIFICATION ))
		{
			for (int n = 0 ; n < states.length; n++)
			{
				CnnDoubleLayerState state = states[n].getLast();
//				Debug.out("output = "+state.outputMaps+"\t(cat="+state.outputMaps.cat+")");
				Debug.out("output of correct feature = "+state.outputMaps.get(0, 0, state.outputMaps.cat)+"(cat="+state.outputMaps.cat+")");
			}
		}
		
		
		if (states == null)
			Debug.warn("no batch learning performed!");
		else
			valuesOfAllObjectives = ObjectiveFunction.computeAllObjectivesValues(this, states);
	}
	*/
	

	public static String output = "";
	public static void output(String out) {
		output  += out + "\r\n";
        System.out.println(out);
        Debug.log(out);
	}
	
	
	
	

	public void outputConstraints(CnnDoubleLayer cnnDoubleLayer2) {
		if (originalObjective instanceof ChaObjective)
		{
			output("  moving means =\n "+Arrays.toString(((ChaObjective)originalObjective).movingMeans));
			output("  means =\n "+Arrays.toString(((ChaObjective)originalObjective).newMeans));
			output("  biases =\n "+Arrays.toString(getLastLayer().biases));
		}
//		Debug.out("\nbiases =\n "+Arrays.toString(getLastLayer().biases));
		
		for (int f = 0 ; f<getLastLayer().params.nFeatures; f++)
		{
			double wl = getLastLayer().weights[f].mapped(DifferentiableFunction.square).totalSum();
			output("feature "+f+" has squared weight length of "+ wl);
		}
		
		double max = Double.NEGATIVE_INFINITY;
		for (int k = 0 ; k<getLastLayer().params.nFeatures; k++)
			for (int j = 0 ; j<k; j++)
		{
			double c = getLastLayer().weights[k].times(getLastLayer().weights[j]).totalSum();
			if (c*c>max)
				max = c*c;
			if (getLastLayer().params.nFeatures < 4)
				output("w_"+k+" ^T w_"+j+" = "+ c);
		}
		output("largest square orthogonality constraint: "+max);
	}
	
	
	
	
	
	
	/*
	 * Batch Processing
	 */
	private double signalTime;
	private double learnTime;

	public NetworkState[] processBatch() {
		double before = System.currentTimeMillis();

		
		
		NetworkState[] states = signalBatch();
		
		
		
		signalTime += System.currentTimeMillis() - before;
		before = System.currentTimeMillis();
	
//		if (originalObjective instanceof ChaObjective)
//			updateBatch(states, 0);
//		else
			updateBatch(states);
		
		if (netParams.rescaleWeights)
			for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
				limitWeights(cnnDoubleLayer);
		
		learnTime += System.currentTimeMillis() - before;
		before = System.currentTimeMillis();

		
		return states;
	}

	private void limitWeights(CnnDoubleLayer cnnDoubleLayer2) {
		for (int f = 0 ; f<cnnDoubleLayer2.params.nFeatures ; f++)
		{
			double wLength = Math.sqrt(cnnDoubleLayer2.weights[f].foldr(MathFunction2D.squaredAddition, 0));
			
			if (wLength > netParams.weightMax)
			{
//				Debug.out("weights vector too large! (>"+netParams.weightMax+") rescaling...");
				cnnDoubleLayer2.weights[f].multiply(netParams.weightMax/wLength);
			}
			
			if (Math2.square(cnnDoubleLayer2.biases[f]) > 10*10)
			{
				cnnDoubleLayer2.biases[f] = 10 * Math.signum(cnnDoubleLayer2.biases[f]);
			}
		}
	}
	private void rescaleWeightVectorsToUnitLength(CnnDoubleLayer cnnDoubleLayer2) {
		for (int f = 0 ; f<cnnDoubleLayer2.params.nFeatures ; f++)
		{
			double wLength = Math.sqrt(cnnDoubleLayer2.weights[f].foldr(MathFunction2D.squaredAddition, 0));
			
			cnnDoubleLayer2.weights[f].multiply(netParams.weightMax/wLength);
			
		}
	}

	public NetworkState[] signalBatch() {
		return signalBatch(currentBatchSize);
	}
	public NetworkState[] signalBatch(int nSamplesUsed) {

		if (!netParams.staticData)
			inputNewImages(nSamplesUsed);

		final NetworkState[] ret = new NetworkState[nSamplesUsed];
		
		if (nSamplesUsed == netParams.nSamples)
			dataCounter = 0; 
			
		if (netParams.staticData && nSamplesUsed!=netParams.nSamples 
				&& dataCounter + nSamplesUsed >= imageSamples.length ) 
		{ 
			dataCounter = 0; 
			List<InputSample> dataList = Arrays.asList(imageSamples);
			Collections.shuffle(dataList, random);
			imageSamples = (InputSample[]) dataList.toArray();
			//output("[Data shuffled!!!!!!]");
		}
		
//		if (Debug.threading)
			new ForThreader() {
				
				@Override
				public void run(int i, int threadTag, int nThreads) {
					int imgTag = i+dataCounter;
					NetworkState nwState = new NetworkState(Network.this, imageSamples[imgTag]);
					ret[i] = nwState;
					signal(nwState);
				}
				
				@Override
				public void initializeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finalizeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
			}.delegate(nSamplesUsed);
//		else
//			for (int i =0; i<nSamplesUsed; i++) 
//			{
//				int imgTag = i+dataCounter;
//				if (netParams.staticData && netParams.batchSize!=netParams.nSamples && imgTag >= imageSamples.length ) { 
//					synchronized(Network.this) { // < TODO! ?!
//						dataCounter = -i; // s.t. i+dataCounter = 0;
//						imgTag = i+dataCounter;
//						List<InputSample> dataList = Arrays.asList(imageSamples);
//						Collections.shuffle(dataList, random);
//						imageSamples = (InputSample[]) dataList.toArray();
//						//output("[Data shuffled!!!!!!]");
//					}
//				}
//				NetworkState nwState = new NetworkState(this, imageSamples[imgTag]);
//				ret[i] = nwState;
//				signal(nwState);
//			}
			
			
		if (netParams.staticData && netParams.batchSize!=netParams.nSamples) 
			dataCounter += netParams.batchSize;

		return ret;
	}

	public void signal(NetworkState nwState) {
		for (int l = 0; l < cnnDoubleLayers.size(); l++)
		{
			CnnDoubleLayer layer = cnnDoubleLayers.get(l);
			CnnDoubleLayerState state = nwState.get(l);
			
			if (l >= 1)
				state.inputMaps = nwState.get(l-1).outputMaps;
			
			layer.signal(state);
			state.outputMaps.cat = state.inputMaps.cat; 
		}
	}
	public void inputNewImages(int nSamplesUsed) {
		imageSamples = new InputSample[nSamplesUsed];
		for (int i =0; i<nSamplesUsed; i++) 
		{
			imageSamples[i] = dataGenerator.generateData(netParams, i); 
			if (! netParams.tanhDataVsBinary)
			{
				imageSamples[i].multiply(.5);
				imageSamples[i].add(.5);
			}
		}
	}
	private double totalDerDistance;
	
	public boolean significantChange = false;
	
	
	
	
	
	
	
	
	
	/*
	 *   U P D A T E
	 */
	
	
	
	private void updateBatch(NetworkState[] states) {
		objective.computeCommonDerivativeStuff(states);

		for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
			cnnDoubleLayer.resetDerivatives();
		if (originalObjective instanceof ConvAutoEncoder)
			((ConvAutoEncoder) originalObjective).resetDerivatives();
		
		if (!(originalObjective instanceof ConvAutoEncoder))
			computeOutputDerivativesBatch(states);
		Debug.checkNaN(getLastLayer().weightsDerivatives[0].get(0, 0, 0));
		objective.addWeightDerivatives(states);
		
//		System.out.println("means = "+ Arrays.toString(((ChaObjective) objective).means)+"\t\tbiases= " +Arrays.toString(cnnDoubleLayer.biases));
//		System.out.println();
		
		if (!Debug.noUpdate)
            updateWithDerivatives();
	}
	

	private void computeOutputDerivativesBatch(final NetworkState[] states) {
		
		totalDerDistance = 0;

//		if (Debug.threading)
			new ForThreader() {
				
				@Override
				public void run(int i, int threadTag, int nThreads) {
					NetworkState nwState = states[i];
					setAllOutputDerivativesSample(nwState.getLast());
					
					for (int l = cnnDoubleLayers.size()-1 ; l>= 0; l--)
					{
						CnnDoubleLayer cnnDoubleLayer = cnnDoubleLayers.get(l);
						cnnDoubleLayer.addBackpropagatedDerivatives(nwState.get(l), threadTag);
					}
				}
				
				@Override
				public void initializeForThreads(int nThreads) {
					for (CnnDoubleLayer layer : cnnDoubleLayers)
					{
						layer.initializeTemporaryDerivatives(nThreads);
					}
						
				}
				
				@Override
				public void finalizeForThreads(int nThreads) {
					for (CnnDoubleLayer layer : cnnDoubleLayers)
					{
						layer.mergeTemporaryDerivatives(nThreads);
					}
				}
			}.delegate(states.length);
//		else
//			for (int i = 0; i< states.length; i++) 
//			{
//				NetworkState nwState = states[i];
//				setAllOutputDerivativesSample(nwState.getLast());
//				
//				for (int l = cnnDoubleLayers.size()-1 ; l>= 0; l--)
//				{
//					CnnDoubleLayer cnnDoubleLayer = cnnDoubleLayers.get(l);
//					cnnDoubleLayer.addBackpropagatedDerivatives(nwState.get(l), -1);
//				}
//			}

	}
	public void setAllOutputDerivativesSample(CnnDoubleLayerState state) {
		InputSample out = state.outputMaps;
		for (int f = 0; f<out.depth;f++)
			setAllOutputDerivativesSample(state, f);
	}
	


	private void updateWithDerivatives() {
		for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
		{
			learningMechanism.updateWeights(cnnDoubleLayer, this);
			learningMechanism.updateBiases(cnnDoubleLayer);
			if (originalObjective instanceof ConvAutoEncoder)
				learningMechanism.updateBiases((ConvAutoEncoder) originalObjective);
		}
	}

	
	
	
	/*
	 * C H A   U P D A T E 
	 */
	
	public NetworkState[] processBatch(int f) {
		double before = System.currentTimeMillis();
		
		
		
		NetworkState[] states = signalBatchCHA(f);
		
		
		signalTime += System.currentTimeMillis() - before;
		before = System.currentTimeMillis();
		
		updateBatch(states, f);
		
		if (netParams.rescaleWeights)
			for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
				limitWeights(cnnDoubleLayer);
		
		learnTime += System.currentTimeMillis() - before;
		before = System.currentTimeMillis();
		
		
		return states;
	}
	
	public NetworkState[] signalBatchCHA(int f) {
		return signalBatchCHA(currentBatchSize, f);
	}
	public NetworkState[] signalBatchCHA(int nSamplesUsed, final int f) {

		if (!netParams.staticData)
			inputNewImages(nSamplesUsed);

		final NetworkState[] ret = new NetworkState[nSamplesUsed];
		
		if (nSamplesUsed == netParams.nSamples)
			dataCounter = 0; 
			
		if (netParams.staticData && nSamplesUsed!=netParams.nSamples 
				&& dataCounter + nSamplesUsed >= imageSamples.length ) 
		{ 
			dataCounter = 0; 
			List<InputSample> dataList = Arrays.asList(imageSamples);
			Collections.shuffle(dataList, random);
			imageSamples = (InputSample[]) dataList.toArray();
			//output("[Data shuffled!!!!!!]");
		}
		
//		if (Debug.threading)
			new ForThreader() {
				
				@Override
				public void run(int i, int threadTag, int nThreads) {
					int imgTag = i+dataCounter;
					NetworkState nwState = new NetworkState(Network.this, imageSamples[imgTag]);
					ret[i] = nwState;
					signal(nwState, f);
				}
				
				@Override
				public void initializeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finalizeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
			}.delegate(nSamplesUsed);
//		else
//			for (int i =0; i<nSamplesUsed; i++) 
//			{
//				int imgTag = i+dataCounter;
//				if (netParams.staticData && netParams.batchSize!=netParams.nSamples && imgTag >= imageSamples.length ) { 
//					synchronized(Network.this) { // < TODO! ?!
//						dataCounter = -i; // s.t. i+dataCounter = 0;
//						imgTag = i+dataCounter;
//						List<InputSample> dataList = Arrays.asList(imageSamples);
//						Collections.shuffle(dataList, random);
//						imageSamples = (InputSample[]) dataList.toArray();
//						//output("[Data shuffled!!!!!!]");
//					}
//				}
//				NetworkState nwState = new NetworkState(this, imageSamples[imgTag]);
//				ret[i] = nwState;
//				signal(nwState);
//			}
			
			
		if (netParams.staticData && netParams.batchSize!=netParams.nSamples) 
			dataCounter += netParams.batchSize;

		return ret;
	}
	
	public void signal(NetworkState nwState, int f) {
		for (int l = 0; l < cnnDoubleLayers.size(); l++)
		{
			if (l== cnnDoubleLayers.size()-1)
				cnnDoubleLayers.get(l).signalUpTo(nwState.get(l), f);
			cnnDoubleLayers.get(l).signal(nwState.get(l));
		}
	}
	
	
	private void updateBatch(NetworkState[] states, int f) {
		objective.computeCommonDerivativeStuff(states);
		
		for (CnnDoubleLayer cnnDoubleLayer : cnnDoubleLayers)
			cnnDoubleLayer.resetDerivatives();
		computeOutputDerivativesBatch(states, f);
		Debug.checkNaN(getLastLayer().weightsDerivatives[0].get(0, 0, 0));
		completeDerivatives(states, f);
		
//		System.out.println("means = "+ Arrays.toString(((ChaObjective) objective).means)+"\t\tbiases= " +Arrays.toString(cnnDoubleLayer.biases));
//		System.out.println();
		
		if (!Debug.noUpdate)
			updateWithDerivatives(); // TODO: introduce f
	}
	
	
	private void computeOutputDerivativesBatch(final NetworkState[] states, final int f) {
		
		totalDerDistance = 0;
		
//		if (Debug.threading)
		new ForThreader() {
			
			@Override
			public void run(int i, int threadTag, int nThreads) {
				NetworkState nwState = states[i];
				setAllOutputDerivativesSample(nwState.getLast(), f);
				
				for (int l = cnnDoubleLayers.size()-1 ; l>= 0; l--)
				{
					CnnDoubleLayer cnnDoubleLayer = cnnDoubleLayers.get(l);
					cnnDoubleLayer.addBackpropagatedDerivatives(nwState.get(l), f, threadTag);
				}
			}
			
			@Override
			public void initializeForThreads(int nThreads) {
				for (CnnDoubleLayer layer : cnnDoubleLayers)
				{
					layer.initializeTemporaryDerivatives(nThreads);
				}
				
			}
			
			@Override
			public void finalizeForThreads(int nThreads) {
				for (CnnDoubleLayer layer : cnnDoubleLayers)
				{
					layer.mergeTemporaryDerivatives(nThreads);
				}
			}
		}.delegate(states.length);
//		else
//			for (int i = 0; i< states.length; i++) 
//			{
//				NetworkState nwState = states[i];
//				setAllOutputDerivativesSample(nwState.getLast());
//				
//				for (int l = cnnDoubleLayers.size()-1 ; l>= 0; l--)
//				{
//					CnnDoubleLayer cnnDoubleLayer = cnnDoubleLayers.get(l);
//					cnnDoubleLayer.addBackpropagatedDerivatives(nwState.get(l), -1);
//				}
//			}
		
	}
	public void setAllOutputDerivativesSample(CnnDoubleLayerState state, int f) {
		InputSample out = state.outputMaps;
		for (int xO = 0; xO<out.width; xO++)
			for (int yO = 0; yO<out.height; yO++) 
			{
				double der = objective.getDerivative(f, xO, yO, state);
				
				totalDerDistance = Math.max(der*der, totalDerDistance) ; 
				
				if (netParams.objective == Objective.CROSS_ENTROPY_CLASSIFICATION && getLastLayer().params.outputFunction == OutputFunctionType.SOFT_MAX_ACT)
					state.poolingMapsDerivatives.set(xO, yO, f, der);
				else
					state.outputMapsDerivatives.set(xO, yO, f, der);
			}
	}
	private void completeDerivatives(NetworkState[] states, int f) {
		if (originalObjective instanceof ChaObjective)
			((ChaObjective) originalObjective).addWeightDerivatives(states, f);
		else
			Debug.err("expecting CHA objective when computing derivatives of only one feature");
	}
	
	
	
	/*
	 *  BS?
	 */
	

	// output error to true output TIMES -1 !!
	@SuppressWarnings("unused")
	private static <T extends DoubleArray3Dext<T>> double sumOfSquaresError(DoubleArray3Dext<T> predicteds, DoubleArray3Dext<T> trues) {
//		double ret = 0;
		@SuppressWarnings("unchecked")
		DoubleArray3Dext<T> diffs = predicteds.zippedWith(new MathFunction2D() {
			@Override public double apply(double pred, double tru) {
				return pred-tru;
			}}, (T) trues);
		return -.5 * diffs.foldr(new MathFunction2D() {
			@Override public double apply(double l, double r) {
				return r+l*l;
			}}, 0);
		// : -1/2 * sum_k { (y_k - t_k)^2 }
	}
	
	public static void main(String[] args) {
		Main.main(args);
	}

	
	
	
	
	
	/*
	 *   I / O
	 */

	@Override
	public JsonObject toJsonObject() {
		JsonArrayBuilder layerArrayBuilder = Json.createArrayBuilder();
		for (CnnDoubleLayer layer : cnnDoubleLayers)
			layerArrayBuilder.add(layer.toJsonObject());
		return Json.createObjectBuilder()
			.add("cnnDoubleLayers", layerArrayBuilder)
//			.add("netParams", netParams.toJsonObject()) ==> done in separate file!
			.build();
	}

	@Override
	public Network fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException 
	{
        if (Debug.readWrite) Debug.out(o);
        
//        netParams = new NetworkParameters().fromJsonObject(o.getJsonObject("netParams")); ==> done in separate file!
        
        JsonArray layersArray = o.getJsonArray("cnnDoubleLayers");
        cnnDoubleLayers = new LinkedList<CnnDoubleLayer>();
        int p = 0;
        for (JsonValue layerObject : layersArray)
        {
        	cnnDoubleLayers.add(new CnnDoubleLayer(this, p).fromJsonObject((JsonObject) layerObject));
        	p++;
        }
        
        setDataGenerator();
        
		if (originalObjective instanceof ConvAutoEncoder)
			((ConvAutoEncoder) originalObjective).construct(getLastLayer());

        
        return this;
	}










	
	
}
