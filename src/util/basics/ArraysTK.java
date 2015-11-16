package util.basics;

import generics.Copyable;
import generics.Function1D;

import java.lang.reflect.Array;
import java.util.Iterator;

import network.analysis.Debug;

public class ArraysTK {
	private static class ArrayIterator<E> implements Iterator<E> {
		public ArrayIterator(E[] in) {
			cursor = 0;
			re=in;
		}
		E[] re;
		int cursor;
		
		@Override
		public boolean hasNext() {
			return cursor < re.length;
		}
	
		@Override
		public E next() {
			return re[cursor++];
		}
	
		@Override
		public void remove() {
		}
	}

	public static <E> Iterator<E> getIterator(E[] in) {
		return new ArrayIterator<E>(in);
	}

	public static <T extends Copyable<T>> T[] copyTo(T[] in, T[] out) {
		if (in.length != out.length)
			try { throw new Exception("Sizes don't match!");
			} catch (Exception e) {e.printStackTrace();
			}
		for (int i = 0; i<in.length; i++)
			if (i<out.length) out[i] = in[i].copy();
		return out;
	}

	public static <T extends Copyable<T>> Object[] deepCopy(T[] ts) {
		Object[] ret =  new Object[ts.length];
		for (int i = 0; i<ts.length; i++)
			ret[i] = ts[i].copy();
		return ret;
	}
	
	public static String toString(Object[] array) {
		if (Object[][].class.isInstance(array)) return Arrays2D.toString((Object[][]) array);
		String ret = "";
			for (Object point : array)
				ret += point+"; ";
			ret += "\r\n";
		return ret;
	}

	public static int depth (double[][][] in) { return in.length; }
	public static int height(double[][][] in) { 
		if (in.length==0) Debug.err("Multidimensional Array has flat dimension! ArrayIndexOutOfBoundsException: 0");
		return in[0].length; }
	public static int width (double[][][] in) { 
		if (in.length==0 || in[0].length==0) Debug.err("Multidimensional Array has flat dimension! ArrayIndexOutOfBoundsException: 0");
		return in[0][0].length; 
	}

	@SuppressWarnings("unchecked")
	public static <T, F>  T[] map(Function1D<F, T> f, F[] re) {
		T dummy = f.apply(re[0]);
		T[] ret = (T[]) Array.newInstance(dummy.getClass(), re.length); // (T[]) new Object[re.length];//
		for (int i =0; i<re.length; i++)
			ret[i] = f.apply(re[i]);
		return ret;
	}

	public static int getArrayLength(Object o) {
		if (o.getClass().getComponentType().isPrimitive()) {
		    return Array.getLength(o);
		}
		else {
			// TODO: check whether it is an array
            Debug.out(o.getClass());
		    return ((Object[]) o).length;
		}
	}
//	public static void main(String[] args) {
//		String[] q = new String[]{" asf","asf"};
//		Integer[] qs = map(new Function1D<String, Integer>(){
//
//			@Override
//			public Integer apply(String a1) {
//				return a1.length();
//			}
//			}, q);
//        Debug.out(Arrays.toString(qs));
//	}
}

