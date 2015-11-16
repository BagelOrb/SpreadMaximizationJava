package generics;

public class Triple<A,B,C> implements Multiple {

	public A fst;
	public B snd;
	public C thrd;
	
	public Triple(A f, B s, C c) {
		fst = f; snd=s; thrd = c;
	}
	@Override
	public String toString() {
		return "("+fst.toString() + ", "+ snd.toString()+", "+ thrd.toString()+")"; 
	}
}
