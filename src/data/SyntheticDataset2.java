package data;

import io.Images.InputSample;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import network.Static.DataGeneration;
import network.analysis.Debug;

public class SyntheticDataset2 extends DataSet {

	public int inputSize;
	private int patternId;
	
	public SyntheticDataset2(boolean training, int limit, boolean standardized, int inputSize, int pattern) {
		super(training, limit, standardized);
		this.inputSize = inputSize;
		this.patternId = pattern;
		contructDataset();
	}
	
	public static void main(String[] args) {
		outputImages();
	}
	
	public static void outputImages() {
		{
			SyntheticDataset2 q = new SyntheticDataset2(true, -1, false, 10, 3);
			for (InputSample img : q)
			{
				try {
//					img.
//					Images.get
					img.writePNGfilesTo(new File("C:/Users/TK/Documents/Java/Thesis/Images/SyntheticDataset2/AZtrain"), false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		{
			SyntheticDataset2 q = new SyntheticDataset2(false, -1, false, 10, 3);
			for (InputSample img : q)
			{
				try {
					img.writePNGfilesTo(new File("C:/Users/TK/Documents/Java/Thesis/Images/SyntheticDataset2/AZtest"), false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void contructDataset() {

		int fieldSize = inputSize-5+1;
		
		ArrayList<InputSample> allImages = new ArrayList<InputSample>(144);
		
		for (int x = 0; x<fieldSize; x++)
			for (int y = 0; y<fieldSize; y++)
				for (boolean circle : new boolean[]{true,false})
					for (boolean inverted : new boolean[]{true,false})

					{
						
						
						InputSample img = new InputSample(-1, inputSize, inputSize, 1, (circle)? 1 : 0);
						
						double[][] pattern;
						switch (patternId)
						{
						case 1:
							pattern = (circle)? circlePattern : crossPattern;
							break;
						case 2:
							pattern = (circle)? tPattern : kPattern;
							break;
						case 3:
							pattern = (circle)? aPattern : zPattern;
							break;
						default:
							pattern = null;
						}
						
						Debug.check(pattern == aPattern || pattern == zPattern);
						
						for (int xx = 0 ; xx<5; xx++)
							for (int yy = 0; yy<5; yy++)
								img.add(x+xx, y+yy, 0, pattern[yy][xx] * ((inverted)? 1 : -1));
						
						allImages.add(img);
					}
		
		Collections.shuffle(allImages, new Random(1));
		test = allImages.subList(0, 44);
		for (int i = 0; i < test.size(); i++)
			test.get(i).tag = i;
		train = allImages.subList(44, allImages.size());
		for (int i = 0; i < train.size(); i++)
			train.get(i).tag = i;
	}
	@SuppressWarnings("unused")
	private void contructDatasetOLD() { // all images in the right and lower edge are test samples
		int counterTest = 0;
		int counterTrain = 0;
		
		int fieldSize = inputSize-5+1;
		int trainSize = fieldSize*5/6;
		
		
		for (int x = 0; x<fieldSize; x++)
			for (int y = 0; y<fieldSize; y++)
				for (boolean circle : new boolean[]{true,false})
					for (boolean inverted : new boolean[]{true,false})
						
					{
						
						boolean training;
						if (x >= trainSize || y >= trainSize)
							training = false;
						else 
							training = true;
						
						
						InputSample img = new InputSample((training)? counterTrain : counterTest, inputSize, inputSize, 1, (circle)? 1 : 0);
						
						double[][] pattern;
						switch (patternId)
						{
						case 1:
							pattern = (circle)? circlePattern : crossPattern;
							break;
						case 2:
							pattern = (circle)? tPattern : kPattern;
							break;
						case 3:
							pattern = (circle)? aPattern : zPattern;
							break;
						default:
							pattern = null;
						}
						
						Debug.check(pattern == aPattern || pattern == zPattern);
						
						for (int xx = 0 ; xx<5; xx++)
							for (int yy = 0; yy<5; yy++)
								img.add(x+xx, y+yy, 0, pattern[yy][xx] * ((inverted)? 1 : -1));
						
						
						if (!training)
						{
							test.add(img);
							counterTest++;
						}
						else 
						{
							train.add(img);
							counterTrain++;
						}
					}
	}

	List<InputSample> train = new ArrayList<InputSample>();
	List<InputSample> test = new ArrayList<InputSample>();
	
	
	double[][] crossPattern = new double[][] {
			{ 1, 0, 0, 0, 1},  // #       #
			{ 0, 1, 1, 1, 0},  //   # # #
			{ 0, 1, 0, 1, 0},  //   #   #
			{ 0, 1, 1, 1, 0},  //   # # #
			{ 1, 0, 0, 0, 1}   // #       #
	};
	double[][] circlePattern  = new double[][] {
			{ 0, 1, 1, 1, 0},//   # # #
			{ 1, 0, 0, 0, 1},// #       #
			{ 1, 0, 0, 0, 1},// #       #
			{ 1, 0, 0, 0, 1},// #       #
			{ 0, 1, 1, 1, 0} //   # # # 
	};
	double[][] tPattern = new double[][] {
			{ 1, 1, 1, 1, 1},  // # # # # #
			{ 1, 0, 1, 0, 1},  // #   #   #
			{ 0, 0, 1, 0, 0},  //     # 
			{ 0, 0, 1, 0, 0},  //     # 
			{ 0, 0, 1, 0, 0}   //     # 
	};
	double[][] kPattern  = new double[][] {
			{ 1, 0, 0, 1, 0},//   #     #
			{ 1, 0, 1, 0, 0},//   #   #
			{ 1, 1, 0, 0, 0},//   # #
			{ 1, 0, 1, 0, 0},//   #   #
			{ 1, 0, 0, 1, 0},//   #     #
			{ 1, 0, 0, 0, 1} //   #       #
	};
	double[][] aPattern = new double[][] {
			{ 0, 0, 1, 0, 0},  //      # 
			{ 0, 1, 0, 1, 0},  //    #   #
			{ 0, 1, 1, 1, 0},  //    # # # 
			{ 1, 0, 0, 0, 1},  //  #       #
			{ 1, 0, 0, 0, 1}   //  #       #
	};
	double[][] zPattern  = new double[][] {
			{ 1, 1, 1, 1, 1},//   # # # # #
			{ 0, 0, 0, 1, 0},//         #
			{ 0, 0, 1, 0, 0},//       #
			{ 0, 1, 0, 0, 0},//     #
			{ 1, 1, 1, 1, 1},//   # # # # #
	};
	
	@Override
	public Iterator<InputSample> iterator() {
		if (training)
			return train.iterator();
		else 
			return test.iterator();
	}

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.SYNTHETIC2;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public int getWidth() {
		return inputSize;
	}

	@Override
	public int getHeight() {
		return inputSize;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public int getBatchSize() {
		Debug.err("getBatchSize shouldnt be called!");
		return 0;
	}

	@Override
	public int getDataSetSize() {
		return (training)? 100 : 44; // TODO check!
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		if (training)
			return train.get(sample);
		else
			return test.get(sample);
	}
	

}
