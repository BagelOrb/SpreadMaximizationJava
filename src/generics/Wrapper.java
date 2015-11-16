package generics;

public class Wrapper<T> {
	public T t;
	public Wrapper(T tt) { t=tt; }
	
	@Override
	public String toString() { return t.toString(); }
}
