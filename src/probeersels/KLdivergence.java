package probeersels;

import java.util.Arrays;

import network.analysis.Debug;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import util.basics.DoubleArray2D;

@SuppressWarnings({"unused"})
public class KLdivergence {
	static int k = 2;
	static RealMatrix s0 = new BlockRealMatrix(new double[][] {
			{.8,.2},
			{.2,.8}
	});
	static RealMatrix s1 = new BlockRealMatrix(new double[][] {
			{.6,.4},
			{.4,.6}
	});
	static RealMatrix s1inv = new LUDecomposition(s1).getSolver().getInverse();
	
//	static RealVector m0 = new ArrayRealVector(new double[]{0,0});
	static RealVector m1 = new ArrayRealVector(new double[]{.1,0});
	

	public static void main(String[] args) {
		checkDers(args);
	}
	public static void converge(String[] args) {
        Debug.out("m1: "+m1);
        Debug.out("s1: "+s1);
        Debug.out("KL: "+computeKL(s0, s1, m1));
		double d = -1E-3;
		for (int i = 0; i<1000; i++) {
			s1 = s1.add(computeSigmaDerivatives().scalarMultiply(d));
			m1 = m1.add(asColumnVector(computeMuDerivatives().scalarMultiply(d)));
			s1inv = new LUDecomposition(s1).getSolver().getInverse();
		}
        Debug.out("m1: "+m1);
        Debug.out("s1: "+s1);
        Debug.out("KL: "+computeKL(s0, s1, m1));
		
	}
	public static RealVector asColumnVector(RealMatrix in) {
		return new ArrayRealVector(in.getColumn(0));
	}
	public static void checkDers(String[] args) {
        Debug.out(computeMuDerivatives());
        Debug.out("sigmaDerivatives: "+computeSigmaDerivatives());
		
		double d = 1E-8;
		
		double f,m,l;
		
		
		RealMatrix s1Backup = s1.copy();
		double klBefore = computeKL(s0, s1, m1);
		f = first; m=mid; l=last;
		m1.addToEntry(0, d);
		double klAfter = computeKL(s0, s1, m1);
		
		double der = (klAfter - klBefore)/d;
        Debug.out("estimated Der : "+der);
        Debug.out("==========");
		
//        Debug.out("est traceDer: "+((first-f)/d));
//        Debug.out("est covDer: "+((mid-m)/d));
//        Debug.out("est detDer: "+((last-l)/d));
		
        Debug.out();
        Debug.out();
        Debug.out();
		s1=s1Backup;
		double klBefore2 = computeKL(s0, s1, m1);
		f = first; m=mid; l=last;
		s1.addToEntry(0, 0, d);
		double klAfter2 = computeKL(s0, s1, m1);
		
		double der2 = (klAfter2 - klBefore2)/d;
        Debug.out();
        Debug.out("estimated Der : "+der2);
        Debug.out("==========");
		
	}
	public static RealMatrix computeMuDerivatives() {
		// 2 S m
		RealMatrix ret = s1inv.multiply(asColumnMatrix(m1)).scalarMultiply(2);
		
//		// 2 m^T S
//		RealMatrix ret = asColumnMatrix(m1).transpose().multiply(s1inv).scalarMultiply(2);
		
		return ret.scalarMultiply(.5);
	}
	public static RealMatrix computeSigmaDerivatives() {
		RealMatrix traceDer = s1inv.multiply(s0).multiply(s1inv).scalarMultiply(-1);
//        Debug.out("traceDer: "+traceDer);
		RealMatrix covDer = s1inv.multiply(m1.outerProduct(m1)).multiply(s1inv).scalarMultiply(-1);
//        Debug.out("covDer: "+covDer);
		RealMatrix detDer = s1inv.scalarMultiply(-1);
//        Debug.out("detDer: "+detDer);
		RealMatrix ret = traceDer.add(covDer).add(detDer.scalarMultiply(-1)).scalarMultiply(.5);
//        Debug.out("sigma derivative: "+ret);
//		return ret;
//		return s1inv.multiply(	MatrixUtils.createRealIdentityMatrix(k)
//								.subtract(s0.multiply(s1inv))
//								.subtract(m1.outerProduct(m1).multiply(s1inv))).scalarMultiply(.5);
		return s1inv.multiply(	MatrixUtils.createRealIdentityMatrix(k)
				.subtract(s0.add(m1.outerProduct(m1))
						.multiply(s1inv)))				.scalarMultiply(.5);
	}
//	public static void mainOLD(String[] args) {
//		
//        Debug.out("s1inv: "+s1inv);
//		
//		RealMatrix invDinv = s1inv.multiply(getDer(s1)).multiply(s1inv);
//        Debug.out(s1inv.multiply(getDer(s1)));
//        Debug.out("invDinv: "+invDinv);
//
//		RealMatrix first =  invDinv.multiply(s0).scalarMultiply(-1);
//		
//		RealMatrix mid1 = m1.outerProduct(m1).multiply(invDinv).scalarMultiply(-1);
//		
////		RealMatrix mid2 = s1.add(s1.transpose())
////				.multiply(asColumnMatrix(m1))
////				.multiply(asColumnMatrix(getDer(m1)));
//		
//		RealMatrix last = s1inv.transpose().multiply(getDer(s1)).scalarMultiply(-1);
//		
////		RealMatrix derivative = first.add(mid1).add(mid2).add(last).scalarMultiply(.5);
//		RealMatrix derivative = first.add(mid1).add(last).scalarMultiply(.5);
////        Debug.out(derivative);
//        Debug.out(first);
//        Debug.out(mid1);
////        Debug.out(mid2);
//        Debug.out(last);
//	}

//	public static void main2(String[] args) {
//		
//
//		double derivative  = 0;
//		for (int i = 0; i<100; i++) {
//			derivative = computeDer(s0,s1,m1);
//			
//		
//		}
//        Debug.out("derivative: "+derivative);
//        Debug.out("--------");
//		
//		
//		
//		double before  = computeKL(s0,s1,m1);
//		
//		double firstBefore = KLdivergence.first;
//		double midBefore = KLdivergence.mid;
//		double lastBefore = KLdivergence.last;
//		
//		double d = 1E-8;
//		m1 = m1.add(getDer(m1).mapMultiply(d));
//		s1 = s1.add(getDer(s1).scalarMultiply(d));
//
//		double after = computeKL(s0,s1,m1);
//		double estimatedDer = (after-before)/d;
//        Debug.out("----------");
//        Debug.out("est first: "+((KLdivergence.first-firstBefore)/d));
//        Debug.out("est mid: "+((KLdivergence.mid-midBefore)/d));
//        Debug.out("est last: "+((KLdivergence.last-lastBefore)/d));
//        Debug.out("estimatedDer: "+estimatedDer);
//		
//	}
	static double first,mid,last;
	static double computeKL(RealMatrix s0, RealMatrix s1, RealVector m1) {
		int k = s0.getColumnDimension();
		RealMatrix m = asColumnMatrix(m1);
		RealMatrix s1inv = new LUDecomposition(s1).getSolver().getInverse();
		double first = s1inv.multiply(s0).getTrace() ;
		double mid = m.transpose().multiply(s1inv).multiply(m).getEntry(0, 0);
		double last = Math.log(new LUDecomposition(s0).getDeterminant() / new LUDecomposition(s1).getDeterminant() );
//        Debug.out("first: "+ first);
//        Debug.out("mid: "+mid);
//        Debug.out("last: "+last);
		KLdivergence.first = first;
		KLdivergence.mid = mid;
		KLdivergence.last = last ;
		return .5* (first + mid - k - last   );
	}
//	static double computeDer(RealMatrix s0, RealMatrix s1, RealVector m1) {
//		int k = s0.getColumnDimension();
//		RealMatrix s1inv = new LUDecomposition(s1).getSolver().getInverse();
////        Debug.out("s1inv: "+s1inv);
//		
//		RealMatrix invDinv = s1inv.multiply(getDer(s1)).multiply(s1inv);
////        Debug.out("invDinv: "+invDinv);
//		
//		double first =  -invDinv.multiply(s0).getTrace() ;
//		
//		double mid1 = asColumnMatrix(getDer(m1)).transpose().multiply(s1inv).multiply(asColumnMatrix(m1)).getEntry(0, 0);
//		double mid2 = - asColumnMatrix(m1).transpose().multiply(s1inv).multiply(getDer(s1)).multiply(s1inv).multiply(asColumnMatrix(m1)).getEntry(0, 0);
//		double mid3 = asColumnMatrix(m1).transpose().multiply(s1inv).multiply(asColumnMatrix(getDer(m1))).getEntry(0, 0);
//		double mid = mid1+mid2+mid3;
//		
//		double last = -s1inv.multiply(getDer(s1)).getTrace();
//		
//        Debug.out("first: "+ first);
//        Debug.out("mid: "+mid+"\t mid1:"+mid1+"\t mid2: "+mid2+"\t mid3: "+mid3);
//        Debug.out("last: "+last);
//
//		double derivative = .5*(first+mid1+mid2+mid3-last);
//		
////        Debug.out(first);
////        Debug.out(mid1);
////        Debug.out(mid2);
////        Debug.out(mid3);
////        Debug.out(last);
//
//		return derivative;
//	}
	static RealMatrix getDer(RealMatrix m) {
		DoubleArray2D q = new DoubleArray2D(m.getColumnDimension(), m.getRowDimension());
		q.fill(1);
		q.data[0][0] = 0;
		return new BlockRealMatrix(q.data);
	}
	static RealVector getDer(RealVector v) {
		double[] q = new double[v.getDimension()];
		Arrays.fill(q,1);
		q[0]=0;
		return new ArrayRealVector(q);
	}
	static RealMatrix asColumnMatrix(RealVector v) {
		RealMatrix ret = new BlockRealMatrix(v.getDimension(), 1);
		ret.setColumn(0, v.toArray());
		return ret;
	}
	
