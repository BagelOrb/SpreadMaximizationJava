package io;

import generics.Tuple;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;

import network.Static;
import network.Static.ColoringType;
import network.analysis.Debug;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import util.basics.DoubleArray3D;
import util.basics.DoubleArray3Dext;
import util.basics.DoubleArrays;
import util.basics.DoubleArrays2D;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.Statistics;
import data.DoubleMatrixIO;

public class Images {

	public static final File imageFolder = new File("imageOutput\\");
/*
 * Copied from AEChain4.java :
 */
	public static class InputSample extends DoubleArray3Dext<InputSample> implements DoubleMatrixIO { // implements IOable  {
//		public double[][][] data;
//		
//		public int width;
//		public int height;
		
		public InputSample() {} // used in parsing
		
		
		public int tag = -1;
		public int cat = -1;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if (tag ==-1) try { throw new Exception("Image untagged!");
							} catch (Exception e) { e.printStackTrace(); }
			return tag;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InputSample other = (InputSample) obj;
			if (tag ==-1 || other.tag == -1) 
				try { throw new Exception("Can't compare untagged Images!");
				} catch (Exception e) { e.printStackTrace(); }
			if (tag != other.tag)
				return false;
			return true;
		}
		public InputSample(int tag, File imgFile, int cat) {
			BufferedImage im = readImg(imgFile);
			WritableRaster r = im.getRaster();
			data = new double[1][r.getWidth()][r.getHeight()];
			width = r.getWidth();
			height = r.getHeight();
			depth = 1;
			this.tag = tag; this.cat = cat;
			for (int x=0; x<width; x++)
				for (int y = 0; y<height; y++)
					data[0][y][x] = r.getPixel(x, y, new int[3])[0] /255d *2d-1d; // takes RED only!

		}

//		public InputSample(int tag, int w, int h, int d) { this(w, h, d); this.tag = tag; }
		public InputSample(int tag, int w, int h, int d, int cat) {
			if (Debug.debug && (d<0||w<0||h<0))
                Debug.out("Negative array Index! @ Images 130");
			
			if (d<0) d = 0;
			if (w<0) w=0;
			if (h<0) h=0;
			width = w;
			height = h;
			depth = d;
			this.tag = tag; this.cat = cat;
			Debug.check(!(depth==0 || height==0 || width==0), ": Zero length dimension in 3D data of image!");
			data = new double[d][height][width];
		}

		public InputSample(int tag, double[][][] in, int cat) {
			this(in); this.tag = tag; this.cat = cat;}
		public InputSample(double[][][] in) {
			data = DoubleArrays.deepCopy(in);
			depth = in.length;
			height = in[0].length;
			width = in[0][0].length;
			Debug.check(!(depth==0 || height==0 || width==0));
		}
		
		@Override
		public InputSample clone(){
			InputSample ret = new InputSample(DoubleArrays.deepCopy(data));
			ret.tag = this.tag; // TODO: dangerous?
			return ret ;
		}

