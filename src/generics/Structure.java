package generics;

import java.util.Iterator;

import util.basics.OutOfBoundsException;

public interface Structure<Type, Loc>  extends Iterable<Loc>{

	public abstract void set(Loc l, Type d) throws OutOfBoundsException;
	public abstract Type get(Loc l) throws OutOfBoundsException;
	@Override
	public abstract Iterator<Loc> iterator();
}
