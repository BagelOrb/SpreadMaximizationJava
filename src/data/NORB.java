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

public class NORB extends DataSet 
//implements HasDefaults 
{

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.NORB;
	}
	
	public NORB(boolean training, int limit, boolean standardized) {
		super(training, limit, standardized);
	}

	final static int singlePrecisionMatrix  	= 0x1E3D4C51;
	final static int packedMatrix  				= 0x1E3D4C52;
	final static int doublePrecisionMatrix  	= 0x1E3D4C53;
	final static int integerMatrix 				= 0x1E3D4C54;
	final static int byteMatrix 				= 0x1E3D4C55;
	final static int shortMatrix 				= 0x1E3D4C56;
	
	private static File path = new File(Defaults.defaults.getString("NORB.path"));
	
//	@Override
//	public void setDefaults(JsonObjectBuilder o) {
//		o.add("NORB.path", "..\\Thesis\\Images\\NORB");
//	}
//
//	@Override
//	public void getDefaults(JsonObject o) {
//		path = new File(o.getString("NORB.path"));
//	}
//	/**
//	 * used in reflection only!
//	 */
//	public NORB() {};
	
	
	static final File batch1 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-01");
	static final File batch2 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-02");
	static final File batch3 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-03");
	static final File batch4 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-04");
	static final File batch5 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-05");
	static final File batch6 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-06");
	static final File batch7 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-07");
	static final File batch8 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-08");
	static final File batch9 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-09");
	static final File batch10 = new File(path+"\\norb-5x46789x9x18x6x2x108x108-training-10");
	static final File[] batchesTraining = new File[]{batch1, batch2, batch3, batch4, batch5, batch6, batch7, batch8, batch9, batch10};
	
	static final File batchTest1 = new File(path+"\\norb-5x01235x9x18x6x2x108x108-testing-01");
	static final File batchTest2 = new File(path+"\\norb-5x01235x9x18x6x2x108x108-testing-02");
	static final File[] batchesTest = new File[]{batchTest1, batchTest2};

	static final String[] labels = new String[]{"animal", "human", "airplane", "truck", "car", "none"};
	
	
	static int getInt(BufferedInputStream byteStream) throws IOException {
		byte[] fourBytes = new byte[4];
		byteStream.read(fourBytes);
		return ByteBuffer.wrap(fourBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	@Override
	public String getPath() { return path+""; }

	@Override
	public int getWidth() {
		return 108;
	}

	@Override
	public int getHeight() {
		return 108;
	}

	@Override
	public int getDepth() { return 2; }

	@Override
	public int getBatchSize() { return  29160; }

	@Override
	public int getDataSetSize() {
		if (training)
			return 10*getBatchSize(); 
		else
			return 2*getBatchSize();
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		int batch = sample / getBatchSize();
		int sampleInBatch = sample % getBatchSize();
		
		final File[] batches;
		if (training)
			batches = batchesTraining;
		else 
			batches = batchesTest;
		
		BufferedInputStream datStream = new BufferedInputStream(new FileInputStream(batches[batch]+"-dat.mat"));
		BufferedInputStream catStream = new BufferedInputStream(new FileInputStream(batches[batch]+"-cat.mat"));
		
		Debug.check(
				datStream.skip(4*6 + sampleInBatch*getImgSize())
				== 4*6 + sampleInBatch*getImgSize());
		Debug.check(
				catStream.skip(4*(5 + sampleInBatch))
				== 4*(5 + sampleInBatch));		
		
		InputSample ret = readImageDataStereo(datStream, catStream);
		ret.tag = sample;
		
		datStream.close();
		catStream.close();
		return ret;
	}



	@Override
	public FileIterator<InputSample> iterator() {
		final File[] batches;
		if (training)
			batches = batchesTraining;
		else 
			batches = batchesTest;
		
		
		final BufferedInputStream datStreamF ;
		final BufferedInputStream catStreamF ;
		try {
			datStreamF =  new BufferedInputStream(new FileInputStream(batches[0]+"-dat.mat"));
			catStreamF =  new BufferedInputStream(new FileInputStream(batches[0]+"-cat.mat"));
			
//			for (int i = 0; i < 6 ; i++)
//				Debug.out(getInt(datStreamF));
			Debug.check(
					datStreamF.skip(4*6)
					== 4*6);
			Debug.check(
					catStreamF.skip(4*5)
					== 4*5);
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
				boolean hasNext = counter < getBatchSize() * batches.length && counter < getLimit();
//				Debug.breakPoint(!hasNext, "data finished!");
				return hasNext;
			}

			@Override public InputSample next() {
				InputSample ret = null;
				try {
					
					if (counterInBatch >= getBatchSize())
					{
						counterInBatch = counterInBatch % getBatchSize();
						batch++;
						datStream.close();
						datStream = new BufferedInputStream(new FileInputStream(batches[batch]+"-dat.mat"));
						catStream.close();
						catStream = new BufferedInputStream(new FileInputStream(batches[batch]+"-cat.mat"));
					}
					
					ret = readImageDataStereo(datStream, catStream);
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
		testSingleImgLookup();
		
//		new NORB(true, 10000, false).calculateAndSaveStandardizationStatistics();
		
	}
	
	@SuppressWarnings("unused")
	private static void testSingleImgLookup() {
		NORB norb = new NORB(true, 10, false);
		for (int i = 0; i< 5; i++)
		try {
			InputSample img = norb.readImage(i);
			img.writePNGfilesTo(new File(norb.getPath()+"\\rendered\\ffcrap"+img.tag+"_"+labels[img.cat]+"_2_"), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void testIterator() {
		NORB norb = new NORB(true, 10, false);
		for (InputSample img : norb)
		{
			System.out.println("cat= "+img.cat);
			try {
				img.writePNGfilesTo(new File("crap\\ffcrap"+img.tag+"_"+labels[img.cat]+"_"), false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public  InputSample readImageDataStereo(BufferedInputStream datStream, BufferedInputStream catStream) throws IOException {
//		int cat = catStream.read(); 
		int cat = getInt(catStream);
		InputSample img = new InputSample(-1, getWidth(), getHeight(), getDepth(), cat);
		
		byte[] data = new byte[getDepth()*getHeight()*getWidth()];
		int nRead = datStream.read(data);
		Debug.check(nRead == data.length);
		
		int counter = 0;
		for (int z = 0; z<getDepth(); z++)
			for (int y = 0; y<getHeight(); y++)
				for (int x = 0; x<getWidth(); x++)
				{
					img.set(x, y, z, readImageValue(data[counter], z)); 		// ((   127.5 = 255/2   ))
					counter++;
				}
		return img;
	}


}
