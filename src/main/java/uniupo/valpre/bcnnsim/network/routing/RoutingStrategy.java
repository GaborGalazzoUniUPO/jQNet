package uniupo.valpre.bcnnsim.network.routing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.serializer.JsonSerializable;

import java.util.Collection;
import java.util.Map;

public abstract class RoutingStrategy extends JsonSerializable
{
	public RoutingStrategy() {
		super();
	}

	public RoutingStrategy(JsonObject json, Map<String, Node> memory) {
		super(json, memory);
	}

	public abstract Node choose(Collection<Node> outputs, RandomGenerator stream);

}
