package uniupo.valpre.bcnnsim.network.node;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.event.ArrivalEvent;
import uniupo.valpre.bcnnsim.network.event.DepartureEvent;
import uniupo.valpre.bcnnsim.network.event.Event;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.sim.NodeReport;

import java.util.*;

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

	public Queue(JsonObject json, Map<String, Node> memory) {
		super(json, memory);
		this.servers = new Servers(json.get("numServer").getAsInt());
	}

	@Override
	public JsonObject jsonSerialize() {
		var jNode = super.jsonSerialize();
		jNode.addProperty("numServer", this.servers.count());
		return jNode;
	}

	@Override
	public boolean isValid()
	{
		return !getInputs().isEmpty() && !getOutputs().isEmpty();
	}

	@Override
	public List<Event> manageEvent(Event event, RandomGenerator activityStream, RandomGenerator routingStream)
	{
		// Aggiorno gli Accumulatori
		servers.updateBusyTime((event.getTime() - lastEventTime));
		accQueueLen += (customerQueue.size())* (event.getTime() - lastEventTime);
		accCustomerInStation += ( customerQueue.size() + servers.busyCount())* (event.getTime() - lastEventTime);

		if (customerQueue.size() > maxQueueLen)
		{
			maxQueueLen = customerQueue.size();
		}

		var futureEvents = new ArrayList<Event>();

		// Evento Di arrivo
		if (event instanceof ArrivalEvent e)
		{
			// Aggiorno gli accumulatori specifici
			numberOfArrivals++;
			accArrivalTime += e.getTime() - lastEventTime;

			// Aggiungo il cliente alla coda
			var customer = new Customer(e);
			customerQueue.add(customer);

			// Se ci sono server disponibili
			if (servers.hasAvailable())
			{
				// assegno ai server il primo della coda
				var server = servers.assignCustomer(customerQueue.poll());

				// aggiungo come evento futuro l'evento di fine servizio (Partenza dalla coda)
				futureEvents.add(
						new DepartureEvent(
								this,
								event.getCustomerClass(),
								server.id,
								e.getTime(),
								e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(activityStream))
				);
			}
		}
		// Evento di Fine Servizio (Partenza)
		else if (event instanceof DepartureEvent e)
		{
			// Rilascio un servitore
			servers.freeServer(e.getServerId());
			// Se ci sono dei clienti in coda li servo come sopra
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
								e.getTime() + getServiceTimeDistribution(e.getCustomerClass()).generate(activityStream))
				);
				// Aggiorno gli accumulatori specifici
				accQueueTime += e.getTime() - customer.getArrivalEvent().getTime();
			}

			// Aggiorno gli accumulatori specifici
			accResponseTime += e.getTime() - e.getArrivalTime();
			numberOfDepartures++;

			// Aggiungo l'evento di arrivo alla stazione sucessiva
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

	public NodeReport generateNodeReport()
	{
		var resp = super.generateNodeReport();
		resp.put("SIMULATION_RUN_LENGTH", lastEventTime, true);
		resp.put("NODE_THROUGHPUT", (double) numberOfDepartures / lastEventTime);
		resp.put("MEAN_NUMBER_OF_CUSTOMERS_IN_THIS_NODE", accCustomerInStation / lastEventTime);
		resp.put("MEAN_NUMBER_OF_CUSTOMERS_IN_QUEUE", accQueueLen / lastEventTime);
		resp.put("MEAN_NUMBER_OF_CUSTOMERS_SERVED", (accCustomerInStation - accQueueLen) / lastEventTime);
		resp.put("MAX_NUMBER_OF_CUSTOMERS_IN_QUEUE", maxQueueLen);
		resp.put("AVG_QUEUE_TIME", accQueueTime / numberOfDepartures);
		resp.put("AVG_SERVICE_TIME", (accResponseTime - accQueueTime) / numberOfDepartures);
		resp.put("AVG_RESPONSE_TIME", accResponseTime / numberOfDepartures);
		resp.put("AVG_ARRIVAL_TIME", accArrivalTime / numberOfDepartures);
		for (Server server : servers.servers)
		{
			resp.put("SERVER_UTILIZATION_" + server.id, server.busyTime / lastEventTime);
		}
		return resp;
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
		private final int id;
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
