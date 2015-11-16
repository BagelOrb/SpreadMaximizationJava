package probeersels;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class generateBATs {

	static int[] tagsU72 = new int[]{1,2,3,4,11,12,13,14};
	static int[] tagsU77 = new int[]{5,6,7,8,17,18,19,20};
	
	public static void main(String[] args) {
		
		
		for (int i = 0; i<8; i++)
		{
			String baseFileLoc = "imageOutput/U 7 2/smNetwork"+tagsU72[i]+"/layer1_0";
			String bat = "java -jar continueFinalization.jar \""+baseFileLoc+"\" 0 \r\n";
			bat += "java -jar trainSecondLayerClassificationLayer.jar \""+baseFileLoc+"_continued0\" 2 \r\n";
			bat += "java -jar continueLearningWholeNetwork.jar \""+baseFileLoc+"_continued0_layerClass0\" 0 \r\n";
			try {
				FileUtils.writeStringToFile(new File("72U"+i+".bat"), bat);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i<8; i++)
		{
			String baseFileLoc = "imageOutput/U 7 7/smNetwork"+tagsU77[i]+"/layer1_0";
			String bat = "java -jar continueFinalization.jar \""+baseFileLoc+"\" 0 \r\n";
			bat += "java -jar trainSecondLayerClassificationLayer.jar \""+baseFileLoc+"_continued0\" 2 \r\n";
			bat += "java -jar continueLearningWholeNetwork.jar \""+baseFileLoc+"_continued0_layerClass0\" 0 \r\n";
			try {
				FileUtils.writeStringToFile(new File("77U"+i+".bat"), bat);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
