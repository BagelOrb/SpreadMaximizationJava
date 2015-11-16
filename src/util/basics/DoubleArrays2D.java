package util.basics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import network.analysis.Debug;
import util.math.Math2;
import util.math.MathFunction1D;


public class DoubleArrays2D { 

	
	static DecimalFormat df;
	static {
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
		dfSymbols.setDecimalSeparator('.');
		df = new DecimalFormat("0.0000", dfSymbols);
	}
	
	public static String format(double in) {
		return in+"";//Double.toString(in);
//		if (in < 0 ) return df.format(in);
//		else return "+"+df.format(in);
	}
	
	public static double[][] copyOf(double[][] or) {
		int h = height(or);
		int w = width(or);
		double[][] ret = new double[h][w];
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y][x] = or[y][x];
		return ret;
	}
	public static void copyTo(double[][] from, double[][] to) {
		for (int x = 0; x<width(from);x++)
			for (int y = 0; y<height(from); y++)
				to[y][x]=from[y][x];
	}
	
	public static int width(double[][] in) {
		int h = in.length;
		//TODO: case for when height = 0
		if (h == 0)
			Debug.err("Cannot copy 2D array with zero width.");	
		return in[0].length;		
	}
	public static int height(double[][] in) {
		return in.length;
	}
	public static double[] collapse(double[][] or) {
		int h = height(or);
		int w = width(or);
		double[] ret = new double[w*h];
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y*w+x] = or[y][x];
		return ret;
	}
	public static String toString(double[][] array) {
		String ret = "";
		for (double[] line : array) {
			for (double point : line)
				ret += format(point)+"; ";
			ret += "\r\n";
		}
		return ret;
	}
	public static double[][] mapped(Method m, double[][] or) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		double[][] ret = copyOf(or);
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
					ret[y][x] = (Double) m.invoke(Math2.class.newInstance(), (Double) or[y][x]);
		return ret;
	}
	public static double[][] mapped(MathFunction1D m, double[][] or) {
		double[][] ret = copyOf(or);
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y][x] = m.apply(or[y][x]);
		return ret;
	}
	public static void map(MathFunction1D m, double[][] or) {
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				or[y][x] = m.apply(or[y][x]);
	}
	
	
	public static double fold(Method m, double[][] or) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		double ret = or[0][0];
		int h = height(or);
		int w = width(or);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				if (!(x==0 && y==0))
						ret = (Double) m.invoke(Math2.class.newInstance(), ret, or[y][x]);
				return ret;
	}
	
	public static double[][] zipWith(Method m, double[][] one, double[][] two) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (!(width(one)==width(two) && height(one) == height(two)))
			try {
				throw new Exception("Dimensions don't coincide!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		double[][] ret = copyOf(one);
		int h = height(one);
		int w = width(one);
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++) 
					ret[y][x] = (Double) m.invoke(m.getClass(), one[y][x], two[y][x]);
		return ret;		
	}
	
	public static void main2(String[] args) {
        //Debug.out(Arrays.toString(collapse(new double[][]{{1d,2d},{3d,4d},{5d,6d}})));
        //Debug.out(Arrays.deepToString(new Double[][]{{1d,2d},{3d,4d},{5d,6d}}));
        //Debug.out(doubleArrayToString(new double[][]{{1d,2d},{3d,4d},{5d,6d}}));

//			try {
//                Debug.out(Arrays2D.toString(zipWith(
//						Statistics.class.getDeclaredMethod("distance", double.class, double.class),new double[][]{{1,0},{0,1}},new double[][]{{0,0},{1,3}} 
//						)));
//			} catch (Exception e) {e.printStackTrace();}


		
	}

	
