package util.basics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mathViz.Render2D;
import network.analysis.Debug;
import util.math.DifferentiableFunction;
import util.math.Math2;

public class KDTreeViz extends KDTreeForUniformization {

	public KDTreeViz(int dimensionality, int mins, int maxs) {
		super(dimensionality, mins, maxs);
	}
	
	static final double etaRpropPlus = 1.2;
	static final double etaRpropMin = .5;
	
	public static class PPoint extends Point {

		public double[] deltas;
		public double[] input;
		
		public double[] lastDers;
		public PPoint(double[] ds) {
			super(new DoubleArray1D(ds).mapped(DifferentiableFunction.logisticSigmoid).data);
			lastDers = new double[ds.length];
			deltas = new double[ds.length];
			Arrays.fill(deltas, .1);
			input = ds;
		}
		
	}
	
	static boolean randomization = false; 
	public static void main(String[] args) {
		int dim = 2;
		double nPoints = Math2.pow(3, 6); // 3;// Math2.pow(2, 7);
		int outputImageSize = 1000;
		String fileNames = "KDTree_learning_crap2_";
		boolean outputImages = true;
		boolean outputLast10iters = false;
		int renderingDetail = 2 ;
		boolean renderRaster = false;
		boolean RPROP = true;
		
		double delta = 1E-1;

		int iterations = 100;
		int nPics = 20;

		
		Random r = new Random();
		final KDTreeViz tree = new KDTreeViz(dim, 0, 1);
		
		List<Point> ps = new ArrayList<Point>();
		for (int i =0; i<nPoints ; i++) {
			double x,y;
//			x = r.nextDouble()*4-2;
//			y = r.nextDouble()*4-2;
			
			if (i<nPoints/2) {
				x = r.nextGaussian()*.1 +.4;
				y = r.nextGaussian()*.05+.5*x -.4;
			} else {
				x = r.nextGaussian()*.1 -.4;
				y = r.nextGaussian()*.025-.75*x +.4;
			}
			
			
//			double x = r.nextGaussian()*.4;
//			double y = r.nextGaussian()*.4*.5+.5*x;
//			x+=.3;
//			y+=.2;
//			if (i<2) { x = .1; y=.3; }
			ps.add(new PPoint(new double[]{x,y}));
			
		}
		tree.constructFrom(ps);
		Render2D  ri = null;
//        Debug.out(tree);
		if (outputImages) {
			ri = new Render2D(outputImageSize, outputImageSize, 0, 1, 0, 1);
			if (renderRaster) ri.renderAdjustingRaster(.1);
			tree.render(ri, renderingDetail);
			ri.write(fileNames+0);
		}
		

		

		int ouputEvery = Math.max(1, iterations/nPics);
		int picNr = 1;
		for (int i = 0; i<iterations; i++) {
//            Debug.out("iteration: "+i+ " : total error= "+tree.totalError);
            Debug.out(i+";"+tree.totalError);
			tree.rand = new Random(tree.seed);
			
			ps = new ArrayList<Point>();
			for (Point p : tree.root.orderedLists.get(0)) {
				ps.add(p);
				PPoint pp = (PPoint) p;
				for (int d =0; d<dim; d++) {
					if (randomization) p.derivatives[d] *= new Random().nextDouble();
					if (randomization) pp.input[d] += r.nextDouble()*.1-.05;
					if (RPROP) {
						double der = p.derivatives[d] *     p.coords[d]  * (1-p.coords[d] );
						if (pp.lastDers[d]*der > 0) pp.deltas[d] *= etaRpropPlus; 
						else if (pp.lastDers[d]*der < 0) pp.deltas[d] *= etaRpropMin;
						pp.lastDers[d] = der;
						pp.input[d] += pp.deltas[d] * Math.signum(der);
					}
					else pp.input[d] += delta * p.derivatives[d] *     p.coords[d]  * (1-p.coords[d] );
					p.coords[d] = DifferentiableFunction.logisticSigmoid.apply(pp.input[d]);
				}
				Arrays.fill(p.derivatives, 0);
//				p.derivatives = new double[dim];
			}
	
			tree.totalError = 0;
			
			tree.constructFrom(ps);
//            Debug.out(tree);
			if (outputImages) 
				if (i%ouputEvery==0 || (outputLast10iters && i>iterations-10)) {
					ri = new Render2D(outputImageSize, outputImageSize, 0, 1, 0, 1);
					if (renderRaster) ri.renderAdjustingRaster(.1);
					tree.render(ri, renderingDetail);
//					ri.renderVline(1./3, 0, .5);
					ri.write(fileNames+picNr);
					picNr++;
				}
		}
		
//		ri = new Render2D(outputImageSize, outputImageSize, 0, 1, 0, 1);
//		for (Point p : tree.root.orderedLists.get(0)) {
//			double x = p.coords[0];
//			double y = p.coords[1];
//			double x2 = x + p.derivatives[0]/20;
//			double y2 = y + p.derivatives[1]/20;
//			ri.renderLine(x, y, x2, y2, Color.BLUE.getRGB());
//		}
//		ri.write(fileNames+"end");
	}
	int seed = 123;
	Random rand = new Random(seed);
	
	public void render(Render2D image, int detail) {
		if (dim!=2) { Debug.warn("Can't render output data other than 2D!"); return; }
		LinkedList<Point> points = root.orderedLists.get(0);
		if (detail >0)
			for (Point p : points)
				image.renderBlock(p.coords[0], p.coords[1], 2, Color.BLUE.getRGB(), .5);

		if (detail>1 || detail == -1) {
			for (Point p : root.orderedLists.get(0)) {
				double x = p.coords[0];
				double y = p.coords[1];
				double x2 = x + p.derivatives[0]/10;
				double y2 = y + p.derivatives[1]/10;
				image.renderLine(x, y, x2, y2, Color.BLUE.getRGB());
			}
		}
		
		if (detail>2)
			renderNode(image, detail, root);
	}
	
	private void renderNode(Render2D image, int detail, Node n) {
		double xF = n.minima[0]; 
		double yF = n.minima[1]; 
		double xT = n.maxima[0]; 
		double yT = n.maxima[1]; 

		int randomColor = Color.HSBtoRGB(rand.nextFloat(), 1f, .85f);

		if (detail > 4)
			if (n.lt==null || n.ge==null)
				image.renderRectangle(xF, yF, xT, yT, randomColor , .1);
		
		if (n.lt!=null) {
			renderNode(image, detail, n.lt);
		}
		if (n.ge!=null) {
			renderNode(image, detail, n.ge);
		}

		
		if (n.splittingDimension==0) {
			image.renderLine(n.splittingValue, yF, 		n.splittingValue, yT, randomColor);
			image.renderLine(n.middleOptimalSplit, yF, 	n.middleOptimalSplit, yT, randomColor, .2);
		}
		else {
			image.renderLine(xF, n.splittingValue, xT, 		n.splittingValue, randomColor);
			image.renderLine(xF, n.middleOptimalSplit, xT, 	n.middleOptimalSplit, randomColor, .2);
		}
		
	}

}
