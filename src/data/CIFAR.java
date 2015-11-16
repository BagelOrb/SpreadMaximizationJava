package data;

import io.Images;
import io.Images.InputSample;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;

import network.Static.DataGeneration;
import network.analysis.Debug;
import network.main.Defaults;

import org.apache.commons.io.FileUtils;

import util.basics.IntegerArray3D;


public class CIFAR extends DataSet 
//implements HasDefaults
{

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.CIFAR;
	}
	
	public static String path = Defaults.defaults.getString("CIFAR.path");
	final int imgWidth = 32;
	final int imgHeight = 32;
	final int imgDepth = 3;
	final int batchSize = 10000;
	
//	@Override
//	public void setDefaults(JsonObjectBuilder o) {
//		o.add("CIFAR.path", "../Thesis/Images/cifar-10-binary//");
//	}
//
//
//
//	@Override
//	public void getDefaults(JsonObject o) {
//		path = o.getString("CIFAR.path");
//	}
//	public CIFAR() { super(); rgb = true; };
	
	final File batch1 = new File(path+"data_batch_1.bin");
	final File batch2 = new File(path+"data_batch_2.bin");
	final File batch3 = new File(path+"data_batch_3.bin");
	final File batch4 = new File(path+"data_batch_4.bin");
	final File batch5 = new File(path+"data_batch_5.bin");
	final File batchTest = new File(path+"test_batch.bin");

	final File[] batches = new File[]{batch1, batch2, batch3, batch4, batch5};
	static String[] labels;
	private final boolean rgb;
	
	public CIFAR(boolean training, int limit, boolean standardized, boolean rgb) {
		super(training, limit, standardized);

		this.rgb = rgb;
		try {
			labels = FileUtils.readFileToString(new File(getPath()+"batches.meta.txt")).split("\r?\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public String getPath() {
		return path;
	}

	public int getWidth() {
		return imgWidth;
	}

	@Override
	public int getHeight() {
		return imgHeight;
	}

	@Override
	public int getDepth() {
		return imgDepth;
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}
	@Override
	public int getDataSetSize() {
		if (training)
			return batchSize*5;
		else
			return batchSize;
	}




	
	
	@SuppressWarnings("unused")
	private void testTransformed() {
		try {
			
			int rndSample = new Random().nextInt(50000);
			
			
			{
				InputSample img = readImage(rndSample);
				BufferedImage clrImg = img.toBufferedImageRBG();
				RenderedImage bigImg = Images.getScaledImage(16, clrImg);
				
				File imgPath = new File(getPath()+"rendered//biological_transformation//img"+img.tag+"_"+labels[img.cat]+"_rgb.png");
				imgPath.mkdirs();
				ImageIO.write(bigImg, "PNG", imgPath);
			}
			InputSample img = readImage(rndSample);
			
			{
				BufferedImage clrImg = img.toBufferedImageRBG();
				RenderedImage bigImg = Images.getScaledImage(16, clrImg);
				
				File imgPath = new File(getPath()+"rendered//biological_transformation//img"+img.tag+"_"+labels[img.cat]+"_rgb.png");
				imgPath.mkdirs();
				ImageIO.write(bigImg, "PNG", imgPath);
			}
			
			
			BufferedImage[] bwImgs = img.toBufferedImages();
			for (int i = 0; i < bwImgs.length; i++)
			{
				RenderedImage bigBwImg = Images.getScaledImage(16, bwImgs[i]);
				
				File imgPath = new File(getPath()+"rendered//biological_transformation//img"+img.tag+"_"+labels[img.cat]+"_inputmap"+i+".png");
				ImageIO.write(bigBwImg, "PNG", imgPath);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	

	/**
	 * Read 1 CIFAR image 
	 * @param rgb 
	 */
	@Override
	public  InputSample readImage(int sample) throws IOException {
		File inImage;
		if (training) {
			if (sample < getBatchSize()*1)
				inImage = batch1;
			else if (sample < getBatchSize()*2)
				inImage = batch2;
			else if (sample < getBatchSize()*3)
				inImage = batch3;
			else if (sample < getBatchSize()*4)
				inImage = batch4;
			else if (sample < getBatchSize()*5)
				inImage = batch5;
			else 
			{
				inImage = batch1;
				Debug.warn("Out of input samples! reverting to batch 1!");
			}
		}
		else {
			inImage = batchTest;
		}
		
		InputSample ret = readImageData(inImage, sample);
		return ret;
	}
	public  InputSample readImageData(File in, int sample) throws IOException {
		InputStream byteStream = new FileInputStream(in);
		
		int sampleInBatch = sample % getBatchSize(); 

		
		byteStream.skip((1+getWidth()*getHeight()*getDepth())*sampleInBatch);
		
		InputSample img = readImageData(byteStream);
		img.tag = sample;
		
		byteStream.close();
		return img;
	}
	public  InputSample readImageData(InputStream byteStream) throws IOException {
		if (rgb)
			return readImageDataRGB(byteStream);
		else
			return readImageDataTransformed(byteStream);
			
	}
	public  InputSample readImageDataTransformed(InputStream byteStream) throws IOException {
		int cat = byteStream.read();
		IntegerArray3D dataF = new IntegerArray3D(getWidth(), getHeight(), getDepth());
		
		byte[] dataRaw = new byte[getDepth()*getHeight()*getWidth()];
		int nRead = byteStream.read(dataRaw);
		Debug.check(nRead == dataRaw.length);
		
		int counter = 0;
		for (int z = 0; z<3; z++)
			for (int y = 0; y<32; y++)
				for (int x = 0; x<32; x++)
				{
					dataF.set(x, y, z, dataRaw[counter]); 		// ((   128 = 256/2   ))
					counter++;
				}
		
		InputSample img = new InputSample(-1, getWidth(), getHeight(), getDepth(), cat);
		for (int y = 0; y<32; y++)
			for (int x = 0; x<32; x++)
			{
//				float[] rgbVals = Color.RGBtoHSB(data.get(x, y, 0), data.get(x, y, 1), data.get(x, y, 2), null);
//				img.set(x, y, 0, rgbVals[0]*2.-1.);
//				img.set(x, y, 1, rgbVals[1]*2.-1.);
//				img.set(x, y, 2, rgbVals[2]*2.-1.);
				double r = dataF.get(x, y, 0)/128. -1.;
				double g = dataF.get(x, y, 1)/128. -1.;
				double b = dataF.get(x, y, 2)/128. -1.;
				
				img.set(x, y, 0, (r+g+b)/3);
				img.set(x, y, 1, (r+g-2*b)/2);
				img.set(x, y, 2, (r-g));
			}
		return img;
	}
	public  InputSample readImageDataRGB(InputStream byteStream) throws IOException {
		int cat = byteStream.read();
		InputSample img = new InputSample(-1, getWidth(), getHeight(), getDepth(), cat);
		byte[] data = new byte[getDepth()*getHeight()*getWidth()];
		int nRead = byteStream.read(data);
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

	
	@Override
	public Iterator<InputSample> iterator() {
		return new Reading(getLimit()).iterator();
	}
	
	public class Reading implements Iterable<InputSample> {
		
		private BufferedInputStream imageByteStream;
		public int size;
		private int limit;
		
//		public ReadingFile(FileInputStream in)  {
//			byteStream = new BufferedInputStream(in);
//		}
		public Reading(int limit) {
			File inImage;
			if (training) {
				inImage = batches[0];
				size = 50000;
			}
			else {
				inImage = batchTest;
				size = 10000;
			}
			try {
				imageByteStream = new BufferedInputStream(new FileInputStream(inImage));
			} catch (FileNotFoundException e) { e.printStackTrace(); }
			if (limit <=0 )
				this.limit = size;
			else
				this.limit = limit;
		}
		
		@Override
		public Iterator<InputSample> iterator() {
			if (imageByteStream==null) return null; // means that the constructor already threw an error!

			
			Iterator<InputSample> it = new Iterator<InputSample>() {
				InputStream byteStream = imageByteStream;
				private int counter = 0;
				int batch = 0;
				private int counterInBatch = 0;
				
				@Override public boolean hasNext() {
					boolean hasNext = counter < size && counter < limit;
					Debug.breakPoint(!hasNext, "data finished!");
					return hasNext;
				}
				
				@Override public InputSample next() {
					InputSample ret = null;
					try {
						
						if (counterInBatch >= getBatchSize())
						{
							counterInBatch = counterInBatch % getBatchSize();
							batch++;
								byteStream.close();
								byteStream = new BufferedInputStream(new FileInputStream(batches[batch]));
						}
						ret = readImageData(byteStream);
					
						counter++;
						counterInBatch++;
						
						if (!hasNext())
							byteStream.close();
						
					} catch (IOException e) { e.printStackTrace(); }
					return ret;
				}
				
				@Override public void remove() { } // unused
			};
			return it;
		}
		
	}





	public static void main(String[] args) {
		CIFAR cifar = new CIFAR(true, 10000, false, true);
//		cifar.calculateAndSaveStandardizationStatistics();
		try {
			int tag = new Random().nextInt(cifar.getDataSetSize());
			InputSample sample = new CIFAR(true, 1000, true, true).readImage(tag);
			BufferedImage img = sample.toBufferedImageRBG();
			ImageIO.write(img, "PNG", new File(cifar.getPath()+"rendered\\"+labels[sample.cat]+tag+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}













}