		public BufferedImage[] toBufferedImages() {
			BufferedImage[] ret = new BufferedImage[data.length];
			for (int iF = 0; iF<data.length; iF++) {
				ret[iF] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				for (int x = 0; x<width; x++)
					for (int y = 0; y<height; y++) {
						float val = (float) data[iF][y][x]/2f+.5f;
						if (val>=0 && val <= 1) ret[iF].setRGB(x, y, Color.HSBtoRGB(2f/3f, 2f* (float) Math2.sigmoidD((val-.5f)*20d), val));
						else if (val > 1) ret[iF].setRGB(x, y, Color.HSBtoRGB(.333f, 1f-1f/val, 1f));
						else if (val < 0) ret[iF].setRGB(x, y, Color.HSBtoRGB(0f, 1f, 1f-1f/(1f-val)) );
					}
			}
			return ret;
		}
		
		
		// NOT ANYMORE : order is different to accomodate for 3D glasses which use only red and blue
		// order is RGB for CIFAR
		public BufferedImage toBufferedImageRBG() { 
			BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x<width; x++)
				for (int y = 0; y<height; y++) {
					float r=0,g=0,b=0;
					switch (depth) {
					case 3:
						b = (float) Math2.limit(0, data[2][y][x]/2.+.5, 1);
					case 2:
						g = (float) Math2.limit(0, data[1][y][x]/2.+.5, 1);
					case 1:
						r = (float) Math2.limit(0, data[0][y][x]/2.+.5, 1);
					}
					ret.setRGB(x, y, new Color(r, g, b).getRGB());
				}
			return ret;
		}
		
		public void applyWeightConversion(DifferentiableFunction weightConversion) {
			for (int iF = 0; iF<data.length; iF++) 
				for (int x = 0; x<width; x++)
					for (int y = 0; y<height; y++) 
						data[iF][y][x] = weightConversion.apply(data[iF][y][x]); 
		}
		public Tuple<Double,Double> getMinMax(int iF) {
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for (int x = 0; x<width; x++)
				for (int y = 0; y<height; y++) {
					max = Math.max(max, data[iF][y][x]);
					min = Math.min(min, data[iF][y][x]);
				} 
			return new Tuple<Double, Double>(min,max);
		}
		public void rescaleToFit() {
			for (int iF = 0; iF<data.length; iF++) {
				double min = Double.POSITIVE_INFINITY;
				double max = Double.NEGATIVE_INFINITY;
				for (int x = 0; x<width; x++)
					for (int y = 0; y<height; y++) {
						max = Math.max(max, data[iF][y][x]);
						min = Math.min(min, data[iF][y][x]);
					} 
				rescale(iF, min, max);
			}
		}
//		public void rescaleToFitRetainZeroEach() {
//			for (int iF = 0; iF<data.length; iF++) {
//				double min = Double.POSITIVE_INFINITY;
//				double max = Double.NEGATIVE_INFINITY;
//				for (int x = 0; x<width; x++)
//					for (int y = 0; y<height; y++) {
//						max = Math.max(max, data[iF][y][x]);
//						min = Math.min(min, data[iF][y][x]);
//					} 
//				double max2 = Math.max(-min, max);
//				rescale(iF, -max2, max2);
//			}
//		}
		public void rescaleToFitRetainZeroTotal() {
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for (int iF = 0; iF<data.length; iF++) {
				for (int x = 0; x<width; x++)
					for (int y = 0; y<height; y++) {
						max = Math.max(max, data[iF][y][x]);
						min = Math.min(min, data[iF][y][x]);
					} 
			}
			double max2 = Math.max(-min, max);
			for (int iF = 0; iF<data.length; iF++) 
				rescale(iF, -max2, max2);
		}
		public void rescaleVarianceBased() {
			for (int iF = 0; iF<data.length; iF++) {
				double var = Statistics.variance(DoubleArrays2D.collapse(data[iF]));
				double mean = Statistics.mean(DoubleArrays2D.collapse(data[iF]));
				rescale(iF, mean-2*var,mean+2*var);
			}
		}

		public void rescale(int iF, double min, double max) {
				for (int x = 0; x<width; x++)
					for (int y = 0; y<height; y++) 
						data[iF][y][x] = (data[iF][y][x]-min)/(max-min)*2-1; 
		}
		public void rescale(int i, int j) {
			for (int z = 0; z<depth; z++)
				rescale(z,i,j);
		}
