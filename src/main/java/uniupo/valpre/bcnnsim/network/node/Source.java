package uniupo.valpre.bcnnsim.network.node;

import uniupo.valpre.bcnnsim.ArrivalEvent;
import uniupo.valpre.bcnnsim.Event;
import uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.List;
import java.util.Random;

public class Source extends Node
{

	public Source(String name, RoutingStrategy routingStrategy)
	{
		super(name, routingStrategy);
	}

	@Override
	public boolean isValid()
	{
		return getInputs().isEmpty() && !getOutputs().isEmpty();
	}

	@Override
	public List<Event> manageEvent(Event event, RandomGenerator stream)
	{
		if(event instanceof ArrivalEvent e){
			return List.of(
					new ArrivalEvent(getRoutingStrategy().choose(getOutputs()),e.getCustomerClass(), e.getTime()),
					new ArrivalEvent(this, e.getCustomerClass(),e.getTime() +  getServiceTimeDistribution(e.getCustomerClass()).generate(stream))
			);
		}
		return List.of();
	}
}
