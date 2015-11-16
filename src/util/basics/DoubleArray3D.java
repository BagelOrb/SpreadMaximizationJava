package util.basics;

public class DoubleArray3D extends DoubleArray3Dext<DoubleArray3D> {

	public DoubleArray3D(double[][][] ds) {
		super(ds);
	}

	public DoubleArray3D(int width, int height, int depth) {
		super(width, height, depth);
	}
	public DoubleArray3D(double[] dataConcat, int width, int height, int depth) {
		super(dataConcat, width, height, depth);
	}

	public DoubleArray3D() {
		super();
	}







//	public static void main(String[] args) {
//		DoubleArray3D q = new DoubleArray3D(new double[][][] {
//				{{0,0},
//				 {0,0}},
//				{{0,0},
//  				 {0,0}},
//		});
//		
//		for (Loc l : q) {
//			q.set(l, 1d);
//            Debug.out(q+"\r\n---");
//		}
//	}

}
