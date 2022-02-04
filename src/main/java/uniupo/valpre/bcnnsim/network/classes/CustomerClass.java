package uniupo.valpre.bcnnsim.network.classes;

import uniupo.valpre.bcnnsim.network.node.Node;

public abstract class CustomerClass
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
