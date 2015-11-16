package util.basics;

import generics.StructuredIntegers;

import java.util.Arrays;
import java.util.Iterator;


public class IntegerArray2D extends StructuredIntegers<IntegerArray2D, IntegerArray2D.Loc>{

    public int[][] data;
	public int width;
	public int height;

	public static class Loc {
		int x, y;
		public Loc(int i, int j) { x = i; y=j; }
	}
	@Override
    public void set(Loc l, Integer d) { data[l.y][l.x] = d; }

    public void set(int x, int y, int val) {
		data[y][x] = val;
	}
	
	@Override
    public Integer get(Loc l) { return data[l.y][l.x]; }

    public int get(int xp, int yp) {
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
//        IntegerArray2D q = new IntegerArray2D(new int[][]{{1,2},{3,4}});
//		q.add(q);
//        Debug.out(q.toString());
//	}


    public IntegerArray2D() { };
	
    public IntegerArray2D(int[][] data) {
		this.data = data;
		height = data.length;
		if (height > 0)
			width = data[0].length;
	}
    public IntegerArray2D(int w, int h) {
        data = new int[h][w];
		width = w; height = h;
	}
	
    public IntegerArray2D(int[][] i, int width2, int height2) {
		data = i; width = width2; height = height2;
	}


	@Override
    public IntegerArray2D clone() {
		IntegerArray2D ret = new IntegerArray2D(width, height);
		for (Loc l : this)
			ret.set(l, this.get(l));
        return ret;
	}




	@Override
    public String toString() { return Arrays.deepToString(data); }

    public int[][] subArray(int x1, int x2, int y1, int y2) {
        int[][] ret = new int[y2-y1+1][x2-x1+1];
		for (int y = y1; y<=y2; y++)
			ret[y-y1] = Arrays.copyOfRange(data[y], x1, x2+1);
		return ret;
	}

//	@Override
//	public JsonObject toJsonObject() {
//		return Json.createObjectBuilder()
//			.add("data", JsonArrays.toJsonArray(data))
//			.build();
//	}
//
//	@Override
//    public IntegerArray2D fromJsonObject(JsonObject o)
//			throws IllegalArgumentException, SecurityException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException {
//		data = JsonArrays.fromJsonArray2D(o.getJsonArray("data"));
//		return this;
//	}

	



}
