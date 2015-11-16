package network.main;

import io.Images.InputSample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.CombisParameterConfigs;
import network.CombisParameterConfigs.CombisNetworkParameters;
import network.LayerParameters;
import network.LayerParameters.NetworkParameters;
import network.Static;
import network.Static.DataGeneration;
import network.Static.Objective;
import network.Static.OutputFunctionType;
import network.Static.Pooling;
import network.Static.WeightConstraint;
import network.analysis.Debug;

import org.apache.commons.io.FileUtils;

import data.MNIST;
import util.math.DifferentiableFunction;
import util.math.MathFunction1D;

public class ParameterConfigs {
	

	/* _____  __       _ _
	 *   |   |  )  /\   |  |\  |
	 *   |   |-<  /__\  |  | \ |
	 *   |   |  \ |  | _|_ |  \|
	 */
	@SuppressWarnings("unused")
	public static LayerParameters train() {
		Main.initializeClassRandoms(); 
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		params.netParams.convergedIfBelow = 1E-7;
		
		// debug shite:
		params.netParams.randomizeBiases = false;
//		Debug.debug = true;				// ==> do in Debug.java !
//		Debug.checkDerivatives = false; // ==> do in Debug.java !

		params.netParams.rescaleWeights = false;

		params.netParams.etaWeights = 1E-2; 
		
        params.netParams.learningMechanismType = Static.LearningMechanismType.RPROP_AT_WEIGHTS; // NORMAL_BACKPROP; //  NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .001;
		
		params.useBias = true; 
		
//		static boolean convolution = true;
		params.nInputFeatures = 1;
//		int inputSize = 9;// = 1*5+(5-1)  
		int localFieldSize = 3;
		int poolSize = 3;
		
		params.nFeatures =6;
		
		params.netParams.absMaxInitWeights =.05;  
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // GRADIENT_CURVE; 
		params.netParams.nIterations = 5000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 1000; // <= nIterations * batchSize 
		
		params.netParams.nItersTrainInputs = 0; 
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localFieldSize; params.heightConvolutionField = localFieldSize;
//		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
//		params.widthInput = inputSize; params.heightInput = inputSize;
		
		
		params.deriveOtherParams();
		return params;
	}
	/*  __    ___  __          _
	 * |  \  |    |  ) |   |  / \
 	 * |   | |--  |-<  |   | |  _
	 * |__/  |___ |__)  \_/|  \_/|
	 */
	
	public static LayerParameters debug() {
		Main.initializeClassRandoms(); // 1234  
		
		Debug.dontDocument = true;
		Debug.checkDerivatives = true;
		
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		

		// debug shite:
		params.netParams.randomizeBiases=true; 

		params.netParams.nItersTrainInputs = 1; 

		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 5;
		
		params.netParams.etaWeights = 5E-5; 

		params.signallingFunction = DifferentiableFunction.linear;
		params.netParams.objective = Static.Objective.SQUARED_TOTAL; //CONV_HEBBIAN_ALG; // DECORRELATION; //    KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 10; 
		
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		

        params.netParams.learningMechanismType = Static.LearningMechanismType.RPROP; // MOMENTUM; // GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .001;

		params.nInputFeatures = 1;
		int localConvolutionFieldSize = 6;
		int poolSize = 4;
		int inputSize = 13;
		
		params.pooling = Pooling.ARG_MAX_ABS; // SOFT_ARG_MAX_ABS; // SOFT_ARG_MAX_SQUARE; // ARG_MAX_SQUARE; //      
		
		params.nFeatures = 4;
		
		params.netParams.absMaxInitWeights = .1;  
		params.netParams.dataGeneration = Static.DataGeneration.GRADIENT_CURVE; // MNIST; //   TEST_CASES; // 
		
		params.netParams.nIterations = 10; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 4; // <= nIterations * batchSize 
		params.netParams.batchSize = 4;  

		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;

		params.deriveOtherParams();
		return params;
	}
	/*  __    ___  __          _      __
	 * |  \  |    |  ) |   |  / \    /  \
 	 * |   | |--  |-<  |   | |  _      /
	 * |__/  |___ |__)  \_/|  \_/|   /___
	 */
	
	public static LayerParameters debug2() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = true;
		Debug.checkDerivatives = true;

		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		

		// debug shite:
		params.netParams.randomizeBiases=true; 

		params.netParams.nItersTrainInputs = 1; 

		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E1; 

		params.netParams.objective = Static.Objective.CONV_HEBBIAN_ALG; //  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 100; 

		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		

        params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; //  NORMAL_BACKPROP; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .001;

		params.netParams.alpha_momentum = .9;

		params.nInputFeatures = 1;
		int localConvolutionFieldSize = 6;
		int poolSize = 6;
		int inputSize = 12;
		
		params.nFeatures = 3;
		
		params.netParams.absMaxInitWeights = .01;  
		params.netParams.dataGeneration = Static.DataGeneration.GRADIENT_CURVE; // MNIST; //   TEST_CASES; // 
		
