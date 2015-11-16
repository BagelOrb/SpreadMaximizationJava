package util.basics;

import generics.StructuredDoubles;
import io.JsonArrays;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;


public class DoubleArray1D extends StructuredDoubles<DoubleArray1D, DoubleArray1D.Loc>{

	public double[] data;
	public int width;

	public static class Loc {
		int x;
		public Loc(int i) { x = i; }
	}
	
	public void add(int z, double d) {
		data[z] += d;
	}

	@Override
	public void set(Loc l, Double d) { data[l.x] = d; }

	@Override
	public Double get(Loc l) { return data[l.x]; }

	@Override
	public Iterator<Loc> iterator() {
		return new Iterator<Loc>() {
			Loc last = new Loc(-1);
			
			@Override
			public boolean hasNext() {
				return last.x<width-1;
			}

			@Override
			public Loc next() {
				last.x++; 
				return last; 
			}

			@Override public void remove() { } // unused....
			
		};
	}
	
//	public static void main(String[] args) {
//		DoubleArray2D q = new DoubleArray2D(new double[][]{{1,2},{3,4}});
//		q.add(q);
//        Debug.out(q.toString());
//	}


	public DoubleArray1D() { };
	
	public DoubleArray1D(double[] data) {
		this.data = data;
		width = data.length;
	}
	public DoubleArray1D(int w) {
		data = new double[w];
		width = w; 
	}
	
	public DoubleArray1D(double[] i, int width2) {
		data = i; width = width2; 
	}


	@Override
	public DoubleArray1D clone() {
		return new DoubleArray1D(Arrays.copyOf(data,data.length),width);
	}




	@Override
	public String toString() { return Arrays.toString(data); }

	public double[] subArray(int x1, int x2) {
		return Arrays.copyOfRange(data, x1, x2+1);
	}

	@Override
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
			.add("data", JsonArrays.toJsonArray(data))
			.build();
	}

	@Override
	public DoubleArray1D fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		data = JsonArrays.fromJsonArray1D(o.getJsonArray("data"));
		return this;
	}

	public double get(int z) {
		return data[z];
	}


}
