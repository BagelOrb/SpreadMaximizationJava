package util.math;

import generics.Triple;

import java.util.Random;

import network.analysis.Debug;

public class Encoding3DCoords {
	
	int w,h,d;
	
	public Encoding3DCoords(int w, int h, int d) {
		this.w = w; this.h=h; this.d=d;
	}
	
	public int encode(int x,int y,int z) {
		if (	x>=w || y>=h || z>=d
			||	x<0	 || y<0  || z<0  ) return -1;
		return x+y*w+z*w*h;
	}
	public Triple<Integer,Integer,Integer> decode(int i) {
		if (i>=getSize() || i<0) 
			return null;
		int x,y,z;
		x = i % w;
		y = ((i-x)/w ) % h;
		z = ((i-y-x)/w/h ) % d;
		return new Triple<Integer, Integer, Integer>(x,y,z);
	}

	public int getSize() { return w*h*d; }
	
	public static void test() {
		Random r = new Random();
		for (int i = 0; i<10000; i++) {
			int w = r.nextInt(1000)+1;
			int h = r.nextInt(1000)+1;
			int d = r.nextInt(1000)+1;
			int x = r.nextInt(w);
			int y = r.nextInt(h);
			int z = r.nextInt(d);
			Encoding3DCoords q = new Encoding3DCoords(w,h,d);
			int e = q.encode(x,y,z);
			Triple<Integer, Integer, Integer> a = q.decode(e);
			if (!(a.fst==x && a.snd==y && a.thrd==z))
                Debug.out("Shite is incorrect!");
    //        Debug.out(e);
    //        Debug.out(q.decode(e));
		}
        Debug.out("Done!");
	}
}
