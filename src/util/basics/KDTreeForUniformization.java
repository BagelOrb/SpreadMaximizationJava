package util.basics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import network.analysis.Debug;
import util.math.Math2;


public class KDTreeForUniformization {

	int dim;
	Node root;
	
	public double totalError = 0;
	private double mins;
	private double maxs;
	
	private boolean splitBasedOnMeanInsteadOfMedian = true;
	
	private boolean objectiveTimesNinsteadOfDerDividedByN = true; // default: false
	
	private double weighLowerDersLessByFactor = 1; // 1 for no difference in levels
	
	public String toString() { return ""+root;}
	
	public static class Point {
		double[] coords;
		
		public double[] derivatives;
		
		boolean goingLeft;
		
		
		public Point(double[] ds) {
			derivatives = new double[ds.length];
			coords = new double[ds.length];
			for (int i = 0; i<ds.length; i++) 
				coords[i] = ds[i];
		}
		
		public String toString() {
			return Arrays.toString(coords);
		}
	}
	


	public class Node {
		
		final int depth;
		
		double[] minima;
		double[] maxima;
		
		Node parent;
		
		double splittingValue;
		double middleOptimalSplit;
		int splittingDimension;
		/**
		 * One for each dimension
		 */
		List<LinkedList<Point>> orderedLists;
		
		Node lt;
		Node ge;
		
		public String toString() {
			if (orderedLists.get(0).size() == 1 )
				return Arrays.toString(orderedLists.get(0).toArray());
			return "splitting on: "+splittingValue+" in dimension "+splittingDimension +"\r\nLT="+StringUtilsTK.indent(""+lt)+"\r\nGE="+StringUtilsTK.indent(""+ge);
		}
		