		params.netParams.nIterations = 1; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 100; // <= nIterations * batchSize 
		params.netParams.batchSize = 100;  

		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;

		params.deriveOtherParams();
		return params;
	}

	/*   __   
	 *  /      |   |    /\
	 * |  __   |---|   /__\
	 *  \__/|  |   |  /    \
	 */
	
	public static LayerParameters gha() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;

		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		params.pooling = Pooling.ARG_MAX_ABS;
		
		params.netParams.etaWeights = 1E-2; 
		
		params.netParams.objective = Static.Objective.CONV_HEBBIAN_ALG;// SQUARED_ERROR_CLASSIFICATION ; //  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 10; 

		params.netParams.rescaleWeights = true;
		params.netParams.weightMax = 2;

		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType. TK_LEARNING ;// RPROP ; //  MOMENTUM; // MOMENTUM2; //  GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .1;
		params.netParams.eta_rPropPlus = 1.2; 
		params.netParams.eta_rPropMin = .8; 

		params.netParams.alpha_momentum = .7;


		params.signallingFunction = DifferentiableFunction.linear;

		params.nInputFeatures = 1;
		int poolSize = 1;

		int inputSize = 28;
		int localConvolutionFieldSize = 28;
		
		params.nFeatures = 15;
		
		params.netParams.absMaxInitWeights = .01;  
		params.netParams.dataGeneration = Static.DataGeneration. MNIST; //  GRADIENT_CURVE; // 
		
		params.netParams.staticData = false;

		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 10000; 
		params.netParams.batchSize = 10;  
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;}
	/*   __   
	 *  /     |   |    /\
	 * |      |---|   /__\
	 *  \__/  |   |  /    \
	 */
	
	public static LayerParameters cha() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;

		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		
		params.netParams.etaWeights = 1E-5; 
		params.netParams.etaBiases = 1E-3; 
		
		params.netParams.convergedIfBelow = 1E-4;
		params.netParams.objective = Static.Objective.CONV_HEBBIAN_ALG;// SQUARED_ERROR_CLASSIFICATION ; //  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 10; 

		params.netParams.rescaleWeights = true;
		params.netParams.weightMax = 2;

		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // GRADIENT_ASCENT; //  TK_LEARNING; //   RPROP ; // MOMENTUM2; //  GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;

		params.netParams.absMaxInitWeights = 0.01;  

		params.signallingFunction = DifferentiableFunction.linear;

		params.nFeatures = 20;
		int localConvolutionFieldSize = 7;
		int poolSize = 7;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 2;
//		int inputSize = 108;
//		params.netParams.dataGeneration = Static.DataGeneration.NORB; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 3;
//		int inputSize = 32;
//		params.netParams.dataGeneration = Static.DataGeneration.CIFAR;// MNIST; // CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
		
		
		
		params.netParams.staticData = false;
		netParams.standardizedInput = false; // <<<< change this when doing CIFAR!!!!

		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize(); 
		params.netParams.batchSize = 24;  
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	public static final CombisParameterConfigs compareCha;
	static {
		LayerParameters params = ParameterConfigs.cha();// .classifyFromOutput(); //  .debug(); //.testCase();

		compareCha = new CombisParameterConfigs(params);
		CombisNetworkParameters netParamss = compareCha.netParams;
//		netParamss.dataGeneration = new DataGeneration[]{
////				DataGeneration.CIFAR,
////				DataGeneration.NORB,
//				DataGeneration.MNIST
//				};
		netParamss.objective = new Objective[]{
//				Objective.DECORRELATION, 
//				Objective.GAUSS_UNIFORMIZATION,
				Objective.CONV_HEBBIAN_ALG
				};
	}

	public static LayerParameters chaFAST() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		
		params.netParams.etaWeights = 1E-4; 
		params.netParams.etaBiases = 5E-3; 
		
		params.netParams.convergedIfBelow = 1E-3;
		params.netParams.objective = Static.Objective.CONV_HEBBIAN_ALG;// SQUARED_ERROR_CLASSIFICATION ; //  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 1; 
		
		params.netParams.rescaleWeights = true;
		params.netParams.weightMax = 2;
		
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // GRADIENT_ASCENT; //  TK_LEARNING; //   RPROP ; // MOMENTUM2; //  GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .02;
		params.netParams.eta_rPropPlus = 1.2; 
		params.netParams.eta_rPropMin = .8; 
		
		params.netParams.alpha_momentum = .9;
		
		params.netParams.absMaxInitWeights = .01;  
		
		params.signallingFunction = DifferentiableFunction.linear;
		
		params.nFeatures = 20;
		int localConvolutionFieldSize = 8;
		int poolSize = 8;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 2;
