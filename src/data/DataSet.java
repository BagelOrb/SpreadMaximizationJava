package data;

import io.Images.InputSample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import network.DataGenerator;
import network.LayerParameters.NetworkParameters;
import network.Static;
import network.analysis.Debug;
import util.basics.DoubleArray3Dext.Loc;
import util.math.Math2;

public abstract class DataSet implements Iterable<InputSample>, DataGenerator {

	public abstract Static.DataGeneration getDatasetType();
	
	public static Random random;
	
	protected final boolean training;

	int limit;

	protected final boolean standardized;

	
	private double[] means;

	private double[] invStdDevs;
	
	/**
	 * used in reflection only!
	 */
	public DataSet() {
		training = true; standardized = false;
	};
	
	public DataSet(boolean training) {
		this.training = training; standardized = false;
	};
	
	/**
	 * @return the path
	 */
	public abstract String getPath();

	/**
	 * @return the imgwidth
	 */
	public abstract int getWidth();

	/**
	 * @return the imgheight
	 */
	public abstract int getHeight();

	/**
	 * @return the imgdepth
	 */
	public abstract int getDepth();

	public int getImgSize() {
		return getDepth()*getHeight()*getWidth();
	}
	/**
	 * @return the batchsize
	 */
	public abstract int getBatchSize();

	public abstract int getDataSetSize();
	
	
	double readImageValue(byte in, int f) throws IOException{
		double val = (in & 0xFF)  /127.5-1;
		if (standardized)
			return (val-means[f])*invStdDevs[f];
		else
		{
			Debug.breakPoint(Math.abs(val)>1);
			return val;
		}
	}
	
	/**
	 * Read 1 image 
	 * @param rgb TODO
	 */
	public abstract InputSample readImage(int sample) throws IOException;
	
	DataSet(boolean training, int limit, boolean standardized, boolean thisIsForOutputDataOnly) {
		this.training = training;
		this.limit = limit;
		this.standardized = standardized;

	}
	public DataSet(boolean training, int limit, boolean standardized) {
		this.training = training;
		if (limit > 0)
			this.limit = limit;
		else 
			this.limit = getDataSetSize();
		this.standardized = standardized;
		
		
		if (standardized)
			readMeansAndVars();
	}


	public LinkedList<FileIterator<?>> fileIterators = new LinkedList<FileIterator<?>>();
	
	public interface FileIterator<T> extends Iterator<T> {
		public void close();
	}
	
	
	
	
	
	public void calculateAndSaveStandardizationStatistics() {
		int n = 0;
	    double[] sum1 = new double[getDepth()];
	    for (InputSample img : this)
	    {
	        for (Loc l : img)
	        {
	        	n++;
	        	double val = img.get(l);
	        	Debug.breakPoint(Math.abs(val) > 1.);
	        	sum1[l.z] += val;
	        }
	    }
	    double[] means = new double[getDepth()];
	    for (int i = 0; i< means.length; i++)
	    	means[i] = sum1[i]/n;
	 
	    System.out.println("means = "+Arrays.toString(means));
	    
	    
	    double[] sum2 = new double[getDepth()];
	    double[] sum3 = new double[getDepth()];
	    for (InputSample img : this)
	    {
	    	for (Loc l : img)
	    	{
		        sum2[l.z] += Math2.square(img.get(l) - means[l.z]);
		        sum3[l.z] += (img.get(l) - means[l.z]);
	    	}
	    }
	    double[] vars = new double[getDepth()];
	    for (int i = 0; i< means.length; i++)
	    	vars[i] = (sum2[i] - Math2.square(sum3[i])/n)/(n - 1);
	    

	    System.out.println("vars = "+Arrays.toString(vars));
	    
	    JsonArrayBuilder meansJsonArray = Json.createArrayBuilder();
	    JsonArrayBuilder varsJsonArray = Json.createArrayBuilder();
	    for (int i = 0; i< means.length; i++)
	    {
	    	meansJsonArray.add(means[i]);
	    	varsJsonArray.add(vars[i]);
	    }
	    
	    JsonObject json = Json.createObjectBuilder()
	    	.add("means", meansJsonArray)
	    	.add("vars", varsJsonArray)
	    	.build();
	    
		try {
			JsonWriter writer = Json.createWriter(new FileOutputStream(new File(getPath()+"\\meanAndVar.txt")));
			writer.writeObject(json);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	private void readMeansAndVars() {
		try {
			JsonReader reader = Json.createReader(new FileInputStream(new File(getPath()+"\\meanAndVar.txt")));
			JsonObject o = reader.readObject();
			JsonArray jMeans = (JsonArray) o.get("means");
			JsonArray jVars = (JsonArray) o.get("vars");
			
			means = new double[jMeans.size()];
			invStdDevs = new double[jVars.size()];
			
			for (int i = 0 ; i<jMeans.size(); i++)
			{
				means[i] = jMeans.getJsonNumber(i).doubleValue();
				invStdDevs[i] = 1./Math.sqrt(jVars.getJsonNumber(i).doubleValue());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public InputSample generateData(NetworkParameters params, int i) {
		try {
			return readImage(random.nextInt(getDataSetSize()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLimit() {
		return limit;
	}

//	public void setLimitREMOVE(int limit) {
//		this.limit = limit;
//	}
	
}
