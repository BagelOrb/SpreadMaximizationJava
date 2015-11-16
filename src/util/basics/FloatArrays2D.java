package util.basics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import util.math.Math2;
import util.math.MathFunction1D;


public class FloatArrays2D { 

	
	static DecimalFormat df;
	static {
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
		dfSymbols.setDecimalSeparator('.');
		df = new DecimalFormat("0.0000", dfSymbols);
	}
	
	public static String format(float in) {
		return in+"";//Float.toString(in);
//		if (in < 0 ) return df.format(in);
//		else return "+"+df.format(in);
	}
	
	public static float[][] copyOf(float[][] or) {
		int h = height(or);
		int w = width(or);
		float[][] ret = new float[h][w];
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y][x] = or[y][x];
		return ret;
	}
	public static void copyTo(float[][] from, float[][] to) {
		for (int x = 0; x<width(from);x++)
			for (int y = 0; y<height(from); y++)
				to[y][x]=from[y][x];
	}
	
	public static int width(float[][] in) {
		int h = in.length;
		//TODO: case for when height = 0
		if (h == 0)
			try {throw new Exception("Cannot copy 2D array with zero width.");	} catch (Exception e) {	e.printStackTrace();}
		return in[0].length;		
	}
	public static int height(float[][] in) {
		return in.length;
	}
	public static float[] collapse(float[][] or) {
		int h = height(or);
		int w = width(or);
		float[] ret = new float[w*h];
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y*w+x] = or[y][x];
		return ret;
	}
	public static String toString(float[][] array) {
		String ret = "";
		for (float[] line : array) {
			for (float point : line)
				ret += format(point)+"; ";
			ret += "\r\n";
		}
		return ret;
	}
	public static float[][] map(Method m, float[][] or) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		float[][] ret = copyOf(or);
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
					ret[y][x] = (Float) m.invoke(Math2.class.newInstance(), (Float) or[y][x]);
		return ret;
	}
	public static float[][] map(MathFunction1D m, float[][] or) {
		float[][] ret = copyOf(or);
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y][x] = (float) m.apply(or[y][x]);
		return ret;
	}
	
	
	public static float fold(Method m, float[][] or) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		float ret = or[0][0];
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				if (!(x==0 && y==0))
						ret = (Float) m.invoke(Math2.class.newInstance(), ret, or[y][x]);
				return ret;
	}
	
	public static float[][] zipWith(Method m, float[][] one, float[][] two) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (!(width(one)==width(two) && height(one) == height(two)))
			try {
				throw new Exception("Dimensions don't coincide!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		float[][] ret = copyOf(one);
		int h = height(one);
		int w = width(one);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++) 
					ret[y][x] = (Float) m.invoke(m.getClass(), one[y][x], two[y][x]);
		return ret;		
	}
	
	public static void main2(String[] args) {
        //Debug.out(Arrays.toString(collapse(new float[][]{{1d,2d},{3d,4d},{5d,6d}})));
        //Debug.out(Arrays.deepToString(new Float[][]{{1d,2d},{3d,4d},{5d,6d}}));
        //Debug.out(floatArrayToString(new float[][]{{1d,2d},{3d,4d},{5d,6d}}));

//			try {
//                Debug.out(Arrays2D.toString(zipWith(
//						Statistics.class.getDeclaredMethod("distance", float.class, float.class),new float[][]{{1,0},{0,1}},new float[][]{{0,0},{1,3}} 
//						)));
//			} catch (Exception e) {e.printStackTrace();}


		
	}

	
