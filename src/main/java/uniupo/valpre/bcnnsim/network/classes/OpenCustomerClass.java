package uniupo.valpre.bcnnsim.network.classes;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.network.node.Source;
import uniupo.valpre.bcnnsim.random.distribution.Distribution;
import uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution;

import java.util.Map;

public class OpenCustomerClass extends CustomerClass
{
	private final Distribution interArrivalTimeDistribution;

	public OpenCustomerClass(String name, long priority, Distribution interArrivalTimeDistribution, Source referenceStation)
	{
		super(name, priority, referenceStation);
		this.interArrivalTimeDistribution = interArrivalTimeDistribution;
	}

	public OpenCustomerClass(String name, Source referenceStation)
	{
		super(name, 0L, referenceStation);
		this.interArrivalTimeDistribution = ExponentialDistribution.DEFAULT;
	}


	public Distribution getInterArrivalTimeDistribution()
	{
		return interArrivalTimeDistribution;
	}

	@Override
	public JsonObject jsonSerialize() {
		var json =  super.jsonSerialize();
		json.addProperty("name", this.getName());
		json.addProperty("priority", this.getPriority());
		json.addProperty("referenceStation", this.getReferenceStation().getName());
		json.add("interArrivalTimeDistribution", this.interArrivalTimeDistribution.jsonSerialize());
		return json;
	}

	@SneakyThrows
	public OpenCustomerClass(JsonObject jsonObject, Map<String, Node> memory) {
		super(jsonObject, memory);
		this.interArrivalTimeDistribution = (Distribution) Utils.deserializeJson(jsonObject, "interArrivalTimeDistribution", memory);
	}
}
