package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

public abstract class JsonAble<T extends JsonAble<T>> implements JsonAbleInterface<T> {

	/* (non-Javadoc)
	 * @see io.JsonInterface#toJsonObject()
	 */
	@Override
	public abstract JsonObject toJsonObject();
	/* (non-Javadoc)
	 * @see io.JsonInterface#fromJsonObject(javax.json.JsonObject)
	 */
	@Override
	public abstract T fromJsonObject(JsonObject o) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	
	public void writeToFile(File file) throws IOException {
		JsonWriter writer = Json.createWriter(new FileWriter(file));
		writer.write(toJsonObject());
		writer.close();
			
	}

	public T readFromFile(File dir) throws IllegalArgumentException, SecurityException, FileNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		JsonReader reader = Json.createReader(new FileInputStream(dir));
		T object = fromJsonObject(
			(JsonObject) reader
				.read() );
		reader.close();
		return object; 
	}
}
