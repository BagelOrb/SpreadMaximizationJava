package mathViz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import network.analysis.Debug;
import util.basics.Arrays2D;
import util.math.DerivingFunction1D;
import util.math.DifferentiableFunction;
import util.math.Math2;
import util.math.MathFunction1D;
import util.math.Statistics;

public class Render2D {

	public int imageWidth;
	public int imageHeight;
	public double xFrom;
	public double xTo;
	public double yFrom;
	public double yTo;
	
	public final boolean debug = false;
	
	public int[][] pixels;
	
	public Render2D(int imW, int imH, double xF, double xT, double yF, double yT) {
		pixels = new int[imH][imW];
		Arrays2D.fill(pixels, Color.WHITE.getRGB());
		this.imageHeight = imH;
		this.imageWidth = imW;
		this.xFrom = xF;
		this.xTo = xT;
		this.yFrom = yF;
		this.yTo = yT;
	}
	
	public static Render2D graphValues(List<double[]> values, int imW, int imH) {
		double max = .25; 
		double min = -.25;
		for (int i = 0 ; i< values.size(); i++)
			for (int v = 0; v < values.get(i).length; v++)
			{
				min = Math.min(values.get(i)[v], min);
				max = Math.max(values.get(i)[v], max);
			}
		
		
		int[]  colors = new int[values.get(0).length];
		for (int i = 0 ; i < colors.length; i++)
			colors[i] = Color.HSBtoRGB(((float) i)/colors.length, 1f, .9f);
		
		Render2D ret = new Render2D(imW, imH, 0, 1, min, max);
		
		
		ret.renderHline(0, Color.BLACK.getRGB(), 1);
		ret.renderHline(.25, Color.GRAY.getRGB(), .5);
		ret.renderHline(-.25, Color.GRAY.getRGB(), .5);
		
		for (int v = 0; v < values.get(0).length; v++)
		{
			double lastX = 0;
			double lastY = min;
			for (int i = 0 ; i< values.size(); i++)
			{
				double x = ((double)i)/(values.size()-1);
				double y= values.get(i)[v];
				ret.renderLine(lastX, lastY, x, y, colors[v]);
				lastX = x;
				lastY = y;
			}
			
		}
		
		return ret;
		
	}
	public static void test_graphValues() {
		Render2D rendered = graphValues(Arrays.asList(new double[][]{{0,3},{2,2.1},{1,3}}), 1000, 500);
		
		try {
			ImageIO.write(rendered.toBufferedImage(), "PNG", new File("testGraph.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public void setPixel(int x, int y, int color, double alpha) {
		if (0<=x && x<imageWidth &&
			0<=y && y<imageHeight)
			pixels[y][x] = mixColors(color, Math.min(Math.max(0, alpha), 1), pixels[y][x]);
	}
	public void renderFunctionSmooth(MathFunction1D f, int color) {
		double lastY = imageHeight-1;
		for (int x = 0; x<imageWidth; x++) {
			double yImg = Math2.limit(0, fieldYtoImgY(f.apply(imgXtoFieldX(x))), imageHeight);
            if (debug) Debug.out("x: "+x+" / " + imageWidth);
			if (Double.isInfinite(yImg)) continue;
//			double p = yImg - Math.floor(yImg);
			forloop:
			for (int y = (int) Math.floor(lastY); y*Math.signum(yImg-lastY)<Math.signum(yImg-lastY)*yImg+1 && y<imageHeight && y>=0; y+=Math.signum(yImg-lastY)) {
				if (yImg==lastY) {
					setPixel(x, y, color, 1);
					break forloop;
				}
				setPixel(x-1, y, color, 1-(y-lastY)/(yImg-lastY));
				setPixel(x, y, color,     (y-lastY)/(yImg-lastY));
                if (debug) Debug.out("y: "+y+" / " + imageHeight);
			}
			lastY = yImg;
		}
	}
	public void renderFunctionPoints(MathFunction1D f, int color) {
		for (int x = 0; x<imageWidth; x++) {
			int yImg = (int) fieldYtoImgY(f.apply(imgXtoFieldX(x)));
			setPixel(x,yImg, color, 1d);
            if (debug) Debug.out("x: "+x+" / " + imageWidth);
		}
	}
	public void renderBlock(double xField, double yField, int pixelRadius, int color, double alpha) {
		int xx = (int) fieldXtoImgX(xField);
		int yy = (int) fieldYtoImgY(yField);
		
		for (int x = xx-pixelRadius; x<xx+pixelRadius; x++)
			for (int y = yy-pixelRadius; y<yy+pixelRadius; y++)
				setPixel(x, y, color, alpha);
	}
	
	public void renderRectangle(double xFrom, double yFrom, double xTo, double yTo, int color, double alpha) {
		int xf = (int) fieldXtoImgX(xFrom);
		int yf = (int) fieldYtoImgY(yFrom);
		int xt = (int) fieldXtoImgX(xTo);
		int yt = (int) fieldYtoImgY(yTo);
		
		if (xf>xt) { int xff = xt; xt=xf; xf = xff; }
		if (yf>yt) { int yff = yt; yt=yf; yf = yff; }
		
		for (int x = xf; x<xt; x++)
			for (int y = yf; y<yt; y++)
				setPixel(x, y, color, alpha);
	}
	
	public void renderVline(double x, int color, double alpha) {
		int imgX = (int) fieldXtoImgX(x);
		for (int y = 0; y<imageHeight; y++)
			setPixel (imgX, y, color,alpha); 
	}
	public void renderHline(double y, int color, double alpha) {
		int imgY = (int) fieldYtoImgY(y);
		for (int x = 0; x<imageWidth; x++)
			setPixel (x, imgY, color,alpha); 
	}
	public void renderLine(double x1, double y1, double x2, double y2, int color, double alpha) {
		int xFrom = (int) fieldXtoImgX(x1);
		int xTo = (int) fieldXtoImgX(x2);
		int yFrom = (int) fieldYtoImgY(y1);
		int yTo = (int) fieldYtoImgY(y2);
		int xF = Math.min(xFrom, xTo);
		int xT = Math.max(xFrom, xTo);
		int yF = Math.min(yFrom, yTo);
		int yT = Math.max(yFrom, yTo);
		
		double slope = ((double) (yT-yF))/((double) (xT-xF));
		double slopeInv = ((double) (xT-xF))/((double) (yT-yF));
		
		int direction = (int) Math.signum((x2-x1)*(y2-y1))*-1;
		if (slope > 1)
			for (int y = 0; y<yT-yF; y++) {
				int xStart = (direction>0)? xF : xT;
//                Debug.out((xF+slopeInv*y*direction)+" , " + (yF+y));
				setPixel ((int) (xStart+slopeInv*y*direction), yF+y, color, alpha);
			}
		else
			for (int x = 0; x<xT-xF; x++) { 
				int yStart = (direction>0)? yF : yT;
				setPixel (xF+x, (int) (yStart+slope*x*direction), color, alpha);
			}
			
	}

	public void renderLine(double x1, double y1, double x2, double y2, int color) {
		renderLine(x1, y1, x2, y2, color, 1);
	}
	
	
	public void renderAxes() {
		renderVline(0, Color.GRAY.brighter().getRGB(), 1);
		renderHline(0, Color.GRAY.brighter().getRGB(), 1);
	}
	
	private int mixColors(int c1, double p, int c2) {
		Color c11 = new Color(c1);
		Color c22 = new Color(c2);
		int r = (int) (c11.getRed() *p + (1-p) * c22.getRed());
		int g = (int) (c11.getGreen() *p + (1-p) * c22.getGreen());
		int b = (int) (c11.getBlue() *p + (1-p) * c22.getBlue());
		return new Color(r,g,b).getRGB();
	}
	
	public double imgYtoFieldY(int y) { return ((double)y)/((double)imageHeight)*(yTo - yFrom) + yFrom; }
	public double imgXtoFieldX(int x) { return ((double)x)/((double)imageWidth)*(xTo - xFrom) + xFrom; }
	public double fieldYtoImgY(double y) { return imageHeight - 1 - (y -yFrom)/(yTo-yFrom)*imageHeight; }
	public double fieldXtoImgX(double x) { return (x -xFrom)/(xTo-xFrom)*imageWidth; }
	
	public BufferedImage toBufferedImage() {
		BufferedImage ret = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x<imageWidth; x++)
			for (int y = 0; y<imageHeight; y++) 
				ret.setRGB(x, y, pixels[y][x]);
		return ret;
	}
	
	public static void mainVarianceEnhancement(String[] args) {
		Render2D ri = new Render2D(1000,500, -5,5,-1,1);
		ri.renderAxes();
		DifferentiableFunction f = DifferentiableFunction.tanh;
		ri.renderFunctionSmooth(f , Color.BLACK.getRGB());
		
		double[] xs = new double[10];
		int c = 0;
		for (double y = -.9; y<=.01; y+=.1) {
			ri.renderHline(y, Color.GREEN.getRGB(), 1);
			xs[c] = .5*Math.log((1.+y)/(1.-y));
//			ri.renderVline(xs[c], Color.RED.getRGB());
			c++;
		}
		
		double mean = Statistics.mean(xs);
		ri.renderHline(mean, Color.GREEN.darker().getRGB(), 1);
		
		
		double[] xs2 = new double[10];
		for (c = 0; c<xs.length; c++) {
//			ri.renderVline(xs[c] + f.applyD(xs[c]) * (xs[c]-mean), Color.BLUE.getRGB()) ;
			xs2[c] = f.apply(xs[c] + f.applyD(xs[c]) * (xs[c]-mean));
			ri.renderHline(xs2[c], Color.BLUE.getRGB(), 1) ;
		}
		ri.renderHline(Statistics.mean(xs2), Color.BLUE.darker().getRGB(), 1);
		
		try {
            ImageIO.write(ri.toBufferedImage(), "PNG", new File("varianceEnhancement.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void mainBetaDistribution(String[] args) {
		Random random = new Random();
		
		Render2D ri = new Render2D(1000,1000, 0,1,0,5);
		ri.renderAxes();
		
		double a,b;

		
//		double[] points = new double[] {.1,.15,.05,.45,.5,.55,.85,.9,.95};
		
//		double[] points = new double[101];
//		for (int i = 0; i<=100; i++)
//			points[i] = i/100d;
		int nPoints = 100000;
		double[] points = new double[nPoints]; 
		for (int i = 0; i<nPoints; i++)
//			points[i] = DifferentiableFunction.logisticSigmoid.apply(random.nextGaussian()*(1.685)); //*(1+Math.E/4)
			points[i] = DifferentiableFunction.logisticSigmoid.apply(random.nextGaussian()*(1.685)); //*(1+Math.E/4)
		
		double mean = Statistics.mean(points);
		double var = Statistics.varianceSample(points);
		
		double term =  mean*(1-mean)/var -1;
		
		a = mean*term;
		b = (1-mean)*term;
		
        Debug.out("a: "+a+", b: "+b);
		
		ri.renderFunctionSmooth(Statistics.betaDistribution(a,b),  Color.black.getRGB());

		
//		for (double p : points)
//			ri.renderVline(p, Color.red.getRGB());
		
		try {
            ImageIO.write(ri.toBufferedImage(), "PNG", new File("betaDistribution_sigmoid(normal_x1.685).png"));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void mainLogisticDistribution(String[] args) {
		
		Render2D ri = new Render2D(2000,1000, 0,1,0,5);
		ri.renderAxes();
		
		double a,b;

		DerivingFunction1D dist = Statistics.logisticDistribution(0, 1.8217); // 1.8217
		
		int nPoints = 1000000;
		double[] points = new double[nPoints]; 
		for (int i = 0; i<nPoints; i++)
			points[i] = DifferentiableFunction.logisticSigmoid.apply(Statistics.sampleFromDistribution(dist));
		
		double mean = Statistics.mean(points);
		double var = Statistics.varianceSample(points);
		
		double term =  mean*(1-mean)/var -1;
		
		a = mean*term;
		b = (1-mean)*term;
		
        Debug.out("a: "+a+", b: "+b);
		
		ri.renderFunctionSmooth(Statistics.betaDistribution(a,b),  Color.black.getRGB());

//		for (double p : points)
//			ri.renderVline(p, Color.red.getRGB());
		
//		ri.renderFunctionSmooth(dist,  Color.black.getRGB());

		ri.write("beta_sigmoid(logisticDistribution)");
	}
	public void write(String fileName) {
		try {
			ImageIO.write(toBufferedImage(), "PNG", new File(fileName+".png"));
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	public static void mainLogistic_vs_normal(String[] args) {
		
		Render2D ri = new Render2D(2000,1000, -3,3,0,.5);
		ri.renderAxes();
		
		final double m,s;
		m=0;
		s=1;
		
		DerivingFunction1D log = Statistics.logisticDistribution(m, s); // 1.8217
		
		ri.renderFunctionSmooth(log,  Color.black.getRGB());

		MathFunction1D sigm = new MathFunction1D(){
			@Override public double apply(double x) {
				return Math.exp(-(x-m)*(x-m)/(2*s*s))/(s*Math.sqrt(2*Math.PI));
			}};
			
		ri.renderFunctionSmooth(sigm ,  Color.black.getRGB());

		ri.write("logistic_vs_normal");
	}
	public void renderAdjustingRaster(double alpha) {
		double hRange = xTo - xFrom;
		double hScale = Math.pow(10, Math.round(Math.log10(hRange)))/10.;
		double xBegin = Math.floor(xFrom/hScale)*hScale;
		for (double x = xBegin; fieldXtoImgX(x)<imageWidth; x+=hScale)
			renderVline(x, Color.black.getRGB(), alpha);
		
		double vRange = yTo- yFrom;
		double vScale = Math.pow(10, Math.round(Math.log10(vRange)))/10.;
		double yBegin = Math.floor(yFrom/vScale);
		for (double y = yBegin; fieldYtoImgY(y)>0; y+=vScale)
			renderHline(y, Color.black.getRGB(), .2);
		
		
	}
}
