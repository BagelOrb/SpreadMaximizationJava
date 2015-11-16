package probeersels;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import network.analysis.Debug;

public class ExecutorTry {

	static int nThreads = 6;
	static int[] q = new int[nThreads];
	public static void main(String[] args) {
		ExecutorService tp = Executors.newFixedThreadPool(nThreads);
		
		Future<?>[] fs = new Future<?>[nThreads]; 
		for (int i = 0 ; i<nThreads; i++) {
			final int i2= i;
			fs[i] = tp.submit(new Runnable(){

				@Override
				public void run() {
					for (int j = 0; j<1234567891; j++)
						ExecutorTry.simpleMethod(i2);
				}});
		}
		try {
			for (Future<?> f : fs)
				f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
        Debug.out(Arrays.toString(q));
		q=new int[nThreads];
        Debug.out(Arrays.toString(q));
		
		tp.shutdown();
		
	}
	public static void simpleMethod(int i) {
		q[i]++;
	}
	
	public static class MyThread extends Thread {
		final int n ;
		public MyThread(Runnable r, int i) {
			super(r);
			n = i;
		}
	}
}
