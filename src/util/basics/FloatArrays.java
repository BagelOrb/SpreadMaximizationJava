package util.basics;

import generics.FoldingFunctionFloat;
import generics.Function2D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import util.math.MathFunction1D;

public class FloatArrays {

	
	public static void deepFill(float[][][][][] in, float v) {for (float[][][][] i : in) deepFill(i,v);	}
	public static void deepFill(float[][][][] in, float v) { 	for (float[][][] i : in) fill(i,v);	}
	public static void fill(float[][][] in, float v) { 	for (float[][] i : in) deepFill(i,v);	}
	public static void deepFill(float[][] in, float v) { 		for (float[] i : in) deepFill(i,v);	}
	public static void deepFill(float[] in, float v) { 		for (int i = 0; i<in.length; i++) in[i]= v;	}


	public static float[] collapse(float[][][] data) {
		try {
			float[] ret = new float[data.length*data[0].length*data[0][0].length];
			int d = data.length; int h = data[0].length; int w = data[0][0].length;
			for (int z = 0; z<d; z++)
				for (int y = 0; y<h; y++)
					for (int x = 0; x<w; x++)
						ret[z*h*w+y*w+x] = data[z][y][x];
			return ret;
		}
		catch (Exception e) { return new float[0]; }
	}


	public static float[][][] map(Method declaredMethod, float[][][] in) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		float[][][] ret = new float[in.length][in[0].length][in[0][0].length];
		for (int i = 0; i<in.length; i++)
			ret[i] = FloatArrays2D.map(declaredMethod, in[i]);
		return ret;
	}
	public static float[] map(MathFunction1D m, float[] or) {
		float[] ret = Arrays.copyOf(or, or.length);
		int w = or.length;
		for (int x = 0; x<w; x++)
					ret[x] = (float) m.apply(or[x]);
		return ret;
	}

	public static void add(float[][][] data, float[][][] in) {
		for (int i = 0; i<in.length; i++)
			FloatArrays2D.add(data[i],in[i]);
	}
	public static void substract(float[][][] data, float[][][] in) {
		for (int i = 0; i<in.length; i++)
			FloatArrays2D.substract(data[i],in[i]);
	}

	public static void map(MathFunction1D in, float[][][] data) {
		for (int i = 0; i<data.length; i++)
			FloatArrays2D.map(in,data[i]);
	}

	public static void multiply(float[][][] data, float in) {
		for (int i = 0; i<data.length; i++)
			FloatArrays2D.multiply(data[i],in);
	}

	public static float fold(FoldingFunctionFloat f, float begin, float[] data) {
		float ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = f.apply(data[i], ret);
		return ret;
	}
	public static float fold(FoldingFunctionFloat f, float begin, float[][] data) {
		float ret = begin;
		for (int i = 0; i<data.length; i++) 
			ret = fold(f,ret,data[i]);
		return ret;
	}
	public static float fold(FoldingFunctionFloat f, float begin, float[][][] data) {
		float ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = fold(f,ret,data[i]);
		return ret;
	}

	public static <T> T fold(Function2D<Float, T> f, T begin, float[] data) {
		T ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = f.apply(data[i], ret);
		return ret;
	}
	public static <T> T fold(Function2D<Float, T> f, T begin, float[][] data) {
		T ret = begin;
		for (int i = 0; i<data.length; i++)
			ret = fold(f,ret,data[i]);
		return ret;
	}
	public static <T> T fold(Function2D<Float, T> f, T begin, float[][][] data) {
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
	public static float[][][] deepCopy(float[][][] i) {
		int d = i.length;
		int h = i[0].length;
		int w = i[0][0].length;
		float[][][] ret = new float[d][h][w];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
				for (int x = 0; x<w; x++)
					ret [z][y][x] = i[z][y][x];
		return ret;
	}
	public static float[][] deepCopy(float[][] i) {
		int d = i.length;
		int h = i[0].length;
		float[][] ret = new float[d][h];
		for (int z = 0; z<d; z++)
			for (int y = 0; y<h; y++)
					ret [z][y] = i[z][y];
		return ret;
	}
	public static String toString(float[][][] data) {
		String ret = "[ ";
		for (int i = 0; i<data.length; i++)
			ret += ((i==0)? "" : ",\r\n") + FloatArrays2D.toString(data[i]); //+ "\r\n";
		return ret+" ]";
	}
	public static String toString(float[][] data) {
		return FloatArrays2D.toString(data);
	}


	
	public static double[] castToDouble(float[] in) {
		double[] ret = new double[in.length];
		for (int i = 0; i<in.length; i++)
			ret[i] = in[i];
		return ret;
	}
	public static double[][] castToDouble(float[][] in) {
		double[][] ret = new double[in.length][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToDouble(in[i]);
		return ret;
	}
	public static double[][][] castToDouble(float[][][] in) {
		double[][][] ret = new double[in.length][][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToDouble(in[i]);
		return ret;
	}
	public static double[][][][] castToDouble(float[][][][] in) {
		double[][][][] ret = new double[in.length][][][];
		for (int i = 0; i<in.length; i++)
			ret[i] = castToDouble(in[i]);
		return ret;
	}

//	public static <T, F> T[] cast(Class<T> clazz, F[] from) {
//		if (from==null) return null;
//		if (Object[].class.isInstance(from))
//	}

}
