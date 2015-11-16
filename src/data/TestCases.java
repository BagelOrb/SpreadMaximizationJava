package data;

import io.Folder;
import io.Images.InputSample;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import network.Static;
import network.analysis.Debug;
import util.basics.DoubleArray2D;

public class TestCases {
	
	public static final InputSample[] allDirections24Contrast;
	public static final InputSample[] allDirections24ContrastWithNoise;
	public static final InputSample[] allDirections360ContrastWithNoise;
	public static final InputSample[] allDirectionsLines;
	static {
		Random random = new Random(123);
		Random genDataRandomOld = GenerateData.random;
		GenerateData.random = random;

		
		{
			int nAngles = 24;
			int nSamplesPerAngle =  256;// 1024; // 160;//
			int w,h; w=h=6;
			double ratioNoise = 0;//.3;//.15;
			double angleNoiseRange = 1;
			double maxOffsetNoise = 0; // .5
			allDirections24Contrast = new InputSample[nAngles*nSamplesPerAngle];
			for (int a = 0; a<nAngles; a++) {
				random.setSeed(a);
				for (int i = 0; i<nSamplesPerAngle; i++) { 
	//				random.setSeed(197*a+i); // 197 is prime
					double angleNoise = 2*Math.PI /((double) nAngles)*.5 *angleNoiseRange * (random.nextDouble()*2. -1.);
					double offsetNoise = (random.nextDouble()*2-1)*maxOffsetNoise; // max half a pixel offset
					double[][] noise = GenerateData.generateRandom(w, h);
					double[][] contrastLine = GenerateData.generatePureContrastLine(6, 6, ((double)a)* 2*Math.PI/nAngles + angleNoise , offsetNoise);
					double[][] data = new DoubleArray2D(noise).times(ratioNoise).plus(new DoubleArray2D(contrastLine).times(1-ratioNoise)).data;
					allDirections24Contrast[a*nSamplesPerAngle + i] = new InputSample(a*nSamplesPerAngle + i, new double[][][]{ data }, a); // angle category
				}
			}
	//		Collections.shuffle(Arrays.asList(...), new Random(1));
		}
			
		
		
		
		
		{
			int nAngles = 24;
			int nSamplesPerAngle =  256;// 1024; // 160;//
			int w,h; w=h=6;
			double ratioNoise = 0.3;//.3;//.15;
			double angleNoiseRange = 1;
			double maxOffsetNoise = 0.5; // .5
			allDirections24ContrastWithNoise = new InputSample[nAngles*nSamplesPerAngle];
			for (int a = 0; a<nAngles; a++) {
				random.setSeed(a);
				for (int i = 0; i<nSamplesPerAngle; i++) { 
	//				random.setSeed(197*a+i); // 197 is prime
					double angleNoise = 2*Math.PI /((double) nAngles)*.5 *angleNoiseRange * (random.nextDouble()*2. -1.);
					double offsetNoise = (random.nextDouble()*2-1)*maxOffsetNoise; // max half a pixel offset
					double[][] noise = GenerateData.generateRandom(w, h);
					double[][] contrastLine = GenerateData.generatePureContrastLine(6, 6, ((double)a)* 2*Math.PI/nAngles + angleNoise , offsetNoise);
					double[][] data = new DoubleArray2D(noise).times(ratioNoise).plus(new DoubleArray2D(contrastLine).times(1-ratioNoise)).data;
					allDirections24ContrastWithNoise[a*nSamplesPerAngle + i] = new InputSample(a*nSamplesPerAngle + i, new double[][][]{ data }, a); // angle category
				}
			}
	//		Collections.shuffle(Arrays.asList(...), new Random(1));
		}
		
		
		
		
		
		{
			int nAngles = 360;
			int nSamplesPerAngle = 8;
			int w,h; w=h=6;
			double ratioNoise = 0.3;//.15;
			double angleNoiseRange = .5;
			allDirections360ContrastWithNoise = new InputSample[nAngles*nSamplesPerAngle];
			for (int a = 0; a<nAngles; a++) {
				random.setSeed(a);
				for (int i = 0; i<nSamplesPerAngle; i++) { 
	//				random.setSeed(197*a+i); // 197 is prime
					double angleNoise = 2*Math.PI /((double) nAngles) *angleNoiseRange * (random.nextDouble()*2. -1.);
					double offsetNoise = random.nextDouble()*.5-.25; // max half a pixel offset
					double[][] noise = GenerateData.generateRandom(w, h);
					double[][] contrastLine = GenerateData.generatePureContrastLine(6, 6, ((double)a)* 2*Math.PI/nAngles + angleNoise , offsetNoise);
					double[][] data = new DoubleArray2D(noise).times(ratioNoise).plus(new DoubleArray2D(contrastLine).times(1-ratioNoise)).data;
					allDirections360ContrastWithNoise[a*nSamplesPerAngle + i] = new InputSample(a*nSamplesPerAngle + i, new double[][][]{ data }, a); // angle category
				}
			}
	//		Collections.shuffle(Arrays.asList(...), new Random(1));
		}
		
		
		
		
		
		
		int nAngles = 8; // lines are symmetric! the further 8 lines are the same..
		allDirectionsLines = new InputSample[nAngles];
		for (int i = 0; i<nAngles; i++) {
			allDirectionsLines[i] = new InputSample(i, new double[][][]{
					GenerateData.generatePureLine(5, 5, i/nAngles* Math.PI + Math.PI , 0)
			}, i); // angle category
		}
//		Collections.shuffle(Arrays.asList(...), new Random(1));

	
		GenerateData.random = genDataRandomOld;
	}
	
