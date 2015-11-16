package io;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import network.analysis.Debug;

public class JsonArrays {

	
	public static JsonArray toJsonArray(double[] ts) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (double o : ts)
			builder.add(o+"");
		return builder.build();
	}
	
	public static JsonArray toJsonArray(Object[] ts) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Object o : ts)
			if (Object[].class.isInstance(o)) builder.add(toJsonArray((Object[]) o));
			else if (double[].class.isInstance(o))
				builder.add(toJsonArray((double[]) o));
			else // if (JsonAbleInterface.class.isInstance(o)) 
				builder.add(((JsonAbleInterface<?>) o).toJsonObject());
		return builder.build();
	}


		
	public static double[] fromJsonArray1D(JsonArray jre) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double[] ret = new double[jre.size()];
		for ( int i = 0; i<jre.size(); i++)
			ret[i] = Double.parseDouble( jre.getString(i) );
		return ret;
	}
	public static double[][] fromJsonArray2D(JsonArray jre) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double[][] ret = new double[jre.size()][];
		for ( int i = 0; i<jre.size(); i++)
			ret[i] = fromJsonArray1D((JsonArray) jre.get(i));
		return ret;
	}
	public static double[][][] fromJsonArray3D(JsonArray jre) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double[][][] ret = new double[jre.size()][][];
		for ( int i = 0; i<jre.size(); i++)
			ret[i] = fromJsonArray2D((JsonArray) jre.get(i));
		return ret;
	}
	public static double[][][][] fromJsonArray4D(JsonArray jre) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double[][][][] ret = new double[jre.size()][][][];
		for ( int i = 0; i<jre.size(); i++)
			ret[i] = fromJsonArray3D((JsonArray) jre.get(i));
		return ret;
	}
	
	
	public static <T extends JsonAbleInterface<T>> T[] fromJsonArray1D(Class<T> clazz, JsonArray jre, Object... constructorParameters) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (Debug.debug && jre==null) {
            Debug.out("Array is null!!");
            Debug.out(jre);
		}
//		if (parameters.length>1)
//			throw new IllegalArgumentException("Multiple outer class instances given, while there can only be one outer class!");
		@SuppressWarnings("unchecked")
		T[] ret = (T[]) Array.newInstance(clazz, jre.size());
		for ( int i = 0; i<jre.size(); i++) {
//			if (parameters.length==1)
//				newInst = generics.Reflect.newClassInstance(clazz, parameters[0]);
//			else //  if (parameters.length==0) // already caught by above if ::  IllegalArgumentException
//				newInst = generics.Reflect.newClassInstance(clazz); // it shouldn't be a member class
			
			T newInst = generics.Reflect.newClassInstance(clazz, constructorParameters);
				
			ret[i] = newInst.fromJsonObject((JsonObject) jre.get(i));
			if (Debug.debug && ret[i]==null)
                Debug.out("JSONarrays 83 element = null!");
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends JsonAbleInterface<T>> T[][] fromJsonArray2D(Class<T> clazz, JsonArray jre, Object... parameters) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ArrayList<T[]> l = new ArrayList<T[]>();
		for ( int i = 0; i<jre.size(); i++)
			l.add(fromJsonArray1D(clazz, jre.getJsonArray(i), parameters));
		return (T[][]) l.toArray();
	}
	@SuppressWarnings("unchecked")
	public static <T extends JsonAbleInterface<T>> T[][][] fromJsonArray3D(Class<T> clazz, JsonArray jre, Object... parameters) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ArrayList<T[][]> l = new ArrayList<T[][]>();
		for ( int i = 0; i<jre.size(); i++)
			l.add(fromJsonArray2D(clazz, jre.getJsonArray(i), parameters));
		return (T[][][]) l.toArray();
	}
	@SuppressWarnings("unchecked")
	public static <T extends JsonAbleInterface<T>> T[][][][] fromJsonArray4D(Class<T> clazz, JsonArray jre, Object... parameters) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ArrayList<T[][][]> l = new ArrayList<T[][][]>();
		for ( int i = 0; i<jre.size(); i++)
			l.add(fromJsonArray3D(clazz, jre.getJsonArray(i), parameters));
		return (T[][][][]) l.toArray();
	}
	

}
