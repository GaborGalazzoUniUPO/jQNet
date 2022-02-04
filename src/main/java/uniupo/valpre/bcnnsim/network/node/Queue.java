package uniupo.valpre.bcnnsim.network.node;

import uniupo.valpre.bcnnsim.ArrivalEvent;
import uniupo.valpre.bcnnsim.DepartureEvent;
import uniupo.valpre.bcnnsim.Event;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Queue extends Node
{

	private final LinkedList<Customer> customerQueue = new LinkedList<>();
	private final Servers servers;

	private double lastEventTime = 0;

	private int maxQueueLen = 0;
	private double accCustomerInStation = 0;
	private double accQueueLen = 0;
	private double accQueueTime = 0;
	private double accResponseTime = 0;
	private double accArrivalTime = 0;


	public Queue(String name, int numServer, RoutingStrategy routingStrategy)
	{
		super(name, routingStrategy);
		this.servers = new Servers(numServer);
	}


	@Override
	public boolean isValid()
	{
		return !getInputs().isEmpty() && !getOutputs().isEmpty();
	}

	@Override
	public List<Event> manageEvent(Event event, Random stream)
	{
		servers.updateBusyTime((event.getTime() - lastEventTime));
		accQueueLen += (customerQueue.size())* (event.getTime() - lastEventTime);
		accCustomerInStation += ( customerQueue.size() + servers.busyCount())* (event.getTime() - lastEventTime);
		if (customerQueue.size() > maxQueueLen)
		{
			maxQueueLen = customerQueue.size();
		}
		var futureEvents = new ArrayList<Event>();
		if (event instanceof ArrivalEvent e)
		{
			accArrivalTime += e.getTime() - lastEventTime;
			var customer = new Customer(e);
			customerQueue.add(customer);
			if (servers.hasAvailable())
			{
				var server = servers.assignCustomer(customerQueue.poll());
				futureEvents.add(
						new DepartureEvent(
								this,
								event.getCustomerClass(),
								server.id,
								e.getTime(),
								e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(stream))
				);
			}
		} else if (event instanceof DepartureEvent e)
		{
			servers.freeServer(e.getServerId());
			if (!customerQueue.isEmpty())
			{
				var customer = customerQueue.poll();
				var server = servers.assignCustomer(customer);
				futureEvents.add(
						new DepartureEvent(
								this,
								event.getCustomerClass(),
								server.id,
								customer.arrivalEvent.getTime(),
								e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(stream))
				);
				accQueueTime += e.getTime() - customer.getArrivalEvent().getTime();
			}

			accResponseTime += e.getTime() - e.getArrivalTime();
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

	public void generateReport()
	{
		System.out.println("----------------------------------------------------");
		System.out.printf("REPORT FOR Queue '%s'\n", getName());
		System.out.printf("%-50s %d\n", "#servers", servers.count());
		getServiceTimeDistributions().forEach(e -> {
			System.out.printf("%-30s class: %-12s %s\n", "Service time distribution", e.getKey() , e.getValue().toString());
		});

		System.out.println();
		System.out.println();
		System.out.printf("%-50s %d cus\n", "NUMBER OF CUSTOMERS SERVED", numerOfDepartures);
		System.out.printf("%-50s %.4f min\n", "SIMULATION RUN LENGTH ", lastEventTime);
		System.out.printf("%-50s %.4f cus/min\n", "NODE THROUGHPUT ", numerOfDepartures / lastEventTime);
		System.out.printf("%-50s %.6f\n", "MEAN NUMBER OF CUSTOMERS IN THIS NODE", accCustomerInStation / lastEventTime);
		System.out.printf("%-50s %.6f\n", "MEAN NUMBER OF CUSTOMERS IN QUEUE", accQueueLen / lastEventTime);
		System.out.printf("%-50s %.6f\n", "MEAN NUMBER OF CUSTOMERS SERVER", (accCustomerInStation - accQueueLen) / lastEventTime);
		System.out.printf("%-50s %d\n", "QUEUE MAX LEN", maxQueueLen);
		System.out.printf("%-50s %.6f\n", "QUEUE MEAN LENGTH ", accQueueLen / lastEventTime);
		System.out.printf("%-50s %.2f min\n", "AVG QUEUE TIME ", accQueueTime / numerOfDepartures);
		System.out.printf("%-50s %.2f min\n", "AVG SERVICE TIME ", (accResponseTime - accQueueTime) / numerOfDepartures);
		System.out.printf("%-50s %.2f min\n", "AVG RESPONSE TIME ", accResponseTime / numerOfDepartures);
		System.out.printf("%-50s %.2f min\n", "AVG ARRIVAL TIME ", accArrivalTime / numerOfDepartures);
		System.out.println();
		for (Server server : servers.servers)
		{
			System.out.printf("%-30s %-19s %.6f%%\n", "SERVER UTILIZATION", server.id, server.busyTime / lastEventTime);
		}
		System.out.println("----------------------------------------------------");
	}

	private class Servers
	{
		private final Server[] servers;
		private final LinkedList<Server> availableServers = new LinkedList<>();

		private Servers(int size)
		{
			servers = new Server[size];
			for (int i = 0; i < size; i++)
			{
				servers[i] = new Server(i);
				availableServers.add(servers[i]);
			}
		}

		public void updateBusyTime(double time)
		{
			for (Server server : servers)
			{
				server.busyTime += server.busy * time;
			}
		}

		public boolean hasAvailable()
		{
			return !availableServers.isEmpty();
		}

		public Server assignCustomer(Customer he)
		{
			var server = availableServers.poll();
			assert server != null;
			server.busy = 1;
			he.setServer(server);
			return server;
		}

		public void freeServer(Server server)
		{
			server.busy = 0;
			availableServers.add(server);
		}

		public int count()
		{
			return servers.length;
		}

		public int busyCount()
		{
			return servers.length - availableServers.size();
		}

		public int availableCount()
		{
			return availableServers.size();
		}

		public void freeServer(int serverId)
		{
			freeServer(servers[serverId]);
		}
	}

	private class Server
	{
		private int id;
		private int busy = 0;
		private double busyTime = 0.0;

		public Server(int id)
		{
			this.id = id;
		}
	}

	private class Customer
	{
		private final ArrivalEvent arrivalEvent;
		private Server server = null;

		private Customer(ArrivalEvent event)
		{
			this.arrivalEvent = event;
		}

		public ArrivalEvent getArrivalEvent()
		{
			return arrivalEvent;
		}

		public Server getServer()
		{
			return server;
		}

		public void setServer(Server server)
		{
			this.server = server;
		}
	}
}