	public static void outputTestImages(String[] args) {
		Folder dir = new Folder("Images\\OwnTestCases\\");
		dir.mkdirs();
		for (InputSample q : allDirections24ContrastWithNoise)
			q.outputImage(dir, "", Static.ColoringType.TYPE_COLOR_BEYOND_LIMITS);
		

		int[] q = new int[]{0,1,2};
		Collections.shuffle(Arrays.asList(q));
        Debug.out(Arrays.toString(q));
	}
	public static final InputSample[] hvLines = new InputSample[] {
		  new InputSample(0, new double[][][] {{ {0,0,0}
												,{1,1,1}
		  										,{0,0,0} }}, 0) // H
		, new InputSample(0, new double[][][] {{ {0,1,0}
												,{0,1,0}
		  										,{0,1,0} }}, 1) // V
	};

	public static final InputSample[] hvContrast = new InputSample[] {
		  new InputSample(0, new double[][][] {{ {0,0}
											 	,{1,1} }}, 0) // H
		, new InputSample(0, new double[][][] {{ {1,1}
		  										,{0,0} }}, 0) // H
		, new InputSample(0, new double[][][] {{ {1,0}
		  										,{1,0} }}, 1) // V
		, new InputSample(0, new double[][][] {{ {0,1}
		  										,{0,1} }}, 1) // V
		
	};
	
	public static final InputSample[] xor = new InputSample[] {
		  new InputSample(0, new double[][][] {{{0,0},{0,0}}}, 0)
		, new InputSample(1, new double[][][] {{{0,1},{0,1}}}, 1)
		, new InputSample(2, new double[][][] {{{1,0},{1,0}}}, 1)
		, new InputSample(3, new double[][][] {{{1,1},{1,1}}}, 0)
	};
	public static final InputSample[] xorAdd = new InputSample[] {
		new InputSample(0, new double[][][] {{{0}}}, 0)
		, new InputSample(1, new double[][][] {{{1}}}, 1)
		, new InputSample(2, new double[][][] {{{1}}}, 1)
		, new InputSample(3, new double[][][] {{{2}}}, 0)
	};
	
	public static final double[][][][] input2 = new double[][][][]
	                           { // inputSize = 6
	    {{	{ 1,-1},
	    	{ 1,-1}
	    }}
	    ,
	    {{	{ 1,-1},
	    	{ 1, 1}
	    }}
	    ,
	    {{	{-1,-1},
	    	{ 1, 1}
	    }}
	    
	    ,
	    
	    {{	{-1, 1}, // inversed..
	    	{-1, 1}
	    }}
	    ,
	    {{	{-1, 1},
	    	{-1,-1}
	    }}
	    ,
	    {{	{ 1, 1},
	    	{-1,-1}
	    }}
	                           }
	    
	    ;
	
	public static final double[][][][] weights2 = new double[][][][]
	                                	                           {
	    {{	{ .5,-.5},
	    	{ .5,-.5}
	    }}
	    ,
	    {{	{-.5,-.5},
	    	{ .5, .5}
	    }}
	    
	                           }
	    
	    ;	
	/*
	 *  gives rise to accumulations:
	 *  neuron 0:
	 *  2, 1, 0, -2, -1, 0
	 *  neuron 1:
	 *  0, 1, 2, 0, -1 ,-2
	 */
	
