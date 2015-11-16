package generics;


import io.JsonAble;

import java.util.Iterator;

import util.basics.OutOfBoundsException;
import util.math.MathFunction1D;
import util.math.MathFunction2D;
import util.math.MathFunction3D;



public abstract class StructuredDoubles<Ext extends StructuredDoubles<Ext, Loc>, Loc>
	extends JsonAble<Ext> implements Structure<Double, Loc>, Cloneable{
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
	public void add(double p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, get(l)+p);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
//	public abstract void multiply(double p);
	public void multiply(Ext p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, get(l)*p.get(l));
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}
	public void multiply(double p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
			double s = get(l)*p;
				set(l, s);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
//	public abstract void fill(double p);
	public void fill(double p) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, p);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}	
	public Ext filled(double p) {
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
	
	public Ext times(double d) {
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
	public Ext plus(double d) {
		Ext ret = this.clone();
		ret.add(d);
		return ret;
	}
	public Ext mapped(MathFunction1D f) {
		Ext ret = this.clone();
		ret.map(f);
		return ret;
	}
	
	
//	public abstract void map(MathFunction1D f);
	public void map(MathFunction1D f) {
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				set(l, f.apply(get(l)));
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
	}
//	public static void main(String[] args) {
//		DoubleArray2D q = new DoubleArray2D(new double[][] {{1,2},{3,4}});
//        Debug.out(q.foldr(MathFunction2D.multiplication(), 1));
//	}
	public double foldr(MathFunction2D f, double start) {
		double val = start;
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				val = f.apply(get(l), val);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}		
		return val;
	}
	public <T> T foldr(Function2D<Double, T> f, T start) {
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
	
	
	
	public double vectorLength() {
		return Math.sqrt(foldr(new MathFunction2D() {
			
			@Override
			public double apply(double arg1, double arg2) {
				return arg1*arg1 + arg2;
			}
		}, 0));
	}
	public double dotProduct(Ext t2) {
		return zipFold(new MathFunction3D() {
			
			@Override
			public double apply(double arg1, double arg2, double prevResult) {
				return prevResult + arg1*arg2;
			}
		}, 0, t2);
	}
	
//	public abstract T zipWith(MathFunction2D f, T t2);
	public Ext zippedWith(MathFunction2D f, Ext t2){
		Ext ret = clone(); 
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				ret.set(l, f.apply(get(l), t2.get(l)));
			} catch (OutOfBoundsException e) {  e.printStackTrace();  }
		}
		return ret;
	}
	public double zipFold(MathFunction3D f, double startingResult, Ext t2){
		double ret = startingResult; 
		Iterator<Loc> it = iterator();
		while (it.hasNext()) {
			Loc l = it.next();
			try {
				ret = f.apply(get(l), t2.get(l), ret);
			} catch (OutOfBoundsException e) { e.printStackTrace(); }
		}
		return ret;
	}
		
	
	public double totalSum() {
		return foldr(MathFunction2D.addition, 0);
	}
	int size = -1;
	public int sizeFinal() {
		if (size==-1) return size=size();
		return size;
	}
	public int size() {
		return (int) mapped(new MathFunction1D() {
			@Override public double apply(double arg) {
				return 1;
			}
		}).foldr(MathFunction2D.addition, 0);
	}
}
