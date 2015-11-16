package network;

import generics.Tuple;
import io.Folder;
import io.Images;
import io.Images.InputSample;
import io.JsonArrays;
import io.UniqueFile;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import layer.CnnDoubleLayer;
import layer.CnnDoubleLayerState;
import network.LayerParameters.NetworkParameters;
import network.analysis.Debug;
import network.main.JFrameExperiment;
import network.main.Main;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import util.basics.DoubleArrays2D;
import util.math.Math2;
import data.CIFAR;
import data.DataSet;
import data.MNIST;
import data.NORB;
import data.OutputData;

public class NetworkIO {

	public static Random random;
	
	public static void document(Network network) throws IOException {
		document(network, path);
	}
	@SuppressWarnings("unused")
	public static void document(Network network, Folder path) throws IOException {
		Network.output("outputting to: "+path+ "...");
		
		Network.output("Signaling 100 samples...");
		network.dataCounter = 0;
		NetworkState[] states = network.signalBatch(Math.min(100, network.netParams.nSamples));
		network.objective.computeCommonDerivativeStuff(states);
		double finalObjectiveValue = network.objective.getValue(states); 
		Network.output("... done!");
		
		
		
		if (!Debug.dontDocument) {
			path.mkdirs();
			
			FileUtils.write(new File(path+"\\callArgs.txt"), StringUtils.join(Main.callArgs, " ")+"\r\nmain function = "+Main.mainFunction+"\r\nAdditional info:\r\n"+Main.additionalOptionRemarks);
			
			FileUtils.write(new File(path+"\\objectiveValue="+finalObjectiveValue+".csv"), ""+network.valuesOfAllObjectives);
			try {
				FileUtils.write(new File(path+"\\objectiveValuesOverTime.csv"), StringUtils.join(network.objectives, "\r\n"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (JFrameExperiment.frame != null)
				ImageIO.write(JFrameExperiment.frame.renderPanel.image, "PNG", new File(path+"\\movingMeans.png"));
			
			documentValuesOverTime(network);
			
			
			{ Debug.out("documenting console output");
	            NetworkIO.documentOutput(path);
	            FileUtils.write(new File(path+"\\log.txt"), Debug.log);
			}

			if (Debug.documentWeightImages) {
                Debug.out("creating weight images");
				NetworkIO.outputWeightImages(network, path, "", Static.ColoringType.TYPE_COLOR_BEYOND_LIMITS);
//				NetworkIO.outputWeightImages(network, path, "gray", Static.ColoringType.TYPE_RESCALE_TO_FIT_GRAY);
			}

			if (Debug.documentOutputs)
			{
				Debug.out("creating outputs CSV"); NetworkIO.documentOutputsCSV(network, path, states);
				Debug.out("creating outputs mat");NetworkIO.documentOutputsMatrix(network, path, states);
			}
		
            if (Debug.documentProgram) { Debug.out("copying java files"); NetworkIO.documentProgram(new Folder(path+"\\program\\"));}
            Debug.out("documenting total network state");NetworkIO.documentState(network, path);
            Debug.out("documenting network parameters");network.netParams.writeToFile(new File(path+"\\networkParameters.json")); // documentParameters(path);
		}
//		if (network.netParams.nItersTrainInputs>0) {
//            Debug.out("document random input (sample frame, etc.)");
//			NetworkIO.documentInput(network, path, states); // does backprop to input, but doesn't write to file when !(Debug.document)
//		}
		if (!Debug.dontDocument) {
			if (network.netParams.dataGeneration == Static.DataGeneration.GRADIENT_CURVE && Debug.documentAllInputs)
				NetworkIO.documentAllInputs(network, path);
            Debug.out("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \r\nDocumented to: "+StringUtils.replaceChars(path+"", '\\', '/')+ "\r\n=============================================================");
		}
		if (network.significantChange) 
			Debug.out("\r\n"+
				"\\\\\\\\\\\\\\\\\\\\\\\\|||||||||||||||||||////////////\r\n" +
				">>>>>>>>>>> SignificantChange! <<<<<<<<<<<  Derivatives are significantly different from estimated derivatives! \r\n" +
				"////////////|||||||||||||||||||\\\\\\\\\\\\\\\\\\\\\\\\" 
				);
		else if (Debug.checkDerivatives || Debug.checkInputDerivatives)
			Debug.out("\r\n"+ ">>>>> Analysis OK! <<<<< \r\n" );
	}


	private static void documentValuesOverTime(Network network) throws IOException {
		StringBuilder str = new StringBuilder();
		for (double[] vals : network.valuesOverTime)
			str.append(StringUtils.join(vals, ";")+"\r\n");
		
		FileUtils.write(new File(path+"\\movingMeans.csv"), str.toString());
	}
	
	static void documentOutput(Folder path2) throws IOException {
		File out = new File(path2+"\\output.txt");
		FileUtils.writeStringToFile(out, Network.output);
	
	}

	static void documentInput(Network network, Folder path2, NetworkState[] states) throws IOException {
		LayerParameters params = network.getFirstLayer().params;
		
		
		String out = path2+"\\Z_sampleInput\\";
		if (! Debug.dontDocument) new File(out).mkdirs();
		int nPics = (Debug.debug)? 2 : 20; // TODO: add '10' to parameters?
		for (int i = 0; i<nPics; i++) { 
			// 1) random original sample 
			// 2) random subsampling frame 
			// 3) best activating inputs of that frame
			// 4) backpropagate to input
			
			// 1)
//			InputSample input = network.dataGenerator.generateData(params, Network.random.nextInt(network.imageSamples.length));
			CnnDoubleLayerState randomState = states[random.nextInt(states.length)].getFirst();
			InputSample input = randomState.inputMaps;
			
			if (Debug.outputRandomPoolAndItsBestInput) {
				InputSample img = input.clone();
				BufferedImage[] imgs = img.toBufferedImages();
				
				
				for (int fi = 0; fi<params.nInputFeatures; fi++) { 
					if (Debug.useWindowQuitExperiment && !JFrameExperiment.keepRunning) return;
                    if (!Debug.dontDocument) ImageIO.write(Images.getScaledImage(16, imgs[fi]), "PNG", new File(out + "sample"+i + "_AA_inputMap"+fi+".png"));
					int xPool = random.nextInt(params.widthPoolingMap(input.width));
					int yPool = random.nextInt(params.heightPoolingMap(input.height));
					InputSample imgPool = new InputSample(new double[][][]{DoubleArrays2D.subArray2D(input.data[fi], 
							xPool*params.widthConvolutionField, (xPool+1)*params.widthConvolutionField+params.widthConvolutionField-2, 
							yPool*params.heightConvolutionField, (yPool+1)*params.heightConvolutionField+params.heightConvolutionField-2)} );
					// TODO: check width of higher layer instead of taking it to be the same size as the local field size of the first layer.
		//					xPool*firstLayerParams.widthPoolingField, (xPool+1)*firstLayerParams.widthPoolingField+firstLayerParams.widthLocalField-2, 
		//					yPool*firstLayerParams.heightPoolingField, (yPool+1)*firstLayerParams.heightPoolingField+firstLayerParams.heightLocalField-2)} );
					imgPool.rescale(0, -1, 1);
					BufferedImage[] poolImgs = imgPool.toBufferedImages();
                    if (!Debug.dontDocument) ImageIO.write(Images.getScaledImage(16, poolImgs [0]), "PNG", new File(out + "sample"+i + "_anyPool.png"));
					
					// 3)
					for (int f = 0; f<params.nFeatures; f++) {
						double[][][] bestInput = network.getFirstLayer().poolingFunction.getBestInput(xPool, yPool, f, randomState);
						InputSample imgInput = new InputSample(bestInput);
						imgInput.rescale(0, -1, 1);
						BufferedImage[] inImgs = imgInput.toBufferedImages();
						if (!Debug.dontDocument) 
                            ImageIO.write(Images.getScaledImage(16, inImgs [0]), "PNG", 
                                new File(out + "sample"+i+"_feat"+f+"_out="+randomState.convolutionMaps.get(xPool,yPool,f) + ".png"));
					}
					
					// TODO: !! check this input feature for-loop! 
					// TODO: remove all weird things where I create new inputSamples with one dummy inputFeature
				}				
			}
			
		}

			
	}


	public static void outputWeightImages(Network network, final Folder path, final String nameAppendix, final Static.ColoringType typeColorBeyondLimits) throws IOException {
		Debug.out("creating layer neuron images");
		
		for (final CnnDoubleLayer layer : network.cnnDoubleLayers)
		{
			new ForThreader() {
				
				@Override
				public void run(int f, int threadTag, int nThreads) {
					InputSample img = new InputSample(layer.weights[f].clone().data);
					img.outputWeightImage(path, "l_"+layer.position+"_"+nameAppendix, Static.ColoringType.TYPE_COLOR_BEYOND_LIMITS, f);
					img.outputWeightImage(path, "l_"+layer.position+"_"+nameAppendix+"_gray", Static.ColoringType.TYPE_RESCALE_TO_FIT_GRAY, f);

					if (layer.params.nInputFeatures <= 3)
					{
						try {
							img.rescaleToFitRetainZeroTotal();
							RenderedImage imgClr = Images.getScaledImage(16, img.toBufferedImageRBG());
							File file = UniqueFile.newUniqueFile(path, "img="+img.tag+"_feature_"+f+"_"+nameAppendix, " cat="+img.cat+".png");
							ImageIO.write(imgClr, "PNG", file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
				@Override
				public void initializeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finalizeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
			}.delegate(layer.params.nFeatures);
		}
	}
	public static void outputWeightImages(Network network, final Folder path) throws IOException {
		Debug.out("creating neuron images at "+path);
		
		for (final CnnDoubleLayer layer : network.cnnDoubleLayers)
		{
			new ForThreader() {
				
				@Override
				public void run(int f, int threadTag, int nThreads) {
					InputSample img = new InputSample(layer.weights[f].clone().data);
					
					{
						BufferedImage[] imgs = img.toBufferedImages();
						for (int iF = 0; iF<img.depth; iF++) {
							RenderedImage rendImg = Images.getScaledImage(16, imgs[iF]);		
							try {
			                    ImageIO.write(rendImg, "PNG", UniqueFile.newUniqueFile(path, "color"+f+"_im"+iF+"_", ".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					}
					{
						InputSample img2 = img.clone();
						img2.rescaleToFitRetainZeroTotal();
						
						BufferedImage[] imgs = img2.toBufferedImages();
						for (int iF = 0; iF<img2.depth; iF++) {
							RenderedImage rendImg = Images.getScaledImage(16, imgs[iF]);		
							try {
								ImageIO.write(rendImg, "PNG", UniqueFile.newUniqueFile(path, "gray"+f+"_im"+iF+"_", ".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}
					}

					if (layer.params.nInputFeatures <= 3)
					{
						try {
							img.rescaleToFitRetainZeroTotal();
							RenderedImage imgClr = Images.getScaledImage(16, img.toBufferedImageRBG());
							File file = UniqueFile.newUniqueFile(path, "red"+f, ".png");
							ImageIO.write(imgClr, "PNG", file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
				@Override
				public void initializeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finalizeForThreads(int nThreads) {
					// TODO Auto-generated method stub
					
				}
			}.delegate(layer.params.nFeatures);
		}
	}

	public static void documentProgram(Folder path2) throws IOException {
		if (Debug.documentNetworkJavaFilesOnly)
			documentProgram(new File("src\\network"), path2, false);
		else
			documentProgram(new File("src\\network"), path2, true);
	}
	static void documentProgram(File from, Folder path2, boolean recursively) throws IOException {
		File[] allJavaFiles = from.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				return (FilenameUtils.getExtension(arg1).equals("java")); 
			}});
		
		for (File e : allJavaFiles) {
			FileUtils.copyFile(e, new File(path2.getPath()+"\\"+e.getName()));
		}
		if (recursively)
			for (File dir : from.listFiles())
				if (dir.isDirectory()) 
					documentProgram(dir, new Folder(path2+"\\"+dir.getName()), recursively);
	}

	static void documentAllInputs(Network network, Folder path2) throws IOException {
		File file = new File(path2+"\\allInputs.json");
		FileWriter writer = new FileWriter(file);
		JsonWriter jsonWriter = Json.createWriter(writer);
		jsonWriter.writeArray(JsonArrays.toJsonArray(network.imageSamples));
		jsonWriter.close();
		
        // output png-image files
		File dir = new File(path2+"\\allInputs\\");
		dir.mkdirs();
		
		// output all images ever used as input (also in backprop to input...)
//		ArrayList<? extends State> states = network.firstLayer.states;
//		for (State s : states) {
//			InputSample img = s.input.clone();
//			img.rescale(-1, 1);
//			BufferedImage[] imgs = img.toBufferedImages();
//			for (int iF = 0; iF<imgs.length; iF++)
//                ImageIO.write(Images.getScaledImage(16, imgs[iF]), "PNG", new File (dir+"\\imageSample "+s.input.tag+" _inFeat "+iF+".png"));
//		}
			
		
		
		// output only normal input images (not backpropagated..)
		for (int i = 0; i<network.imageSamples.length ; i++) {
			InputSample img = network.imageSamples[i].clone();
			img.writePNGfilesTo(new File(dir+"\\imageSample "), true);
		}
		
	}
	
    static void documentOutputsPNG(Network network, Folder path2) throws IOException {
		File dir = new File(path2+"\\allOutputs\\");
		dir.mkdirs();
		
		// output only normal input images (not backpropagated..)
		for (int i = 0; i<network.imageSamples.length ; i++) {
			InputSample img = network.imageSamples[i].clone();
			BufferedImage[] imgs = img.toBufferedImages();
			for (int iF = 0; iF<imgs.length; iF++)
                ImageIO.write(Images.getScaledImage(16, imgs[iF]), "PNG", new File (dir+"\\imageSample "+network.imageSamples[i].tag+" _inFeat "+iF+".png"));
		}
		
	}
	static void documentOutputsJson(Network network, Folder path2, CnnDoubleLayerState[] states) throws IOException {
		new File(path2+"\\allOutputs\\").mkdirs();
		for ( CnnDoubleLayerState state : states) {
			File file = new File(path2+"\\allOutputs\\imgOut"+state.inputMaps.tag+".json");
			FileWriter writer = new FileWriter(file);
			
			JsonWriter jsonWriter = Json.createWriter(writer);
			jsonWriter.writeObject(state.outputMaps.toJsonObject());
			
//			Debug.check(state.inputMaps.tag >= 0);
			
			jsonWriter.close();
		}
	}
	static void documentOutputsMatrix(final Network network, Folder path2, NetworkState[] states) throws IOException {
		new File(path2+"\\allOutputs\\").mkdirs();
		final File file = new File(path2+"\\allOutputs\\outputs.mat");
		for ( NetworkState nwState : states) 
		{
			CnnDoubleLayerState state = nwState.getLast();
			InputSample img = new InputSample(state.inputMaps.tag, state.outputMaps.data, state.inputMaps.cat);
			FileUtils.writeByteArrayToFile(file, img.toByteArray(), true); // last arg says whetehr we want to append this data to the file...
//			Debug.check(state.inputMaps.tag >= 0);
		}
	}
//	public static void main(String[] args) {
//		DoubleArray3D out = new DoubleArray3D(new double[][][]{{{1,2},{3,4}},{{5,6},{7,8}}});
//		DoubleArray3D in = new DoubleArray3D(new double[][][]{{{1,2},{3,4}},{{5,6},{7,9}}});
//		DoubleArray3D sames = out.zipWith(new MathFunction2D(){
//			@Override public double apply(double arg1, double arg2) {
//				return (arg1==arg2)? 0 : 1;
//			}}, in);
//        Debug.out(sames.totalSum());
//		testMatrixIO();
//	}
//	public static void testMatrixIO() {
//		LayerParameters params = ParameterConfigs.testCase();
//		Network network = Main.initializeNetwork(params);
//        Debug.out("Starting signalling");
//		network.signalBatch();
//		try {
//			documentAllOutputsMatrix(network, new Folder("IOtest"), states);
//            Debug.out("Finished documenting outputs");
//			parseAllImagesMatrix(network, new Folder("IOtest//allOutputs"));
//            Debug.out("Finished reading outputs");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		double nDifferences = 0;
//		for (int i = 0; i< network.imageSamples.length/100; i++) {
//            DoubleArray3D out = states[i].poolingMaps;
//			InputSample in = network.imageSamples[i];
//			InputSample sames = out.zipWith(new MathFunction2D(){
//				@Override public double apply(double arg1, double arg2) {
//					return (arg1==arg2)? 0 : 1;
//				}}, in);
//			nDifferences += sames.totalSum();
//            if (nDifferences>0) Debug.out("Problem!");
//		}
//		if (nDifferences!=0)
//			Debug.warn("Not all samples are the same! nDifferences="+nDifferences);
//        else Debug.out("No problem here! :)");
//			
//	}
	static void documentOutputsCSV(final Network network, Folder path2, NetworkState[] states) throws IOException {
		new File(path2+"\\allOutputs\\").mkdirs();
		final File file = new File(path2+"\\allOutputs\\outputs.csv");
		String headers = "cat ";
		for (int d = 0; d<states[0].getLast().outputMaps.depth; d++)
			headers += "; OM"+d;
		
		FileUtils.writeStringToFile(file, headers+ "\r\n", false); // overwrites existing data in file!
		for (NetworkState nwState : states) 
		{
			CnnDoubleLayerState state = nwState.getLast();
			InputSample out = state.outputMaps;
			StringBuilder outData = new StringBuilder();
			for (int x = 0; x<out.width; x++)
				for (int y = 0; y<out.height; y++) {
					outData.append(state.inputMaps.cat);
					for (int d = 0; d<out.depth; d++)
						outData.append("; ").append(out.get(x, y, d));
					outData.append("\r\n");
				}
			FileUtils.writeStringToFile(file, outData.toString(), true);
//			Debug.check(state.inputMaps.tag >= 0);
		}
	}

	static void parseImagesJson(Network network, Folder dir) throws FileNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (network.netParams.dataGeneration == Static.DataGeneration.MNIST ||
				network.netParams.dataGeneration == Static.DataGeneration.NORB) 
			network.initializeData();
		else {
			JsonReader r = Json.createReader(new FileInputStream(dir.getAbsolutePath()+"\\allInputs.json"));
			network.imageSamples = JsonArrays.fromJsonArray1D(InputSample.class, (JsonArray) r.read());
			r.close();
		}
	}
	static void parseImagesMatrix(Network network, Folder dir) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		if (network.netParams.dataGeneration == Static.DataGeneration.MNIST ||
				network.netParams.dataGeneration == Static.DataGeneration.NORB) 
			network.initializeData();
		else {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(dir.getAbsolutePath()+"\\images.mat"));
			network.imageSamples = InputSample.readArray(stream);
			stream.close();
		}
	}

	public static Network recover(Folder dir) throws SecurityException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
			NetworkParameters params = NetworkParameters.readFromFileStatic(new File(dir+"\\networkParameters.json"));
			Network network = parseState(dir, params);
			if (new File(dir.getAbsolutePath()+"\\images.mat").exists())
				NetworkIO.parseImagesMatrix(network, dir);
			else if (new File(dir.getAbsolutePath()+"\\allInputs.json").exists())
				NetworkIO.parseImagesJson(network, dir);
				
	//		network.learnMeans(); // TODO
			return network;
		}

	static void documentState(Network network, Folder path2) throws IOException {
		File file = new File(path2+"\\state.json");
		FileWriter writer = new FileWriter(file);
		JsonWriter jsonWriter = Json.createWriter(writer);
		jsonWriter.writeObject(network.toJsonObject());
		writer.close();
	}

	public static Network parseState(Folder dir, NetworkParameters params) throws SecurityException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
	
		Network net = new Network(params);
		
		JsonReader reader = Json.createReader(new FileInputStream(dir.getAbsolutePath()+"\\state.json"));
		JsonObject ob = (JsonObject) reader.read();
		reader.close();
		try{
			net.fromJsonObject(ob);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.out("For Json object: \r\n"+ob);
		}
		
		return net;
	}

	public static Folder basePath = new Folder("imageOutput");
	public static Folder path;

	
	public static void convertData(Network network, int nSamples, String fileNameAppendix) throws IOException {
		DataSet fromData = getFromData(network);
		convertData(fromData, network, nSamples, fileNameAppendix);
	}
	public static void convertData(Network network, String fileNameAppendix) throws IOException {
		DataSet fromData = getFromData(network);
		convertData(fromData, network, fromData.getDataSetSize(), fileNameAppendix);
		
	}
	private static DataSet getFromData(Network network) {
		switch(network.netParams.dataGeneration) {
		case CIFAR:
			return new CIFAR(true, -1, true, true);
		case NORB:
			return new NORB(true, -1, true);
		case MNIST:
			return new MNIST(true, -1, false);
//		case READ_FROM_MAT_FILE:
		
		default:
			Debug.err("data conversion for "+network.netParams.dataGeneration+" not implemented!");
			return null;
		}
	}
	private static void convertData(DataSet fromData, Network network, int nSamples, String fileNameAppendix) throws IOException {
		Debug.out("Converting data...");
		File out = UniqueFile.newUniqueFile(new File(path+"\\allOutputs"), "convertedData"+fileNameAppendix+"_", ".mat");
		out.getParentFile().mkdirs();
		
		NetworkState someState = new NetworkState(network, fromData.readImage(0));
		short nInts = 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(nInts*Integer.SIZE/8);
		int offset = 0;
		
		int width, height, depth, size;
		size = nSamples; // fromData.getDataSetSize();
		width  = someState.getLast().outputMaps.width;
		height = someState.getLast().outputMaps.height;
		depth  = someState.getLast().outputMaps.depth;
		
		buffer.asIntBuffer().put(new int[]{width, height, depth, size});
		buffer.position(offset += nInts*Short.SIZE/8);

//		byte[] headerBA = new byte[ nShorts * Short.SIZE/8]; 
		
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(out, false));
		outStream.write(buffer.array());
		int i = 0;
		for (InputSample in : fromData)
		{
			NetworkState nwState = new NetworkState(network, in);
			network.signal(nwState);
			nwState.getLast().outputMaps.cat = nwState.getFirst().inputMaps.cat;
			
			outStream.write(nwState.getLast().outputMaps.toByteArrayData());

			
			i++;
			if (i % (fromData.getDataSetSize()/10)==0)
				Debug.out(i*100 / fromData.getDataSetSize()+"%");
			
			if (i >= nSamples)
				break;
		}
		outStream.close();
		
		Debug.out("Data converted!");
	}
	
	private static void checkConvertedData() {
		OutputData data = new OutputData(true, -1, false, 
				new File("final tests\\NORB1\\config0\\network0\\allOutputs\\convertedData2x2_0.mat"));
		try {
			for (int i = 0; i<10; i++)
			{
				File file = new File("crapTry\\MNISTout"+i+"\\img");
				InputSample img = data.readImage(i);
				img.writePNGfilesTo(file, true);
				img.rescaleToFitRetainZeroTotal();
				img.writePNGfilesTo(new File(file+"rescaled"), true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		checkConvertedData();
//	}
}