//	public static float[][] parse(String input){
//		return parse(new Scanner(input).useDelimiter("[ \t]*;[ \t]")).snd;
//	}
//	public static Tuple<Scanner, float[][]> parse(Scanner scanner){
////		Scanner scanner = new Scanner(input).useDelimiter("\\p{Blank}*;\\p{Blank}*");
//		scanner.useLocale(Locale.US);
//		LinkedList<LinkedList<Float>> field = new LinkedList<LinkedList<Float>>();
//		scanner = scanner.skip("\\p{Blank}*"); // skip " " and "\t" but not "\n"
//
//		field.add(new LinkedList<Float>());
//		while (scanner.hasNext()) {
//			if (scanner.hasNextFloat()) { field.getLast().add(scanner.nextFloat()); scanner.skip(";*");}
//			else if (scanner.hasNextLine()) { scanner.nextLine(); field.add(new LinkedList<Float>()); }
////			else break;
//			scanner = scanner.skip("\\p{Blank}*"); // skip " " and "\t" but not "\n"
//		}
//		
//		// check dimensions:
//		Integer w = null;
//		for (LinkedList<Float> line : field)
//			if (w==null || w == line.size()) w = line.size();
//            else { w = Math.max(w, line.size()); Debug.out("Width dimension not consistent!!");}
//		
//		int x =0, y = 0;
//		float[][] ret = new float[field.size()][w];
//		for (LinkedList<Float> line : field) {
//			for (Float in : line) {
//				ret[y][x] = in;
//				x++;
//			}
//			x=0;
//			y++;
//		}
//		
//        Debug.out(toString(ret));
//		return new Tuple<Scanner, float[][]>(scanner, ret);
//	}
//	
//	public static float[][] parseOld(String in) {
//		String[] lines = in.split("\n\r");
//		String[][] data = new String[lines.length][lines[0].split("; ").length];
//		int yff = 0;
//		for (String line : lines) {
//			data[yff] = line.split("; ");
//			yff++; }
//		
//		int h = height(data);
//		int w = width(data)-1;
//		float[][] numbers = new float[h][w];
//		for (int x = 0; x<w; x++)
//			for (int y = 0; y<h; y++) 
//				numbers[y][x] = Float.parseFloat(data[y][x]);
//		return numbers;
//	}

	public static float[][] plus(float[][] i1,
			float[][] i2) {
		if (i1 == null) return null;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		float[][] ret = new float[height(i1)][width(i1)];
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] = i1[y][x]+i2[y][x];
		
		return ret;
	}

	public static void add(float[][] i1, float[][] i2) {
		if (i1 == null) return;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] += i2[y][x];
	}
	public static void substract(float[][] i1, float[][] i2) {
		if (i1 == null) return;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] -= i2[y][x];
	}
	
	public static float[][] times(float[][] i1, float i2) {
		if (i1 == null) return null;
		float[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] *= i2;
		return ret;
	}
	
	public static void multiply(float[][] i1, float i2) {
		if (i1 == null) return;
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] *= i2;
	}

	public static float[][] squared(float[][] i1) {
		if (i1 == null) return null;
		float[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] *= ret[y][x];
		return ret;
	}	
	public static float[][] subArray2D(float[][] in, int xFrom, int xTo, int yFrom, int yTo) {
		float[][] ret = new float[yTo-yFrom+1][xTo-xFrom+1];
		for (int x = 0; x< xTo-xFrom+1; x++)
			for (int y = 0; y< yTo-yFrom+1; y++)
				ret[y][x] = in[yFrom+y][xFrom+x];
		return ret;
	}
//	public static void main(String[] args) {
//		float[][] re = new float[][]{
//				{1,2,3},
//				{4,5,6},
//				{7,8,9}};
//        Debug.out(toString(subArray2D(re,0,1,0,2)));
//	}
	public static float[][] fill(float[][] in, float fill) {
//		float[][] ret = new float[height(in)][width(in)];
		for (int x = 0; x< width(in); x++)
			for (int y = 0; y<height(in); y++)
				in[y][x] = fill;
		return in;
	}
	public static float[][] blurred(float[][] i1, int range, float hardness) {
		int h = height(i1); int w = width(i1); 
		float[][] ret = new float[h][w];
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++){
				float totalContribution = 0;
				float newVal = 0;
				for (int yff = Math.max(0, y-range); yff<Math.min(h, y+range); yff++)
					for (int xff = Math.max(0, x-range); xff<Math.min(w, x+range); xff++){
						float invDist = (float) (1./ (1+Math.exp(hardness)*Math.sqrt(Math2.square(x-xff)+Math2.square(y-yff))));
						totalContribution += invDist;
						newVal += invDist* i1[yff][xff];
					}
				ret[y][x] = newVal / totalContribution;
			}
		return ret;
				
	}

//	public static void main(String[] args) {
//		Static.random = new Random(9);
//		float[][] img = GenerateData.generateRandom(5, 5);
//		InputSample imgg = new InputSample(blurred(img, 4, 2));
//		imgg.rescale(-1, 1);
//		InputSample nonB = new InputSample(img);
//		nonB.rescale(-1, 1);
//		try {
//            ImageIO.write(Images.getScaledImage(16, nonB.toBufferedImages()), "PNG", 
//                    new File("nonBlurredS.png"));
//            ImageIO.write(Images.getScaledImage(16, imgg.toBufferedImages()), "PNG", 
//                    new File("blurredS.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static float mean(float[][] re) {
		float ret = 0;
		int h = height(re); int w = width(re); 
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++)
				ret += re[y][x];
		return ret/(w*h);
	}

	public static void add(float[][] i1, float v) {
		int h = height(i1); int w = width(i1); 
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++)
				i1[y][x] += v;
	}

	public static float[][] plus(float[][] i1, float i2) {
		if (i1 == null) return null;
		float[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] += i2;
		return ret;
	}



}
