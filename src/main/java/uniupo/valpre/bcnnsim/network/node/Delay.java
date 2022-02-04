package uniupo.valpre.bcnnsim.network.node;

import uniupo.valpre.bcnnsim.ArrivalEvent;
import uniupo.valpre.bcnnsim.DepartureEvent;
import uniupo.valpre.bcnnsim.Event;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;

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

	@Override
	public boolean isValid()
	{
		return false;
	}

	@Override
	public List<Event> manageEvent(Event event, Random stream)
	{
		accCustomerInStation += busyServerCount * (event.getTime() - lastEventTime);
		var futureEvents = new ArrayList<Event>();
		if (event instanceof ArrivalEvent e)
		{
			accArrivalTime += e.getTime() - lastEventTime;
			busyServerCount++;
			futureEvents.add(
					new DepartureEvent(
							this,
							event.getCustomerClass(),
							0,
							e.getTime(),
							e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(stream))
			);
		} else if (event instanceof DepartureEvent e)
		{
			accDelayTime += e.getTime() - e.getArrivalTime();
			busyServerCount--;
			numerOfDepartures++;

			futureEvents.add(
					new ArrivalEvent(
							getRoutingStrategy().choose(getOutputs()),
							e.getCustomerClass(),
							e.getTime())
			);
		}
		lastEventTime = event.getTime();
		return futureEvents;
	}

	@Override
	public void generateReport()
	{
		System.out.println("----------------------------------------------------");
		System.out.printf("REPORT FOR Delay '%s'\n", getName());
		getServiceTimeDistributions().forEach(e -> {
			System.out.printf("%-30s class: %-12s %s\n", "Service time distribution", e.getKey() , e.getValue().toString());
		});
		System.out.println();
		System.out.println();
		System.out.printf("%-50s %d cus\n", "NUMBER OF CUSTOMERS SERVED", numerOfDepartures);
		System.out.printf("%-50s %.4f min\n", "SIMULATION RUN LENGTH ", lastEventTime);
		System.out.printf("%-50s %.4f cus/min\n", "NODE THROUGHPUT ", numerOfDepartures / lastEventTime);
		System.out.printf("%-50s %.6f\n", "MEAN NUMBER OF CUSTOMERS IN THIS NODE", accCustomerInStation / lastEventTime);
		System.out.printf("%-50s %.2f min\n", "AVG DELAY TIME ", accDelayTime / numerOfDepartures);
		System.out.printf("%-50s %.2f min\n", "AVG ARRIVAL TIME ", accArrivalTime / numerOfDepartures);
		System.out.println("----------------------------------------------------");
	}
}