//		public void setProps() {
//			double min = Double.POSITIVE_INFINITY;
//			double max = Double.NEGATIVE_INFINITY;
//			for (int x = 0; x<width; x++)
//				for (int y = 0; y<height; y++) {
//					max = Math.max(max, data[y][x]);
//					min = Math.min(min, data[y][x]);
//				} 
//			if (props==null) props = new Properties();
//			props.min = min;
//			props.max = max;
//		}

			
		public void outputWeightImage(Folder path, String nameAppendix, ColoringType coloringType, int featureNumber) {
			path.mkdirs();
			
//			for (int iF = 0; iF<depth; iF++) {
				switch (coloringType) {
					case TYPE_RESCALE_TO_FIT_GRAY:
						rescaleToFitRetainZeroTotal(); break;
					case TYPE_COLOR_BEYOND_LIMITS:
//						rescale(iF, -1,1); 
						break; // outputs colors when beyond limits
				}
//			}
			BufferedImage[] imgs = toBufferedImages();
			for (int iF = 0; iF<depth; iF++) {
				Tuple<Double, Double> minMax = getMinMax(iF);
				double min = minMax.fst;
				double max = minMax.snd;
				RenderedImage rendImg = Images.getScaledImage(16, imgs[iF]);		
				String props = " min="+Static.df.format(min)+", max="+Static.df.format(max);
				try {
                    ImageIO.write(rendImg, "PNG", UniqueFile.newUniqueFile(path, "feature"+featureNumber+"_inputMap"+iF+"_"+nameAppendix, props+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
		
		public void outputImage(Folder path, String nameAppendix, ColoringType coloringType) {
			for (int iF = 0; iF<depth; iF++) {
				switch (coloringType) {
				case TYPE_RESCALE_TO_FIT_GRAY:
					rescaleToFitRetainZeroTotal(); break;
				case TYPE_COLOR_BEYOND_LIMITS:
//					rescale(iF, -1,1); 
					break; // outputs colors when beyond limits
				}
			}
			BufferedImage[] imgs = toBufferedImages();
			for (int iF = 0; iF<depth; iF++) {
				RenderedImage rendImg = Images.getScaledImage(16, imgs[iF]);		
				String props = " cat="+cat;
				try {
					ImageIO.write(rendImg, "PNG", UniqueFile.newUniqueFile(path, "img="+tag+"_inputMap_"+iF+"_"+nameAppendix, props+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}


		
		// done already in DoubleArray3D !
//		@Override
//		public void write(JsonGenerator g) {
//			g.writeStartObject();
//			JSONdoubleArray.writeArray(g, data, "data");
//			g.writeEnd();
//		}
//
//		@Override
//		public IOable parse(JsonParser p) throws IOException, InstantiationException, IllegalAccessException { //, Class<T> c
//			double[][][] datas = null;
//			
//			outerLoop:
//			while (p.hasNext()) {
//				JsonParser.Event e = p.next();
//				switch(e) {
//				case START_OBJECT: break; 
//				case END_OBJECT: 
//					break outerLoop;
//				case KEY_NAME:
//					String key = p.getString();
//					if (key.equals("data")) datas = JSONdoubleArray.parse3D(p, -1, -1, -1);
//					break;
//		      	default:
//		      		throw new IOException("Invalid Images item: "+e.toString()+" at "+p.getLocation());
//			   }
//			}
//			if (datas==null) throw new IOException("No data read!");
//			InputSample ret = new InputSample(datas);
//
//			return ret;
//		}
//		
		@Override
		public String toString() {
			String ret = "Image "+tag+"\r\n    Category: "+cat +"\r\n \t data: \r\n";
			if (Debug.debug && data == null)
                Debug.out("InputSample not initialized properly!");
			ret += DoubleArrays.toString(this.data);
			return ret;
		}
		
		@Override
		public JsonObject toJsonObject() {
			return Json.createObjectBuilder()
				.add("data", JsonArrays.toJsonArray(data))
				.add("tag", tag)
				.build();
		}

		@Override
		public InputSample fromJsonObject(JsonObject o)
				throws IllegalArgumentException, SecurityException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			data = JsonArrays.fromJsonArray3D(o.getJsonArray("data"));
			depth = data.length;
			height = DoubleArrays2D.height(data[0]);
			width = DoubleArrays2D.width(data[0]);
			Debug.check(!(depth==0 || height==0 || width==0));
			tag = o.getInt("tag");
			return this;
		}
		public void add(DoubleArray3D in) {
			DoubleArrays.add(data, in.data);
		}
		
		public static InputSample example() {
			InputSample ret = new InputSample(new double[][][]{
					{	{1,2,3},
						{4,5,6}	},
					{	{7,8,9},
						{0,1,2}	}
					});
			ret.tag = 11;
			ret.cat = 12;
			return ret ;
		}
		public static InputSample example2() {
			InputSample ret = new InputSample(new double[][][]{
					{	{.5131/3,.5122/3,.7433/3},
						{.154/3,.0435/3,.876/3}	},
						{	{.6877/3,.6188/3,.06489/3},
							{.4+160/3,.6401/3,.0872/3}	}
			});
			ret.tag = 10000;
			ret.cat = -1;
			return ret ;
		}

		
		@Override
		public byte[] toByteArray() {
			
			final short nShorts = 4;
			final short nInts = 1;
			int matrixSize = width*height*depth;
			
			
			ByteBuffer buffer = ByteBuffer.allocate(nInts*Integer.SIZE/8 + nShorts * Short.SIZE/8 + matrixSize*Double.SIZE/8 );
			int offset = 0;
			
			buffer.asIntBuffer().put(tag);
			buffer.position(offset += nInts*Integer.SIZE/8);
			
			buffer.asShortBuffer().put(new short[]{(short) width, (short) height, (short) depth, (short) cat});
			buffer.position(offset += nShorts*Short.SIZE/8);

			buffer.position(offset);
			for (int d = 0; d<depth; d++)
				for (int h = 0; h<height; h++) {
					buffer.asDoubleBuffer().put(data[d][h]);
					buffer.position(offset+=Double.SIZE/8*width);
				}
					
			return buffer.array();
		}
		
		public byte[] toByteArrayData() {
			final short nShorts = 1;
			final short nInts = 0;
			int matrixSize = width*height*depth;
			
			
			ByteBuffer buffer = ByteBuffer.allocate(nInts*Integer.SIZE/8 + nShorts * Short.SIZE/8 + matrixSize*Double.SIZE/8 );
			int offset = 0;
			
			buffer.asShortBuffer().put(new short[]{(short) cat});
			buffer.position(offset += nShorts*Short.SIZE/8);

			buffer.position(offset);
			for (int d = 0; d<depth; d++)
				for (int h = 0; h<height; h++) {
					buffer.asDoubleBuffer().put(data[d][h]);
					buffer.position(offset+=Double.SIZE/8*width);
				}
					
			return buffer.array();
		}
		
		@Override
		public InputSample fromByteArray(byte[] ba)
				throws IllegalArgumentException, SecurityException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			
			try {
				BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(ba));
				InputSample ret = fromByteArray(in );
				in.close();
				return ret ;
			} catch (IOException e) {e.printStackTrace();
			Debug.err("Invalid input sample byte data!");
			return null;
			}
		}
		public InputSample fromByteArray(BufferedInputStream in) throws IOException
		{
			fromByteArraySize(in);
			fromByteArrayData(in);

			return this;
		}
		public InputSample fromByteArraySize(BufferedInputStream in) throws IOException
		{
			final short nShorts = 3;
			final short nInts = 1;
			int offset = 0;

			byte[] ba = new byte[nShorts*Short.SIZE/8 + nInts*Integer.SIZE/8];
			in.read(ba);
			ByteBuffer buffer = ByteBuffer.wrap(ba);
			
			tag = buffer.asIntBuffer().get();
			buffer.position(offset+=nInts*Integer.SIZE/8);
			
			width = buffer.asShortBuffer().get(0);
			height= buffer.asShortBuffer().get(1);
			depth = buffer.asShortBuffer().get(2);
//			cat = buffer.asShortBuffer().get(3);
			
			data = new double[depth][height][width];
			
			return this;
		}
		public InputSample fromByteArrayData(BufferedInputStream in) throws IOException
		{
			
			byte[] catBA = new byte[Short.SIZE/8];
			in.read(catBA);
			ByteBuffer catBuffer = ByteBuffer.wrap(catBA);
			cat = catBuffer.asShortBuffer().get(0);
			
			
			int matrixSize = width*height*depth*Double.SIZE/8;
//			ByteBuffer buffer = ByteBuffer.wrap(ba);
			byte[] ba = new byte[matrixSize];
			int read = in.read(ba);
			Debug.check(read == matrixSize);
			ByteBuffer buffer = ByteBuffer.wrap(ba);
			
			
			int offset = 0;
			buffer.position(offset);
			for (int d = 0; d<depth; d++)
				for (int h = 0; h<height; h++) {
					buffer.asDoubleBuffer().get(data[d][h]);
					buffer.position(offset+=Double.SIZE/8*width);
				}
			
			return this;
		}



		public static InputSample[] readArray(BufferedInputStream in) throws IOException {
			LinkedList<InputSample> ret = new LinkedList<InputSample>();
			while (in.available()>0)
				ret.add(new InputSample().fromByteArray(in));
			in.close();
			return ret.toArray(new InputSample[0]); // supply the type of array to return
		}
		public static byte[] toByteArray(InputSample[] ins) {
			byte[] ret = new byte[0];
			for (InputSample in : ins)
				ret  = ArrayUtils.addAll(ret, in.toByteArray());
			return ret;
				
		}
		
		@Deprecated public static void fixOutputtedImages() {
			for (File dir : new File("C:\\Users\\TK\\Documents\\Java\\Thesis\\imageOutput\\testCase_allDirs_contrast_decorelation").listFiles())
				if (dir.getName().startsWith("simpleNetwork")) {
					try {
						File matFile = new File(dir+"\\allOutputs\\images.mat");
						File oldFile = new File(dir+"\\allOutputs\\imagesOLD.mat");
						
						InputSample[] re = InputSample.readArray(new BufferedInputStream(new FileInputStream(oldFile)));
						matFile.renameTo(oldFile);
						for (int i = 0; i<re.length; i++)
							re[i].cat = i;
						FileUtils.writeByteArrayToFile(new File(dir+"\\allOutputs\\images.mat"), InputSample.toByteArray(re));
					} catch (Exception e) { e.printStackTrace();
					}
					
				}
					
				
		}
		public void writePNGfilesTo(File file, boolean rescale) throws IOException {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			BufferedImage[] imgs = toBufferedImages();
			for (int iF = 0; iF<imgs.length; iF++) {
				RenderedImage scaled;
				if (rescale)
					scaled = Images.getScaledImage(16, imgs[iF]);
				else
					scaled = imgs[iF];
				
				ImageIO.write(scaled, "PNG", new File(file+""+tag+"_cat"+cat+" _inFeat "+iF+".png"));
			}

		}


	}
//	public static void main(String[] args) {
//		checkSampleArrayMatrixIO();
//	}
	@SuppressWarnings("unused")
	private static void checkSampleArrayMatrixIO() {
//		InputSample[] samples = new InputSample[2];
//		samples[0] = InputSample.example();
//		samples[1] = InputSample.example2();
		
		byte[] ba;
//		ba = InputSample.toByteArray(samples);
		try {
			ba = FileUtils.readFileToByteArray(new File("C:\\Users\\TK\\Documents\\Java\\Thesis\\imageOutput\\run3\\config0\\simpleNetwork0\\allOutputs\\images.mat"));
			InputSample[] read = InputSample.readArray(new BufferedInputStream(new ByteArrayInputStream(ba)));
            Debug.out(Arrays.toString(read));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void checkSingleSampleMatrixIO() {
		byte[] ba = InputSample.example2().toByteArray();
        Debug.out(Arrays.toString(ba));
		InputSample is;
		try {
			is = new InputSample().fromByteArray(ba);
            Debug.out(is);
		} catch (Exception e) { e.printStackTrace();
		}
        Debug.out("ByteArray length="+ba.length);
		int jsonLength = InputSample.example2().toJsonObject().toString().length();
        Debug.out("Json String length="+jsonLength +" , in bytes:"+jsonLength*Character.SIZE/8);
	}
	
	public static BufferedImage readImg(File f) {
		try {
			return ImageIO.read(f);
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	
	public static BufferedImage getScaledImage(int i, BufferedImage img) {
		BufferedImage ret = new BufferedImage(img.getWidth()*i, img.getHeight()*i, img.getType());
		WritableRaster r = ret.getRaster();
		for (int x = 0; x<r.getWidth(); x++)
			for (int y = 0; y<r.getHeight(); y++) 
				r.setPixel(x, y, img.getRaster().getPixel(x/i, y/i, new int[3]));
			
		return ret;
	}

	
	

}
