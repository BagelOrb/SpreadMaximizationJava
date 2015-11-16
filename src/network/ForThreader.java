package network;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import network.analysis.Debug;


/**
 * Threading a for-loop: splitting the iterations up into a number parts equal to the number of cores on the computer.
 * For for-loops of the form {@code for(int i =0; i<max; i++){...}}.
 * // % The main thread is used for one part of the iterations as well.
 * 
 * After all threads have finished, normal execution resumes on the main thread.
 * 
 * All iterations are assumed to have similar workload.
 * 
 * Make sure the threads can't interact! 
 * 
 * 
 * @author TK
 *
 */
public abstract class ForThreader {
	
	/**
	 * The number of threads used, which is the number of available processors in the current system.
	 */
	public static final int nThreads = Runtime.getRuntime().availableProcessors();
	/**
	 * The thread pool which is used to execute all for loop chunks
	 */
	public static final ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(){ // is this necessary?
			@Override
			public void run() {
				threadPool.shutdown();
			}
			
		});
	}
	
	
	/** Anything needed before we can work with threads. Making sure the threads we are going to use can't interact.
	 * @param nThreads the number of threads that are going to be used
	 */
	public abstract void initializeForThreads(int nThreads);
	
	/** One iteration of the for-loop, by one of the threads.
	 * @param i The iteration to run
	 * @param threadTag The thread from which this iteration is run
	 * @param nThreads The total number of threads
	 */
	public abstract void run(int i, int threadTag, int nThreads);
	
	/** Rounding up the work done by the threads.
	 * @param nThreads The number of threads that were used.
	 */
	public abstract void finalizeForThreads(int nThreads);
	
	
	
	/** Split the for-loop into threads and run them.
	 * Halts untill all threads are finished.
	 * 
	 * @param tot The max of the for-thread.
	 */
	public void delegate(int tot) {
		int nThreadsUsed = nThreads;
		Debug.check(Thread.currentThread().getName().equals("main"), "Creating threads from other place than main!");
		if (Debug.threadingDebug) Debug.out("nThreads available: "+nThreadsUsed);
		if (nThreadsUsed>tot) nThreadsUsed = tot; // then we will produce less threads! (amount threads will then be tot)
		if (Debug.threadingDebug) Debug.out("nThreads used: "+nThreadsUsed);
		
		initializeForThreads(nThreadsUsed);
		
		Runnable[] runners = new Runnable[nThreadsUsed];
		for (int p = 0; p<nThreadsUsed; p++) 
			runners[p] = new ForRunner(p, p*tot/nThreadsUsed, (p+1)*tot/nThreadsUsed, nThreadsUsed);
		

		if (Debug.fakeThreading || !Debug.threading) {
			for (int p = 0; p<nThreadsUsed; p++) 
				runners[p].run();
		}
		else {
			Future<?>[] futures = new Future<?>[nThreadsUsed];
			for (int p = 0; p<nThreadsUsed; p++) 
				try {
					futures[p] = threadPool.submit(runners[p]);
				} catch(RejectedExecutionException e) {
					e.printStackTrace();
					Debug.breakPoint(true);
				}
//			futures[0].run(); // run one thread in main!
//			for (int p = 1; p<nThreads; p++) 
//				futures[p].start();
			
			for (int p = 0; p<nThreadsUsed; p++) 
				try {
					futures[p].get(); // await all computation
				} catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) {
					e.printStackTrace();
				}
		}
		
		finalizeForThreads(nThreadsUsed);
	}
	
	/** The kind of thread used.
	 * 
	 * @author TK
	 */
	private class ForRunner implements Runnable { 
		/**
		 * The lower bound of the range which is assigned to this thread.
		 */
		int from; 
		/**
		 * The higher bound of the range which is assigned to this thread.
		 */
		int to; 
		/**
		 * The number of this thread.
		 */
		int treadTag; 
		/**
		 * The total number of threads.
		 */
		int nThreads;
		public ForRunner(int i, int f, int t, int n) { treadTag = i; 	from=f;		to=t; 	nThreads = n;}
		@Override
		public void run() {
			if (Debug.threadingDebug) Debug.out("nThreads active = "+ java.lang.Thread.activeCount());
			if (Debug.threadingDebug) Debug.out("thread number = "+ java.lang.Thread.currentThread().getName());
			for (int i =from; i<to; i++) 
				ForThreader.this.run(i, treadTag, nThreads);
		}
	}
}