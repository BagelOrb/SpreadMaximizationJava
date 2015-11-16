package probeersels;
//import java.util.Random;

import generics.Function1D;
import generics.Reflect;
import generics.Tuple;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import network.analysis.Debug;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import probeersels.FastCrapTry.Outer.Member;
import util.basics.ArraysTK;
import util.basics.DoubleArray2D;
import util.basics.DoubleArray3D;
import util.basics.DoubleArrays2D;
import util.math.Arith;
import util.math.Math2;
import util.math.MathFunction2D;
import util.math.Statistics;

@SuppressWarnings({"deprecation", "unused"})
public class FastCrapTry {

	
	private static Random random = new Random();
	
	static boolean q;
	public static void main(String[] args) {

		System.out.println(new File("sdggds/sdggsd").getPath());
		
	}
	
	public static void texWeightImages(String[] args) {
		String ret = "";
		
		for (int p = 0; p<=8; p++)
				ret+= 
				"\\begin{figure}[h]\r\n\\begin{center}\r\n\\input{Experimentation/Images/SM/simpleNetwork"+p+"/imgs.tex}\r\n\\end{center}\r\n\\caption{"+p+"}\r\n\\end{figure}\r\n\r\n";		
		System.out.println(ret);
	}
	
	public static void tikzCNNconnections(String[] args) {
		String ret = "";
		
		for (int p = 0; p<3; p++)
			for (int c = 0; c < 2 ; c++)
				ret += "\\draw [cyan] (c"+(p*2+c)+")  edge[in=-80, out=115, ->] (p"+p+");\r\n";
//		for (int c = 0; c<7; c++)
//			for (int i = 0; i < 3 ; i++)
//		ret += "\\draw [arrow] (i"+(i+c)+") -> (c"+c+") node {};\r\n";
		
		System.out.println(ret);
	}
	
	public static void variancetest(String[] args) {
		
		Variance v = new Variance();
		Random r = new Random();
		for (int t = 0; t < 1000000; t++)
			v.increment(r.nextGaussian());
		
		System.out.println(v.getResult());
	}
	
	public static void testForThreading(String[] args) {
		int nThreadsUsed = 6;
		int tot = 36864;
		for (int p = 0; p<nThreadsUsed; p++) 
            Debug.out(p+", "+ p*tot/nThreadsUsed+ ", "+ (p+1)*tot/nThreadsUsed+ ", "+ nThreadsUsed);
	}
	
	public static void mainInputActivationMapsToTotalActivations(String[] args) {
		DoubleArray3D qwersg = DoubleArray3D.connectDepthwise(ArraysTK.map(new Function1D<DoubleArray3D, DoubleArray2D>(){
			@Override
			public DoubleArray2D apply(DoubleArray3D a1) {
				return a1.fold1D(2, MathFunction2D.addition, 0);
			}}, new DoubleArray3D[]{new DoubleArray3D(new double[][][]{{{1,2},{3,4}},{{5,6},{7,8}}}) , 
					new DoubleArray3D(new double[][][]{{{1,2},{3,4}},{{5,6},{7,8}}}) } ))  ;
		
        Debug.out(qwersg);
	}	
	public static void mainAngleDistance(String[] args) {
		double cat = 1;
		double outNow = 1.5;
		double dist = cat - outNow;
		dist = dist % 360;
		if (dist <0) dist += 360;
        Debug.out(dist);
		if (Math.abs(dist-360)<dist) dist = dist-360;
        Debug.out(dist);
	}
	public static void mainMedian(String[] args) {
		int n = 3;
        Debug.out("for "+n);
        Debug.out("----");
        Debug.out("two points around above middle");
        Debug.out((n+1)/2);
        Debug.out((n+1)/2-1);
        Debug.out("two points around below middle");
        Debug.out(n/2);
        Debug.out(n/2-1);
        Debug.out("middle point");
        Debug.out(n/2);
        Debug.out((n-1)/2);
	}
	public static void main412(String[] args) {
		Boolean[] re = new Boolean[6];
		for (int p = 0; p < re.length/2; p++)
			re[p] = true;
		for (int p = re.length/2; p < re.length; p++)
			re[p] = false;
		
        Debug.out(Arrays.toString(re));
	}
	
