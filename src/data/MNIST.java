package data;

import io.Images.InputSample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.Static.DataGeneration;
import network.analysis.Debug;
import network.main.Defaults;

public class MNIST extends DataSet 
//implements HasDefaults 
{

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.MNIST;
	}
	
	public MNIST(boolean training, int limit, boolean standardized) {
		super(training, limit, standardized);
	}
	
	
	static String path = Defaults.defaults.getString("MNIST.path"); 
	
//	@Override
//	public void setDefaults(JsonObjectBuilder o) {
//		o.add("MNIST.path",  "../Thesis/Images//MNIST//");
//	}
//
//	@Override
//	public void getDefaults(JsonObject o) {
//		path = o.getString("MNIST.path");
//	}
//	/**
//	 * used in reflection only!
//	 */
//	public MNIST() {};
	
	
	static final File batch1 = new File(path+"\\train");
	
	static final File batchTest1 = new File(path+"\\t10k");

	
	static int getInt(BufferedInputStream byteStream) throws IOException {
		byte[] fourBytes = new byte[4];
		byteStream.read(fourBytes);
		return ByteBuffer.wrap(fourBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	@Override
	public String getPath() { return path+""; }

	@Override
	public int getWidth() {
		return 28;
	}

	@Override
	public int getHeight() {
		return 28;
	}

	@Override
	public int getDepth() { return 1; }

	@Override
	public int getBatchSize() { return  getDataSetSize(); }

	@Override
	public int getDataSetSize() {
		if (training)
			return 60000; 
		else
			return 10000;
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		
		final File batch;
		if (training)
			batch = batch1;
		else 
			batch = batchTest1;
		
		BufferedInputStream datStream = new BufferedInputStream(new FileInputStream(batch+"-images.idx3-ubyte"));
		BufferedInputStream catStream = new BufferedInputStream(new FileInputStream(batch+"-labels.idx1-ubyte"));
		
		int sampleInBatch = sample;
		
		Debug.check(
				datStream.skip(4*4 + sampleInBatch*getImgSize())
				== 4*4 + sampleInBatch*getImgSize());
		Debug.check(
				catStream.skip(4*2 + sampleInBatch)
				== 4*2 + sampleInBatch);		
		
		InputSample ret = readImageData(datStream, catStream);
		ret.tag = sample;
		
		datStream.close();
		catStream.close();
		return ret;
	}



	@Override
	public FileIterator<InputSample> iterator() {
		final File batch;
		if (training)
			batch = batch1;
		else 
			batch = batchTest1;
		
		
		final BufferedInputStream datStreamF ;
		final BufferedInputStream catStreamF ;
		try {
			datStreamF = new BufferedInputStream(new FileInputStream(batch+"-images.idx3-ubyte"));
			catStreamF = new BufferedInputStream(new FileInputStream(batch+"-labels.idx1-ubyte"));
			
			Debug.check(
					datStreamF.skip(4*4 + 0)
					== 4*4 + 0);
			Debug.check(
					catStreamF.skip(4*2 + 0)
					== 4*2 + 0);	
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
		FileIterator<InputSample> ret = new FileIterator<InputSample>() {
			BufferedInputStream datStream = datStreamF;
			BufferedInputStream catStream = catStreamF;
			private int counter = 0;
			int batch = 0;
			private int counterInBatch = 0;
			
			public void close() {
				try {
					datStream.close();
					catStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override public boolean hasNext() {
				boolean hasNext = counter < getDataSetSize() && counter < getLimit();
//				Debug.breakPoint(!hasNext, "data finished!");
				return hasNext;
			}

			@Override public InputSample next() {
				InputSample ret = null;
				try {
					ret = readImageData(datStream, catStream);
					ret.tag = counter;
				
					counter++;
					counterInBatch++;
					
					if (!hasNext())
					{
						datStream.close();
						catStream.close();
					}
					
				} catch (IOException e) { e.printStackTrace(); }
				return ret;
			}
			
			@Override public void remove() { } // unused
		};
		return ret;
	}
	

	
	public static void main(String[] args) {
//		testIterator();
//		testImgLookup();
//		testSingleImgLookup(1);
		
		new MNIST(true, -1, false).calculateAndSaveStandardizationStatistics();
		
	}
	
	private static void testSingleImgLookup(int tag) {
		MNIST norb = new MNIST(true, tag, false);
		try {
			InputSample img = norb.readImage(tag);
			img.writePNGfilesTo(new File("crap\\ffcrap"+img.tag+"_cat="+img.cat+"_1_"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unused")
	private static void testImgLookup() {
		MNIST norb = new MNIST(true, 10, false);
		for (int i = 0; i< 5; i++)
		try {
			InputSample img = norb.readImage(i);
			img.writePNGfilesTo(new File("crap\\ffcrap"+img.tag+"_cat="+img.cat+"_2_"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void testIterator() {
		MNIST norb = new MNIST(true, 10, false);
		for (InputSample img : norb)
		{
			System.out.println("cat= "+img.cat);
			try {
				img.writePNGfilesTo(new File("crap\\ffcrap"+img.tag+"_cat="+img.cat+"_"), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public  InputSample readImageData(BufferedInputStream datStream, BufferedInputStream catStream) throws IOException {
		int cat = catStream.read(); 
//		int cat = getInt(catStream);
		InputSample img = new InputSample(-1, getWidth(), getHeight(), getDepth(), cat);
		byte[] data = new byte[getDepth()*getHeight()*getWidth()];
		int nRead = datStream.read(data);
		Debug.check(nRead == data.length);
		
		int counter = 0;
		for (int z = 0; z<getDepth(); z++)
			for (int y = 0; y<getHeight(); y++)
				for (int x = 0; x<getWidth(); x++)
				{
					double val = readImageValue(data[counter], z);
					
					img.set(x, y, z, val); 		// ((   127.5 = 255/2   ))
					counter++;
				}
		return img;
	}



}
