package data;

import io.Images.InputSample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import network.Static.DataGeneration;

public class DatasetInRAM extends DataSet {

	private DataSet onFile;

	ArrayList<InputSample> data;
	
	public DatasetInRAM(DataSet onFile) {
		this.onFile = onFile;
		construct();
	}

	private void construct() {
		data = new ArrayList<InputSample>(onFile.getDataSetSize());
		for (InputSample img : onFile)
		{
			data.add(img);
		}
	}

	@Override
	public Iterator<InputSample> iterator() {
		return data.iterator();
	}

	@Override
	public DataGeneration getDatasetType() {
		return onFile.getDatasetType();
	}

	@Override
	public String getPath() {
		return onFile.getPath();
	}

	@Override
	public int getWidth() {
		return onFile.getWidth();
	}

	@Override
	public int getHeight() {
		return onFile.getHeight();
	}

	@Override
	public int getDepth() {
		return onFile.getDepth();
	}

	@Override
	public int getBatchSize() {
		return onFile.getBatchSize();
	}

	@Override
	public int getDataSetSize() {
		return onFile.getDataSetSize();
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		return data.get(sample);
	}
	
	@Override
	public int getLimit() {
		return onFile.getLimit();
	}

	
	
}
