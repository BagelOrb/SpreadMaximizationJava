package generics;


import java.util.Iterator;

import util.basics.OutOfBoundsException;



public abstract class StructuredIntegers<Ext extends StructuredIntegers<Ext, Loc>, Loc>
//    extends JsonAble<Ext> 
	implements Structure<Integer, Loc>, Cloneable{
//	implements IOable {

	@Override
	public abstract Ext clone();
	
//	public abstract void reset(); // TODO: replace with fill(0)
	
//	public abstract void add(T p);
	public void add(Ext p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, get(l)+p.get(l));
			} catch (OutOfBoundsException e) { 
				e.printStackTrace(); }
		}
	}
    public void add(int p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, get(l)+p);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
//    public abstract void multiply(int p);
	public void multiply(Ext p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, get(l)*p.get(l));
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}
    public void multiply(int p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
            int s = get(l)*p;
				set(l, s);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
//    public abstract void fill(int p);
    public void fill(int p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, p);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
    public Ext filled(int p) {
		Ext ret = this.clone();
		ret.fill(p);
		return ret;
	}	
	public void set(Ext p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, p.get(l));
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
	
    public Ext times(int d) {
		Ext ret = this.clone();
		ret.multiply(d);
		return ret;
	}
	public Ext times(Ext d) {
		Ext ret = this.clone();
		ret.multiply(d);
		return ret;
	}
	public Ext plus(Ext t) {
		Ext ret = this.clone();
		ret.add(t);
		return ret;
	}
//	public Ext mapped(MathFunction1D f) {
//		Ext ret = this.clone();
//		ret.map(f);
//		return ret;
//	}
	
	
//	public abstract void map(MathFunction1D f);
//	public void map(MathFunction1D f) {
//		Iterator<Loc> it = iterator();
//		while (it.hasNext()) {
//			Loc l = it.next();
//			try {
//				set(l, f.apply(get(l)));
//			} catch (OutOfBoundsException e) { e.printStackTrace(); }
//		}
//	}
////	public static void main(String[] args) {
////        IntegerArray2D q = new IntegerArray2D(new int[][] {{1,2},{3,4}});
////        Debug.out(q.foldr(MathFunction2D.multiplication(), 1));
////	}
//    public int foldr(MathFunction2D f, int start) {
//        int val = start;
//		Iterator<Loc> it = iterator();
//		while (it.hasNext()) {
//			Loc l = it.next();
//			try {
//				val = f.apply(get(l), val);
//			} catch (OutOfBoundsException e) { e.printStackTrace(); }
//		}		
//		return val;
//	}
    public <T> T foldr(Function2D<Integer, T> f, T start) {
		T val = start;
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				val = f.apply(get(l), val);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }

		}		
		return val;
	}
	
//	public abstract T zipWith(MathFunction2D f, T t2);
//	public Ext zipWith(MathFunction2D f, Ext t2){
//		Ext ret = clone(); 
//		Iterator<Loc> it = iterator();
//		while (it.hasNext()) {
//			Loc l = it.next();
//			try {
//				ret.set(l, f.apply(get(l), t2.get(l)));
//			} catch (OutOfBoundsException e) { e.printStackTrace(); }
//		}
//		return ret;
//	}
//		
//	
//    public int totalSum() {
//		return foldr(MathFunction2D.addition, 0);
//	}
//	int size = -1;
//	public int sizeFinal() {
//		if (size==-1) return size=size();
//		return size;
//	}
//	public int size() {
//		return (int) mapped(new MathFunction1D() {
//            @Override public double apply(double arg) {
//				return 1;
//			}
//		}).foldr(MathFunction2D.addition, 0);
//	}
}
