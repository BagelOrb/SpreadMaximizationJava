package data;

import java.lang.reflect.InvocationTargetException;

public interface DoubleMatrixIO {

	public byte[] toByteArray();
	public DoubleMatrixIO fromByteArray(byte[] ba) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	

	// use 	readFileToByteArray(File file) 	and		writeByteArrayToFile(File file,  byte[] data) 
	// from org.apache.commons.io.FileUtils
}
