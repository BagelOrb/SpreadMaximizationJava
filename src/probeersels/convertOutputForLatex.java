package probeersels;

import io.UniqueFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


public class convertOutputForLatex {

	static File baseOutput = new File("C:\\Users\\TK\\Documents\\Java\\ThesisSimple\\WeightImages\\CAE SP");
//	static File baseOutput = new File("C:\\Users\\TK\\Documents\\Java\\ThesisSimple\\WeightImagesColor");
//	static File baseOutput = new File("C:\\Users\\TK\\Documents\\Thesis\\Thesis Text\\Thesis Chapters\\");
	
	public static void generateTexs(String[] args) {

		
		
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\U 7 7");
//		File toFolder = new File("Experimentation/Images/U_7_7");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\U 7 2");
//		File toFolder = new File("Experimentation/Images/U_7_2");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\CAE 7 7");
//		File toFolder = new File("Experimentation/Images/CAE_7_7");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\CAE 7 2");
//		File toFolder = new File("Experimentation/Images/CAE_7_2");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\EED72");
//		File toFolder = new File("Experimentation/Images/EED72");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\EED77");
//		File toFolder = new File("Experimentation/Images/EED77");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\PSG72");
//		File toFolder = new File("Experimentation/Images/PSG72");
//		File folder = new File("C:\\Users\\TK\\Documents\\Thesis\\Results\\PSG77");
		
		File folder = new File("D:\\Thesis test results\\from uni\\EED72");
		File toFolder = new File("Experimentation/Images/EED77");
		
		boolean gray = true;
		generateTexs(gray, folder, toFolder, 2);
	}
	
