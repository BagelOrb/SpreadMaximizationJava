package data;

import io.Images;
import io.Images.InputSample;
import io.UniqueFile;

import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import util.basics.DoubleArray2D;
import util.math.Math2;

public class GenerateData {
	public static Random random;

	
	public static double[][] generateBinaryContrastLine(int width, int height, double angle, double offsetMaxRelative) { // TODO: check offset
		double[][] ret = new double[height][width];
		double offsetMax = Math.sqrt(width*width/4. + height*height/4.);
		double offset = offsetMaxRelative*offsetMax*(random.nextDouble()*2.-1.); 
		for (int x = 0; x<width; x++)
			for (int y = 0; y<height; y++) {
				double dist = (distancePointToLine(x-(width-1d)*.5, y-(height-1d)*.5, angle) - offset);
				if (dist<0) ret[y][x] = -1;
				else ret[y][x] = 1;
			}
		return ret;
	}
	public static double[][] generatePureContrastLine(int width, int height, double angle, double offsetMaxRelative) { // TODO: check offset
		double[][] ret = new double[height][width];
		double offsetMax = Math.sqrt(width*width/4. + height*height/4.);
		double offset = offsetMaxRelative*offsetMax*(random.nextDouble()*2.-1.); 
		for (int x = 0; x<width; x++)
			for (int y = 0; y<height; y++) {
				double dist = (distancePointToLine(x-(width-1d)*.5, y-(height-1d)*.5, angle) - offset);
				if (dist<-.5) ret[y][x] = -1;
				else if (dist>.5) ret[y][x] = 1;
				else ret[y][x] = dist*2;
			}
		return ret;
	}
	public static double[][] generateGradientContrastLine(int width, int height, double angle, double hardness, double offsetMaxRelative) { // TODO: check offset
		double[][] ret = new double[height][width];
		double offsetMax = Math.sqrt(width*width/4. + height*height/4.);
		double offset = offsetMaxRelative*offsetMax*(random.nextDouble()*2.-1.); 
		for (int x = 0; x<width; x++)
			for (int y = 0; y<height; y++)
				ret[y][x] = Math2.tanh((distancePointToLine(x-(width-1d)*.5, y -(height-1d)*.5, angle)- offset) * hardness);
		return ret;
	}
	public static double[][] generateGradientContrastCurve(int width, int height, double angle, double hardness, double offsetMaxRelative) { // TODO: check offset
		double[][] ret = new double[height][width];
		double offsetMax = Math.sqrt(width*width/4. + height*height/4.);
		double offset = offsetMaxRelative*offsetMax*(random.nextDouble()*2.-1.); 
		double squeeze = random.nextDouble()*2; squeeze *= squeeze; // to promote low curvatures more
		int curvature = random.nextInt(2)+2;
		for (int x = 0; x<width; x++)
			for (int y = 0; y<height; y++) {
				double xFromMiddle = x-(width-1d)*.5;
				double yFromMiddle = y-(height-1d)*.5;
				double ptToLine = distancePointToLine(xFromMiddle, yFromMiddle, angle) - offset;
				double ptOnLine = distancePointOnLine(xFromMiddle, yFromMiddle, angle);
				//double ptOnLine = Math.sqrt(xFromMiddle*xFromMiddle + yFromMiddle*yFromMiddle - ptToLine*ptToLine);
				ret[y][x] = Math2.tanh(
						(Math.pow(ptOnLine/offsetMax,curvature)*offsetMax*squeeze 
								+ ptToLine) * hardness);
			}
		return ret;
	}
	public static double[][] generateRandom(int width, int height) {
		double[][] ret = new double[height][width];
		for (int x = 0; x<width; x++)
			for (int y = 0; y<height; y++)
				ret[y][x] = random.nextDouble()*2-1;
		return ret;
	}

	

	public static double distancePointToLine(double x, double y, double angle){
		return(x*Math.sin(angle)+y*Math.cos(angle));
	}
	public static double distancePointOnLine(double x, double y, double angle){
		return(x*Math.cos(angle)-y*Math.sin(angle));
	}
	
	public static void outputGeneratedData() {
//		double hardness = 2;
		for (int i = 0; i<16; i++)
			try {
				InputSample img = new Images.InputSample(new double[][][] {
						generatePureLine(5, 5, 2./(16)*i*Math.PI, .1)  
//						generatePureContrastLine(6, 6, 2./(16)*i*Math.PI, 0)  
//						generateRandomBinaryContrastLine(6, 6, random.nextDouble()*2*Math.PI, 0)  
//						generateRandomGradientContrastCurve(7,7,hardness, .8)  
						});
//				img.rescale(0, -1, 1);
                ImageIO.write(Images.getScaledImage(16, img.toBufferedImages()[0]), "PNG", UniqueFile.newUniqueFile(
                        "imageOutput\\generatedData\\line\\", "try_", ".png"));
//                        "imageOutput\\generatedData\\curve\\", "gradient_hardness"+hardness+"_", ".png"));
			} catch (IOException e) {e.printStackTrace();}
	}

	public static void main(String[] args) {
//		int q = 3;
//        Debug.out(q/2.);
//		System.exit(0);
		
		random = new Random();
		outputGeneratedData();
	}
	public static double[][] generatePureLine(int w, int h, double angle, double offset) {
		double dydx = Math.tan(angle);
		double dxdy = 1/dydx;
		if ( Double.isInfinite(dydx)) dxdy=0;
		
		
		double xoffset = Math.sin(angle)*offset;
		double yoffset = Math.cos(angle)*offset*-1;
		
		double[][] ret = new double[h][w];
		new DoubleArray2D(ret).fill(-1);
		if (Math.abs(dydx)<1) 
			for (int x = 0; x<w; x++) {
				int y = (int)(dydx*(x-w/2)+w/2.+yoffset);
				if (Math2.liesWithin(0, y, h-1))
					ret[y][x] = 1;
			}
		else //if (Math.abs(dydx)<1) 
			for (int y = 0; y<h; y++) { 
				int x = (int)(dxdy*(y-h/2.)+h/2.+xoffset);
				if (Math2.liesWithin(0, x, w-1))
					ret[y][x] = 1;
			}
		
		return ret;
	}
}
