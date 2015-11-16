package util.basics;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListsTK {

	public static <T> Iterable<T> reversed(List<T> list) {
		final ListIterator<T> it = list.listIterator(list.size());
		return new Iterable<T>(){

			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
		
					@Override
					public boolean hasNext() {
						return it.hasPrevious();
					}
		
					@Override
					public T next() {
						return it.previous();
					}
		
					@Override
					public void remove() {
						it.remove();
					}
				};
			}};
	}
	
	
}
