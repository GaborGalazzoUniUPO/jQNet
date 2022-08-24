package uniupo.valpre.bcnnsim.network.event;

import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;

public class DepartureEvent extends Event
{

	private final int serverId;
	private final double arrivalTime;

	public DepartureEvent(Node referenceStation, CustomerClass customerClass, int serverId, double arrivalTime, double time)
	{
		super(referenceStation,customerClass, time);
		this.serverId = serverId;
		this.arrivalTime = arrivalTime;
	}

	public int getServerId()
	{
		return serverId;
	}

	public double getArrivalTime()
	{
		return arrivalTime;
	}
}
