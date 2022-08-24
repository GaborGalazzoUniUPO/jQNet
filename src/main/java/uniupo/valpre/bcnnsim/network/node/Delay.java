package uniupo.valpre.bcnnsim.network.node;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.event.ArrivalEvent;
import uniupo.valpre.bcnnsim.network.event.DepartureEvent;
import uniupo.valpre.bcnnsim.network.event.Event;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.sim.NodeReport;

import java.util.*;

public class Delay extends Node
{
	private double accArrivalTime = 0;
	private double lastEventTime = 0;
	private double accDelayTime = 0;
	private int busyServerCount = 0;
	private double accCustomerInStation;

	public Delay(String name, RoutingStrategy routingStrategy)
	{
		super(name, routingStrategy);
	}

	public Delay(JsonObject json, Map<String, Node> memory) {
		super(json, memory);
	}

	@Override
	public boolean isValid()
	{
		return false;
	}

	@Override
	public List<Event> manageEvent(Event event, RandomGenerator activityStream, RandomGenerator routingStream)
	{
		accCustomerInStation += busyServerCount * (event.getTime() - lastEventTime);
		var futureEvents = new ArrayList<Event>();
		if (event instanceof ArrivalEvent e)
		{
			numberOfArrivals++;
			accArrivalTime += e.getTime() - lastEventTime;
			busyServerCount++;
			futureEvents.add(
					new DepartureEvent(
							this,
							event.getCustomerClass(),
							0,
							e.getTime(),
							e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(activityStream))
			);
		} else if (event instanceof DepartureEvent e)
		{
			accDelayTime += e.getTime() - e.getArrivalTime();
			busyServerCount--;
			numberOfDepartures++;

			futureEvents.add(
					new ArrivalEvent(
							getRoutingStrategy().choose(getOutputs(), routingStream),
							e.getCustomerClass(),
							e.getTime())
			);
		}
		lastEventTime = event.getTime();
		return futureEvents;
	}

	@Override
	public NodeReport generateNodeReport()
	{
		var resp = super.generateNodeReport();
		resp.put("SIMULATION_RUN_LENGTH", lastEventTime, true);
		resp.put("NODE_THROUGHPUT", (double) numberOfDepartures / lastEventTime);
		resp.put("MEAN_NUMBER_OF_CUSTOMERS_IN_THIS_NODE", accCustomerInStation / lastEventTime);
		resp.put("AVG_DELAY_TIME", accDelayTime / numberOfDepartures);
		resp.put("AVG_ARRIVAL_TIME", accArrivalTime / numberOfDepartures);
		return resp;
	}
}
