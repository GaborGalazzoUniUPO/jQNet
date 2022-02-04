package uniupo.valpre.bcnnsim.network.routing;

import uniupo.valpre.bcnnsim.network.node.Node;

import java.util.Collection;
import java.util.Random;

public class RandomRoutingStrategy extends RoutingStrategy
{

	private final Random stream;

	public RandomRoutingStrategy(Random stream)
	{
		this.stream = stream;

	}

	public Node choose(Collection<Node> outputs)
	{
		if(outputs.isEmpty()) return null;
		if(outputs.size() == 1) return (Node) outputs.toArray()[0];
		return (Node) outputs.toArray()[stream.nextInt(0,outputs.size()-1)];
	}
}