	public static void mainTestMeanVariance(String[] args) {
		double[] x = new double[10000000];//{1,2,3,4};
		for (int i =0; i<x.length; i++)
			x[i] = random.nextGaussian()*10;
		
		long before;
		

		before = System.currentTimeMillis();
		Tuple<Double, Double> a = Statistics.meanVariance(x);
        Debug.out(a.fst+", "+ a.snd);
        Debug.out(System.currentTimeMillis()-before);
		
		
		before = System.currentTimeMillis();
		

		Tuple<Double, Double> b = Statistics.meanVarSinglePass(x);
		
        Debug.out(b.fst+", "+ b.snd);
        Debug.out(System.currentTimeMillis()-before);
	}
	

	
	public static void main2(String[] args) {
		// m*log(a)+(n-m)*log(1-a)-.5*log(2*pi*m) - .5* log(2*pi*(n-m)) - m*log(m/e)-(n-m)*log((n-m)/e)
//		Random r = new Random();
        //Debug.out( r.nextInt(8)+1);
        //Debug.out(Statistics.correlation(new double[]{0,0,0,0,0,1},new double[]{0,1,1,1,1,1}));
//        Debug.out(limitSumToOne2(.9,100));
//        Debug.out(limitSumToOne2(.5,2));
//        Debug.out(limitSumToOne2(.5,-.25));
//        Debug.out(limitSumToOne2(.5,.25));
//        Debug.out(limitSumToOne2(.5,-2));
//        Debug.out(limitSumToOne2(0,-.25));

		/* 
		double e1 = 0;
		double e2 = 0;
		int N = 1000;
		for (int n = 0; n<N ; n++){
			e1 += Math2.square(r.nextBinary(.9) - r.nextBinary(.9) );  
			e2 += Math2.square(r.nextBinary(.9) - 1 );  
		}
        Debug.out(Math.sqrt(e1/N));
        Debug.out(Math.sqrt(e2/N));
		*/
        //Debug.out((-1d/0d));

		//double[][] qw = new double[2][3];
//		qw[0][0] = 1; qw[1][0] = 2;
//		qw[0][1] = 3; qw[1][2] = 4;
		
		

		//new Escape();
		//QuitExperiment.run();

//		double detBefore;
//		{
//			double a=0.5050590554126382;
//			double b = -0.45122378461637824;
//			double c = 0.6269831778294913;
//			double e = 0.5773323269435554;
//			double f = -0.5396582981465924;
//			double i = 0.7906793072446097;
//			detBefore = a*e*i + 2*b*f*c - (c*e*c + b*b*i + a*f*f);
//            Debug.out(detBefore);
//		}
//		
//		{
//			double eta = 10000000;
//			double a=0.5050590554126382 + eta* 0.16525364555917454;
//			double b = -0.45122378461637824 + eta* 0.018416634718763605;
//			double c = 0.6269831778294913 + eta* -0.11847099732142742;
//			double e = 0.5773323269435554 + eta* 0.006231838770114228	;
//			double f = -0.5396582981465924 + eta* -0.010350412083517103;
//			double i = 0.7906793072446097 + eta* 0.08798401590176583;
//			
//			double detAfter = a*e*i + 2*b*f*c - (c*e*c + b*b*i + a*f*f);
//            Debug.out(detAfter);
//            Debug.out("diff: "+(detAfter-detBefore));
//		}
		
//		double q = 0;
//		double w = 0;
//		q = 2* (0.011215201690784573 *(0.38044765294250343 - 0.27399237266321325) + 0.061702483035629044 * (-0.43509355461788596 - -0.46256344581706826));
//		w += q * (1- 0.38044765294250343*0.38044765294250343);
//        Debug.out(q);
//		
//		q = 2* (0.011215201690784573 *(-0.5223348269859511 - 0.27399237266321325) + 0.061702483035629044 * (-0.3487960136180251 - -0.46256344581706826));
//		w += q * (1- -0.5223348269859511*-0.5223348269859511);
//        Debug.out(q);
//		
//		q = 2* (0.011215201690784573 *(0.9638642920330875 - 0.27399237266321325) + 0.061702483035629044 * (-0.6038007692152937 - -0.46256344581706826));
//		w += q * (1- 0.9638642920330875*0.9638642920330875);
//        Debug.out(q);
//		
//		
//        Debug.out("total der: "+q);
//		
//		double a = 0;
//		double s = 0;
//		a = 2* ( 0.061702348088451875*(0.38044765294250343-0.27399452987504597) +0.3737964308119692*(-0.43509355461788596- -0.46256344581706826));
//		s += a * (1- -0.43509355461788596*-0.43509355461788596);
//        Debug.out(a);
//		
//		a = 2* ( 0.061702348088451875*(-0.5223348269859511-0.27399452987504597) +0.3737964308119692*(-0.3487960136180251- -0.46256344581706826));
//		s += a * (1- -0.3487960136180251*-0.3487960136180251);
//        Debug.out(a);
//		
//		a = 2* ( 0.061702348088451875*(0.9638642920330875-0.27399452987504597) +0.3737964308119692*(-0.6038007692152937- -0.46256344581706826));
//		s += a * (1- -0.6038007692152937*-0.6038007692152937);
//        Debug.out(a);
//		
//        Debug.out("total der: "+s);
		
//        Debug.out(2.3494675863309295E-7*2.3494675863309295E-7);
////		Ratio weightSigmoidPoolingDer: 2.9292350047649563E-7
////		Estimated weightSigmoidPoolingDer: 6.88214269644119E-14
////		Computed weightSigmoidPoolingDer: 2.3494675863309295E-7
//        Debug.out(1.543879437370133E-4*1.543879437370133E-4 / 2.383345799363001E-8);
////		Ratio weightSigmoidPoolingDer: 1.5437382878956063E-4
////		Estimated weightSigmoidPoolingDer: 2.383345799363001E-8
////		Computed weightSigmoidPoolingDer: 1.543879437370133E-4
		

////		Object qwe = (Object) new NetworkParameters();
//		double[] qwe = new double[] {0,1};
//        Debug.out(Object[].class.isInstance(qwe));

//        Debug.out();
//        Debug.out(Arrays.toString(new File("src\\nnet").listFiles(new FilenameFilter(){
//
//			@Override
//			public boolean accept(File arg0, String arg1) {
//				return (FilenameUtils.getExtension(arg1).equals("java")); 
//			}})));
//		
//		File[] allJavaFiles = new File("src\\nnet").listFiles(new FilenameFilter(){
//			@Override
//			public boolean accept(File arg0, String arg1) {
//				return (FilenameUtils.getExtension(arg1).equals("java")); 
//			}});
//		
//		for (File e : allJavaFiles) {
//            Debug.out(e.getName());
//		}
		
//		double median = 0;
//		double eta = .1;
//		Random random = new Random();
//		double val;
//		for (int i = 0; i<100000000; i++) {
//			double r1 = random.nextDouble();
////			if (r1 < .499)
//				if (i%10000==0)
//				val = 1;
////				else if (r1<.6+.3) val = 100; 
//				else val = 10000;
//			median += eta * Math.signum(val-median);
//		}
//        Debug.out(median);
		
//        Debug.out(DifferentiableFunction.tanhAlmost.apply(DifferentiableFunction.tanhAlmost.applyInverse(1)+100));
//        Debug.out(DifferentiableFunction.tanhAlmost.applyInverse(1)*100);
//        Debug.out((Double.POSITIVE_INFINITY)/2);
//		
//        Debug.out(3/3);
//		
//		main2();
		
//		double[][][] q = new double[][][]{{{1,2},{3,4}}};
//		ArraysTK.add(q, new double[][][]{{{1,1},{2,2}}});
//        Debug.out(Arrays2D.toString(q[0]));
		
//		catastrophicCancellation();
		
//		double[][] q = new double[][] {{1,2},{3,4}};
//		double[][] w = new double[][] {{1,2},{3,4}};
//		w[0] = q[0];
//		q[0][0] = 0;
//		q[1][0] = 0;
//        Debug.out(Arrays2D.toString(w));
//		
		
//        Debug.out((8-(3-1))/1);
		
//		testCase();
		
		// checking sigmoid:
//		double[] q = new double[]{0.9999999932049052, 0.9999999977242636, 0.999821858192126, 0.9999527030372384};
//		double[] w = new double[]{0.6748177585749693, 0.5852972181770887, 0.902468447041476, -0.2793976355663543};
//		
//		double acc = 0;
//		for (int i = 0; i<4 ; i++)
//			acc += q[i]*w[i];
//		
//		acc += 0.34244986729816423; // bias
//        Debug.out(acc);
//        Debug.out(Math2.tanh(acc));
//		
//		
//        Debug.out(Math2.tanh(1.883038));
		
//		// checking pooling:
//		double[] t = new double[] {0.9769347602967975, 0.9769396132571123, 0.8221274387903945, 0.9329022456707977}; 
//		double[] et = ArraysTK.map(new MathFunction1D(){
//
//			@Override
//			public double apply(double arg) {
//				return Math.exp(-0.732345149608754*arg*arg);
//			}}, t);
//		
//		double out0;
//		{
//			double[] sigOuts = t;//	new double[]{ t1, t1, t1, t0};
//			double[] eSigOuts = et;//new double[]{et1,et1,et1,et0};
//			
//			double poolDenom = Statistics.total(eSigOuts);
//			double pooled = 0;
//			for (int i = 0; i<sigOuts.length; i++) 
//				pooled += sigOuts[i] * eSigOuts[i];
//			pooled /= poolDenom;
//            Debug.out(pooled);
//			out0 = pooled;
//		}
//		
//        Debug.out(out0);
		
//		// checking der:
//		double[] outs0 = new double[] {
//				0.9217668054028237, 0.9759972741310116, 
//				-0.30584765666007013, 0.003774563859083198, 
//
//
//				-0.9129291087295023, -0.8990997963352207, 
//				0.06680938360518522, 0.7019434024738676, 
//
//
//				0.9768351749427054, 0.9769408708500804, 
//				0.976941487773865, 0.9769414877746946, 
//
//
//				0.9573147141531823, 0.9769413021207198, 
//				-7.100061544525149E-4, 0.8119552091826338, 
//
//
//				-0.9121002135584476, -0.3427114955341353, 
//				-0.9122439410834664, -0.7940321402669814, 
//
//
//				0.980227905901784, 0.5124806068368168, 
//				-0.23640036708371168, -0.9034511830766897	};
//		double[] outs1 = new double[] {
//				0.44831540509506945, 0.7382670865974611, 
//				-0.9556343301889876, -0.8713391217279062, 
//
//				-0.9906118074235389, -0.977542313485226, 
//				-0.27000550996936645, 0.41199946456439596, 
//
//				0.7464329554098766, 0.7472735155633313, 
//				0.7472783084876213, 0.7472783084942922, 
//
//				0.648253573946222, 0.7472764791587299, 
//				-0.8870062224546656, 0.25183670319635465, 
//
//				-0.9906292718970362, -0.9230890963406643, 
//				-0.9906374314791626, -0.9848958733554528, 
//
//				0.7189881285710691, -0.0974203365303626, 
//				-0.9059832499820311, -0.9898681359106322   };
//		
//		double var0 = Statistics.variance(outs0);
//		double var1 = Statistics.variance(outs1);
//		double covar = Statistics.covariance(outs0, outs1);
//		double det = var0*var1-covar*covar;
//		
//        Debug.out(det);
		
		// der for first sample = 0.007550979597021251
		
		// checking der wrt left upper sigmoid neuron output
		// not done..
		
		// threading check
//		int n = 4;
//		int l = 4;
//		for (int p = 0; p<l; p++) 
//            Debug.out(p*n/l +" - "+ ((p+1)*n/l));
//
//		
//		new ForThreader(){
//			@Override void run(int i) {
//                Debug.out(i);;
//			}}.delegate(80);
//            Debug.out("DONE!!!");
		
//		final double etaplus = 1000;
//		final double etamin = 1./etaplus;
//		
//		final Random r = new Random(1);
//		DoubleArray3D wD = new DoubleArray3D(2,2,1);
//		wD.map(new MathFunction1D(){
//			@Override public double apply(double arg) {
//				return r.nextDouble()-.5;
//			}});
//        Debug.out(wD.toString());
//		
//		DoubleArray3D lD = new DoubleArray3D(2,2,1);
//		lD.map(new MathFunction1D(){
//			@Override public double apply(double arg) {
//				return r.nextDouble()-.5;
//			}});
//		lD.data[0][0][0] = 0;
//        Debug.out(lD.toString());
//		
//		DoubleArray3D deltas = new DoubleArray3D(2,2,1);
//		deltas.map(new MathFunction1D(){
//			@Override public double apply(double arg) {
//				return r.nextDouble()-.5;
//			}});
//        Debug.out(deltas.toString());
//		
//		DoubleArray3D ds = wD.zipWith(new MathFunction2D() {
//			@Override public double apply(double wD, double lD) {
//				if (wD*lD>0) return etaplus;
//				if (wD*lD<0) return etamin;
//				return 1;
//			}}, lD);	
//		deltas.multiply(ds);
//		
//        Debug.out(deltas.toString());
		
		
//		DoubleArray3D q = new DoubleArray3D(new double[][][]{{{1,2},{3,4}}});
//		q.multiply(2);
//        Debug.out(q);
//		
//		
//		
//        Debug.out(Math.min(Double.POSITIVE_INFINITY, 1E50));
//        Debug.out(Math.exp(115));
//		
//		
//		try {
////			new AA<C>(){};
//			new AA<C>() {}.newT();
////			new A<C>(){}.m();
//		} catch (Exception e) { e.printStackTrace(); }

		
		
		
		
		// flaot vs double
//		long before = System.currentTimeMillis();
//		
//		float q = 1;
//		for (int n = 0; n<100; n++) {
//			q = 1;
//			for (float i = 1; i<10000000f; i++)
//				q += i/q;
//		}
//        Debug.out(q);
//		
//        Debug.out(System.currentTimeMillis()-before);
		
//		double[] q = new double[83000000];
//		float[] q = new float[167000000];
		
        Debug.out(Object[][].class.isInstance(new double[][][]{}));
	}
	
	
	
//		new A();
//		A.main(args);
//		
//        Debug.out(Object[].class.isInstance(new double[1]));
	
//	public static class A {
//		public static void main(String[] args) {
//			try {
//                Debug.out(Reflect.newClassInstance(B.class));
//			} catch (Exception e) { e.printStackTrace(); }
//		}
//		public A() {}
////		public A(FastCrapTry f) {}
//		public class B  {
//			public B() {}
////			public String toString() { return "B"; }
//		}
//	}


	
	
	
//		A<?>[] as = new A[2];
//		as[0] = new C();
//		as[1] = new D();
//		for (A<?> a: as)
//			a.add(a.t);
//		for (A<?> a: as)
//			insideFor(a);
//		for (A a: as)
//			a.add(a.t);
//
//        Debug.out(Arrays.toString(as));
//	public static <T, S extends A<T>> void insideFor(A<T> a) {
//		a.add(a.t);
//	}
//	public static abstract class A<T> { 
//		T t;
//		public abstract void add(T t2);
//	}
//	public static class C extends A<Double> {
//		@Override public void add(Double t2) { t += t2;	}
//	}
//	public static class D extends A<String> {
//		@Override public void add(String t2) { t.concat(t2); }
//	}
	
	
	// generic newInstance of abstract and concrete classes
//	public static abstract class AA<T> {
//			T[] ts;
//		  T t;  
//		  public AA() throws Exception {
////              Debug.out(t);
////              Debug.out(this.getClass());
////              Debug.out();
//////              Debug.out(Arrays.toString(this.getClass().getTypeParameters()[0].getBounds()));
////              Debug.out(this.getClass().getGenericSuperclass());
////              Debug.out();
//////			  t = (T) ((Class)this.getClass().getTypeParameters()[0].getBounds()[0]).newInstance();
////              Debug.out(t);
//////			  t = (T) ((Class)((ParameterizedType)this.getClass().
//////					  getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
//		  }
//		  
//		  @SuppressWarnings("unchecked")
//		public T newT() throws Exception {
//              Debug.out();
//              Debug.out(this.getClass());
//              Debug.out((this.getClass().
//					  getGenericSuperclass()));
//              Debug.out(Arrays.toString(((ParameterizedType) this.getClass().
//					  getGenericSuperclass()).getActualTypeArguments()));
//              Debug.out();
//			  ts = (T[]) Array.newInstance(((Class<?>)((ParameterizedType) this.getClass().
//					  getGenericSuperclass()).getActualTypeArguments()[0]), 5);
//              Debug.out(Arrays.toString(ts));
//			  return (T) ((Class<?>)((ParameterizedType) this.getClass().
//					  getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
//		  }
//		  public void m() throws Exception {
//                Debug.out(t);
//				t = newT(); 
//                Debug.out(t);
//		  }
//
//		}
//	public static class A<T > {
//		
//		T t;
//		@SuppressWarnings("unchecked")
//		public T newT() throws InstantiationException, IllegalAccessException {
//			  return (T) ((Class<?>)((ParameterizedType)this.getClass().
//					  getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();		}
//		public void m() throws InstantiationException, IllegalAccessException {
//            Debug.out(t);
//			t = newT(); 
//            Debug.out(t);
//		}
//	}
//	public static abstract class B<T> {
//		Class<T> b;
//		public abstract T newInst();
//		public abstract T newInst(Class<T> clazz);
//	}
//	@SuppressWarnings("unchecked")
//	public static class C extends B<C> {
//		{ super.b = (Class<C>) this.getClass(); } 
//		@Override
//		public C newInst(Class<C> clazz) {
//			// TODO Auto-generated method stub
//			try {
//				return clazz.newInstance();
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		public C newInst() {
//			// TODO Auto-generated method stub
//			return newInst(C.class);
//		}
//		@Override
//		public String toString() { return "C"; }
//	}
	private abstract static class ForThreader {
		
		abstract void run(int i);
		
		public void delegate(int tot) {
			int nProcessors = Runtime.getRuntime().availableProcessors();
			if (nProcessors>tot) nProcessors = tot; // then we will produce less threads! (amount threads will then be tot)
			Thread[] threads = new Thread[nProcessors];
			for (int p = 0; p<nProcessors; p++) {
				threads[p] = new ForThread(p, p*tot/nProcessors, (p+1)*tot/nProcessors){ };
				threads[p].start();
			}
			for (int p = 0; p<nProcessors; p++) {
				try {
					threads[p].join();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
		private abstract class ForThread extends Thread { 
			int from; int to; int tag;
			public ForThread(int i, int f, int t) { tag = i; 	from=f;		to=t; }
			@Override
			public void run() {
				for (int i =from; i<to; i++)
					ForThreader.this.run(i);
			}
			//abstract public void run(int i); 
		}
	}
	
	public static void testCase() {
//		0.21575362485734584 // computed
//		0.21575362416343388 // observed by my old network
		double t2 = Math2.tanh(2);
		double t1 = Math2.tanh(1);
		double t0 = 0;
		double et2 = Math.exp(100*t2*t2);
		double et1 = Math.exp(100*t1*t1);
		double et0 = Math.exp(0);
		
		
		double out0,out1;
		{
			double[] sigOuts = 	new double[]{ t1, t1, t1, t0};
			double[] eSigOuts = new double[]{et1,et1,et1,et0};
			
			double poolDenom = Statistics.total(eSigOuts);
			double pooled = 0;
			for (int i = 0; i<sigOuts.length; i++) 
				pooled += sigOuts[i] * eSigOuts[i];
			pooled /= poolDenom;
            Debug.out(pooled);
			out0 = pooled;
		}
		{
			double[] sigOuts = 	new double[]{ t2, t2, t0, t0};
			double[] eSigOuts = new double[]{et2,et2,et0,et0};
			
			double poolDenom = Statistics.total(eSigOuts);
			double pooled = 0;
			for (int i = 0; i<sigOuts.length; i++) 
				pooled += sigOuts[i] * eSigOuts[i];
			pooled /= poolDenom;
            Debug.out(pooled);
			out1 = pooled;
		}

		
		// 0.7615941559557647 0.9640275800758169
		double var = 1d/6d * 2*(out0*out0+ out1*out1);
        Debug.out(var);
		
		double covar =  1d/6d * 2*(out0*out0);
        Debug.out(covar);
		
		double det = var*var-covar*covar;
        Debug.out(det);
	}
//	public static void generics() {
//		A<? extends D>[] re = new A<?>[2]; 
//		re[0] = new A<B>(new B()); re[1] = new A<C>(new C());
//		for (A<?> a : re)
//            Debug.out(a.etoString());
//	}
//	public static class A<T extends D> {
//		T t;
//		public A(T t){ this.t = t;  } 
//		public String etoString() { return t.etoString(); }
//	}
//	public static class D { public String etoString() { return "Sdg"; } }
//	public static class B extends D { @Override public String etoString() { return "Sdgsdf"; } }
//	public static class C extends D { @Override public String etoString() { return "S"; } }
//	public static class E {}
	
	
	
	
	private static void catastrophicCancellation() {
		double res = 0;
		random= new Random();
		double n = 1E8;
		for (int i = 0; i<n; i++)
			res += random.nextDouble() * ( (random.nextDouble()<.1)? 1 : 1E-5); 
        Debug.out(res/n);
	}




	public static void main2() {
		RealMatrix detD1, detD2;
			double[][] q = new double[][] 
	            {	{10,1 ,2 },
					{1 ,10,3 },
					{2 ,3 ,10}};
			{
	//            {	{.7 ,.1,.2},
	//				{.1,.7 ,.1},
	//				{.2,.1,.7 }};
	//		Network net = new Network(new NetworkParameters());
			BlockRealMatrix cov = new BlockRealMatrix(q);
			double det = new LUDecomposition(cov).getDeterminant();
            Debug.out(det);
			RealMatrix detD = new LUDecomposition(cov).getSolver().getInverse().transpose().scalarMultiply(det);
            Debug.out(detD);
			detD1 = detD;//.getEntry(0, 0);
			}
			double[][] e = // Arrays2D.plus(q, .0000001);
				new double[][] 
			     {	{10.00001,1 ,2 },
					{1 ,10,3 },
					{2 ,3 ,10}};
			{
			//            {	{.7 ,.1,.2},
			//				{.1,.7 ,.1},
			//				{.2,.1,.7 }};
			//		Network net = new Network(new NetworkParameters());
			BlockRealMatrix cov = new BlockRealMatrix(e);
			double det = new LUDecomposition(cov).getDeterminant();
            Debug.out(det);
			RealMatrix detD = new LUDecomposition(cov).getSolver().getInverse().transpose().scalarMultiply(det);
            Debug.out(detD);
			detD2 = detD;//.getEntry(0, 0);
		}
        Debug.out(DoubleArrays2D.toString(detD1.subtract(detD2).getData()));
		{
			double[][] w = new double[][] 
			   {	{10,1 ,2 },
					{1 ,10,3 },
					{2 ,3 ,10}};
			//            {	{.7 ,.1,.2},
			//				{.1,.7 ,.1},
			//				{.2,.1,.7 }};
			//		Network net = new Network(new NetworkParameters());
			BlockRealMatrix cov = new BlockRealMatrix(w);
			double det = new LUDecomposition(cov).getDeterminant();
			RealMatrix detDD = new LUDecomposition(cov.multiply(cov)).getSolver().getInverse().scalarMultiply(-det);
			RealMatrix xt = new LUDecomposition(cov).getSolver().getInverse().transpose();
//			detDD = detDD.add(xt.multiply(xt.scalarMultiply(det)));
			detDD = detDD.add(xt.multiply(xt.transpose().scalarMultiply(det)));
            Debug.out(DoubleArrays2D.toString(detDD.getData()));
			//detD2 = detD;//.getEntry(0, 0);
		}
		
        Debug.out("------");
		BlockRealMatrix a = new BlockRealMatrix( // Arrays2D.plus(q, .0000001);
			new double[][] 
		     {	{10,1 ,2 },
				{4 ,11,3 },
				{5 ,6 ,12}} );
		BlockRealMatrix b = new BlockRealMatrix(// Arrays2D.plus(q, .0000001);
			new double[][] 
		     {	{0,0,0},
				{0,1,0 },
				{0,0,0}});
		BlockRealMatrix ab = a.multiply(b);
        Debug.out(DoubleArrays2D.toString(ab.getData()));
        Debug.out(DoubleArrays2D.toString(ab.multiply(ab).getData()));
		
	}
	// SQUARE_WEIGHTED_SOFTMAX check:		 \/
	//
//		o = new double[]{.0107,.012,.01,-.011};
//        Debug.out((o(100+1E-8)-o(100))/1E-8);
//        Debug.out(der(100));
//	}
//	public static double[] o;
//	public static double f(double a, int k) { return a*o[k]*o[k]; }
//	public static double fD(double a, int k) { return o[k]*o[k]; }
//	public static double c(double a, int k) { 
//		double denom = 0;
//		for (int l = 0; l<o.length; l++)
//			denom += Math.exp(f(a,l));
//		return Math.exp(f(a,k))/ denom;
//	}
//	public static double o(double a) {
//		double denom = 0;
//		for (int l = 0; l<o.length; l++)
//			denom += Math.exp(f(a,l));
//		double ret = 0;
//		for (int l = 0; l<o.length; l++)
//			ret += o[l]* Math.exp(f(a,l)) /denom;
//		return ret;
//	}
//	public static double der(double a) {
//		double ret = 0;
//		for (int k = 0; k<o.length; k++)
//			ret += c(a,k)*fD(a,k)*(o[k]-o(a));
//		return ret;
//	}

	public static void compareAbsToMultiplication() {
		long before = System.currentTimeMillis();
		int o = 0;
		for (double i = 0; i<1000000000; i++)
			if ((i-500)*(i-500)>((i-100)*2)*((i-100)*2)) // 4680 ms
				o++;
        Debug.out(o);
        Debug.out("using multiplication: "+(System.currentTimeMillis()-before));
		before = System.currentTimeMillis();
		o = 0;
		for (double i = 0; i<1000000000; i++)
			if (Math.abs(i-500)>(Math.abs(i-100)*2)) // 4778 ms
				o++;
        Debug.out(o);
        Debug.out("using Math.abs: "+(System.currentTimeMillis()-before));

	}
	
	public static double limitSumToOne(double w, double dw) { return limitSumToOne(w,dw,w,dw);}
	public static double limitSumToOne(double w, double dw, double wtot, double dwtot) {
		double constrained = sumToBefore(w,dw,wtot,dwtot);
		return constrained*constrained + (1-constrained)*(w+dw);
	}
	public static double limitSumToOne2(double w, double dw) { return limitSumToOne2(w,dw,w,dw);}
	public static double limitSumToOne2(double w, double dw, double wtot, double dwtot) {
		// based on: solution = solution * constrained + (1-solution)*normal
		double constrained = sumToBefore(w,dw,wtot,dwtot);
		double normal = w+dw;
		return normal/(normal+1-constrained);
	}
	public static double sumToBefore(double w, double dw) { return sumToBefore(w,dw,w,dw);}
	public static double sumToBefore(double w, double dw, double wtot, double dwtot) {
		double constrained = (w + dw) / (1 + dwtot/wtot);
		return constrained;
	}
	
	public static void qwert() {
		//nouja ik ga nu ff checke of idd 
		// a^x*b^y*x+y boven x = (a+b)^(x+y)
		for (int x = 1; x<10; x++) 
			for (int y = 1; y<10; y++) 
				for (double a = .1; a<1; a+=.1)
					for (double b = .1; b<1; b+=.1) {
						double one = Math.pow(a, x)*Math.pow(b, y)*Arith.factorial(x+y)/(Arith.factorial(x)*Arith.factorial(y));
						double two = Math.pow(a+b, x+y);
                        Debug.out(two/one);
					}
		
	}
	
	
	public static void test() {
        Debug.out(likelihood(401));
        Debug.out(likelihood(401));
        Debug.out(likelihood(450));
        Debug.out(likelihood(500));
        Debug.out(likelihood(550));
        Debug.out(likelihood(599));
		double x = 100;
	}

	public static double likelihood(double x) {
		return x*Math.log(.1)+(600-x)*Math.log(.2)+(800-x)*Math.log(.4)+(1000-800-600+x)*Math.log(.3)+.5*Math.log(.5*Math.PI*1000)+1000*Math.log(1000/Math.E)-.5*Math.log(.5*Math.PI*x)-x*Math.log(x/Math.E)-.5*Math.log(.5*Math.PI*(600-x))-(600-x)*Math.log((600-x)/Math.E)-.5*Math.log(.5*Math.PI*(800-x))-(800-x)*Math.log((800-x)/Math.E)-.5*Math.log(.5*Math.PI*(1000-800-600+x))-(1000-800-600+x)*Math.log((1000-800-600+x)/Math.E);
	}
	
	
	public static class QQ {
		public int get() { return 1; }
		public int gett() { return get(); }
	}
	public static class QW extends QQ {
		@Override public int get() { return 2; }
	}
	public static void main3(String[] args) {
        Debug.out(new QW().gett());
	}
	
	public static class FinalInitialization {
		final int q;
		public FinalInitialization() { q = 1; }
		public FinalInitialization(int w) { q = w; }
	}
	public static class FinalInitialization2 extends FinalInitialization {
		public FinalInitialization2() { super(); }
		public FinalInitialization2(int w) { super(w); }
	}
	public static void main41(String[] args) {
        Debug.out(-0.15215599667434498 - -0.15215599667434498);
		
        Debug.out(
				new DoubleArray3D(new double[][][]{{{0.20974608745870663, 0.21789024163056037}, {0.2215928062771134, 0.17624571599336233}}, {{0.06059966231316845, 0.03867795298459517}, {0.040218358444200714, 0.03502917489829284}}}
				).foldr(MathFunction2D.addition, 0)  );
		
        Debug.out(new FinalInitialization(5).q);
		
		Object wq = new FinalInitialization(5);
        Debug.out(wq.getClass());
	}
	
	
	public static class AAA { }
	public static class BBB { }

	public static void varArgs(Object... os) { 
        for ( Object o : os) Debug.out(o.getClass());
	}
	public static class Outer {
		public class Member {
			int q;
			public Member(Integer i) { q = i; }
		}
	}
	
	public static void main253(String[] args) {
		try {
			Outer out = Reflect.newClassInstance(Outer.class);
			Member a = Reflect.newClassInstance(Outer.Member.class, out, 1);
            Debug.out(a.q);

			varArgs(new AAA(), new BBB());
		
		} catch (Exception e) { e.printStackTrace();
		
		
		}
	}
	
	public static void main4531(String[] args) {
		int i = 1;
		int j = 2;
		double h = 1d/j;
        Debug.out(h);
		
	}
	
	public static class Super {};
	public static class Sub extends Super{};
	
	public static void main532(String[] args) {
		Super s = new Sub();
        Debug.out(s.getClass());
	}
}

/*

Limit[n^n*e^-n*
  Sqrt[2*pi*n] / ( (m*n)^(m^n) *e^(-m*n)*
     Sqrt[2*pi*m*n] * ((q - m) n)^((q - m)*n)*e^((m - q)*n) *
     Sqrt[2*pi*(q - m)*n] * ((r - m)*n)^((m - r)*n)*
     Sqrt[2*pi*(r - m)*n] * ((1 - r - q + m)*n)^((1 - r - q + m)*n)*
     e^((r + q - 1 - m)*n) *Sqrt[2*pi*(1 - r - q + m)*n]  ), 
 n -> Infinity]

Maximize[{a^(m n) b^(n (-m + q)) (1 - a - b - c)^(n (1 + m - q - r))
    c^(n (-m + r)), a > 0, b > 0, c > 0, a + b + c < 1, n > 0, m > 0, 
  q > 0, r > 0}, {m}]
 */

