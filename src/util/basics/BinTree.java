package util.basics;

import java.util.Iterator;
import java.util.Stack;

import network.analysis.Debug;

import org.apache.commons.collections4.iterators.IteratorChain;

public abstract class BinTree<A> implements Iterable<A> { //, Structure<A, BinTree.TreeLoc>{

	public BinTree<A> parent;
	
	private BinTree() { }
//	public BinTree(BinTree<A> r) { root = r; }
	
	public static class Node<A> extends BinTree<A> {
		public BinTree<A> left;
		public BinTree<A> right;
		public Node(BinTree<A> aa, BinTree<A> bb) { left = aa; left.parent = this; right = bb; right.parent = this; }
		@Override
		public String toString() { return "Node<"+left+", "+right+">"; }
		@Override
		public Iterator<A> iterator() {
			IteratorChain<A> its = new IteratorChain<A>();
			its.addIterator(left.iterator());
			its.addIterator(right.iterator());
			return its ;
		}
	}
	public static class Leaf<A> extends BinTree<A> {
		public A item;
		public Leaf(A aa) { item = aa; }
		@Override
		public String toString() { return "Leaf<"+item+">"; }
		@Override
		public Iterator<A> iterator() {
			return new Iterator<A>(){
				boolean done = false;
				@Override public boolean hasNext() { return !done; }
				@Override public A next() { done = true; return item; }
				@Override public void remove() { }};
		}
	}
	
	public static void main(String[] args) {
		BinTree<Double> q = new Node<Double>(new Leaf<Double>(1d), new Leaf<Double>(2d));
        Debug.out(q);
	}
	
	@Override
	public String toString() { return "BinTree<"+">"; }

	
	@SuppressWarnings("serial")
	public static class TreeLoc extends Stack<Boolean>{
	}

//	@Override
//	public void set(TreeLoc l, A d) throws OutOfBoundsException {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public A get(TreeLoc l) throws OutOfBoundsException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Iterator<TreeLoc> iterator() {
//		TreeLoc path = new TreeLoc();
//		BinTree<A> now = this;
//		while (Node.class.isInstance(now)) {
//			path.add(true);
//			now = (BinTree<A>) ((Node<A>) now).a;
//		}
//		final TreeLoc pathToLeftmostLeaf = path;
//		return new Iterator<TreeLoc>() {
//			TreeLoc path = pathToLeftmostLeaf;
//			@Override
//			public boolean hasNext() {
//				return path.search(o);
//			}
//
//			@Override
//			public TreeLoc next() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public void remove() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		};
//	}



}