//		int inputSize = 108;
//		params.netParams.dataGeneration = Static.DataGeneration.NORB; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 3;
//		int inputSize = 32;
//		params.netParams.dataGeneration = Static.DataGeneration.CIFAR;// MNIST; // CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
		
		
		
		params.netParams.staticData = false;
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 120; 
		params.netParams.batchSize = 36;  
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	/*   ___  _ _   __   ___                __                     ____
 	 *  |      |   /    |    |\  | \    /  /  \  |    |   | |\ /| |      
	 *  |--    |  |  __ |--  | \ |  \  /  |    | |    |   | | V | |--          
	 *  |___  _|_  \_/| |___ |  \|   \/    \__/  |___  \_/| |   | |___               
	 */ 
	
	public static LayerParameters eigenvolumeExpansion() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;

		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		
		params.netParams.etaWeights = 1E-3; 
		params.netParams.etaBiases = 1E-3; 
		
		params.netParams.convergedIfBelow = 0;// 1E-4;
		params.netParams.objective = Static.Objective.DECORRELATION;// CONV_HEBBIAN_ALG;// SQUARED_ERROR_CLASSIFICATION ; //  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.etaMean = 1; 

		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 2;

		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.LIMITED_GRADIENT_ASCENT; //MOMENTUM; //   TK_LEARNING; //   RPROP ; // MOMENTUM2; //  GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;

		params.netParams.absMaxInitWeights = .01;  

		params.signallingFunction = DifferentiableFunction.tanh;

		params.nFeatures = 20;
		int localConvolutionFieldSize = 7;
		int poolSize = 7;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 2;
//		int inputSize = 108;
//		params.netParams.dataGeneration = Static.DataGeneration.NORB; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 3;
//		int inputSize = 32;
//		params.netParams.dataGeneration = Static.DataGeneration.CIFAR;// MNIST; // CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
		
		
		
		params.netParams.staticData = false;
		
		netParams.standardizedInput = false; // <<<< change this when doing CIFAR!!!!

		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.nSamples = 24; 
		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize(); 
		params.netParams.batchSize = 24;  
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	
	/*  ___   _    ___       ___  _ _   ___          __   _ _  __        __                 ____   ____                                           
	 *  |  \ |  \ |         (___   |   /   ' |\ /|  /  \   |  |  \      /  '    /\   |   | (____  (____                                                  
	 *  |__/ |__/ |--   --      \  |  |   __ | V | |    |  |  |   |    |   __  /__\  |   |      \      \                                                        
	 *  |    |  \ |___       ___/ _|_  \__/| |   |  \__/  _|_ |__/      \__/| |    |  \_/|  ____/  ____/                                                                       
	 */ 
	
	public static LayerParameters preSigmoidGaussApproximation() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		
		params.netParams.etaWeights = 1E-3; 
		params.netParams.etaBiases = 1E-3; 
		
		params.netParams.convergedIfBelow = 0;// 1E-4;
		params.netParams.objective = Static.Objective.GAUSS_UNIFORMIZATION;
		params.netParams.etaMean = 1; 
		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 2;
		
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.LIMITED_GRADIENT_ASCENT; //MOMENTUM; //   TK_LEARNING; //   RPROP ; // MOMENTUM2; //  GRADIENT_ASCENT; // RPROP_AT_WEIGHTS; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		params.netParams.absMaxInitWeights = .001;  
		
		params.signallingFunction = DifferentiableFunction.tanh;
		
		params.nFeatures = 20;
		int localConvolutionFieldSize = 7;
		int poolSize = 7;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 2;
