package util.basics;

import generics.StructuredDoubles;
import io.JsonArrays;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;


public class DoubleArray2D extends StructuredDoubles<DoubleArray2D, DoubleArray2D.Loc>{

	public double[][] data;
	public int width;
	public int height;

	public static class Loc {
		int x, y;
		public Loc(int i, int j) { x = i; y=j; }
	}
	@Override
	public void set(Loc l, Double d) { data[l.y][l.x] = d; }

	public void set(int x, int y, double val) {
		data[y][x] = val;
	}
	
	@Override
	public Double get(Loc l) { return data[l.y][l.x]; }

	public double get(int xp, int yp) {
		return data[yp][xp];
	}
	
	@Override
	public Iterator<Loc> iterator() {
		return new Iterator<Loc>() {
			Loc last = new Loc(-1,0);
			
			@Override
			public boolean hasNext() {
				return !(last.x==width-1 && last.y==height-1);
			}

			@Override
			public Loc next() {
				last.x++; if (last.x==width) { last.x=0; last.y++; }
				return last; // TODO: returns the same Loc !?!?!?
			}

			@Override public void remove() { } // unused....
			
		};
	}
	
//	public static void main(String[] args) {
//		DoubleArray2D q = new DoubleArray2D(new double[][]{{1,2},{3,4}});
//		q.add(q);
//        Debug.out(q.toString());
//	}


	public DoubleArray2D() { };
	
	public DoubleArray2D(double[][] data) {
		this.data = data;
		width = DoubleArrays2D.width(data);
		height = DoubleArrays2D.height(data);
	}
	public DoubleArray2D(int w, int h) {
		data = new double[h][w];
		width = w; height = h;
	}
	
	public DoubleArray2D(double[][] i, int width2, int height2) {
		data = i; width = width2; height = height2;
	}


	@Override
	public DoubleArray2D clone() {
		return new DoubleArray2D(DoubleArrays2D.copyOf(data),width,height);
	}




	@Override
	public String toString() { return DoubleArrays.toString(data); }

	public double[][] subArray(int x1, int x2, int y1, int y2) {
		double[][] ret = new double[y2-y1+1][x2-x1+1];
		for (int y = y1; y<=y2; y++)
			ret[y-y1] = Arrays.copyOfRange(data[y], x1, x2+1);
		return ret;
	}

	@Override
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
			.add("data", JsonArrays.toJsonArray(data))
			.build();
	}

	@Override
	public DoubleArray2D fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		data = JsonArrays.fromJsonArray2D(o.getJsonArray("data"));
		return this;
	}

	



}