//	public static double[][] parse(String input){
//		return parse(new Scanner(input).useDelimiter("[ \t]*;[ \t]")).snd;
//	}
//	public static Tuple<Scanner, double[][]> parse(Scanner scanner){
////		Scanner scanner = new Scanner(input).useDelimiter("\\p{Blank}*;\\p{Blank}*");
//		scanner.useLocale(Locale.US);
//		LinkedList<LinkedList<Double>> field = new LinkedList<LinkedList<Double>>();
//		scanner = scanner.skip("\\p{Blank}*"); // skip " " and "\t" but not "\n"
//
//		field.add(new LinkedList<Double>());
//		while (scanner.hasNext()) {
//			if (scanner.hasNextDouble()) { field.getLast().add(scanner.nextDouble()); scanner.skip(";*");}
//			else if (scanner.hasNextLine()) { scanner.nextLine(); field.add(new LinkedList<Double>()); }
////			else break;
//			scanner = scanner.skip("\\p{Blank}*"); // skip " " and "\t" but not "\n"
//		}
//		
//		// check dimensions:
//		Integer w = null;
//		for (LinkedList<Double> line : field)
//			if (w==null || w == line.size()) w = line.size();
//            else { w = Math.max(w, line.size()); Debug.out("Width dimension not consistent!!");}
//		
//		int x =0, y = 0;
//		double[][] ret = new double[field.size()][w];
//		for (LinkedList<Double> line : field) {
//			for (Double in : line) {
//				ret[y][x] = in;
//				x++;
//			}
//			x=0;
//			y++;
//		}
//		
//        Debug.out(toString(ret));
//		return new Tuple<Scanner, double[][]>(scanner, ret);
//	}
//	
//	public static double[][] parseOld(String in) {
//		String[] lines = in.split("\n\r");
//		String[][] data = new String[lines.length][lines[0].split("; ").length];
//		int yff = 0;
//		for (String line : lines) {
//			data[yff] = line.split("; ");
//			yff++; }
//		
//		int h = height(data);
//		int w = width(data)-1;
//		double[][] numbers = new double[h][w];
//		for (int x = 0; x<w; x++)
//			for (int y = 0; y<h; y++) 
//				numbers[y][x] = Double.parseDouble(data[y][x]);
//		return numbers;
//	}

	public static double[][] plus(double[][] i1,
			double[][] i2) {
		if (i1 == null) return null;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		double[][] ret = new double[height(i1)][width(i1)];
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] = i1[y][x]+i2[y][x];
		
		return ret;
	}

	public static void add(double[][] i1, double[][] i2) {
		if (i1 == null) return;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] += i2[y][x];
	}
	public static void substract(double[][] i1, double[][] i2) {
		if (i1 == null) return;
		if (width(i1) != width(i2) || height(i1) != height(i2))
			try {throw new Exception("2D Array dimensions don't coincide!");
			} catch (Exception e) {e.printStackTrace();	}
		
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] -= i2[y][x];
	}
	
	public static double[][] times(double[][] i1, double i2) {
		if (i1 == null) return null;
		double[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] *= i2;
		return ret;
	}
	
	public static void multiply(double[][] i1, double i2) {
		if (i1 == null) return;
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				i1[y][x] *= i2;
	}

	public static double[][] squared(double[][] i1) {
		if (i1 == null) return null;
		double[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] *= ret[y][x];
		return ret;
	}	
	public static double[][] subArray2D(double[][] in, int xFrom, int xTo, int yFrom, int yTo) {
		double[][] ret = new double[yTo-yFrom+1][xTo-xFrom+1];
		for (int x = 0; x< xTo-xFrom+1; x++)
			for (int y = 0; y< yTo-yFrom+1; y++)
				ret[y][x] = in[yFrom+y][xFrom+x];
		return ret;
	}
//	public static void main(String[] args) {
//		double[][] re = new double[][]{
//				{1,2,3},
//				{4,5,6},
//				{7,8,9}};
//        Debug.out(toString(subArray2D(re,0,1,0,2)));
//	}
	public static double[][] fill(double[][] in, double fill) {
//		double[][] ret = new double[height(in)][width(in)];
		for (int x = 0; x< width(in); x++)
			for (int y = 0; y<height(in); y++)
				in[y][x] = fill;
		return in;
	}
	public static double[][] blurred(double[][] i1, int range, double hardness) {
		int h = height(i1); int w = width(i1); 
		double[][] ret = new double[h][w];
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++){
				double totalContribution = 0;
				double newVal = 0;
				for (int yff = Math.max(0, y-range); yff<Math.min(h, y+range); yff++)
					for (int xff = Math.max(0, x-range); xff<Math.min(w, x+range); xff++){
						double invDist = 1./ (1+Math.exp(hardness)*Math.sqrt(Math2.square(x-xff)+Math2.square(y-yff)));
						totalContribution += invDist;
						newVal += invDist* i1[yff][xff];
					}
				ret[y][x] = newVal / totalContribution;
			}
		return ret;
				
	}

//	public static void main(String[] args) {
//		Static.random = new Random(9);
//		double[][] img = GenerateData.generateRandom(5, 5);
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
	
	public static double mean(double[][] re) {
		double ret = 0;
		int h = height(re); int w = width(re); 
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++)
				ret += re[y][x];
		return ret/(w*h);
	}

	public static void add(double[][] i1, double v) {
		int h = height(i1); int w = width(i1); 
		for (int y = 0; y<h; y++)
			for (int x = 0; x<w; x++)
				i1[y][x] += v;
	}

	public static double[][] plus(double[][] i1, double i2) {
		if (i1 == null) return null;
		double[][] ret = copyOf(i1);
		for (int y = 0; y<height(i1); y++)
			for (int x = 0; x<width(i1); x++)
				ret[y][x] += i2;
		return ret;
	}



}