	public static void main(String[] args) {
//		baseOutput = new File("C:/Users/TK/Documents/TAI/Thesis/Thesis Chapters/Experimentation/WeightImages");
//		baseOutput = new File("C:\\Users\\TK\\Documents\\Java\\ThesisSimple\\WeightImagesNoPre");
		
		
		generateTexs(true, new File("D:/Thesis test results/thesisSimple/imageOutput/CAE77sp/caeNetwork1"), new File("ff"), 20);
		
//		generateTexsAndBat();
	}
	
	
	public static void generateTexsAndBat() {
		
		mainGenerateAllImages();
		
		StringBuilder bat = new StringBuilder();
		
		for (File file : baseOutput.listFiles())
		{
			if (file.isDirectory())
				continue;
			
//			if (! file.getName().contains("First"))
//				continue;
			
			if ( ! FilenameUtils.getExtension(file+"").equals("tex"))
				continue;
			
			
			
			bat.append("pdflatex \""+FilenameUtils.getBaseName(file+"")+"\"\r\n");
			
		}
		
		try {
			FileUtils.writeStringToFile(new File(baseOutput+"/pdfLatexAll.bat"), bat.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void mainGenerateAllImages() {
		boolean gray = true;
//		File resultsDir = new File("D:/Thesis test results/thesisSimple/imageOutput/noPreLearning");
		File resultsDir = new File("C:\\Users\\TK\\Documents\\Java\\ThesisSimple\\imageOutput\\Synthetic_test\\SP");
//		File resultsDir = new File("D:\\Thesis test results\\from uni\\ff");
		for (File dir : resultsDir.listFiles())
		{
			if ( ! dir.isDirectory())
				continue;
			
			File folder = dir;
			File toFolder = new File("Images");
			
			generateTexs(gray, folder, toFolder, 2);
		}
	}
		
	public static void generateTexs(boolean gray, File folder, File toFolder, int nFeatures) {
		
		System.out.println("processing "+folder);
		
		String type = (gray)? "Gray" : "Color";
		
		StringBuilder texImagesAll = new StringBuilder();
		StringBuilder texImagesFirst = new StringBuilder();
		
//		texInclude.append("\\begin{figure}[ht]\r\n\\begin{center}\r\n");
		String code = StringUtils.repeat('c', nFeatures);
		
		texImagesAll.append("\\documentclass{standalone}\r\n\\usepackage{graphicx}\r\n\r\n\\begin{document}");
		texImagesAll.append("\\newcommand*{\\w}{.5cm}\r\n\\setlength\\arraycolsep{2pt}\r\n");
		texImagesAll.append("$\r\n\\begin{array}{l "+code+"}\r\n");
		texImagesFirst.append("\\documentclass{standalone}\r\n\\usepackage{graphicx}\r\n\r\n\\begin{document}");
		texImagesFirst.append("\\newcommand*{\\w}{.5cm}\r\n\\setlength\\arraycolsep{2pt}\r\n");
		texImagesFirst.append("$\r\n\\begin{array}{"+code+"}\r\n");
		
		
		int run = 1;
		for (File subFolder : folder.listFiles())
		{
			if (!subFolder.isDirectory())
				continue;
			
			for (File subSubFolder : subFolder.listFiles())
			{
				if (!subSubFolder.isDirectory())
					continue;
				if (subSubFolder.getName().contains("layerClass"))
					continue;
				
				texImagesAll.append(run+" & \\input{");
				if (run==1)
					texImagesFirst.append("\\input{");
				
				StringBuilder imgsTex = new StringBuilder();
//				imgsTex.append("$\r\n\\begin{array}{cccccccccc cccccccccc}\r\n");
				
				
				String newFolder = toFolder+"/"+folder.getName()+"/"+subFolder.getName()+"/"+subSubFolder.getName();
				newFolder = StringUtils.replace(newFolder, " ", "_");
				
				String[] texIncludes = new String[nFeatures];
				boolean first = true;
				for (File img : subSubFolder.listFiles())
				{
					if ( ! FilenameUtils.getExtension(img.getPath()).equalsIgnoreCase("png"))
						continue;
					if (img.getName().contains("gray") ^ gray)
						continue;
					if (img.getName().contains("img"))
						continue;
					if ( ! img.getName().contains("l_0"))
						continue;
					if ( ! img.getName().startsWith("feature"))
						continue;
					
					String num = img.getName().substring(7, 9);
					if (num.endsWith("_"))
						num = num.substring(0, 1);
					int f = Integer.parseInt(num);

					
					
					String newImgFile = newFolder+"/"+type+f+".png";
					try {
						FileUtils.copyFile(img, new File(baseOutput+"/"+newImgFile));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					texIncludes[f] = "\\includegraphics[width=\\w]{"+newImgFile.replace('\\', '/')+"}";
				}
				
				imgsTex.append(StringUtils.join(texIncludes, " &\r\n"));
//				imgsTex.append("\r\n\\end{array}$");
				
				String imgsTexFile = newFolder+"/imgs"+type+".tex";
				try {
					FileUtils.write(new File(baseOutput+"/"+imgsTexFile), imgsTex);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				texImagesAll.append(imgsTexFile.replace('\\', '/') + "}  \\\\  % "+subFolder.getName()+" \r\n"); //   \\vspace{5pt}\r\n");
				if (run==1)
					texImagesFirst.append(imgsTexFile.replace('\\', '/') + "}  \\\\  % "+subFolder.getName()+" \r\n"); //   \\vspace{5pt}\r\n");
				run++;

			}
			
		}
		
//		texInclude.append("\\end{center}\r\n\\caption{Scaled weight images of all runs of PCAEs with convolution field size  $(7\\times 7)$ and pool size $(2\\times 2)$.}\r\n\\end{figure}");
		texImagesAll.append("\r\n\\end{array}$");
		texImagesAll.append("\r\n\r\n\\end{document}");
		texImagesFirst.append("\r\n\\end{array}$");
		texImagesFirst.append("\r\n\r\n\\end{document}");
		
//		System.out.println(texImagesAll);
//		System.out.println(texImagesFirst);
		
		try {
			FileUtils.writeStringToFile(UniqueFile.newUniqueFile(baseOutput, folder.getName()+"",".tex"), texImagesAll.toString());
			FileUtils.writeStringToFile(UniqueFile.newUniqueFile(baseOutput, folder.getName()+"First",".tex"), texImagesFirst.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
