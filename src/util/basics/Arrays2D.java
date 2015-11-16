package util.basics;

public class Arrays2D {

	public static int[][] copyOf(int[][] or) {
		int h = height(or);
		int w = width(or);
		int[][] ret = new int[h][w];
		for (int x = 0; x<w; x++)
			for (int y = 0; y<h; y++)
				ret[y][x] = or[y][x];
		return ret;
	}

	public static int[][] fill(int[][] in, int fill) {
	//		int[][] ret = new int[height(in)][width(in)];
			for (int x = 0; x< width(in); x++)
				for (int y = 0; y<height(in); y++)
					in[y][x] = fill;
			return in;	
		}

	public static int height(int[][] in) {
		return in.length;
	}

	public static int height(Object[][] in) {
		return in.length;
	}

	public static String toString(Object[][] array) {
		String ret = "";
		for (Object[] line : array) {
			for (Object point : line)
				ret += point+"; ";
			ret += "\r\n";
		}
		return ret;
	}

	public static String toString(float[][] array) {
		String ret = "";
		for (float[] line : array) {
			for (float point : line)
				ret += format(point)+"; ";
			ret += "\r\n";
		}
		return ret;
	}

	public static int width(int[][] in) {
		int h = in.length;
		//TODO: case for when height = 0
		if (h == 0)
			try {throw new Exception("Cannot copy 2D array with zero width.");	} catch (Exception e) {	e.printStackTrace();}
			return in[0].length;		
	}

	public static int width(Object[][] in) {
		int h = in.length;
		//TODO: case for when height = 0
		if (h == 0)
			try {throw new Exception("Cannot copy 2D array with zero width.");	} catch (Exception e) {	e.printStackTrace();}
			return in[0].length;		
	}

	public static String format(double in) {
			return in+"";//Double.toString(in);
	//		if (in < 0 ) return df.format(in);
	//		else return "+"+df.format(in);
		}

}
