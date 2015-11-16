package data;

import io.Images;
import io.Images.InputSample;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import network.Static.DataGeneration;
import network.analysis.Debug;
import network.main.Defaults;

import org.apache.commons.io.FileUtils;

public class STL extends DataSet 
//implements HasDefaults 
{
	
	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.STL;
	}

	private final boolean useUnlabeled;
	
	public STL(boolean training, int limit, boolean standardized, boolean useUnlabeled) {
		super(training, limit, standardized);
		
		String[] labelsFF;
		try {
			labelsFF = FileUtils.readFileToString(new File(getPath()+"\\class_names.txt")).split("\r?\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			labelsFF = null;
			Debug.exit("Couldn't lead STL category labels!");
		}
		labels = labelsFF;
		
		this.useUnlabeled = useUnlabeled;
	}

	private static File path = new File(Defaults.defaults.getString("STL.path"));
	
//	@Override
//	public void setDefaults(JsonObjectBuilder o) {
//		o.add("STL.path", "..\\Thesis\\Images\\STL");
//	}
//
//	@Override
//	public void getDefaults(JsonObject o) {
//		path = new File(o.getString("STL.path"));
//	}
//	/**
//	 * used in reflection only!
//	 */
//	public STL() {};
	
	
	static final File test = new File(path+"\\test_X.bin");
	static final File testCat = new File(path+"\\test_y.bin");
	static final int nTest = 800;
	
	static final File train = new File(path+"\\train_X.bin");
	static final File trainCat = new File(path+"\\train_y.bin");
	static final int nTrain = 500;
	
	static final File unlabeled = new File(path+"\\unlabeled_X.bin");
	static final int nUnlabeled = 100000;
	

	final String[] labels;// = new String[]{"animal", "human", "airplane", "truck", "car", "none"};
	
	
	static int getInt(BufferedInputStream byteStream) throws IOException {
		return byteStream.read();
//		byte[] fourBytes = new byte[4];
//		byteStream.read(fourBytes);
//		return ByteBuffer.wrap(fourBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	@Override
	public String getPath() { return path+""; }

	@Override
	public int getWidth() { 	return 96; }
	@Override
	public int getHeight() { 	return 96; }
	@Override
	public int getDepth() { 	return 3; }
	@Override
	public int getBatchSize() { return  -1; }

	@Override
	public int getDataSetSize() {
		if (training)
		{
			if (useUnlabeled)
				return nTrain + nUnlabeled;
			else
				return nTrain;
		}
		else
			return nTest;
	}

	@Override
	public InputSample readImage(int sample) throws IOException {
		
		final File dat;
		final File cat;
		
		int sampleInBatch = sample;
		if (training)
		{
			if (sample < 500) 
			{
				dat = train;
				cat = trainCat;
			} else
			{
				dat = unlabeled;
				cat = null;
				sampleInBatch = sample - 500;
			}
		}
		else 
		{
			dat = test;
			cat = testCat;
		}
		
		BufferedInputStream datStream = new BufferedInputStream(new FileInputStream(dat));
		BufferedInputStream catStream = (cat==null)? null : new BufferedInputStream(new FileInputStream(cat));
		
		Debug.check(
				datStream.skip(sampleInBatch*getImgSize())
				== sampleInBatch*getImgSize());
		if (catStream != null)
			Debug.check(
					catStream.skip(sampleInBatch)
					==  sampleInBatch);		
		
		InputSample ret = readImageDataColor(datStream, catStream);
		ret.tag = sample;
		
		datStream.close();
		if (catStream != null)
			catStream.close();
		return ret;
	}



	@Override
	public FileIterator<InputSample> iterator() {
		final File dat;
		final File cat;
		if (training)
		{
			dat = train;
			cat = trainCat;
		}
		else 
		{
			dat = test;
			cat = testCat;
		}
		
		
		final BufferedInputStream datStreamF ;
		final BufferedInputStream catStreamF ;
		try {
			datStreamF =  new BufferedInputStream(new FileInputStream(dat));
			catStreamF = (cat==null)? null : new BufferedInputStream(new FileInputStream(cat));
			
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
					if (catStream != null)
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
					
					if (counterInBatch >= getBatchSize())
					{
						if (!(training && useUnlabeled))
							Debug.warn("Switching to unlabeled images while it shouldn't!!!");
						if (batch==1)
							Debug.warn("No more images left!!!!");
						counterInBatch -= 500;
						batch++;
						datStream.close();
						datStream = new BufferedInputStream(new FileInputStream(unlabeled));
						catStream.close();
						catStream = null;
					}
					
					ret = readImageDataColor(datStream, catStream);
					ret.tag = counter;
				
					counter++;
					counterInBatch++;
					
					if (!hasNext())
					{
						datStream.close();
						if (catStream != null)
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
		
//		new STL(true, 10000, false).calculateAndSaveStandardizationStatistics();
		
	}
	
	@SuppressWarnings("unused")
	private static void testSingleImgLookup() {
		STL stl = new STL(true, 10, false, true);
		int i = new Random().nextInt(stl.getDataSetSize());
		try {
			InputSample img = stl.readImage(i);
			File file = new File(stl.getPath()+"\\rendered\\STLimg_"+img.tag+"_"+((img.cat<0)? "UNLABELED" : stl.labels[img.cat]) +".png");
			file.mkdirs();
//			img.writePNGfilesTo(file, false);
			BufferedImage clrImg = img.toBufferedImageRBG();
			RenderedImage bigImg = Images.getScaledImage(16, clrImg);
			ImageIO.write(bigImg, "PNG", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void testIterator() {
		STL stl = new STL(true, 10, false, false);
		for (InputSample img : stl)
		{
			System.out.println("cat= "+img.cat);
			try {
				img.writePNGfilesTo(new File("crap\\ffcrap"+img.tag+"_"+stl.labels[img.cat]+"_"), false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public  InputSample readImageDataColor(BufferedInputStream datStream, BufferedInputStream catStream) throws IOException {
//		int cat = catStream.read(); 
		int cat = (catStream == null)? -1 :  getInt(catStream) - 1 ; // categories are labeled 1-10 instead of 0-9 !!
		InputSample img = new InputSample(-1, getWidth(), getHeight(), getDepth(), cat);
		
		byte[] data = new byte[getDepth()*getHeight()*getWidth()];
		int nRead = datStream.read(data);
		Debug.check(nRead == data.length);
		
		int counter = 0;
		for (int z = 0; z<getDepth(); z++)
			for (int x = 0; x<getWidth(); x++)
				for (int y = 0; y<getHeight(); y++)
				{
					img.set(x, y, z, readImageValue(data[counter], z)); 		// ((   127.5 = 255/2   ))
					counter++;
				}
		return img;
	}


}
