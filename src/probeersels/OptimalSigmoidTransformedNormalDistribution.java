package probeersels;

import java.util.Random;

import network.analysis.Debug;
import util.math.Math2;

public class OptimalSigmoidTransformedNormalDistribution {

	static double d = 1E-8;
	
	static Computation min = new Computation(1.4);
	static Computation mid = new Computation(1.6);
	static Computation max = new Computation(1.8);
	
//	static double smin = 1.74;
//	static double sminEntropy;
//	
//	static double smid = 1.75;
//	static double smidEntropy;
//	
//	static double smax = 1.76;
//	static double smaxEntropy;
//	
	private static class Computation{
		double s;
		double entropy;
		public Computation(double s) {
			this.s = s;
			entropy = entropy(s, d);
		}
		public String toString() { return "(s="+s+", e="+entropy+")"; }
	}
	
	public static void main(String[] args) {
		converge();
	}
	public static void checkAtIntervals() {
		for (double s = min.s; s<max.s;s+=(max.s-min.s)/100)
            Debug.out(s+" ;\t"+entropy(s,d));
	}
	
	public static void converge() {
		Random rand = new Random();
		
//        Debug.out(min+","+mid+","+max);
		
		double snow;
		for (int i = 0; i<75; i++) {
			snow = rand.nextDouble() *(max.s-min.s) + min.s;
			Computation now = new Computation(snow);
            Debug.out(snow+";\t"+now.entropy); // +"\t\tmin,max="+min.s+","+max.s
			
			if (snow>mid.s)
				if (now.entropy>mid.entropy) 
					{ min = mid; mid = now;}
				else{ max = now; }
			else if (now.entropy>mid.entropy) 
					{ max = mid; mid = now;}
				else{ min = now; }
		}
	}
	
	public static double entropy(double s, double d) {
		double ret = 0;
		double y = d;
		for (; y<1; y+= d) {
			double fy = fy(y, s);
			ret += fy*Math.log(fy)*d;
		}
		
		return -ret;
	}
	
	public static double fy(double y, double s) {
		
		double ret = 1/(y*(1-y)) * Math.exp(- (Math2.square( Math.log( - y/(y-1)))/( 2*s*s ))) / (s*Math.sqrt(2*Math.PI));
		
		return ret;
	}
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * smin = 1.6 smax = 1.8
	 * standard middle steps
	 * s=1.7000000000000002	Entropy=-0.010089398762085495
s=1.75	Entropy=-0.00951195974864299
s=1.725	Entropy=-0.009647828673160764
s=1.7375	Entropy=-0.009542188942846372
s=1.74375	Entropy=-0.009517710389945764
s=1.746875	Entropy=-0.00951250181047364
s=1.7484375	Entropy=-0.00951164842709249
s=1.74921875	Entropy=-0.009511658619780398
s=1.7488281250000002	Entropy=-0.00951161714142234
s=1.7486328125000001	Entropy=-0.009511623686880044
s=1.7487304687500003	Entropy=-0.009511618140042156
s=1.7487792968750002	Entropy=-0.009511617072231965
s=1.7488037109375	Entropy=-0.009511616964708244
s=1.7487915039062503	Entropy=-0.00951161698294035
s=1.7487976074218752	Entropy=-0.009511616964941039
s=1.7488006591796876	Entropy=-0.009511616962604446
s=1.7488021850585938	Entropy=-0.009511616963099294
s=1.7488014221191408	Entropy=-0.009511616962711937
s=1.7488010406494143	Entropy=-0.009511616962624397
s=1.7488008499145509	Entropy=-0.009511616962608478
s=1.7488007545471191	Entropy=-0.009511616962604365
s=1.7488007068634035	Entropy=-0.009511616962602016
s=1.7488007307052613	Entropy=-0.00951161696260389
s=1.7488007187843324	Entropy=-0.009511616962603586
s=1.748800712823868	Entropy=-0.009511616962601544
s=1.7488007098436358	Entropy=-0.009511616962600843
s=1.748800711333752	Entropy=-0.009511616962602262
s=1.7488007105886938	Entropy=-0.009511616962605643
s=1.7488007102161647	Entropy=-0.009511616962603707
s=1.7488007100299003	Entropy=-0.009511616962601905
s=1.7488007099367682	Entropy=-0.009511616962603234
s=1.7488007098902019	Entropy=-0.00951161696259901
s=1.7488007098669187	Entropy=-0.009511616962601981
s=1.7488007098785603	Entropy=-0.009511616962603966
s=1.7488007098843812	Entropy=-0.009511616962603164
s=1.7488007098872915	Entropy=-0.009511616962601761
s=1.7488007098887466	Entropy=-0.00951161696260067
s=1.7488007098894742	Entropy=-0.009511616962599084
s=1.7488007098898382	Entropy=-0.009511616962599253
s=1.74880070989002	Entropy=-0.009511616962599091
s=1.7488007098901108	Entropy=-0.009511616962599065
s=1.7488007098901563	Entropy=-0.00951161696259874
s=1.7488007098901792	Entropy=-0.00951161696259896
s=1.7488007098901677	Entropy=-0.009511616962598763
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.7488007098901592	Entropy=-0.009511616962598779
s=1.7488007098901606	Entropy=-0.009511616962598737
s=1.7488007098901612	Entropy=-0.009511616962598717
s=1.7488007098901615	Entropy=-0.009511616962598862
s=1.7488007098901617	Entropy=-0.009511616962598732
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
s=1.748800709890162	Entropy=-0.009511616962598666
	 * 
	 */
}
