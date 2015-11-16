package network.main;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public interface HasDefaults {

	public void setDefaults(JsonObjectBuilder o);
	
	public void getDefaults(JsonObject o);
}
