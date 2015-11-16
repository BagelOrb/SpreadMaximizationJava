package generics;

public class Tuple<A,B> implements Multiple {

	public A fst;
	public B snd;
	
	public Tuple(A f, B s) {
		fst = f; snd=s;
	}
	@Override
	public String toString() {
		return "("+fst.toString() + ", "+ snd.toString()+")"; 
	}
}
