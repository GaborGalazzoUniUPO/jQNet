package uniupo.valpre.bcnnsim.network.routing;

import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Collection;

public abstract class RoutingStrategy
{
	public abstract Node choose(Collection<Node> outputs, RandomGenerator stream);
}
