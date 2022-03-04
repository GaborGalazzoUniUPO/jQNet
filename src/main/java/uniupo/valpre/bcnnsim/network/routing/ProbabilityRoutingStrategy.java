package uniupo.valpre.bcnnsim.network.routing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.LehmerGenerator;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProbabilityRoutingStrategy extends RoutingStrategy
{
	private final Map<String, Double> probabilities;

	public ProbabilityRoutingStrategy(Map<String, Double> probabilities)
	{
		this.probabilities = probabilities;
	}

	public ProbabilityRoutingStrategy(JsonObject json, Map<String, Node> memory){
		var probabilities = new HashMap<String, Double>();
		var jProbabilities = json.get("probabilities").getAsJsonObject();
		jProbabilities.keySet().forEach(k -> probabilities.put(k, jProbabilities.get(k).getAsDouble()));
		this.probabilities = probabilities;
	}

	@Override
	public Node choose(Collection<Node> outputs, RandomGenerator stream)
	{
		if(outputs.isEmpty()) return null;
		if(outputs.size() == 1) return outputs.iterator().next();
		var sum = probabilities.values().stream().reduce(Double::sum).orElse(0.0);
		var pin = stream.random() * sum;
		var last = 0.0;
		for (Map.Entry<String, Double> prob : probabilities.entrySet())
		{
			if(pin <= prob.getValue() + last)
				return outputs.stream().filter(n -> n.getName().equals(prob.getKey())).findFirst().orElse(outputs.iterator().next());
			last += prob.getValue();
		}
		return outputs.iterator().next();
	}

	@Override
	public JsonObject jsonSerialize() {
		var json = super.jsonSerialize();
		var jProbabilities = new JsonObject();
		this.probabilities.entrySet().forEach(e -> jProbabilities.addProperty(e.getKey(), e.getValue()));
		json.add("probabilities", jProbabilities);
		return json;
	}
}
