package uniupo.valpre.bcnnsim.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.node.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonSerializable {
	public JsonSerializable(){}

	public JsonSerializable(JsonObject json, Map<String, Node> memory){}

	public JsonObject jsonSerialize() {
		var jNode = new JsonObject();
		jNode.addProperty("class", this.getClass().getName());
		return jNode;
	}
}