	public static final double[][][][] input3 = new double[][][][]
	                                                             { // inputSize = 6
		{{	{ 1,-1,-1},
			{ 1,-1,-1},
			{ 1,-1,-1}
		}}
		,
		{{	{ 1,-1,-1},
			{ 1, 1,-1},
			{ 1, 1, 1}
		}}
		,
		{{	{-1,-1,-1},
			{ 1, 1, 1},
			{ 1, 1, 1}
		}}
		
		,
		
		{{	{-1, 1, 1}, // inversed
			{-1, 1, 1},
			{-1, 1, 1}
		}}
		,
		{{	{-1, 1, 1},
			{-1,-1, 1},
			{-1,-1,-1}
		}}
		,
		{{	{ 1, 1, 1},
			{-1,-1,-1},
			{-1,-1,-1}
		}}
	                                                             }
	
	;
	
	/*
	 *  gives rise to:
	 *  accumulations	=>	tanh	=> 	pooled
	 *  neuron 0:
	 *  2, 0	.96, .0	=>	.96
	 *  2, 0	.96, .0
	 *  
	 *  1, 1	.76, .76=>	.76
	 *  0, 1	.0 , .76
	 *  
	 *  0, 0	.0, .0	=>	0
	 *  0, 0	.0, .0
	 *  
	 *  and inversed...
	 *  
	 *  neuron 1:
	 *  0, 0	0, 0	=>	0
	 *  0, 0	0, 0
	 *  
	 *  1, 1	.76,.76	=>	.76
	 *  0, 1 	0,	.76
	 *  	
	 *  2, 2	.96, .96=>	.96
	 *  0, 0 	.0, .0
	 *  
	 *  and inversed..
	 *  
	 *  ===============
	 *  
	 *  means : = 0
	 *  var = 1/6 * 2*(.76^2 + .96^2) = .503
	 *  covar = 1/6 * 2*(.76^2) = .1933
	 *  
	 *  det = var^2 - covar^2 = .2157
	 */
	public static final double[][][][] input4 = new double[][][][]
	                                                             { // inputSize = 6
		{{	{ 1, 1,-1,-1},
			{ 1, 1,-1,-1},
			{ 1, 1,-1,-1},
			{ 1, 1,-1,-1}
		}}
		,
		{{	{ 1,-1,-1,-1},
			{ 1, 1,-1,-1},
			{ 1, 1, 1,-1},
			{ 1, 1, 1, 1}
		}}
		,
		{{	{-1,-1,-1,-1},
			{-1,-1,-1,-1},
			{ 1, 1, 1, 1},
			{ 1, 1, 1, 1}
		}}
		
		,
		
		{{	{-1,-1, 1, 1},
			{-1,-1, 1, 1},
			{-1,-1, 1, 1},
			{-1,-1, 1, 1}
		}}
		,
		{{	{-1, 1, 1, 1},
			{-1,-1, 1, 1},
			{-1,-1,-1, 1},
			{-1,-1,-1,-1}
		}}
		,
		{{	{ 1, 1, 1, 1},
			{ 1, 1, 1, 1},
			{-1,-1,-1,-1},
			{-1,-1,-1,-1}
		}}
	                                                             }
	
	;
	
	/*
	 *  gives rise to:
	 *  tanh(1) = .76
	 *  tanh(2) = .96
	 *  
	 *  accumulations	=> 	pooled
	 *  neuron 0:
	 *  0, 2, 0		=>	.96		0?
	 *  0, 2, 0			.96?	0?
	 *  0, 2, 0		
	 *  
	 *  1, 1, 0		.76, .76?
	 *  0, 1, 1		.0?, .76?
	 *  0, 0, 1		
	 *  
	 *  0, 0	.0, .0	=>	0
	 *  0, 0	.0, .0
	 *  
	 *  and inversed...
	 *  
	 *  neuron 1:
	 *  0, 0	0, 0	=>	0
	 *  0, 0	0, 0
	 *  
	 *  1, 1	.76,.76	=>	.76
	 *  0, 1 	0,	.76
	 *  	
	 *  2, 2	.96, .96=>	.96
	 *  0, 0 	.0, .0
	 *  
	 *  and inversed..
	 *  
	 *  ===============
	 *  
	 *  means : = 0
	 *  var = 1/6 * 2*(.76^2 + .96^2) = .503
	 *  covar = 1/6 * 2*(.76^2) = .1933
	 *  
	 *  det = var^2 - covar^2 = .2157
	 */
}
