package uniupo.valpre.bcnnsim;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Utils {

	public static Object deserializeJson(JsonObject jNode, String field, Map<String, Node> memory) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		var routingStrategy = jNode.has(field) ? jNode.get(field).getAsJsonObject() : null;
		if (routingStrategy != null)
			return Class.forName(routingStrategy.get("class").getAsString())
					.getConstructor(JsonObject.class, Map.class).newInstance(routingStrategy, memory);
		else
			return null;
	}

	public static Object deserializeJson(JsonObject jNode, Map<String, Node> memory) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		if (jNode != null)
			return Class.forName(jNode.get("class").getAsString())
					.getConstructor(JsonObject.class, Map.class).newInstance(jNode, memory);
		else
			return null;
	}
}
