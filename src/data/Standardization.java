package data;

import io.Images;
import io.Images.InputSample;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import util.basics.DoubleArray1D;
import util.basics.DoubleArray3Dext.Loc;
import util.math.Math2;
import data.CIFAR.Reading;

public class Standardization {

	public static void main(String[] args) {
		try {
			
			CIFAR cifar = new CIFAR(true, -1, false, true);
			
			String path = cifar.getPath()+"\\standardized\\";
			File file = new File(path+"imagesTRANSFORMED.mat");
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			for (int imgTag = 0; imgTag < 10; imgTag++)
			{
				System.out.println("Reading standardized CIFAR image...");

				
			
			
				File out = new File(path + "\\examples\\");
				out.mkdirs();
				
				InputSample img = new InputSample().fromByteArray(stream);

				
				
				System.out.println("Outputting standardized CIFAR image...");
				
				
				{ // output as separate images
					BufferedImage[] bwImgs = img.toBufferedImages();
					for (int i = 0; i < bwImgs.length; i++)
					{
						RenderedImage bigBwImg = Images.getScaledImage(16, bwImgs[i]);
						
						File imgPath = new File(out+"\\img"+img.tag+"_"+cifar.labels[img.cat]+"_inputmap"+i+".png");
						ImageIO.write(bigBwImg, "PNG", imgPath);
						
					}
				}
				
				
				{ // output color image
					img.rescaleToFitRetainZeroTotal();
					BufferedImage clrImg = img.toBufferedImageRBG();
					
					RenderedImage bigImg = Images.getScaledImage(16, clrImg);
					
					File imgPath = new File(out+"\\img"+img.tag+"_"+cifar.labels[img.cat]+"_rgb.png");
					imgPath.mkdirs();
					ImageIO.write(bigImg, "PNG", imgPath);
				}
				
				
			}
			stream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readAllCIFARtransformed() {
		try {
			System.out.println("Reading standardized CIFAR images...");
			
			
			String path = CIFAR.path+"\\standardized\\";
			File file = new File(path+"imagesTRANSFORMED.mat");
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			InputSample[] imageSamples = InputSample.readArray(stream);
			stream.close();
			
			System.out.println("Outputting standardized CIFAR images...");

			
			for (int imgTag = 0; imgTag < 10; imgTag++)
			{
				File out = new File(path + "\\examples\\image"+imgTag+".png");
				out.mkdirs();
				
				InputSample img = imageSamples[imgTag];
				
				{ // output color image
					BufferedImage clrImg = img.toBufferedImageRBG();
					
					RenderedImage bigImg = Images.getScaledImage(16, clrImg);
					
					File imgPath = new File(path+"\\examples\\img"+img.tag+"_"+CIFAR.labels[img.cat]+"_rgb.png");
					imgPath.mkdirs();
					ImageIO.write(bigImg, "PNG", imgPath);
				}
				
				
				{ // output as separate images
					BufferedImage[] bwImgs = img.toBufferedImages();
					for (int i = 0; i < bwImgs.length; i++)
					{
						RenderedImage bigBwImg = Images.getScaledImage(16, bwImgs[i]);
						
						File imgPath = new File(path+"rendered//biological_transformation//img"+img.tag+"_"+CIFAR.labels[img.cat]+"_inputmap"+i+".png");
						ImageIO.write(bigBwImg, "PNG", imgPath);
						
					}
				}


			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void transformCIFAR() {
//		ArrayList<InputSample> allImages = new ArrayList<InputSample>(CIFAR.batchSize*5);
		
//		Reading it = new CIFAR.Reading(true, CIFAR.batchSize*5, true);
//		for (InputSample img : it)
//			allImages.add(img);
		
		boolean rgb = false;
		
		CIFAR cifar = new CIFAR(true, -1, false, false);
		
		double invImgSize = 1./(cifar.imgHeight * cifar.imgWidth);
		
		
		DoubleArray1D means = new DoubleArray1D(cifar.imgDepth);
		Reading it = cifar.new Reading(cifar.batchSize*5);
		for (InputSample img : it)
//		for (InputSample img : allImages)
		{
			DoubleArray1D meansImg = new DoubleArray1D(cifar.imgDepth);
			for (Loc l : img)
				meansImg.add(l.z, img.get(l.x, l.y, l.z));
			
			meansImg.multiply(invImgSize);
			means.add(meansImg);
		}
		
		means.multiply(1./(cifar.batchSize*5));
		
		System.out.println(means);
		
		
		
		DoubleArray1D vars = new DoubleArray1D(cifar.imgDepth);
		it = cifar.new Reading(cifar.batchSize*5);
		for (InputSample img : it)
//		for (InputSample img : allImages)
		{
			DoubleArray1D varsImg = new DoubleArray1D(cifar.imgDepth);
			for (Loc l : img)
				varsImg.add(l.z, Math2.square(img.get(l.x, l.y, l.z) - means.get(l.z)));
			
			varsImg.multiply(invImgSize);
			vars.add(varsImg);
		}
		
		vars.multiply(1./(cifar.batchSize*5));
		
		
		System.out.println(vars);
		

		final File file = new File(cifar.path+"\\standardized\\images"+((rgb)? "RGB": "TRANSFORMED")+".mat");
		try {

		it = cifar.new Reading(cifar.batchSize*5);
		for (InputSample img : it)
		{
			for (int x = 0; x< img.width; x++)
				for (int y = 0; y< img.height; y++)
					for (int z = 0; z< img.depth; z++)
					{
						img.add(x, y, z, -means.get(z));
						img.multiply(x, y, z, 1./Math.sqrt(vars.get(z)));
					}
			FileUtils.writeByteArrayToFile(file, img.toByteArray(), true);  // last arg says whetehr we want to append this data to the file...
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * CIFAR  transformed
	 * means: 	[-0.056972753753661426, 0.0801807440185547, 0.018408912353515626]
	 * vars: 	[0.22727232467716435, 0.06910887145260322, 0.04232739206824332]
	 * 
	 * 
	 * CIFAR RGB
	 * means:	[-0.021041382904052736, -0.039450295257568366, -0.11042658309936525]
	 * vars: 	[0.2421927435026645, 0.23528683774229078, 0.271573669789059]
	 * 
	 */
}
