package util.basics;

import generics.Reflect;
import generics.StructuredIntegers;
import generics.Tuple;

import java.util.Arrays;
import java.util.Iterator;

import network.analysis.Debug;

import org.apache.commons.lang3.ArrayUtils;

public class IntegerArray3Dext<RE extends IntegerArray3Dext<RE>> extends StructuredIntegers<RE, IntegerArray3Dext.Loc>{

    public int[][][] data;
	
    public int get(int x, int y, int z) { return data[z][y][x]; }
    public void   set(int x, int y, int z, int d) { data[z][y][x] = d; }
    public void   add(int x, int y, int z, int d) { data[z][y][x] += d; }
	
	public int width;
	public int height;
	public int depth; 
	
	public static class Loc {
		public int x, y, z;
		public Loc(int i, int j, int k) { x = i; y=j; z=k; }
	}
	@Override
    public void set(Loc l, Integer d) { data[l.z][l.y][l.x] = d; }

	@Override
    public Integer get(Loc l) { 
		if (l.x>=width || l.y>=height || l.z >= depth )
			try {
				throw new OutOfBoundsException("x="+l.x+", y="+l.y+", z="+l.z+" while, w="+width+", h="+height+", d="+depth+"!");
			} catch (OutOfBoundsException e) {
				e.printStackTrace();
			}
		return data[l.z][l.y][l.x]; }

	@Override
	public Iterator<Loc> iterator() {
		return new Iterator<Loc>() {
			Loc last = new Loc(-1,0,0);
			@Override public boolean hasNext() {
				return !(last.x==width-1 && last.y==height-1 && last.z==depth-1);
			}
			@Override public Loc next() {
				last.x++; 
				if (last.x==width) { last.x=0; last.y++; }
				if (last.y==height) { last.y=0; last.z++; }
				return last; // TODO: returns the same Loc !?!?!?
			}
			@Override public void remove() { } // unused....
		};
	}
	
    public IntegerArray3Dext() { }; // used in parsing
	
    public IntegerArray3Dext(int[][][] data) {
		depth = data.length;
		if (depth > 0)
		{
			height = data[0].length;
			if (height > 0)
				width = data[0][0].length;
		}
		this.data = data;
		Debug.check(!(depth==0 || height==0 || width==0));
	}
    public IntegerArray3Dext(int w, int h, int d) {
        data = new int[d][h][w];
		width = w; height = h; depth = d;
		Debug.check(!(depth==0 || height==0 || width==0));
	}
	
