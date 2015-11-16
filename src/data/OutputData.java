package data;

import io.Images.InputSample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import network.Static.DataGeneration;
import network.analysis.Debug;

public class OutputData extends DataSet {

	@Override
	public DataGeneration getDatasetType() {
		return DataGeneration.READ_FROM_MAT_FILE;
	}
	
	private final File file;
	private int width;
	private int height;
	private int depth;
	private int size;

	public OutputData(boolean training, int limit, boolean standardized, File file) {
		super(training, limit, standardized, true);
		this.file = file;
		
		try {
			readFileHeader(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (limit > 0)
			this.limit = limit;
		else 
			this.limit = getDataSetSize();
		
	}

	private void readFileHeader(File file) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		
		
		int nInts = 4;
		
		byte[] ba = new byte[nInts * Integer.SIZE/8];
		int nBytesRead = in.read(ba);
		Debug.check(nBytesRead == nInts * Integer.SIZE/8, "Couldn't read the required number of bytes!");
		int[] dimensions = new int[4];
		ByteBuffer.wrap(ba).asIntBuffer().get(dimensions); // saves the ints in the dimensions array
		
		width = dimensions[0];
		height= dimensions[1];
		depth = dimensions[2];
		size  = dimensions[3];
		
		in.close();
	}


	@Override
	public String getPath() {
		return file.getParent();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public int getBatchSize() {
		return size;
	}

	@Override
	public int getDataSetSize() {
		return size;
	}


//	@Override 
//	public double readImageValue(InputStream byteStream, int f) throws IOException{
//		double val = readValue(byteStream) /127.5-1;
//		if (standardized)
//			return (val-means[f])*invStdDevs[f];
//		else
//			return val;
//	}
	
	
	@Override
	public InputSample readImage(int sample) throws IOException {
		BufferedInputStream byteStream = new BufferedInputStream(new FileInputStream(file));
		
//		byte[] header = new byte[4 * Integer.SIZE/8];
//		byteStream.read(header);
		
		byteStream.skip(4 * Integer.SIZE/8); // header 
		byteStream.skip(sample * ( Short.SIZE/8 // cat
				+ Double.SIZE/8 * getWidth() * getHeight() * getDepth() // data
				));
		
		
		// done inside fromByteArrayData(.) !!
//		byte[] ba = new byte[Short.SIZE/8]; // one short for the cat!
//		byteStream.read(ba);
//		ByteBuffer buffer = ByteBuffer.wrap(ba);
//		short cat = buffer.asShortBuffer().get(0);
		
		
		InputSample ret = new InputSample(sample, getWidth(), getHeight(), getDepth(), -1);
		
		ret.fromByteArrayData(byteStream);
		
		byteStream.close();
		return ret;
	}

	@Override
	public Iterator<InputSample> iterator() {
		final BufferedInputStream byteStream;
		try {
			byteStream = new BufferedInputStream(new FileInputStream(file));
			byteStream.skip(4 * Integer.SIZE/8); // header
			return new Iterator<InputSample>(){
				int counter = 0;
				@Override
				public boolean hasNext() {
					boolean hasNext = counter < getDataSetSize();
					if (!hasNext)
						try {
							byteStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					return hasNext;
				}

				@Override
				public InputSample next() {
					InputSample ret = new InputSample(counter, getWidth(), getHeight(), getDepth(), -1);
					try {
						ret.fromByteArrayData(byteStream);
						counter++;
						return ret;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override public void remove() {} // unsupported
				};
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return null;
	}

}
