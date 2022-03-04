package uniupo.valpre.bcnnsim.network.classes;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.serializer.JsonSerializable;

import java.util.Map;

public abstract class CustomerClass extends JsonSerializable
{
	private final Node referenceStation;
	private final String name;
	private final long priority;

	protected CustomerClass(String name, long priority, Node referenceStation)
	{
		this.name = name;
		this.priority = priority;
		this.referenceStation = referenceStation;
	}

	public CustomerClass(JsonObject json, Map<String, Node> memory) {
		this(
				json.get("name").getAsString(),
				json.get("priority").getAsInt(),
				memory.get(json.get("referenceStation").getAsString()));

	}

	public String getName()
	{
		return name;
	}

	public long getPriority()
	{
		return priority;
	}

	public Node getReferenceStation()
	{
		return referenceStation;
	}
}
