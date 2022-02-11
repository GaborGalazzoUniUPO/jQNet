package uniupo.valpre.bcnnsim.network.routing;

import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.random.UniformDistribution;

import java.util.Collection;
import java.util.Random;

public class RandomRoutingStrategy extends RoutingStrategy
{



	public RandomRoutingStrategy()
	{
	}

	public Node choose(Collection<Node> outputs, RandomGenerator stream)
	{
		if(outputs.isEmpty()) return null;
		if(outputs.size() == 1) return (Node) outputs.toArray()[0];
		return (Node) outputs.toArray()[new UniformDistribution(0,outputs.size()-1).generate(stream).intValue()];
	}
}