	public static void main_vTMv(String[] args) {
		int k = 2;
		double[][] s0d = new double[][] {
				{.9,.1},
				{.1,.9}
		};
		RealVector v = new ArrayRealVector(new double[]{.1,.2});
		
		RealMatrix m = new BlockRealMatrix(s0d);
		
		RealMatrix der1 = asColumnMatrix(getDer(v)).transpose().multiply(m).multiply(asColumnMatrix(v));
		RealMatrix der2 = asColumnMatrix(v).transpose().multiply(getDer(m)).multiply(asColumnMatrix(v));
		RealMatrix der3 = asColumnMatrix(v).transpose().multiply(m).multiply(asColumnMatrix(getDer(v)));
		
		RealMatrix ders = der1.add(der2).add(der3);
        Debug.out(ders);
		
		double ansBefore = asColumnMatrix(v).transpose().multiply(m).multiply(asColumnMatrix(v)).getEntry(0, 0);
		
		double d = 1E-4;
		v = v.add(getDer(v).mapMultiply(d));
		m = m.add(getDer(m).scalarMultiply(d));
//        Debug.out(getDer(m).scalarMultiply(d));
		
		double ansAfter = asColumnMatrix(v).transpose().multiply(m).multiply(asColumnMatrix(v)).getEntry(0, 0);
		double estimatedDer = (ansAfter-ansBefore)/d;
        Debug.out("estimatedDer: "+estimatedDer);
		
		
		double computed2 = asColumnMatrix(v).transpose().multiply(m.add(m.transpose())).multiply(asColumnMatrix(getDer(v))).getEntry(0, 0)
				+ asColumnMatrix(v).transpose().multiply(getDer(m)).multiply(asColumnMatrix(v)).getEntry(0, 0);
        Debug.out(computed2);
		
	}
}
