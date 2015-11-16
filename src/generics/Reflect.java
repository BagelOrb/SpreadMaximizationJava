package generics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class Reflect {
	
	// when the enclosing outer class instance is given
	public static <T> T newClassInstance(Class<T> c, Object... parameters) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException  {
//        Debug.out("Class "+c+" isMemberClass?="+c.isMemberClass() );
		
		Constructor<T> ctor;
		
		Class<?>[] parameterClasses = new Class<?>[parameters.length];
		for (int p = 0; p<parameters.length; p++)
			parameterClasses[p] = parameters[p].getClass();
		
		// TODO: this below causes errors: static member classes dont need the parameter, but are member classes
		if (c.isMemberClass() && !Modifier.isStatic(c.getModifiers())) {
			Class<?> outerClass = c.getDeclaringClass();
			if (outerClass.isInstance(parameters[0])) 
				while (!outerClass.equals(parameterClasses[0]))
					parameterClasses[0] = outerClass; // parameterClasses[0].getSuperclass(); 
			else 
				throw new InstantiationException("Given outer class instance doesn't have the correct type! Outer class type: "+ outerClass+ ", given instance type: "+ parameters[0].getClass());
			
		}
			ctor = c.getConstructor(parameterClasses);
			return ctor.newInstance(parameters);
	}
	
	
//	// when the enclosing outer class instance is given
//	@Deprecated public static <T, O> T newClassInstance(Class<T> c, O outerInstance) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException  {
////        Debug.out("Class "+c+" isMemberClass?="+c.isMemberClass() );
//		Constructor<T> ctor;
//		try {
//			ctor = c.getConstructor();
//		} catch (NoSuchMethodException e) {
//			if (c.isMemberClass()) {
//				Class<?> outerClass = c.getDeclaringClass();
//				if (!outerClass.isInstance(outerInstance))
//					throw new InstantiationException("Given outer class instance doesn't have the correct type! Outer class type: "+ outerClass+ ", given instance type: "+ outerInstance.getClass());
//				ctor = c.getConstructor(outerClass);
//				return ctor.newInstance(outerInstance);
//			}
//			else 
//				return c.newInstance();
//		}
//		return ctor.newInstance();
//	}
	
//	// when there is/should be no enclosing outer class
//	@Deprecated public static <T> T newClassInstance(Class<T> c) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException  {
////        Debug.out("Class "+c+" isMemberClass?="+c.isMemberClass() );
//		Constructor<T> ctor;
//		try {
//			ctor = c.getConstructor();
//		} catch (NoSuchMethodException e) {
//			if (c.isMemberClass()) {
////				Class<?> outerClass = c.getDeclaringClass();
////				return newInstanceEnclosingClass(c, outerClass);
//				throw new InstantiationException("Trying to instantiate a member class without reference to an outer class!");
//			}
//			else 
//				return c.newInstance();
//		}
//		return ctor.newInstance();
//	}
	
	// when we want a new enclosing outer class instance (as well as the new inner class instance)
	public static <T> T newClassInstance_newOuterClass(Class<T> c) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException  {
//        Debug.out("Class "+c+" isMemberClass?="+c.isMemberClass() );
		Constructor<T> ctor;
		try {
			ctor = c.getConstructor();
		} catch (NoSuchMethodException e) {
			if (c.isMemberClass()) {
				Class<?> outerClass = c.getDeclaringClass();
				return newInstanceEnclosingClass(c, outerClass);
			}
			else 
				return c.newInstance();
		}
		return ctor.newInstance();
	}
	private static <T,O> T newInstanceEnclosingClass(Class<T> c, Class<O> outerClass) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Object outer = newClassInstance(outerClass);
//        Debug.out("instantiating "+outerClass+ "...");
		Constructor<T> ctor = c.getConstructor(outerClass);
		return ctor.newInstance(outer);
	}
	
	public static boolean isSuperclass(Class<?> sub, Class<?> zuper) {
		while (!sub.equals(Object.class)) {
			if (sub.equals(zuper)) return true;
			sub = sub.getSuperclass();
		}
		return false;
	}
	
}
