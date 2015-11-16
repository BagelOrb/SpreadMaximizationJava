package generics;

import java.util.Iterator;

public class Single<A> implements Multiple, Iterable<A> {

	public A a;
	
	public Single(A aa) { a = aa; }
	
	@Override
	public String toString() { return "("+ a.toString()+")"; }

	@Override
	public Iterator<A> iterator() {
		return new Iterator<A>(){
			boolean done = false;
			@Override public boolean hasNext() {	return !done;	}

			@Override public A next() {	return a;}

			@Override public void remove() {}};
	} 
}
