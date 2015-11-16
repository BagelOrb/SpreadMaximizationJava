package network.main;

import generics.Tuple;
import io.Folder;
import io.UniqueFile;
import io.Images.InputSample;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import pooling.PoolingFunction;
import util.basics.DoubleArray3Dext.Loc;
import util.basics.StringUtilsTK;
import util.math.DifferentiableFunction;
import data.DataSet;
import data.DatasetInRAM;
import data.MNIST;
import data.SyntheticDataset2;
import network.Network;
import network.NetworkIO;
import network.NetworkState;
import network.Static;
import network.Static.DataGeneration;
import network.Static.Objective;
import network.Static.Pooling;
import network.analysis.Debug;
import network.main.MainHelperFunctions.NetworkProcessor;

public class MainHelperFunctions {

	public interface NetworkProcessor {

		void process(File folder);
		
		void finallyDo();

	}
//	public NetworkProcessor kurtosisMeasurer 
	
//	public static void main(String[] args) {
////		mainKurtosis(args);
////		mainKurtosisCollect(args);
//	}
	
	
	
//	static File base = new File("C:/Users/TK/Documents/Thesis/Results");
//	static File base = new File("C:/Users/TK/Documents/Thesis/ThesisSimple/imageOutput");
	static File base = new File("D:/Thesis test results/thesisSimple/imageOutput/UB72");
//	static File base = new File("D:/Thesis test results/thesisSimple/imageOutput/bestU72/layer1_0/continued0/continued1");
	
	
	public static void main(String[] args) {
		
//		mainKurtosis(args);
		
		
//		base = new File("D:/Thesis test results/thesisSimple/imageOutput/bestU72/layer1_0/continued0/continued10");
//		base = new File("C:/Users/TK/Documents/Thesis/Results/U 7 7/smNetwork5/layer1_0_continued0");
		
		
		base = new File(args[0]);
		
		
//		measureKurtosisAndCreateOutputCsv(base+"");
//		processAllNetworks(base, measurePreSigmoidStats());
		
		
		
		
//		
//		boolean preSigmoid = Boolean.parseBoolean(args[1]);
//		
//		NetworkProcessor q = measureStats(preSigmoid);
//		q.process(base);
		
		
		
		
		
		
//		if (Debug.useWindowQuitExperiment) JFrameExperiment.run();
//		base = new File("D:/Thesis test results/thesisSimple/imageOutput/bestU72/layer1_0/continued0/continued10");
//		Main.continueFinalization(base+"", "0");
//		if (Debug.useWindowQuitExperiment) JFrameExperiment.frame.dispose();
//		System.exit(0);
		
//		mainPerformanceCollect(args);

		
		
		
		
//		deleteEmptyFolders(args);
//		NetworkProcessor proc = collectEval();

		Main.patternUsed = 3;  Main.additionalOptionRemarks += " syntheticPattern=3";
		
		boolean evaluate = Boolean.parseBoolean(args[1]);
		NetworkProcessor proc = collectResultsCSV(evaluate, Static.DataGeneration.SYNTHETIC2, true);

		
//		NetworkProcessor proc = getUnfinishedsAndCreateBatchFiles();
		
		
		
		processAllNetworks(new File(args[0]), proc);
		proc.finallyDo();
		
		
		
//		appendBatchFiles(args);
	}
	
	
	private static void appendBatchFiles(String[] args) {
		File[] files = new File(args[0]).listFiles();
		if (files== null)
			Debug.err("cannot find directory!");
		for (File f : files)
		{
			if (FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("bat"))
			{
				try {
					FileUtils.writeStringToFile(f, "\r\necho \""+f.getName()+"\" >> finisheds.txt", true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	private static NetworkProcessor getUnfinishedsAndCreateBatchFiles() {
		return new NetworkProcessor() {
			
			ArrayList<String> batchCommands = new ArrayList<String>(); 
			ArrayList<String> resultCodes = new ArrayList<String>(); 
			
			
			@Override
			public void process(File folder) {
				if ( ! folder.getName().startsWith("backup"))
					return;
				
				if ( new File(folder.getParent() +"/output.txt").exists())
					return;
				
				int backupNr = StringUtilsTK.extractDouble(folder.getName()).intValue();
				
				if (new File(folder.getParent()+"/backup"+(backupNr+1)).exists())
					return;
				
				
				try {
					
					
					
					int lastIteration = 0;
					String[] lines = StringUtilsTK.lines(FileUtils.readFileToString(new File(folder+"/output.txt")));
					
					for (String line : lines)
						if (line.startsWith("Intermediate output at itration "))
							lastIteration = StringUtilsTK.extractDouble(line).intValue();

					
					String jar;
					if (folder.getPath().toLowerCase().contains("whole") 
							|| folder.getPath().toLowerCase().contains("unlocked")
							|| folder.getParentFile().getName().startsWith("leNet1")
							|| folder.getPath().toLowerCase().contains("noprelearning"))
						jar = "continueLearningWholeNetwork.jar";
					else // if (folder.getPath().toLowerCase().contains("layerclass"))
						jar = "continueLearning.jar";
					
					String cmd = "java -jar "+jar+ " \""+folder+"\" "+lastIteration;
					
					System.out.println(cmd);
					
					batchCommands.add(cmd);
					
					resultCodes.add(getResult(folder, null, false, false)+"");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void finallyDo() {
				try {
					Folder batchFolder = Folder.newUniqueFolder(base +"/generatedBatFiles");
					FileUtils.writeStringToFile(new File(batchFolder+"/batchCommands.txt"), StringUtils.join(batchCommands, "\r\n"));
					for (String cmd : batchCommands)
						FileUtils.writeStringToFile(UniqueFile.newUniqueFile(batchFolder, "", ".bat"), cmd);
					
					System.out.println(StringUtils.join(resultCodes, "\r\n"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}


	enum Method { EED, PSGU, CHA, PCAE, none  }
	enum Structure { Simple, LeNet }
	enum UnsupPoolSize { S77, S72, S54 }
	enum LearningPhase { PRE, PRE_POST, POST }
	
	
	public static NetworkProcessor collectResultsCSV(final boolean evaluate, DataGeneration data, final boolean training) {
		final DataSet dataset;
		if (data == DataGeneration.SYNTHETIC2)
			dataset = new SyntheticDataset2(training, -1, false, 10, Main.patternUsed);
		else if (data == DataGeneration.MNIST)
			dataset = new DatasetInRAM(new MNIST(training, -1, false));
		else
			dataset = null;
		
		return new NetworkProcessor() {
			
			public ArrayList<Result> results = new ArrayList<Result>(160); 
			
			

			
			@Override
			public void process(File folder) {
				try {
					if (folder.getName().contains("backup"))
						return;
					
					if ( ! (folder.getPath().contains("layerClass") 
							|| folder.getName().startsWith("leNet1")
							|| folder.getPath().contains("continuedWHOLE")
							|| folder.getName().startsWith("classificationNetwork")
							|| folder.getName().startsWith("noPreLearning")))
						return;
					
					System.out.println("Processing "+folder);
					
					Result result = getResult(folder, dataset, evaluate, training);
					results.add(result);
					
					System.out.println(result);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			


			@Override
			public void finallyDo() {
				try {
					FileUtils.writeStringToFile(UniqueFile.newUniqueFile(base, "evalsCSV", ".csv"), "sep=,\r\n"+StringUtils.join(results, "\r\n"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
	}
	static class Result {
		Method method;
		Pooling pool;
		double weightDecay;
		Structure structure;
		UnsupPoolSize unsupPoolSize;
		LearningPhase learningPhase;
		
		Double percentage;
		
		File location;
		
		public String toString() {
			return method+", "+pool+", "+weightDecay+", "+structure+", "+unsupPoolSize+", "+learningPhase+", "+percentage+", "+location;
		}
	}
	private static LearningPhase getLearningPhase(String path) {
		if (path.toLowerCase().contains("unlocked"))
			return LearningPhase.POST;
		if (path.toLowerCase().contains("whole"))
			return LearningPhase.PRE_POST;
		else 
			return LearningPhase.PRE;
	}

	private static UnsupPoolSize getUnsupPoolsize(String path, Structure structure) {
		if (structure == Structure.LeNet)
			return UnsupPoolSize.S54;
		if (path.toLowerCase().contains("77") || path.toLowerCase().contains("7 7"))
			return UnsupPoolSize.S77;
		if (path.toLowerCase().contains("72") || path.toLowerCase().contains("7 2"))
			return UnsupPoolSize.S72;
		return null;
	}

	private static Structure getStructure(String path) {
		if (path.toLowerCase().contains("lenet"))
			return Structure.LeNet;
		else
			return Structure.Simple;
	}

	private static Method getMethod(String path) {
		String pathLower = path.toLowerCase();
		if (pathLower.contains("cae"))
			return Method.PCAE;
		if (pathLower.contains("eed"))
			return Method.EED;
		if (pathLower.contains("psg"))
			return Method.PSGU;
		if (pathLower.contains("classification") 
				|| pathLower.contains("noprelearning") 
				|| pathLower.contains("pure"))
			return Method.none;
		if (path.contains("U") || path.contains("smNetwork") || path.toLowerCase().contains("chasm"))
			return Method.CHA;
		
		return null;
	}

	private static double getEval(File evalFile) throws IOException {
		String evalString = FileUtils.readFileToString(evalFile);
		String[] lines = StringUtilsTK.lines(evalString);
		String line = lines[1];
		Double percentage = StringUtilsTK.extractDouble(line);
		if (percentage == null)
		{
			Debug.warn("Cannot parse percentage!");
			return -1;
		}
		return percentage;
	}

	private static File getEvalFile(File folder, Network network, DataSet dataset, boolean evaluate, boolean training) {
		for (File f : folder.listFiles())
		{
			if (f.getName().startsWith("eval"))
				return f;
		}

		if (evaluate)
		{
			Main.evaluateNetwork(network, training, folder+"", dataset);
			
			for (File f : folder.listFiles())
			{
				if (f.getName().startsWith("eval "+((training)? "train": "test")))
					return f;
			}
		}
		return null;
	}
	protected static Result getResult(File folder, DataSet dataset, boolean evaluate, boolean training) throws IOException {
		Network network = null;
		try {
			network = NetworkIO.recover(new Folder(folder));
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
		}	
		
		File evalFile = getEvalFile(folder, network, dataset, evaluate, training);
		
		Double percentage = -1.;
		if (evalFile == null)
		{
			Debug.out("Cannot find eval file!");
		}
		else 
			percentage = getEval(evalFile);
	
		Result result = new Result();
		result.method = getMethod(folder.getPath());
		result.pool = network.getFirstLayer().params.pooling;
		result.weightDecay = network.netParams.weightDecay;
		result.structure = getStructure(folder.getPath());
		result.unsupPoolSize = getUnsupPoolsize(folder.getPath(), result.structure);
		result.learningPhase = getLearningPhase(folder.getPath());

		result.percentage = percentage;
		
		result.location = folder;
		
		return result;
	}


	public static NetworkProcessor saveConfig() {
		return new NetworkProcessor() {
			
			@Override
			public void process(File folder) {
				try {
					Network network = NetworkIO.recover(new Folder(folder));
					String config = "";
					config += "Weight decay: "+network.netParams.weightDecay+"\r\n";
					config += "Pooling: "+network.getFirstLayer().params.pooling +"\r\n";
					config += "Pool size: "+network.getFirstLayer().params.widthPoolingField +"\r\n";
					FileUtils.writeStringToFile(new File(folder+"/config.txt"), config);
				} catch (SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| IOException e) {
					e.printStackTrace();
				} 
			}
			
			@Override
			public void finallyDo() {
			}
		};
	}
	
	private static NetworkProcessor collectEval() {
	return new NetworkProcessor() {
		
		String evals = "";
		
		@Override
		public void process(File subFolder) {
			for (File file : subFolder.listFiles())
			{
				if (file.getName().startsWith("eval"))
				{
					try {
						evals += file.getPath() + "\r\n"+ FileUtils.readFileToString(file) +"\r\n";
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		@Override
		public void finallyDo() {
			try {
				FileUtils.writeStringToFile(new File(base+"/evals.txt"), evals);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}

	public static NetworkProcessor createAllEvals() {
		return new NetworkProcessor() {
			
			@Override
			public void process(File folder) {
				if (folder.getName().contains("backup"))
					return;
				
				if ( ! folder.getPath().contains("layerClass"))
					return;
				
				for (File file : folder.listFiles())
					if (file.getName().startsWith("eval"))
						return;
				
				Main.evaluateNetworkOnMNIST(""+folder, "false");
				
			}
			
			@Override
			public void finallyDo() {
			}
		};
	}
	
	public static void mainKurtosis(String[] args) {
		
		Stack<File[]> dfs = new Stack<File[]>();
		dfs.add(base.listFiles());
		
		while ( ! dfs.isEmpty())
		{
			File[] subFiles = dfs.pop();
			for (File subFolder : subFiles)
			{
				if ( ! subFolder.isDirectory())
					continue;
				
				dfs.add(subFolder.listFiles());
				
				if (new File(subFolder+"/output.txt").exists())
				{
					if (new File(subFolder+"/kurtosis.txt").exists())
						continue;
					if (subFolder.getName().contains("layerClass"))
						continue;
					if (subFolder.getName().contains("backup"))
						continue;
					System.out.println("processing "+subFolder);
					try{
						
//						measureKurtosis(subFolder+"");
						
//						measurePreSigmoidStats(subFolder+"");
						
					} catch(Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	public static void deleteEmptyFolders(String[] args) {
		
		Stack<File[]> dfs = new Stack<File[]>();
		dfs.add(base.listFiles());
		
		while ( ! dfs.isEmpty())
		{
			File[] subFiles = dfs.pop();
			if (subFiles == null)
				continue;
			
			for (File subFolder : subFiles)
			{
				if ( ! subFolder.isDirectory())
					continue;
				
				try {
					subFolder.delete();
				} catch (Exception e) {};
					
				dfs.add(subFolder.listFiles());
				
			}
		}
	}
	
	
	public static void processAllNetworks(File base, NetworkProcessor nwProcessor) {
		MainHelperFunctions.base = base;
		
		Stack<File[]> dfs = new Stack<File[]>();
		dfs.add(base.listFiles());
		if (dfs.peek() == null)
			Debug.err("path does not denote a directory!");
		
		if (base.isDirectory())
			if (new File(base+"/output.txt").exists())
			{
	//					System.out.println("processing "+subFolder);
				try{
					nwProcessor.process(base);
				} catch(Exception e) { e.printStackTrace(); }
			}
		
		
		while ( ! dfs.isEmpty())
		{
			File[] subFiles = dfs.pop();
			for (File subFolder : subFiles)
			{
				if ( ! subFolder.isDirectory())
					continue;
				
				dfs.add(subFolder.listFiles());
				
				if (new File(subFolder+"/output.txt").exists())
				{
//					System.out.println("processing "+subFolder);
					try{
						nwProcessor.process(subFolder);
					} catch(Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	public static void mainPerformanceCollect(String[] args) {
		
		processAllNetworks(new File(args[0]), getPerformance());
		
		try {
			FileUtils.writeStringToFile(new File(args[0]+"/allEvals.txt"), performancesString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static String performancesString = "";
	
	public static NetworkProcessor getPerformance() {
		return new NetworkProcessor() {
			
			@Override
			public void process(File subFolder) {
				if ( ! subFolder.getName().contains("layerClass"))
					return;
				
				Folder fromFolder = new Folder(subFolder);
				
//				Network network = null;
//				try {
//					network = NetworkIO.recover(fromFolder);
//				} catch (SecurityException | InstantiationException
//						| IllegalAccessException | IllegalArgumentException
//						| InvocationTargetException | NoSuchMethodException
//						| IOException e) {
//					e.printStackTrace();
//					System.exit(0);
//				}
				
				File evalFile = new File(fromFolder+"/eval0.txt");
				if (evalFile.exists())
				{
					performancesString += fromFolder.getPath() +"\r\n";
					System.out.println(fromFolder.getPath());
					try {
						String eval = FileUtils.readFileToString(evalFile);
						System.out.println(FileUtils.readFileToString(evalFile));
						performancesString += eval +"\r\n";
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					performancesString += fromFolder.getPath() +" [ NO EVAL ] \r\n";
			}

			@Override
			public void finallyDo() {
				// TODO Auto-generated method stub
				
			}
		};
		
		
	}

	
	
	
	
	
	
	
	public static void mainKurtosisCollect(String[] args) {
		
		processAllNetworks(base, getKurtosis());
		
		try {
			FileUtils.writeStringToFile(new File(base+"/allKurtoses.txt"), kurtosesString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static String kurtosesString = "";
	
	public static NetworkProcessor getKurtosis() {
		return new NetworkProcessor() {
			
			@Override
			public void process(File subFolder) {
				if (subFolder.getName().contains("layerClass"))
					return;
				
				Folder fromFolder = new Folder(subFolder);
				
//				Network network = null;
//				try {
//					network = NetworkIO.recover(fromFolder);
//				} catch (SecurityException | InstantiationException
//						| IllegalAccessException | IllegalArgumentException
//						| InvocationTargetException | NoSuchMethodException
//						| IOException e) {
//					e.printStackTrace();
//					System.exit(0);
//				}
				
				File evalFile = new File(fromFolder+"/kurtosis.txt");
				if (evalFile.exists())
				{
					kurtosesString += fromFolder.getPath() +", ";
					System.out.println(fromFolder.getPath());
					try {
						String kurt = FileUtils.readFileToString(evalFile);
						System.out.println(FileUtils.readFileToString(evalFile));
						kurtosesString += kurt +"\r\n";
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else System.out.println(subFolder+"\r\n doesn't contain kurtosis.txt file!");
			}

			@Override
			public void finallyDo() {
				// TODO Auto-generated method stub
				
			}
		};
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	static class Stats {
		
		Kurtosis kurt;
		Variance var;
		
		public Stats() {
			kurt = new Kurtosis();
			var = new Variance();
		}
		
		public String toString() {
			return "kurtosis: "+kurt.getResult() + ";\t var: "+var.getResult();
		}

		public void increment(double d) {
			kurt.increment(d);
			var.increment(d);
		}
	}
	
	
	
	public static NetworkProcessor measureStats(final boolean preSigmoid) {
		return new NetworkProcessor() {
			
			@Override
			public void process(File subFolder) {
				
				
//				if (new File(subFolder+"/kurtosis.txt").exists())
//					return;
				if (subFolder.getName().contains("layerClass"))
					return;
				if (subFolder.getName().contains("backup"))
					return;
				System.out.println("processing "+subFolder);

				
				Folder fromFolder = new Folder(subFolder);
				
				
				int nSamples = 10000;
				
				Network network = null;
				try {
					network = NetworkIO.recover(fromFolder);
				} catch (SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				
				network.netParams.objective = Objective.LINEAR_FUNCTION;
				
				if (preSigmoid)
					network.getLastLayer().params.signallingFunction = DifferentiableFunction.linear;
				else 
					network.getLastLayer().params.signallingFunction = DifferentiableFunction.tanh;
				
				
				DataSet dataset;
				switch (network.netParams.dataGeneration) {
				case MNIST:
					dataset = new MNIST(true, nSamples, false);
					break;
				default:
					Debug.err("dataset not supported yet: "+network.netParams.dataGeneration);
					dataset = null;
				}
				
				
				int nf = network.getLastLayer().params.nFeatures;
				
				Stats[] kurts = new Stats[nf];
				for (int f = 0 ; f< nf; f++)
					kurts[f] = new Stats();
				
				
				
				int counter = 0;
				for (InputSample in : dataset)
				{
					NetworkState state = new NetworkState(network, in);
					network.signal(state);
					InputSample out = state.getLast().outputMaps;
					
					
					int t = in.cat;
					
					for (int x = 0 ; x< out.width; x++)
						for (int y = 0; y<out.height; y++)
							for (int z = 0; z<out.depth; z++)
								kurts[z].increment(out.get(x,y,z));
					
//				if ((counter+1) % Math.max(1, dataset.getLimit()/10) == 0)
//					Debug.out(((counter+1) *100 / dataset.getLimit())+"%");
					counter++;
				}
				
				String[] kurtsStr = new String[nf];
				for (int f = 0 ; f< nf; f++)
					kurtsStr[f] = ""+kurts[f];
				
				String kurtosisString = StringUtils.join(kurtsStr, "\r\n");
				
				try {
					FileUtils.writeStringToFile(new File(fromFolder+"/stats"+((preSigmoid)? "Pre" : "Post" )+".txt"), kurtosisString);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finallyDo() {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	
	
	
	
	public static void measureKurtosis(String folder) {
		Folder fromFolder = new Folder(folder);
		
		
		int nSamples = 1000;
		
		Network network = null;
			try {
				network = NetworkIO.recover(fromFolder);
			} catch (SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		
			network.netParams.objective = Objective.LINEAR_FUNCTION;
			
			DataSet dataset;
			switch (network.netParams.dataGeneration) {
			case MNIST:
				dataset = new MNIST(true, nSamples, false);
				break;
			default:
				Debug.err("dataset not supported yet: "+network.netParams.dataGeneration);
				dataset = null;
			}
			
			
			int nf = network.getLastLayer().params.nFeatures;
			
			Kurtosis[] kurts = new Kurtosis[nf];
			for (int f = 0 ; f< nf; f++)
				kurts[f] = new Kurtosis();
			
			
			
			int counter = 0;
			for (InputSample in : dataset)
			{
				NetworkState state = new NetworkState(network, in);
				network.signal(state);
				InputSample out = state.getLast().outputMaps;
				
				
				int t = in.cat;
				
				for (int x = 0 ; x< out.width; x++)
					for (int y = 0; y<out.height; y++)
						for (int z = 0; z<out.depth; z++)
							kurts[z].increment(out.get(x,y,z));
				
//				if ((counter+1) % Math.max(1, dataset.getLimit()/10) == 0)
//					Debug.out(((counter+1) *100 / dataset.getLimit())+"%");
				counter++;
			}
			
			String[] kurtsStr = new String[nf];
			for (int f = 0 ; f< nf; f++)
				kurtsStr[f] = ""+kurts[f].getResult();
			
			String kurtosisString = StringUtils.join(kurtsStr, ", ");
			
			try {
				FileUtils.writeStringToFile(new File(fromFolder+"/kurtosis.txt"), kurtosisString);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public static void measureKurtosisAndCreateOutputCsv(String... args) {
		Folder fromFolder = new Folder(args[0]);
		
		
		int nSamples = 2000;
		
		Network network = null;
		try {
			network = NetworkIO.recover(fromFolder);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
//		network.netParams.objective = Objective.LINEAR_FUNCTION;
		
		DataSet dataset;
		switch (network.netParams.dataGeneration) {
		case MNIST:
			dataset = new MNIST(false, nSamples, false);
			break;
		default:
			Debug.err("dataset not supported yet: "+network.netParams.dataGeneration);
			dataset = null;
		}
		
		
//		network.getLastLayer().params.signallingFunction = DifferentiableFunction.linear;
		
		int nf = network.getLastLayer().params.nFeatures;
		
		
		Kurtosis[] kurts = new Kurtosis[nf];
		for (int f = 0 ; f< nf; f++)
			kurts[f] = new Kurtosis();
		
		
		StringBuilder outputsCsv = new StringBuilder();
		outputsCsv.append("CAT");
		for (int f = 0 ; f< nf; f++)
			outputsCsv.append(", F"+f);
		outputsCsv.append("\r\n");
		
		int counter = 0;
		for (InputSample in : dataset)
		{
			NetworkState state = new NetworkState(network, in);
			network.signal(state);
			InputSample out = state.getLast().outputMaps;
			
			
			int t = in.cat;
			
			for (int x = 0 ; x< out.width; x++)
				for (int y = 0; y<out.height; y++)
				{
					outputsCsv.append(t);
					for (int z = 0; z<out.depth; z++)
					{
						kurts[z].increment(out.get(x,y,z));
						outputsCsv.append(", "+out.get(x,y,z));
						Debug.breakPoint(z==0);
					}
					outputsCsv.append("\r\n");
					
				}
			
//				if ((counter+1) % Math.max(1, dataset.getLimit()/10) == 0)
//					Debug.out(((counter+1) *100 / dataset.getLimit())+"%");
			counter++;
		}
		
		String[] kurtsStr = new String[nf];
		for (int f = 0 ; f< nf; f++)
			kurtsStr[f] = ""+kurts[f].getResult();
		
		
		
		try {
			FileUtils.writeStringToFile(new File(fromFolder+"/outputs.csv"), outputsCsv.toString());
			FileUtils.writeStringToFile(new File(fromFolder+"/kurtosis.txt"), StringUtils.join(kurtsStr, ", "));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
//	public static void main(String[] args) {
////		File base = new File("C:/Users/TK/Documents/Thesis/Results/CAE 7 2/caeNetwork3");
//
//		processAllNetworks(base, createOutputCsv());
//	}
	
	
	public static NetworkProcessor createOutputCsv() {
		return new NetworkProcessor() {
			
			@Override
			public void process(File subFolder) {
				Folder fromFolder = new Folder(subFolder);
				
				if (subFolder.getName().contains("layerClass"))
					return;
				
				System.out.println("processing "+subFolder.getParentFile().getName() +"/"+subFolder.getName());
				
				int nSamples = 1000;
				
				Network network = null;
				try {
					network = NetworkIO.recover(fromFolder);
				} catch (SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				
				network.netParams.objective = Objective.LINEAR_FUNCTION;
				
				DataSet dataset;
				switch (network.netParams.dataGeneration) {
				case MNIST:
					dataset = new MNIST(false, nSamples, false);
					break;
				default:
					Debug.err("dataset not supported yet: "+network.netParams.dataGeneration);
					dataset = null;
				}
				
				
				int nf = network.getLastLayer().params.nFeatures;
				
				
				StringBuilder outputsCsv = new StringBuilder();
				outputsCsv.append("CAT");
				for (int f = 0 ; f< nf; f++)
					outputsCsv.append(", F"+f);
				outputsCsv.append("\r\n");
				
				int counter = 0;
				for (InputSample in : dataset)
				{
					NetworkState state = new NetworkState(network, in);
					network.signal(state);
					InputSample out = state.getLast().outputMaps;
					
					
					int t = in.cat;
					
					for (int x = 0 ; x< out.width; x++)
						for (int y = 0; y<out.height; y++)
						{
							outputsCsv.append(t);
							for (int z = 0; z<out.depth; z++)
							{
								outputsCsv.append(", "+out.get(x,y,z));
							}
							outputsCsv.append("\r\n");
							
						}
					
				if ((counter+1) % Math.max(1, dataset.getLimit()/5) == 0)
					Debug.out(((counter+1) *100 / dataset.getLimit())+"%");
					counter++;
				}
				
				
				
				try {
					FileUtils.writeStringToFile(new File(fromFolder+"/outputs.csv"), outputsCsv.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void finallyDo() {
				// TODO Auto-generated method stub
				
			}
		};
	}
}
