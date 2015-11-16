package util.basics;

import generics.StructuredIntegers;

import java.util.Arrays;
import java.util.Iterator;


public class IntegerArray1D extends StructuredIntegers<IntegerArray1D, IntegerArray1D.Loc>{

    public int[] data;
	public int width;

	public static class Loc {
		int x;
		public Loc(int i) { x = i; }
	}
	@Override
    public void set(Loc l, Integer d) { data[l.x] = d; }

	@Override
    public Integer get(Loc l) { return data[l.x]; }

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
//        IntegerArray2D q = new IntegerArray2D(new int[][]{{1,2},{3,4}});
//		q.add(q);
//        Debug.out(q.toString());
//	}


    public IntegerArray1D() { };
	
    public IntegerArray1D(int[] data) {
		this.data = data;
		width = data.length;
	}
    public IntegerArray1D(int w) {
        data = new int[w];
		width = w; 
	}
	
    public IntegerArray1D(int[] i, int width2) {
		data = i; width = width2; 
	}


	@Override
    public IntegerArray1D clone() {
        return new IntegerArray1D(Arrays.copyOf(data,data.length),width);
	}




	@Override
	public String toString() { return Arrays.toString(data); }

    public int[] subArray(int x1, int x2) {
		return Arrays.copyOfRange(data, x1, x2+1);
	}

//	@Override
//	public JsonObject toJsonObject() {
//		return Json.createObjectBuilder()
//			.add("data", JsonArrays.toJsonArray(data))
//			.build();
//	}
//
//	@Override
//    public IntegerArray1D fromJsonObject(JsonObject o)
//			throws IllegalArgumentException, SecurityException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException {
//		data = JsonArrays.fromJsonArray1D(o.getJsonArray("data"));
//		return this;
//	}

}
