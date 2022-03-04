package uniupo.valpre.bcnnsim.network.routing;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.random.distribution.UniformDistribution;

import java.util.Collection;
import java.util.Map;

public class RandomRoutingStrategy extends RoutingStrategy
{
	public RandomRoutingStrategy() {
		super();
	}

	public RandomRoutingStrategy(JsonObject json, Map<String, Node> memory) {
		super(json, memory);
	}

	public Node choose(Collection<Node> outputs, RandomGenerator stream)
	{
		if(outputs.isEmpty()) return null;
		if(outputs.size() == 1) return (Node) outputs.toArray()[0];
		return (Node) outputs.toArray()[new UniformDistribution(0,outputs.size()-1).generate(stream).intValue()];
	}
}
