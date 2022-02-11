package uniupo.valpre.bcnnsim.network.node;

import uniupo.valpre.bcnnsim.Event;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.List;
import java.util.Random;

public class Sink extends Node
{


	public Sink(String name)
	{
		super(name, null);
	}

	@Override
	public boolean isValid()
	{
		return !getInputs().isEmpty() && getOutputs().isEmpty();
	}

	@Override
	public List<Event> manageEvent(Event event, RandomGenerator activityStream, RandomGenerator routingStream)
	{
		return List.of();
	}
}