//		int inputSize = 108;
//		params.netParams.dataGeneration = Static.DataGeneration.NORB; // CIFAR;// CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
//		params.nInputFeatures = 3;
//		int inputSize = 32;
//		params.netParams.dataGeneration = Static.DataGeneration.CIFAR;// MNIST; // CIFAR_TRANSFORMED_STANDARDIZED; //   MNIST; // CIFAR_TRANSFORMED; //   GRADIENT_CURVE; // 
		
		
		
		params.netParams.staticData = false;
		
		netParams.standardizedInput = false; // <<<< change this when doing CIFAR!!!!
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.nSamples = 24; 
		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize(); 
		params.netParams.batchSize = 24;  
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	
	/*  __                  _____  __
	 * <__  |\ /|  /\  |      |   |  ) \ /
	 *    \ | V | /__\ |      |   |-<   V
	 *  __/ |   | |  | |___   |   |  \  |
	 */
	public static LayerParameters smallTry() {
		Main.initializeClassRandoms(); 
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		params.netParams.weightConstraints = new Static.WeightConstraint[]
				{};
//				{WeightConstraint.MAX_SUM_OF_SQUARE_INPUT_WEIGHTS_BY_STRETCHING_WEIGHTS};
		params.netParams.weightMax = 16;

		// debug shite:
		params.netParams.randomizeBiases=false; 
		
		params.netParams.nItersTrainInputs = 0; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-3; 
		
		params.netParams.objective = Static.Objective.GAUSS_UNIFORMIZATION; //  DECORRELATION; // ERROR_MINIMIZATION; // LINEAR_FUNCTION; //  
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm.NONE; // SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  NONE; // 
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // GRADIENT_ASCENT; //  RPROP_AT_WEIGHTS; //  MOMENTUM; //    NORMALIZING_AT_OBJECTIVE; // MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		params.nInputFeatures = 1;
		int localConvolutionFieldSize = 5;
		int poolSize = 5;
		int inputSize = 28;
		
		params.netParams.convergedIfBelow = 1E-2;
		
		// R B M !
		params.rbmMiniBatchSize = 10;
		params.signallingFunction = DifferentiableFunction.logisticSigmoid;

		
		params.nFeatures = 4;

		
		params.netParams.absMaxInitWeights = .1;  
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // GRADIENT_CURVE; //  
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 1000; // <= nIterations * batchSize 
		params.netParams.batchSize = 100; // <= nIterations * batchSize 
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		
		Debug.documentWeightImages = true;
		
		params.deriveOtherParams();
		return params;
	}
	
	/* _____  ___  __  _____    __         ___   ___  ___
	 *   |   |    (__    |     /  \  /\   (__   |    (__
	 *   |   |--     \   |    |     /__\     \  |--     \
	 *   |   |___  __/   |     \__/ |   |  __/  |___  __/
	 */
	public static LayerParameters testCase() {
		Main.initializeClassRandoms(); 
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases=false; 
		
		params.netParams.nItersTrainInputs = 0; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-4; 
		
		
		params.netParams.objective = Static.Objective. GAUSS_UNIFORMIZATION;// DECORRELATION; // KDTREE_UNIF; //  
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm.NONE; // SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  NONE; //
		params.netParams.etaObjectiveCorrection = 0;  //1E-6;
		params.netParams.weightDecay = 0.01; // 5E-1;
		
		params.netParams.weightConstraints = new Static.WeightConstraint[]
				{};
//				{WeightConstraint.MAX_SUM_OF_SQUARE_INPUT_WEIGHTS_BY_STRETCHING_WEIGHTS};
		params.netParams.weightMax = 16;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.RPROP_AT_WEIGHTS; // MOMENTUM; //   NORMAL_BACKPROP; //  NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
//		params.netParams.eta_rPropMin = .6;
//		params.netParams.eta_rPropPlus = 1.2;
		
		params.nInputFeatures = 1;
		int localFieldSize = 6;
		
		
		// R B M !
		params.rbmMiniBatchSize = 10;
		params.signallingFunction = DifferentiableFunction.logisticSigmoid;
		

		params.nFeatures = 4;
		params.netParams.tanhDataVsBinary = false;
		
		
		params.netParams.absMaxInitWeights = .1;  
		params.netParams.dataGeneration = Static.DataGeneration.TEST_ALL_DIRS_24_CONTRAST ;// TEST_ALL_DIRS_24_CONTRAST_NOISE; //  TEST_ALL_DIRS_360_CONTRAST_NOISE; //  READ_FROM_MAT_FILE; // MNIST; // GRADIENT_CURVE; //  TEST_CASES; //
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.convergedIfBelow = 1E-8;
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localFieldSize; params.heightConvolutionField = localFieldSize;
//		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
//		params.widthInput = inputSize; params.heightInput = inputSize; 
		
		
		
		
		Debug.documentWeightImages = true;
		
		params.deriveOtherParams();
		return params;
	}
	public static final CombisParameterConfigs compareDecGauKduOnTestCases;
	static {
		LayerParameters params = ParameterConfigs.testCase();// .classifyFromOutput(); //  .debug(); //.testCase();

		compareDecGauKduOnTestCases = new CombisParameterConfigs(params);
		CombisNetworkParameters netParamss = compareDecGauKduOnTestCases.netParams;
		netParamss.weightDecay = new Double[]{0., 1E-2, 1E-1};  
		netParamss.dataGeneration = new DataGeneration[]{DataGeneration.TEST_ALL_DIRS_24_CONTRAST, DataGeneration.TEST_ALL_DIRS_24_CONTRAST_NOISE};
		netParamss.objective = new Objective[]{Objective.DECORRELATION, Objective.GAUSS_UNIFORMIZATION};
	}
	/*    __             ___  ___ ___  ___                    _ _  ___ _____
	 *   /  \ |     /\  (__  (__   |  |    \ /    |\ /| |\  |  |  (__    |
	 *  |     |    /__\    \    \  |  |--   V     | V | | \ |  |     \   |
	 *   \__/ |___ |  |  __/  __/ _|_ |     |     |   | |  \| _|_  __/   |
	 */ 
	
	public static LayerParameters classify() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;

		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-4; 
		params.netParams.etaBiases = 1E-4; 
		
		
		params.outputFunction = OutputFunctionType.SOFT_MAX_ACT; // 
		
		params.pooling = Pooling.ARG_MAX_ABS;
		
		params.netParams.objective = Static.Objective.CROSS_ENTROPY_CLASSIFICATION; //  SQUARED_ERROR_CLASSIFICATION ; // CONV_HEBBIAN_ALG;//  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; // 
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 1000;
		params.netParams.weightDecay = 0;//-1E-2;
		params.netParams.absMaxInitWeights = .001;  
		
	
		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // GRADIENT_ASCENT; //   RPROP; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		
		params.signallingFunction = DifferentiableFunction.linear;
		
		int localConvolutionFieldSize = 28; // 23
		int poolSize = 1;//6;
		
		params.nFeatures = 300;//10;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // TEST_XOR; //  TEST_HV_LINES; // TEST_XOR_ADD; // MNIST; //  GRADIENT_CURVE; //  
		
		params.netParams.staticData = false;

		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 1000; 
		params.netParams.batchSize = 24;  
		
		
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	/*  
	 *  L O W E R   L A Y E R   F O R 
	 *   
	 *    __             ___  ___ ___  ___ _ _   __        _____ _ _   __ 
	 *   /  ' |     /\  (__  (__   |  |     |   /  '   /\    |    |   /  \  |\  |
	 *  |     |    /__\    \    \  |  |--   |  |      /__\   |    |  |    | | \ |
	 *   \__/ |___ |  |  __/  __/ _|_ |    _|_  \__/  |  |   |   _|_  \__/  |  \|
	 */ 
	
	public static LayerParameters firstLayerForClassification() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-4; 
		params.netParams.etaBiases = 1E-4; 
		
		
		params.outputFunction = OutputFunctionType.LINEAR;
		
		params.pooling = Pooling.SOFT_ARG_MAX_ABS;
		
		params.netParams.objective = Static.Objective.CROSS_ENTROPY_CLASSIFICATION; //  SQUARED_ERROR_CLASSIFICATION ; // CONV_HEBBIAN_ALG;//  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; // 
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 0;//1E-6;
		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 1000;
		params.netParams.weightDecay = 0;//-1E-2;
		params.netParams.absMaxInitWeights = .01;  
		
		

		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // GRADIENT_ASCENT; //   RPROP; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		
		params.signallingFunction = DifferentiableFunction.tanh;
		
		params.setConvolutionFieldSize(7);
		params.setPoolingFieldAndStepSize(2);
		
		params.nFeatures = 16;//10;
		
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // TEST_XOR; //  TEST_HV_LINES; // TEST_XOR_ADD; // MNIST; //  GRADIENT_CURVE; //  
		
		params.netParams.staticData = false;
		
		params.netParams.nIterations = 40000; // for staticData, we can reIterate over the same samples
		params.netParams.nSamples = 1000; 
		params.netParams.batchSize = 24;  
		
		
		
		// automatic parameter shite: (squares everywhere!)
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	/*                _ _  ___ _____             _____  __     ___        ___
	 *    |\ /| |\  |  |  (__    |      /\  |  |   |   /  \   |    |\  | /   \
	 *    | V | | \ |  |     \   |     /__\ |  |   |   |  |   |--  | \ | |
	 *    |   | |  \| _|_  __/   |     |  | \_/|   |   \__/   |___ |  \| \___/
	 */  
	
	public static LayerParameters convAutoEncoder() {
		Main.initializeClassRandoms(); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;
		Debug.fakeThreading = false;
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-5; 
		params.netParams.etaBiases = 1E-5; 
		
		
		params.outputFunction = OutputFunctionType.LINEAR; // SOFT_MAX_ACT; // 
		
		params.pooling = Pooling.ARG_MAX_ABS; // SOFT_ARG_MAX_ABS;
		
		params.netParams.objective = Static.Objective.CONV_AUTO_ENCODER; // CROSS_ENTROPY_CLASSIFICATION; //  SQUARED_ERROR_CLASSIFICATION ; // CONV_HEBBIAN_ALG;//  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; // 
		params.netParams.caeNoise = .3;

		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 1000;
		params.netParams.weightDecay = 0;// -1E-4;
		params.netParams.absMaxInitWeights = .1;  
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType. MOMENTUM; //  RPROP; // GRADIENT_ASCENT; //  RPROP; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		
		params.signallingFunction = DifferentiableFunction.tanh;
		
		
		int localConvolutionFieldSize = 7; // 23
		int poolSize = 7;//6;
		
		params.nFeatures = 20;//10;

		netParams.standardizedInput = false;
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // TEST_XOR; //  TEST_HV_LINES; // TEST_XOR_ADD; // MNIST; //  GRADIENT_CURVE; //  
		
//		params.netParams.staticData = true; // false;
		params.netParams.staticData = false;
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.nSamples = 24;// new MNIST(true, -1, true).getDataSetSize(); 
		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize(); 
		params.netParams.batchSize = 24;  
		
		
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	/*                _ _  ___ _____             _____  __     ___        ___     _____  ___   ____ _____
	 *    |\ /| |\  |  |  (__    |      /\  |  |   |   /  \   |    |\  | /   \      |   |     (___    |
	 *    | V | | \ |  |     \   |     /__\ |  |   |   |  |   |--  | \ | |          |   |--       \   |
	 *    |   | |  \| _|_  __/   |     |  | \_/|   |   \__/   |___ |  \| \___/      |   |___   ___/   |
	 */  
	
	public static LayerParameters testConvAutoEncoder() {
		Main.initializeClassRandoms(643); //  
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;
		Debug.fakeThreading = false;
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		// debug shite:
		params.netParams.randomizeBiases = false; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-5; 
		params.netParams.etaBiases = 1E-5; 
		
		
		params.outputFunction = OutputFunctionType.LINEAR; // SOFT_MAX_ACT; // 
		
		params.pooling = Pooling.ARG_MAX_ABS; // MAX; //     SOFT_ARG_MAX_ABS; //
		
		params.netParams.objective = Static.Objective.CONV_AUTO_ENCODER; // CROSS_ENTROPY_CLASSIFICATION; //  SQUARED_ERROR_CLASSIFICATION ; // CONV_HEBBIAN_ALG;//  DECORRELATION; // SQUARED_TOTAL; // KDTREE_UNIF; //  DECORRELATION; //   ERROR_MINIMIZATION; // LINEAR_FUNCTION; //
		params.netParams.caeNoise = .3;
		
		
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm. NONE; //  SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		params.netParams.rescaleWeights = false;
		params.netParams.weightMax = 1000;
		params.netParams.weightDecay = 0; // -1E-1;
		params.netParams.absMaxInitWeights = .1;  
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType.MOMENTUM; // RPROP; //   GRADIENT_ASCENT; //   RPROP; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .01;
		
		params.netParams.alpha_momentum = .9;
		
		
		params.signallingFunction = DifferentiableFunction.tanh;
		
		
		int localConvolutionFieldSize = 8; // 23
		int poolSize = 8;//6;
		
		params.nFeatures = 20;//10;
		
		netParams.standardizedInput = false;
		params.nInputFeatures = 1;
		int inputSize = 28;
		params.netParams.dataGeneration = Static.DataGeneration.MNIST; // TEST_XOR; //  TEST_HV_LINES; // TEST_XOR_ADD; // MNIST; //  GRADIENT_CURVE; //  
		
		params.netParams.staticData = false;
//		params.netParams.staticData = true;
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.nSamples = 24;  
		params.netParams.nSamples = new MNIST(true, -1, false).getDataSetSize(); 
		params.netParams.batchSize = 24;  
		
		
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localConvolutionFieldSize; params.heightConvolutionField = localConvolutionFieldSize;
		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
		params.widthPoolingStep = poolSize; params.heightPoolingStep = poolSize;
		params.netParams.widthInput = inputSize; params.netParams.heightInput = inputSize;
		
		params.deriveOtherParams();
		return params;
	}
	/*    __             ___  ___ ___  ___         __        _____  __        _____
	 *   /  \ |     /\  (__  (__   |  |    \ /    /  \ |   |   |   |  \ |   |   |
	 *  |     |    /__\    \    \  |  |--   V     |  | |   |   |   |--' |   |   |
	 *   \__/ |___ |  |  __/  __/ _|_ |     |     \__/  \_/|   |   |     \_/|   |
	 */
	public static LayerParameters classifyFromOutput(File dir) {
		Main.initializeClassRandoms(); 
		
		NetworkParameters netParams = new NetworkParameters();
		LayerParameters params = new LayerParameters(netParams);
		ParameterConfigs.putStandardParams(params);
		
		
		Debug.dontDocument = false;
		Debug.checkDerivatives = false;

		
		// debug shite:
		params.netParams.randomizeBiases=false; 
		
		params.netParams.nItersTrainInputs = 0; 
		
		
		params.netParams.rescaleWeights = false;
		
		params.netParams.etaWeights = 1E-8; 
		
		params.signallingFunction = DifferentiableFunction.tanh; // DifferentiableFunction.logisticSigmoid; // 
		params.netParams.objective = Static.Objective.CROSS_ENTROPY_CLASSIFICATION;  // SQUARED_ERROR_CLASSIFICATION; //  . INT_ANGLE_REGRESSION; // 
		params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm.NONE; // SQUARED_WEIGHT_VAR_PER_SIGMOID_NEURON_INPUT_MAP; //  NONE; // 
		params.netParams.etaObjectiveCorrection = 1E-6;
		
		
		params.netParams.learningMechanismType = Static.LearningMechanismType. RPROP; // _AT_WEIGHTS; //  NORMAL_BACKPROP; //   NORMALIZING_AT_OBJECTIVE; // NORMAL_BACKPROP; //  MANHATTAN_AT_NEURON; //  RPROP_AT_OUTPUT_PER_SAMPLE; // 
		// MANHATTAN_AT_NEURON doesnt use the etas, since it doesnt make sense to say that sign change is crossing the optimum
		params.netParams.delta_rProp_init = .001;
		
		params.nInputFeatures = 5;
		int localFieldSize = 6;
		
		params.nFeatures = 24;
		
		params.netParams.absMaxInitWeights = .01;  
		params.netParams.dataGeneration = Static.DataGeneration.READ_FROM_MAT_FILE; // TEST_ALL_DIRS_CONTRAST; // MNIST; // GRADIENT_CURVE; //  TEST_CASES; //
		params.netParams.matFileForInputs = 
				new File(dir+ "\\impleNetwork24\\allOutputs\\images.mat");
//				new File( "imageOutput\\simpleNetwork5\\allOutputs\\images.mat");
//				new File( getBestNetwork(new File("imageOutput\\testCase_allDirs_contrast_kdtreeUnif")) +"\\allOutputs\\images.mat");
//				new File( getBestNetwork(new File("imageOutput\\testCase_allDirs_contrast_decorelation")) +"\\allOutputs\\images.mat");
		
		params.netParams.nIterations = 10000; // for staticData, we can reIterate over the same samples
//		params.netParams.nSamples = 1000; // <= nIterations * batchSize 
		params.netParams.convergedIfBelow = 1E-11;
		
		// automatic parameter shite: (squares everywhere!)
		params.widthConvolutionField = localFieldSize; params.heightConvolutionField = localFieldSize;
//		params.widthPoolingField = poolSize; params.heightPoolingField = poolSize;
//		params.widthInput = inputSize; params.heightInput = inputSize;
		
		
		Debug.documentWeightImages = false;
		
		params.deriveOtherParams();
//		if (Debug.removeThis) netParams.nSamples = netParams.batchSize = 10; // TODO: remove this
		return params;
	}
	/** 
	 * Compares the best networks of each config in a run 
	 * @param dir the directory of the outputs of a run
	 * @param accordingTo the objective function with which to decide which network is best
	 * @return
	 */
	public static CombisParameterConfigs compareClassificationOfBestNetworks(File dir, Objective accordingTo) {
		LayerParameters params = ParameterConfigs.classifyFromOutput(dir); //  .debug(); //.testCase();

		CombisParameterConfigs ret = new CombisParameterConfigs(params);
		CombisNetworkParameters netParamss = ret.netParams;
		netParamss.matFileForInputs = getBestNetworkOutputForEachConfig(dir, accordingTo);
		
		return ret;
	}

	/** 
	 * Compares the best networks of each config in a run according to their own objective
	 * @param dir the directory of the outputs of a run
	 * @return
	 */
	public static CombisParameterConfigs compareClassificationOfBestNetworks(File dir) {
		LayerParameters params = ParameterConfigs.classifyFromOutput(dir); //  .debug(); //.testCase();
		
		CombisParameterConfigs ret = new CombisParameterConfigs(params);
		CombisNetworkParameters netParamss = ret.netParams;
		netParamss.matFileForInputs = getBestNetworkOutputForEachConfig(dir);
		
		return ret;
	}
	public static CombisParameterConfigs compareClassificationOfAllNetworks(File dir) {
		LayerParameters params = ParameterConfigs.classifyFromOutput(dir); //  .debug(); //.testCase();
		
		CombisParameterConfigs ret = new CombisParameterConfigs(params);
		CombisNetworkParameters netParamss = ret.netParams;
		netParamss.matFileForInputs = getAllNetworkOutputForEachConfig(dir);
		
		return ret;
	}
	
	/*
	 * C L A S S I F I C A T I O N   H E L P E R   F U N C T I O N S
	 * 
	 * 
	 */
	static File[] getBestNetworkOutputForEachConfig(File dir) {
		File[] configs = dir.listFiles(new FileFilter(){
			@Override public boolean accept(File f) {
				return f.isDirectory() && f.getName().startsWith("config");
			}});
		
		File[] matFileForInputs = new File[configs.length];
		for (int c = 0; c<configs.length; c++) 
			matFileForInputs[c] = new File(getBestNetwork(configs[c])+"\\allOutputs\\images.mat");
		
		return matFileForInputs;
	}
	public static File[] getBestNetworkOutputForEachConfig(File dir, Objective accordingTo) {
		File[] configs = dir.listFiles(new FileFilter(){
			@Override public boolean accept(File f) {
				return f.getName().startsWith("config");
			}});
		
		File[] matFileForInputs = new File[configs.length];
		for (int c = 0; c<configs.length; c++) 
			matFileForInputs[c] = new File(getBestNetwork(configs[c], accordingTo)+"\\allOutputs\\images.mat");
		
		return matFileForInputs;
	}
	public static File[] getAllNetworkOutputForEachConfig(File dir) {
		ArrayList<File> matFileForInputs = new ArrayList<File>(dir.listFiles().length*10);
		for (File configDir : dir.listFiles(new FileFilter(){
			@Override public boolean accept(File f) {
				return f.getName().startsWith("config");
			}}))
		{
			for (File nnDir : configDir.listFiles(new FileFilter(){
				@Override public boolean accept(File f) {
					return f.getName().startsWith("simpleNetwork");
				}}))
				matFileForInputs.add(new File(nnDir+"\\allOutputs\\images.mat"));
			
		}
		return matFileForInputs.toArray(new File[0]);
	}
		
	
//	public static void main(String[] args) {
//        Debug.out(Arrays.toString(
//				getAllNetworkOutputForEachConfig(new File("imageOutput\\run3"))
//				).replaceAll(", ", "\r\n"));
//        Debug.out(getAllNetworkOutputForEachConfig(new File("imageOutput\\run3")).length);
//	}
//	
	
	
	
	/*
	 *  H E L P E R   F U N C T I O N S
	 * 
	 * 
	 */
//	public static void main(String[] args) {
//		checkBestTestCaseObjective();
//	}
//	public static void main(String[] args) {
//		convertOutput(16);
//	}
	public static void convertOutput(int simpleNetwork) {
		convertOutput(new File( "imageOutput\\simpleNetwork"+simpleNetwork+"\\allOutputs\\images.mat"), DifferentiableFunction.tanh);
	}
	public static void convertOutput(File matFile, MathFunction1D f) {
		InputSample[] outputs = null; // for the case below gives an error (this intermediate ref is neccesary cause testSamples is final)
		try {
			outputs = InputSample.readArray(new BufferedInputStream(new FileInputStream(matFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (InputSample output : outputs)
			output.map(f);
		String fileName = matFile.getPath();
		fileName = fileName.substring(0, fileName.length()-4); 
		try {
			FileUtils.writeByteArrayToFile(new File(fileName+"_"+f+"_mapped.mat"), InputSample.toByteArray(outputs));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static File getBestNetwork(File dir, Objective accordingTo) {
		double bestObjective = Double.NEGATIVE_INFINITY;
		File bestDir = null;
		for (File subDir : dir.listFiles())
			if (subDir.isDirectory()) 
				for (File file : subDir.listFiles(new FilenameFilter(){
					@Override public boolean accept(File arg0, String name) {
						return name.startsWith("objectiveValue=");
					}})) 
				{
					double obj = Double.NEGATIVE_INFINITY;
					try {
						List<String> lines = FileUtils.readLines(file);
						for (String line : lines)
							if (line.startsWith(accordingTo.getAbbrev()))
								obj = Double.parseDouble(line.split(";")[1]);
//						OLD:: obj = Double.parseDouble(FileUtils.readFileToString(file));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (obj > bestObjective) {
						bestObjective = obj;
						bestDir = subDir;
					}
				}
//        Debug.out("Best network has objective value: "+bestObjective);
		return bestDir;
	}
	/**
	 * Best in terms of the objective function they have been trained on
	 * @param dir
	 * @return
	 */
	public static File getBestNetwork(File dir) {
		double bestObjective = Double.NEGATIVE_INFINITY;
		File bestDir = null;
		for (File subDir : dir.listFiles())
			if (subDir.isDirectory()) 
				for (File file : subDir.listFiles(new FilenameFilter(){
					@Override public boolean accept(File arg0, String name) {
						return name.startsWith("objectiveValue=");
					}})) 
				{
					double obj = Double.NEGATIVE_INFINITY;
					try {
						obj = Double.parseDouble(file.getName().substring("objectiveValue=".length(), file.getName().length()-4));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} 
					if (obj > bestObjective) {
						bestObjective = obj;
						bestDir = subDir;
					}
				}
//        Debug.out("Best network has objective value: "+bestObjective);
		return bestDir;
	}
	public static double meanObjectiveValueOfNetworksIn(File dir) {
		double totalObjective = 0;
		int nNetworks = 0;
		for (File subDir : dir.listFiles())
			if (subDir.isDirectory()) 
				for (File file : subDir.listFiles(new FilenameFilter(){
					@Override public boolean accept(File arg0, String name) {
						return name.startsWith("objectiveValue=");
					}})) {
					double obj = Double.NEGATIVE_INFINITY;
					try {
						obj = Double.parseDouble(FileUtils.readFileToString(file));
						nNetworks++;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					totalObjective += obj;
				}
		return totalObjective/nNetworks;
	}
	static void putStandardParams(LayerParameters params) {
			
			params.netParams.objective = Static.Objective.DECORRELATION;
			params.netParams.objectiveCorrectionTerm = Static.ObjectiveCorrectionTerm.NONE;
			
			params.netParams.convergedIfBelow = 0; 
	
			params.netParams.etaMean = 1; 
	
			params.netParams.eta_rPropPlus = 1.2; 
			params.netParams.eta_rPropMin = .5; 
			params.netParams.delta_rProp_init = .1;

			params.netParams.weightMax = 1000;

			params.netParams.alpha_momentum = .5;

			params.netParams.weightDecay = 0;
			params.signallingFunction = DifferentiableFunction.tanh;
			params.pooling = Static.Pooling.SOFT_ARG_MAX_ABS; //SQUARE_SOFTMAX;  //  SIGMOID; // ABS_MAX; //   
											// SQUARE_WEIGHTED_SOFTMAX; ==> leads to too great weights, causing NaNs and Infinites!
			params.useBias = true;
			
			
			params.outputFunction = OutputFunctionType.LINEAR;
			
			params.netParams.offset = 1.;
			params.netParams.staticData = true;
	//		params.dataGeneration = Static.DataGeneration.TEST_CASES; // GRADIENT_CURVE; // 
			params.netParams.batchLearning = true; // TODO: fully implement the possibility to switch this to false..
	
			
			params.netParams.tanhDataVsBinary = true;
			
			params.netParams.weightConstraints = new WeightConstraint[0];
		}
}
