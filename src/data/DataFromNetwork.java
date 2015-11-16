package data;

import io.Images.InputSample;

import java.io.IOException;
import java.util.Iterator;

import network.Network;
import network.NetworkState;
import network.Static.DataGeneration;
import network.analysis.Debug;

public class DataFromNetwork extends DataSet {

	@Override
	public DataGeneration getDatasetType() {
		return null;
	}
	
	private Network network;
	private DataSet inDataSet;
	private NetworkState exampleState;

	public DataFromNetwork(Network net, boolean training, int limit) {
		super(training);
		
		
		this.network = net;
		if (!(net.dataGenerator instanceof DataSet))
			Debug.err("input data must be from data set!");
		else
			inDataSet = (DataSet) net.dataGenerator;
		
		exampleState = new NetworkState(network, new InputSample(-1, inDataSet.getWidth(), inDataSet.getHeight(), inDataSet.getDepth(), -1));
		
		if (limit > 0)
			this.limit = limit;
		else 
			this.limit = getDataSetSize();
		
	}
	
	
	@Override
	public Iterator<InputSample> iterator() {
		final Iterator<InputSample> inImgIterator = inDataSet.iterator();
		return new Iterator<InputSample>(){

			@Override
			public boolean hasNext() {
				return inImgIterator.hasNext();
			}

			@Override
			public InputSample next() {
				InputSample inImg = inImgIterator.next();
				NetworkState nwState = new NetworkState(network, inImg); 
				network.signal(nwState);
				return nwState.getLast().outputMaps;
			}

			@Override
			public void remove() {}
			};
	}

	@Override
	public String getPath() {
		return inDataSet.getPath();
	}

	@Override
	public int getWidth() {
		return exampleState.getLast().outputMaps.width;
	}

	@Override
	public int getHeight() {
		return exampleState.getLast().outputMaps.height;
	}

	@Override
	public int getDepth() {
		return exampleState.getLast().outputMaps.depth;
	}

	@Override
	public int getBatchSize() {
		return inDataSet.getBatchSize();
	}

	@Override
	public int getDataSetSize() {
		return inDataSet.getDataSetSize();
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		InputSample inImg = inDataSet.readImage(sample);
		NetworkState nwState = new NetworkState(network, inImg); 
		network.signal(nwState);
		return nwState.getLast().outputMaps;
	}

	
	
}
