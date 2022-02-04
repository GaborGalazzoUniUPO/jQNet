package uniupo.valpre.bcnnsim.network.routing;

import uniupo.valpre.bcnnsim.network.node.Node;

import java.util.Collection;

public abstract class RoutingStrategy
{
	public abstract Node choose(Collection<Node> outputs);
}
