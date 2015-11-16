package probeersels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

public class JSONtry {

	public static void main(String[] args) {
		try {
			qwe();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void qwe() throws IOException {
		File file = new File("crappa.txt");
//		FileUtils.writeStringToFile(file, this.toString());
		FileWriter writer = new FileWriter(file);
		JsonGenerator gen = Json.createGenerator(writer);
		
		gen.writeStartObject();

		gen.writeEnd();
	}
}
