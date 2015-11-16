package generics;

public interface Copyable<T extends Copyable<T>> {
	public abstract T copy();
}
