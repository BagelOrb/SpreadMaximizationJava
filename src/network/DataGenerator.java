package network;

import io.Images.InputSample;
import network.LayerParameters.NetworkParameters;

public interface DataGenerator {
	InputSample generateData(NetworkParameters params, int i);
	
	
}