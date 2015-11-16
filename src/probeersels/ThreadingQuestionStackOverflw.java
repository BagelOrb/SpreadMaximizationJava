package probeersels;

import java.util.Arrays;

import network.analysis.Debug;

public class ThreadingQuestionStackOverflw {

	
	public static void main2(String[] args) {
	    double[] largeArray = getMyLargeArray();
	    double result = 0;
	    for (double d : largeArray)
	        result += d;
        Debug.out(result);
	}
	
	public static void main(String[] args) {
	final double[] largeArray = getMyLargeArray();
	int nThreads = 5;
	final double[] intermediateResults = new double[nThreads];

	Thread[] threads = new Thread[nThreads];
	final int nItemsPerThread = largeArray.length/nThreads;
	for (int t = 0; t<nThreads; t++) {
		final int t2 = t;
		threads[t] = new Thread(){
			@Override public void run() {
				for (int d = t2*nItemsPerThread; d<(t2+1)*nItemsPerThread; d++)
					intermediateResults[t2] += largeArray[d];
			}
		};
	}
	for (Thread t : threads)
		t.start();
	for (Thread t : threads)
		try {
			t.join();
		} catch (InterruptedException e) { }
	double result = 0;
	for (double d : intermediateResults)
		result += d;
    Debug.out(result);
		
	}
	

	private static double[] getMyLargeArray() {
		int l = 100000;
		double[] ret = new double[l];
		Arrays.fill(ret, 1);
		return ret;
	}
}
