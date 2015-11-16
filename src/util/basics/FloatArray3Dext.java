package util.basics;

import generics.Reflect;
import generics.StructuredDoubles;
import io.JsonArrays;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;

public class FloatArray3Dext<RE extends FloatArray3Dext<RE>> extends StructuredDoubles<RE, FloatArray3Dext.Loc>{

	public float[][][] data;
	public int width;
	public int height;
	public int depth; 
	
	public static class Loc {
		int x, y, z;
		public Loc(int i, int j, int k) { x = i; y=j; z=k; }
	}
	@Override
	public void set(Loc l, Double d) { data[l.z][l.y][l.x] = d.floatValue(); }

	@Override
	public Double get(Loc l) throws OutOfBoundsException { 
		if (l.x>=width || l.y>=height || l.z >= depth )
			throw new OutOfBoundsException("x="+l.x+", y="+l.y+", z="+l.z);
		return (double) data[l.z][l.y][l.x]; }

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
	
	public FloatArray3Dext() { }; // used in parsing
	
	public FloatArray3Dext(float[][][] data) {
		depth = data.length;
		width = FloatArrays2D.width(data[0]);
		height = FloatArrays2D.height(data[0]);
		this.data = data;
	}
	public FloatArray3Dext(int w, int h, int d) {
		data = new float[d][h][w];
		width = w; height = h; depth = d;
	}
	
	public FloatArray3Dext(float[][][] i, int width2, int height2, int d) {
		data = i; width = width2; height = height2; depth = d;
	}


	@SuppressWarnings("unchecked")
	@Override
	public RE clone() {
//		return new FloatArray3D(ArraysTK.deepCopy(data),width,height, depth);
		try {
			RE ret;
				ret = (RE) Reflect.newClassInstance(this.getClass());
			ret.data = FloatArrays.deepCopy(data);
			ret.width = width; ret.height = height; ret.depth = depth;
			return ret;
		} catch (Exception e) { e.printStackTrace(); }
		return null; // otherwise
	}

//	public void reset() { ArraysTK.fill(data, 0); 	}
	
	
	@Override
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
			.add("data", JsonArrays.toJsonArray(data))
			.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RE fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		data = DoubleArrays.castToFloats(JsonArrays.fromJsonArray3D(o.getJsonArray("data")));
		return (RE) this;
	}

	@Override
	public String toString() { return FloatArrays.toString(data); }

	public float[][][] subArray(int x1, int x2, int y1, int y2, int z1, int z2) {
		float[][][] ret = new float[z2-z1+1][y2-y1+1][x2-x1+1];
		for (int z = z1; z<=z2; z++)
			for (int y = y1; y<=y2; y++)
				for (int x = x1; x<=x2; x++)
					ret[z-z1][y-y1][x-x1] = data[z][y][x];	
//			new FloatArray2D(data[z]).subArray(x1,x2,y1,y2);
		return ret;
	}
	
//	public static void main(String[] args) {
//		FloatArray3D q = new FloatArray3D(new float[][][] 
//		   {
//				{	{1,2,3},
//					{4,5,6},
//					{7,8,9}}
//				,{	{11,12,13},
//					{14,15,16},
//					{17,18,19}}});
//        Debug.out(new FloatArray3D(q.subArray(0, 1, 1, 2, 0, 1)));
//	}
}
