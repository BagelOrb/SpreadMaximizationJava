package data;

import io.Images.InputSample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import network.Static.DataGeneration;
import network.analysis.Debug;

public class SyntheticDataset extends DataSet {

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.SYNTHETIC;
	}
	
	static List<InputSample> trainingSet = new ArrayList<InputSample>(24);
	static List<InputSample> testSet = new ArrayList<InputSample>(16);
	
	static int w = 6;
	
	static double ratioTrainingToTest = .75;
	
	static {
		constructSet();
	}
	
	public static void main(String[] args) {
		{
			SyntheticDataset q = new SyntheticDataset(true, -1, false);
			for (InputSample img : q)
			{
				try {
//					img.
//					Images.get
					img.writePNGfilesTo(new File("C:/Users/TK/Documents/Thesis/Thesis Java/Images/SyntheticDataset/train"), false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		{
			SyntheticDataset q = new SyntheticDataset(false, -1, false);
			for (InputSample img : q)
			{
				try {
					img.writePNGfilesTo(new File("C:/Users/TK/Documents/Thesis/Thesis Java/Images/SyntheticDataset/test"), false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public SyntheticDataset(boolean training, int limit, boolean standardized) {
		super(training, limit, standardized);
		
	}
	
	private static void constructSet() {
		List<InputSample> images = new ArrayList<InputSample>();
		int counterTest = 0;
		int counterTrain = 0;
		
		
		for (int x = 0; x<w; x++)
			for (int y = 0; y<w; y++)
				for (boolean horizontal : new boolean[]{true,false})
				{
					if (horizontal && x>= w-1)
						continue;
					if (horizontal && (y == 0|| y==w-1))
						continue;
					if (!horizontal && y>= w-1)
						continue;
					if (!horizontal && (x == 0|| x==w-1))
						continue;
					
					boolean train;
					if (x >= w-2 || y >= w-2)
						train = false;
					else 
						train = true;
					
					InputSample img;
					if (train)
						img = new InputSample(counterTrain, w, w, 1, (horizontal)? 1 : 0 );
					else 
						img = new InputSample(counterTest, w, w, 1, (horizontal)? 1 : 0 );
					
					img.add(-1);
					
					img.set(x, y, 0, 1);
					if (horizontal)
						img.set(x+1, y, 0, 1);
					else
						img.set(x, y+1, 0, 1);
					
//					images.add(img);
					if (!train)
					{
						testSet.add(img);
						counterTest++;
					}
					else 
					{
						trainingSet.add(img);
						counterTrain++;
					}
				}
		
		
//		trainingSet = images.subList(0, (int) (ratioTrainingToTest*((double)images.size())));
//		testSet = images.subList((int) (ratioTrainingToTest*((double)images.size())) + 1, images.size() - 1);
	}

	@Override
	public Iterator<InputSample> iterator() {
		if (training)
			return trainingSet.iterator();
		else 
			return testSet.iterator();
	}
	@Override
	public String getPath() {
		return null;
	}
	@Override
	public int getWidth() {
		return w;
	}
	@Override
	public int getHeight() {
		return w;
	}
	@Override
	public int getDepth() {
		return 1;
	}
	@Override
	public int getBatchSize() {
		Debug.err("getBatchSize shouldn't be called!");
		return 0;
	}
	@Override
	public int getDataSetSize() {
		return (training)? 24 : 16;
	}
	@Override
	public InputSample readImage(int sample) throws IOException {
		if (training)
			return trainingSet.get(sample);
		else
			return testSet.get(sample);
	}
	
	
	
}
