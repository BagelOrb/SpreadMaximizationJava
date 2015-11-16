package network.analysis;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class Debug {

	public static boolean checkDerivatives 		= false;

	public static boolean dontDocument 			= false;
	public static boolean documentOutputs 		= false;

	public static final boolean debug			 = true;
	public static int debugLevel = 2; // TODO: use at more places!
	
	
	public static final double significantChange = 0.1;
	
	public static boolean noUpdate = false;


	
	
	public static boolean fakeThreading = false; // no real threads.. everything is executed in the main thread... otherwise the same!
	public static final boolean threadingDebug = false;
	public static final boolean threading = true;

	
	
	
	
	
	
	
	public static final boolean showNeuronOutputs = false; // output of the neurons...
	public static final boolean showDerivatives = false;
	public static final boolean nanChecking = true;
	public static final boolean nullChecking = true;
	public static final boolean checkHeap = false;
	public static final boolean readWrite = false;
	
	public static final boolean documentAllInputs = false;
	public static final boolean documentNetworkJavaFilesOnly = false;
	public static boolean documentWeightImages = true;
	public static boolean documentProgram = false;

	

	public static final boolean removeThis = true;

	
	
	
	public static final boolean addLayerAfterFinished = false;

	
	public static final boolean trainingPhaseOnly = false;
	
	
	public static final boolean checkInputDerivatives 	= false;
	public static final int checkDerivativesPeriodically= 5;
	
	public static final boolean higherRpropForQuickness = true;
	
	public static final boolean outputRandomPoolAndItsBestInput = false;
	
	public static final int outputSize = 1; // -1 is random between 1 and 4
	
	public static final boolean useWindowQuitExperiment = true;
	
    public static final boolean backpropByLayer = true;
	
	public static final boolean checkOutOfRangePanning = true;
	

	
	public static void ensureThisIsTheMainThread() {
		check(Thread.currentThread().getName().equals("main"), " This code is not being executed by the MAIN thread! instead: "+Thread.currentThread());
	}
	
	public static void out(Object... strings) {
		if (debug)
			try { throw new Exception();
			} catch (Exception e) {
				String out = "("+e.getStackTrace()[1].getFileName()+":"+e.getStackTrace()[1].getLineNumber()+") "+ StringUtils.join(strings, ", ");
				log(out);
                System.out.println(out);
			}
	}
	
	
	
	public static void exit(String...strings) {
		if (debug)
			try {
				throw new Exception("EXIT. "+StringUtils.join(strings));
			} catch (Exception e) {
				e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
				e.printStackTrace();
				System.exit(0);
			}
	}

	@SuppressWarnings("unused")
	public static void warn(String string) {
		if (debug)
			if (false)
				out(string);
			else
				try {
					throw new Exception(">>> WARNING! : "+string);
				} catch (Exception e) {
					e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
					e.printStackTrace();
//					System.exit(0);
				}
	}

	public static void err(Object... strings) {
		if (debug)
			try {
				throw new Exception(StringUtils.join(strings, ", "));
			} catch (Exception e) { 
				e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
				e.printStackTrace();
//				System.exit(0);
	}		}



	public static void checkNaN(double der, String...strings) {
		if (nanChecking)	{
			try {
				if (Double.isInfinite(der))	
					throw new Exception("NaNcheck failed! : "+der+"!"+StringUtils.join(strings));
				if (Double.isNaN(der)) 
					throw new Exception("NaNcheck failed! : NaN!"+StringUtils.join(strings));
			} catch (Exception e) {
				e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
				e.printStackTrace();
//				System.exit(0);
			}
		}
			
	}

	public static void breakPoint(boolean b, String...strings) {
		if (b)
			System.out.print(""+StringUtils.join(strings));
	}

	public static void checkNull(Object i, String...strings) {
		if (debug && nullChecking && i==null)
			try {
				throw new Exception("something is NULL! NullPointerException:"+StringUtils.join(strings));
			} catch (Exception e) { 
				e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
				e.printStackTrace();
//				System.exit(0);
			}
	}

	public static void checkArrayIndexBounds(Object[] data, int n, String...strings) {
		if (debug && (n<0 || n>=data.length))
			err("my ArrayIndexOutOfBoundsException: "+n+StringUtils.join(strings));
	}

	/**
	 * @param b should be true
	 * @param strings error info
	 */
	public static void check(boolean b, String...strings ) {
		if (debug && !b)
			try {
				throw new Exception("check failed! "+StringUtils.join(strings));
			} catch (Exception e) { 
				e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 1, e.getStackTrace().length));
				e.printStackTrace();
//				System.exit(0);
			}
	}

	public static void out() {
		System.out.println();
	}

	
	public static StringBuilder log = new StringBuilder();
	public static void log(String out) {
		log.append(out+"\r\n");
	}
}
