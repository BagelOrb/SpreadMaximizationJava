package network;

import io.Images.InputSample;

import java.util.LinkedList;

import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;

@SuppressWarnings("serial")
public class NetworkState extends LinkedList<CnnDoubleLayerState>{
	final Network network;
	
//	public NetworkState(Network network) {
//		this.network = network;
//		CnnDoubleLayerState prevState = null;
//		for (CnnDoubleLayer layer : network.cnnDoubleLayers)
//		{
//			CnnDoubleLayerState newState = new CnnDoubleLayerState(layer);
//			this.add(newState);
//			if (prevState != null)
//				newState.inputMaps = prevState.outputMaps;
//			prevState = newState;
//		}
//	}
	
	public NetworkState(Network network, InputSample inputSample) {
		this.network = network;
		CnnDoubleLayerState prevState = null;
		for (CnnDoubleLayer layer : network.cnnDoubleLayers)
		{
			CnnDoubleLayerState newState;
			if (prevState == null)
				newState = new CnnDoubleLayerState(layer, inputSample);
			else 
			{
				newState = new CnnDoubleLayerState(layer, prevState);
			}
			this.add(newState);
			prevState = newState;
		}
	}
	
	public String toString() {
		return getLast().outputMaps.toString();
	}
	
}
