package util.basics;

import generics.FoldingFunctionDouble;
import generics.Function2D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import util.math.MathFunction1D;

public class DoubleArrays {

	
	public static void deepFill(double[][][][][] in, double v) {for (double[][][][] i : in) deepFill(i,v);	}
	public static void deepFill(double[][][][] in, double v) { 	for (double[][][] i : in) fill(i,v);	}
	public static void fill(double[][][] in, double v) { 	for (double[][] i : in) deepFill(i,v);	}
	public static void deepFill(double[][] in, double v) { 		for (double[] i : in) deepFill(i,v);	}
	public static void deepFill(double[] in, double v) { 		for (int i = 0; i<in.length; i++) in[i]= v;	}


	public static double[] collapse(double[][][] data) {
		try {
			double[] ret = new double[data.length*data[0].length*data[0][0].length];
			int d = data.length; int h = data[0].length; int w = data[0][0].length;
			for (int z = 0; z<d; z++)
				for (int y = 0; y<h; y++)
					for (int x = 0; x<w; x++)
						ret[z*h*w+y*w+x] = data[z][y][x];
			return ret;
		}
		catch (Exception e) { return new double[0]; }
	}


	public static double[][][] mapped(Method declaredMethod, double[][][] in) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		double[][][] ret = new double[in.length][in[0].length][in[0][0].length];
		for (int i = 0; i<in.length; i++)
			ret[i] = DoubleArrays2D.mapped(declaredMethod, in[i]);
		return ret;
	}
	public static double[] mapped(MathFunction1D m, double[] or) {
		double[] ret = Arrays.copyOf(or, or.length);
		int w = or.length;
		for (int x = 0; x<w; x++)
					ret[x] = m.apply(or[x]);
		return ret;
	}

	public static void add(double[][][] data, double[][][] in) {
		for (int i = 0; i<in.length; i++)
			DoubleArrays2D.add(data[i],in[i]);
	}
	public static void substract(double[][][] data, double[][][] in) {
		for (int i = 0; i<in.length; i++)
			DoubleArrays2D.substract(data[i],in[i]);
	}

	public static void map(MathFunction1D in, double[][][] data) {
		for (int i = 0; i<data.length; i++)
			DoubleArrays2D.map(in,data[i]);
	}

	public static void multiply(double[][][] data, double in) {
		for (int i = 0; i<data.length; i++)
			DoubleArrays2D.multiply(data[i],in);
	}

	public static double fold(FoldingFunctionDouble f, double begin, double[] data) {
		double ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = f.apply(data[i], ret);
		return ret;
	}
	public static double fold(FoldingFunctionDouble f, double begin, double[][] data) {
		double ret = begin;
		for (int i = 0; i<data.length; i++) 
			ret = fold(f,ret,data[i]);
		return ret;
	}
	public static double fold(FoldingFunctionDouble f, double begin, double[][][] data) {
		double ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = fold(f,ret,data[i]);
		return ret;
	}

	public static <T> T fold(Function2D<Double, T> f, T begin, double[] data) {
		T ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = f.apply(data[i], ret);
		return ret;
	}
	public static <T> T fold(Function2D<Double, T> f, T begin, double[][] data) {
		T ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = fold(f,ret,data[i]);
		return ret;
	}
	public static <T> T fold(Function2D<Double, T> f, T begin, double[][][] data) {
		T ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = fold(f,ret,data[i]);
		return ret;
	}

	
	public static int[][][] deepCopy(int[][][] i) {
		int d = i.length;
		int h = i[0].length;
		int w = i[0][0].length;
		int[][][] ret = new int[d][h][w];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
				for (int x = 0; x<w; x++)
					ret [z][y][x] = i[z][y][x];
		return ret;
	}
	public static int[][] deepCopy(int[][] i) {
		int d = i.length;
		int h = i[0].length;
		int[][] ret = new int[d][h];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
				ret [z][y] = i[z][y];
		return ret;
	}	
	public static double[][][] deepCopy(double[][][] i) {
		int d = ArraysTK.depth(i);
		int h = ArraysTK.height(i);
		int w = ArraysTK.width(i);
		double[][][] ret = new double[d][h][w];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
				for (int x = 0; x<w; x++) {
					ret [z][y][x] = i[z][y][x];
				}
		return ret;
	}
//	public static void main(String[] args) {
//		double[][][] q = new double[3][16][22];
//		DoubleArrays.fill(q, 1);
//		
//		final Random r = new Random();
//		DoubleArrays.map(new MathFunction1D(){
//
//			@Override public double apply(double arg) {
//				return r.nextDouble();
//			}}, q);
//		
//		double[][][] w = deepCopy(q);
//        Debug.out(DoubleArrays.toString(w));
//	}
//	
	public static double[][] deepCopy(double[][] i) {
		int d = i.length;
		int h = i[0].length;
		double[][] ret = new double[d][h];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
					ret [z][y] = i[z][y];
		return ret;
	}
	public static String toString(double[][][] data) {
		String ret = "[ ";
		for (int i = 0; i<data.length; i++)
			ret += ((i==0)? "" : ",\r\n") + DoubleArrays2D.toString(data[i]); //+ "\r\n";
		return ret+" ]";
	}
	public static String toString(double[][] data) {
		return DoubleArrays2D.toString(data);
	}


	
	public static float[] castToFloat(double[] in) {
		float[] ret = new float[in.length];
		for (int i = 0; i<in.length; i++)
			ret[i] = (float) in[i];
		return ret;
	}
	public static float[][] castToFloat(double[][] in) {
		float[][] ret = new float[in.length][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToFloat(in[i]);
		return ret;
	}
	public static float[][][] castToFloats(double[][][] in) {
		float[][][] ret = new float[in.length][][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToFloat(in[i]);
		return ret;
	}
	public static float[][][][] castToFloat(double[][][][] in) {
		float[][][][] ret = new float[in.length][][][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToFloats(in[i]);
		return ret;
	}

//	public static <T, F> T[] cast(Class<T> clazz, F[] from) {
//		if (from==null) return null;
//		if (Object[].class.isInstance(from))
//	}

}
