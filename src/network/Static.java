package network;

import io.JsonAbleInterface;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

import javax.json.Json;
import javax.json.JsonObject;

import outputFunction.LinearOutputFunction;
import outputFunction.OutputFunction;
import outputFunction.OutputFunctionState;
import outputFunction.SoftMaxAct;
import pooling.ArgMaxPooling;
import pooling.ArgMaxPooling.ArgMaxState;
import pooling.PoolingFunction;
import pooling.PoolingState;
import pooling.SoftArgMaxPooling;
import pooling.SoftArgMaxPooling.SoftArgMaxState;


public class Static {

	public static class WCwrapper implements JsonAbleInterface<WCwrapper> {
		public final WeightConstraint wc;
		public WCwrapper(WeightConstraint wc2) { wc=wc2; }
		
		@Override
		public JsonObject toJsonObject() {
			return Json.createObjectBuilder()
					.add("val",wc.name())
					.build();
		}
		
		@Override
		public WCwrapper fromJsonObject(JsonObject o)
				throws IllegalArgumentException, SecurityException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			return new WCwrapper(WeightConstraint.valueOf(o.getString("val")));
		}
		
		public static WeightConstraint[] getConstraints(WCwrapper[] wrappers) {
			WeightConstraint[] ret = new WeightConstraint[wrappers.length];
			for (int i = 0; i<wrappers.length; i++)
				ret[i] = wrappers[i].wc;
			return ret;
		}
		public static WCwrapper[] getWrappers(WeightConstraint[] constraints) {
			WCwrapper[] ret = new WCwrapper[constraints.length];
			for (int i = 0; i<constraints.length; i++)
				ret[i] = new WCwrapper(constraints[i]);
			return ret;
		}
	}
	public enum WeightConstraint {
		NONE,
		MAX_SUM_OF_SQUARE_INPUT_WEIGHTS_BY_SHIFTING_UPDATE,
		MAX_SUM_OF_ABS_INPUT_WEIGHTS_BY_SHIFTING_UPDATE,
		MAX_SUM_OF_SQUARE_INPUT_WEIGHTS_BY_STRETCHING_WEIGHTS,
		MAX_SUM_OF_ABS_INPUT_WEIGHTS_BY_STRETCHING_WEIGHTS;
	}

	public static enum Pooling {
		ABS_MAX (null,null), SIGMOID(null,null), SQUARE_SOFTMAX(null,null), SQUARE_WEIGHTED_SOFTMAX(null,null), 
		MAX(ArgMaxPooling.class, ArgMaxState.class), 
		ARG_MAX_SQUARE(ArgMaxPooling.class, ArgMaxState.class),
		SOFT_ARG_MAX_SQUARE(SoftArgMaxPooling.class, SoftArgMaxState.class),
		ARG_MAX_ABS(ArgMaxPooling.class, ArgMaxState.class),
		SOFT_ARG_MAX_ABS(SoftArgMaxPooling.class, SoftArgMaxState.class);
		
		public final Class<? extends PoolingFunction<?>> clazz;
		final Class<? extends PoolingState> stateClazz;
		
		<PoolState extends PoolingState> Pooling(Class<? extends PoolingFunction<PoolState>> clazz, Class<? extends PoolingState> stateClazz) {
			this.clazz= clazz;
			this.stateClazz = stateClazz;
		}
		
	}
	public static enum OutputFunctionType {
		LINEAR(LinearOutputFunction.class, LinearOutputFunction.LinearOutputFunctionState.class),
		SOFT_MAX_ACT(SoftMaxAct.class, SoftMaxAct.SoftMaxActState.class);
		
		public final Class<? extends OutputFunction<? extends OutputFunctionState>> clazz;
		public final Class<? extends OutputFunctionState> stateClazz;
		
		<OutputFunctionStateExt extends OutputFunctionState> OutputFunctionType(
				Class<? extends OutputFunction<OutputFunctionStateExt>> clazz, 
				Class<OutputFunctionStateExt> stateClazz) {
			this.clazz= clazz;
			this.stateClazz = stateClazz;
		}
	}

    public static enum LearningMechanismType { 
		GRADIENT_ASCENT, 
		MOMENTUM,
		MOMENTUM2,
		TK_LEARNING, 
		RPROP,
		
		LIMITED_GRADIENT_ASCENT,
		
		RPROP_AT_WEIGHTS, 
		MANHATTAN_AT_NEURON, 
		RPROP_AT_ACCUMULATED, 
		RPROP_AT_OUTPUT_PER_SAMPLE, 
		NORMALIZING_AT_OBJECTIVE
	}
	
	
	public static enum ContrastEnhancement {
		NO_CONTRAST_ENHANCEMENT, MEAN_SHIFT, VAR_STRETCH
	}
	public static enum ContrastEnhancementLocation {
		AT_ACTIVATION, AT_POOL
	}
	
	
	public static enum Objective {
		DECORRELATION					{public String getAbbrev() { return "DEC"; }}, 
		SQUARED_ERROR_CLASSIFICATION	{public String getAbbrev() { return "SQE"; }},
		CROSS_ENTROPY_CLASSIFICATION	{public String getAbbrev() { return "CEC"; }},
		LINEAR_FUNCTION					{public String getAbbrev() { return "LIN"; }}, 
		GAUSS_UNIFORMIZATION			{public String getAbbrev() { return "GAU"; }}, 
		CONV_HEBBIAN_ALG				{public String getAbbrev() { return "CHA"; }}, 
		SQUARED_TOTAL					{public String getAbbrev() { return "SQT"; }},
		CONV_AUTO_ENCODER				{public String getAbbrev() { return "CAU"; }};
		

		public abstract String getAbbrev();
		
	}
	public static enum ObjectiveCorrectionTerm {
		NONE // weight decay is separate
	}
	
	
	public static enum DataGeneration {
		CIFAR,
		CIFAR_TRANSFORMED,
		CIFAR_TRANSFORMED_STANDARDIZED,
		NORB, 
		MNIST, 
		STL,
		
		
		SYNTHETIC,
		SYNTHETIC2,

		GRADIENT_CURVE, 
		DEBUG_CASES, 
		
		TEST_XOR, 
		TEST_XOR_ADD,
		TEST_HV_LINES, 
		TEST_HV_CONTRAST, 
		TEST_ALL_DIRS_24_CONTRAST, 
		TEST_ALL_DIRS_24_CONTRAST_NOISE, 
		TEST_ALL_DIRS_360_CONTRAST_NOISE,
		
		READ_FROM_MAT_FILE;

		public boolean isTest() {
			switch(this) {
			case TEST_ALL_DIRS_24_CONTRAST:
			case TEST_ALL_DIRS_24_CONTRAST_NOISE:
			case TEST_ALL_DIRS_360_CONTRAST_NOISE:
			case TEST_HV_CONTRAST:
			case TEST_HV_LINES:
			case TEST_XOR:
			case TEST_XOR_ADD:
				return true;
			default:
				return false;
			}
		}
	}

public static enum ColoringType {
		TYPE_RESCALE_TO_FIT_GRAY, TYPE_COLOR_BEYOND_LIMITS
	}

	//	public static Random random; // initialized with seed in SimpleNetwork
	public static final DecimalFormat df = new DecimalFormat("0.0000");

}
