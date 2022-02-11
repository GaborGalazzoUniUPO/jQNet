package uniupo.valpre.bcnnsim.network.routing;

import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.LehmerGenerator;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class ProbabilityRoutingStrategy extends RoutingStrategy
{
	private final Map<String, Double> probabilities;

	public ProbabilityRoutingStrategy(Map<String, Double> probabilities)
	{
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
}
