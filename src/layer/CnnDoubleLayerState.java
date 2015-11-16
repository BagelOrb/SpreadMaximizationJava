package layer;

import io.Images.InputSample;
import network.LayerParameters;
import outputFunction.OutputFunctionState;
import pooling.PoolingState;
import util.basics.DoubleArray3D;

public class CnnDoubleLayerState {
	public LayerParameters params;
	public CnnDoubleLayer cnnDoubleLayer;
	
	public InputSample inputMaps;
	public DoubleArray3D inputMapsDerivatives;
	
	public DoubleArray3D convolutionActivationMaps;
	
	public DoubleArray3D convolutionMaps;
	public DoubleArray3D convolutionMapsDerivatives;

	public InputSample poolingMaps;
	public DoubleArray3D poolingMapsDerivatives;
	
	public InputSample outputMaps;
	public DoubleArray3D outputMapsDerivatives;

	public PoolingState poolingState;

	public OutputFunctionState outputFunctionState;


	static int lastId = 0;

	@Deprecated public final int id;

	private CnnDoubleLayerState(CnnDoubleLayer cnnDoubleLayer) {
		id = lastId++;
		
		this.cnnDoubleLayer = cnnDoubleLayer;
		this.params = cnnDoubleLayer.params; 
		int convWidth = params.widthConvolutionMap(params.widthLocalInput);
		int convHeight= params.heightConvolutionMap(params.heightLocalInput);
		int outWidth =  params.widthPoolingMap(params.widthLocalInput);
		int outHeight= params.heightPoolingMap(params.heightLocalInput);
		
		convolutionMaps = new DoubleArray3D(convWidth, convHeight, params.nFeatures);
		convolutionActivationMaps = new DoubleArray3D(convWidth, convHeight, params.nFeatures);
		poolingMaps = new InputSample(-1, outWidth, outHeight, params.nFeatures, -1);
		outputMaps = new InputSample(-1, outWidth, outHeight, params.nFeatures, -1);
		
		convolutionMapsDerivatives = new DoubleArray3D(convWidth, convHeight, params.nFeatures);
		poolingMapsDerivatives = new DoubleArray3D(outWidth, outHeight, params.nFeatures);
		outputMapsDerivatives = new DoubleArray3D(outWidth, outHeight, params.nFeatures);
		
		try {
			poolingState = params.pooling.clazz.newInstance().newPoolingState(this);
			outputFunctionState = params.outputFunction.clazz.newInstance().newOutputFunctionState(this);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CnnDoubleLayerState(CnnDoubleLayer cnnDoubleLayer2, InputSample imageSample) {
		this(cnnDoubleLayer2);
		inputMaps = imageSample;
		outputMaps.tag = imageSample.tag;
		outputMaps.cat = imageSample.cat;
	}
	public CnnDoubleLayerState(CnnDoubleLayer cnnDoubleLayer2, CnnDoubleLayerState prevState) {
		this(cnnDoubleLayer2);
		inputMaps = prevState.outputMaps;
		inputMapsDerivatives = prevState.outputMapsDerivatives;
		outputMaps.tag = inputMaps.tag;
		outputMaps.cat = inputMaps.cat;
	}

	/**
	 * unused?
	 * @return
	 */
	@Deprecated
	public CnnDoubleLayerState copy() {
		CnnDoubleLayerState ret = new CnnDoubleLayerState(this.cnnDoubleLayer);
		
		ret.inputMaps = this.inputMaps;

		ret.convolutionActivationMaps.set(convolutionActivationMaps);
		ret.convolutionMaps.set(convolutionMaps);
		ret.convolutionMapsDerivatives.set(convolutionMapsDerivatives);

		ret.poolingMaps.set(poolingMaps);
		ret.poolingMapsDerivatives.set(poolingMapsDerivatives);
		
		ret.poolingState = this.poolingState.copy();

		ret.outputMaps.set(outputMaps);
		ret.outputMapsDerivatives.set(outputMapsDerivatives);

		ret.outputFunctionState = this.outputFunctionState.copy();
		
		
		return ret;
	}


}