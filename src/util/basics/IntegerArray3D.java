package util.basics;

public class IntegerArray3D extends IntegerArray3Dext<IntegerArray3D> {

    public IntegerArray3D(int[][][] ds) {
		super(ds);
	}

    public IntegerArray3D(int width, int height, int depth) {
		super(width, height, depth);
	}
    public IntegerArray3D(int[] dataConcat, int width, int height, int depth) {
		super(dataConcat, width, height, depth);
	}

    public IntegerArray3D() {
		super();
	}




//	public static void main(String[] args) {
//        IntegerArray3D q = new IntegerArray3D(new int[][][] {
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
