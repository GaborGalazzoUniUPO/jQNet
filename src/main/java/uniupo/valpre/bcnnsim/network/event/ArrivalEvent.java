package uniupo.valpre.bcnnsim.network.event;

import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;

public class ArrivalEvent extends Event
{
	public ArrivalEvent(Node referenceStation, CustomerClass customerClass, double time)
	{
		super(referenceStation, customerClass, time);
	}
}