    public IntegerArray3Dext(int[][][] i, int width2, int height2, int d) {
		data = i; width = width2; height = height2; depth = d;
		Debug.check(!(depth==0 || height==0 || width==0));
	}
    public IntegerArray3Dext(int[] dataConcat, int width2, int height2, int d) {
		width = width2; height = height2; depth = d;
        data = new int[d][height2][width2];
		Debug.check(!(depth==0 || height==0 || width==0));
		for (int z = 0; z<depth; z++)
		{
			int begin = z * height*width;
			for (int y = 0; y<height; y++)
			{
				data[z][y] = Arrays.copyOfRange(dataConcat, begin+y*width, begin+y*width+width);
			}
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public RE clone() {
//        return new IntegerArray3D(ArraysTK.deepCopy(data),width,height, depth);
		try {
			RE ret;
				ret = (RE) Reflect.newClassInstance(this.getClass());
			ret.width = width; ret.height = height; ret.depth = depth;
			for (Loc l : this)
				ret.set(l, this.get(l));
			return ret;
		} catch (Exception e) { e.printStackTrace(); }
		return null; // otherwise
	}

//	public void reset() { ArraysTK.fill(data, 0); 	}
	
	
//	@Override
//	public JsonObject toJsonObject() {
//		return Json.createObjectBuilder()
//			.add("data", JsonArrays.toJsonArray(data))
//			.build();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public RE fromJsonObject(JsonObject o)
//			throws IllegalArgumentException, SecurityException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException {
//		data = JsonArrays.fromJsonArray3D(o.getJsonArray("data"));
//		return (RE) this;
//	}

	@Override
    public String toString() { return Arrays.deepToString(data); }

    public int[][][] subArray(int x1, int x2, int y1, int y2, int z1, int z2) {
        int[][][] ret = new int[z2-z1+1][y2-y1+1][x2-x1+1];
		for (int z = z1; z<=z2; z++)
            ret[z-z1] = new IntegerArray2D(data[z]).subArray(x1,x2,y1,y2);
		return ret;
	}

    public static <RE extends IntegerArray3Dext<RE>> RE connectDepthwise(IntegerArray3Dext<RE> f, IntegerArray3Dext<RE>  s) throws OutOfBoundsException {
		RE ret = f.clone();
		int w,h,d;
		w = f.width; h = f.height;
		if (s.width != w || s.height != h)
			throw new OutOfBoundsException("Different sizes!");
		d = f.depth + s.depth;
		ret.depth = d; // other dimensions are already OK..
		ret.data = ArrayUtils.addAll(f.data, s.data);
		return ret;
	}
	public Tuple<RE,RE> sliceDepthwise(int sizeLeft) {
		RE left = clone();
		RE right = clone();
		left.depth = sizeLeft;
		right.depth = this.depth-sizeLeft;
		left.data = Arrays.copyOfRange(this.data, 0, sizeLeft);
		right.data = Arrays.copyOfRange(this.data, sizeLeft+1, this.depth);
		return new Tuple<RE, RE>(left,right);
	}
	
    public static IntegerArray3D connectDepthwise(IntegerArray2D... res) {
		int w,h,d; w = res[0].width; h= res[0].height; d = res.length;
        IntegerArray3D ret = new IntegerArray3D(w, h, d);
		for (int re = 0; re<d; re++) {
			if (res[re].width != w || res[re].height != h)
				try {
					throw new OutOfBoundsException("Different sizes!");
				} catch (OutOfBoundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			ret.data[re] = res[re].clone().data;
		}
		ret.depth = d; // other dimensions are already OK..
		return ret;
	}
	
//    public IntegerArray2D fold1D(int dimension, MathFunction2D f, int start) {
//		int w,h, foldingDdepth;
//		
//		abstract class DataGetter {
//            public abstract int get(int x, int y, int z);
//		}
//		DataGetter dataGetter;
//		switch (dimension) {
//		case 0: 
//			foldingDdepth = width; 	w = height; h = depth; 
//			dataGetter = new DataGetter(){ 
//                @Override public int get(int x, int y, int z) {
//					return data[y][x][z];
//				}}; break;
//		case 1: 
//			foldingDdepth = height; w = width; 	h = depth;
//			dataGetter = new DataGetter(){ 
//                @Override public int get(int x, int y, int z) {
//					return data[y][z][x];
//				}}; break;
//		case 2: 
//			foldingDdepth = depth; 	w = width; 	h = height;
//			dataGetter = new DataGetter(){ 
//                @Override public int get(int x, int y, int z) {
//					return data[z][y][x];
//				}}; break;
//		default: 
//			try {
//				throw new OutOfBoundsException("Invalid folding dimension!");
//			} catch (OutOfBoundsException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
//		}
//		
//        IntegerArray2D ret = new IntegerArray2D(w,h);
//		for (int x = 0; x<w; x++)
//			for (int y = 0; y<h; y++) {
//                int folded = start;
//				for (int z = 0; z<foldingDdepth; z++)
//					folded = f.apply(dataGetter.get(x, y, z), folded);
//				ret.data[y][x] = folded;
//			}
//		return ret;
//	}
	
    public IntegerArray1D collapse() {
//        IntegerArray1D ret = new IntegerArray1D(width*height*depth);
        int[] ret = new int[0];
		for (int z = 0; z<depth; z++)
			for (int y = 0; y< height; y++)
				ret = ArrayUtils.addAll(ret, data[z][y]);
        return new IntegerArray1D(ret);
	}
	
	
}
