package io;

import java.lang.reflect.InvocationTargetException;

import javax.json.JsonObject;

public interface JsonAbleInterface<T extends JsonAbleInterface<T>> {

	public abstract JsonObject toJsonObject();

	public abstract T fromJsonObject(JsonObject o)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException;

}