		public Node(Node parent, double[] minima2, double[] maxima2, int depth) {
			this.depth = depth;
			minima = minima2;
			maxima = maxima2;
			this.parent = parent;
			orderedLists = new ArrayList<LinkedList<Point>>(dim);
			for (int d = 0; d<dim; d++)
				orderedLists.add(new LinkedList<Point>());
		}
		public void buildTree() {
//			if (orderedLists.get(0).size() == 1  ) return;
			if (orderedLists.get(0).size() == 1  ) { // add derivatives toward actual middle of cell for leafs
				for (Point p : orderedLists.get(0))
					for (int d = 0; d<dim; d++) {
						double der = (maxima[d]-minima[d])/2 + minima[d] - p.coords[d]; // = middle - coord
						double depthFactor = Math2.pow(weighLowerDersLessByFactor, depth);
						p.derivatives[d] += der * depthFactor;
						totalError += .5*der*der * depthFactor;
					}
					
				return;
			}
			
			double mostGroundCovered = setSplittingParams();
			if (mostGroundCovered == 0) return;
			split();
			lt.buildTree();
			ge.buildTree();
		}
		
		
		private void split() {
			double[] maximaLt = Arrays.copyOf(maxima, maxima.length);
			maximaLt[splittingDimension] = splittingValue;
			double[] minimaGe = Arrays.copyOf(minima, minima.length);
			minimaGe[splittingDimension] = splittingValue;

			lt = new Node(this, minima, maximaLt, depth+1);
			ge = new Node(this, minimaGe, maxima, depth+1);

			

			LinkedList<Point> splitDimList = orderedLists.get(splittingDimension);
			int size = splitDimList.size();
			int leftSize = size/2;
			Debug.breakPoint(leftSize ==0);
			for (int p = 0; p < leftSize; p++)
				splitDimList.get(p).goingLeft = true;
			for (int p = leftSize; p < size; p++)
				splitDimList.get(p).goingLeft = false;
			
			double mean = 0;
			for (Point p : orderedLists.get(0)) {
				mean += p.coords[splittingDimension];
			}
			mean /= size;

			double objectiveMod = 1;
			if (objectiveTimesNinsteadOfDerDividedByN)
				objectiveMod = size;
			
			double depthFactor = Math2.pow(weighLowerDersLessByFactor, depth);

			double der;
			if (splitBasedOnMeanInsteadOfMedian) {
				
				double min = minima[splittingDimension];
				double max = maxima[splittingDimension];
	
				double middle = (max-min)/2+min;
				der = (middle - mean) / size * objectiveMod * depthFactor;
				totalError += .5* (middle - mean) * (middle - mean) * objectiveMod * depthFactor;
			} else {
				der = (middleOptimalSplit - splittingValue)/size * objectiveMod *depthFactor;
				totalError += (middleOptimalSplit - splittingValue) * mean * objectiveMod * depthFactor;
			}

			for (Point p : orderedLists.get(0)) {
				p.derivatives[splittingDimension] += der;// * Math2.pow(.5, depth); 
			}
			for(int d = 0; d<dim; d++) {
				LinkedList<Point> orderedList = orderedLists.get(d);
				for (Point p : orderedList) {
					if (p.goingLeft) 
						lt.orderedLists.get(d).add(p);
					else 
						ge.orderedLists.get(d).add(p);
				}
			}
		}
		
		
		private double setSplittingParams() {
			double bestSplitGoodness = -1;
//			int bestDimension = -1;
//			
//			for (int d = 0; d<dim; d++) {
//				if (splitGoodness(d) > bestSplitGoodness) {
//					bestSplitGoodness = splitGoodness(d);
//					bestDimension = d;
//				}
//			}
//			splittingDimension = bestDimension;
			if (parent==null)
				splittingDimension = 0;
			else splittingDimension = (parent.splittingDimension+1)%dim;
			
			LinkedList<Point> splitDimList = orderedLists.get(splittingDimension);
			int size =  splitDimList.size();
			Debug.breakPoint(size==0);

			double min = minima[splittingDimension];
			double max = maxima[splittingDimension];
			middleOptimalSplit = ((double) (size/2))/size * (max-min) + min;
			
			
			double midHigh = splitDimList.get(size/2  ).coords[splittingDimension];
			double midLow = splitDimList.get(size/2 -1).coords[splittingDimension];
//			Debug.breakPoint(mid1<.33);
//            if (depth==0) Debug.out(mid1);
			
//			if (mid2< middleOptimalSplit && middleOptimalSplit<mid1)
//				splittingValue = middleOptimalSplit;
//			else splittingValue = (mid1+mid2)/2d;
			
			if (midHigh < middleOptimalSplit)
				splittingValue = midHigh;
			else if (middleOptimalSplit < midLow)
				splittingValue = midLow;
			else splittingValue = middleOptimalSplit;
			
			return bestSplitGoodness;
		}
		@SuppressWarnings("unused")
		private double splitGoodnessByLargestDifference(int d) {
			LinkedList<Point> orderedList = orderedLists.get(d);
			double l = orderedList.getLast().coords[d] ;
			double f = orderedList.getFirst().coords[d]; 
			return l-f;
		}
		@SuppressWarnings("unused")
		private double splitGoodnessByLargestDerivatives(int d) {
			LinkedList<Point> orderedList = orderedLists.get(d);
			double l = orderedList.getLast().coords[d] ;
			double f = orderedList.getFirst().coords[d]; 

			LinkedList<Point> bestList = orderedLists.get(splittingDimension);
			int size =  bestList.size();
			double mid1 = bestList.get(size/2  ).coords[splittingDimension];
			double mid2 = bestList.get(size/2 -1).coords[splittingDimension];
			double splittingValue = (mid1+mid2)/2d;

			double min = minima[splittingDimension];
			double max = maxima[splittingDimension];
			double middleOptimalSplit = ((double) size/2.)/size * (max-min) + min;
			double direction = middleOptimalSplit - splittingValue;

			Debug.err("this code is incorrect. this measure should measure the squared distance between optimal and actual splitting value");
			return Double.POSITIVE_INFINITY; // direction*direction;
		}
	}
	
	public KDTreeForUniformization(int dimensionality, double mins2, double maxs2) {
		dim = dimensionality;
		totalError = 0;
		this.mins = mins2;
		this.maxs = maxs2;
	}
	public void constructFrom(List<Point> points) {
		List<LinkedList<Point>> orderedLists = new ArrayList<LinkedList<Point>>(dim);
		for (int d = 0; d<dim; d++) {
			final int dimNow = d;
			Collections.sort(points, new Comparator<Point>(){
				@Override public int compare(Point o1, Point o2) {
					return Double.compare(o1.coords[dimNow], o2.coords[dimNow]);
				}});
			LinkedList<Point> sorteds = new LinkedList<Point>();
			sorteds.addAll(points);
			orderedLists.add(sorteds);
		}
//		for (int d = 0; d<dim; d++) 
//			for (Point p : orderedLists.get(d))
//				p.placesInOrderedLists.add(p); // d
		
		double[] minima = new double[dim];
		Arrays.fill(minima, mins);
		double[] maxima = new double[dim];
		Arrays.fill(maxima, maxs);
		root = new Node(null, minima, maxima, 0);
		root.orderedLists = orderedLists;
		
		root.buildTree();
		
	}
	public KDTreeForUniformization(List<Point> points, int dimensionality) {
		dim = dimensionality;
		
		constructFrom(points);
		
//		root.buildTree();
			
	}
}
