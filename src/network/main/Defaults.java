package network.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

public class Defaults 
//extends JsonAble<Defaults> 
{

//	public static LinkedList<Class<? extends HasDefaults>> defaultClasses = new LinkedList<Class<? extends HasDefaults>>();
//	static {
//		defaultClasses.add(MNIST.class);
//		defaultClasses.add(NORB.class);
//		defaultClasses.add(CIFAR.class);
//	}
	
	public static JsonObject defaults;
	static {
		try {
			File file = new File("defaults.json");
			if (!file.exists())
				try {
					throw new IOException("defaults json file not found!");
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			defaults = Json.createReader(new FileInputStream(file)).readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
//	@Override
//	public JsonObject toJsonObject() {
//		JsonObjectBuilder builder = Json.createObjectBuilder();
//		for (Class<? extends HasDefaults> clazz : defaultClasses)
//			try {
//				clazz.newInstance().setDefaults(builder);
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		return builder.build();
//	}
//
//	@Override
//	public Defaults fromJsonObject(JsonObject o) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		for (Class<? extends HasDefaults> clazz : defaultClasses)
//			clazz.newInstance().getDefaults(o);
//		return null;
//	}
	
//	public static void writeDefaults(File out) throws FileNotFoundException {
//		JsonWriter writer = Json.createWriter(new FileOutputStream(out));
//		writer.writeObject(new Defaults().toJsonObject());
//		writer.close();
//	}
//	
//	public static void readDefaults(File in) throws FileNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		new Defaults().fromJsonObject(Json.createReader(new FileInputStream(in)).readObject());
//	}
	
//	public static void main(String[] args) {
//		try {
//			JsonWriter writer = Json.createWriter(new FileOutputStream(new File("defaults.json")));
//			writer.writeObject(new Defaults().toJsonObject());
//			writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

//	public static void getStandardDefaults() {
//		try {
//			File file = new File("defaults.json");
//			if (file.exists())
//				readDefaults(file);
//			else 
//				writeDefaults(file);
//		} catch (FileNotFoundException | IllegalArgumentException
//				| SecurityException | InstantiationException
//				| IllegalAccessException | InvocationTargetException
//				| NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//	}

}
