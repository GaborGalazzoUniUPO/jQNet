package uniupo.valpre.bcnnsim;

import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.classes.ClosedCustomerClass;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.node.*;
import uniupo.valpre.bcnnsim.network.routing.ProbabilityRoutingStrategy;
import uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy;
import uniupo.valpre.bcnnsim.random.LehmerGenerator;
import uniupo.valpre.bcnnsim.random.MultipleLehmerStreamGenerator;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution;
import uniupo.valpre.bcnnsim.random.distribution.PositiveNormalDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class Simulator {
	private MultipleLehmerStreamGenerator streamsGenerator = new MultipleLehmerStreamGenerator();
	private List<RandomGenerator> streams = streamsGenerator.generateStreams(LehmerGenerator.DEFAULT_SEED);
	private HashMap<String, RandomGenerator> activityStreams = new HashMap<>();
	private HashMap<String, RandomGenerator> routingStreams = new HashMap<>();
	private int usedStreams = 0;


	public void runSimulation(QueueNetwork originalNetwork, int numRuns, String referenceStation, long maxNumOfDeparture) {
		IntStream.range(0, numRuns).parallel().forEach(num -> {
			boolean ended = false;
			double simTime = 0L;
			PriorityQueue<Event> fel = new PriorityQueue<Event>();
			var network = originalNetwork.clone();
			for (CustomerClass aClass : network.getClasses()) {
				if (aClass instanceof OpenCustomerClass c) {
					fel.add(new ArrivalEvent(c.getReferenceStation(), c,
							simTime + c.getInterArrivalTimeDistribution().generate(
									getActivityStream(c.getReferenceStation())
							)));
				} else if (aClass instanceof ClosedCustomerClass c) {
					for (int i = 0; i < c.getNumCustomer(); i++) {
						fel.add(new ArrivalEvent(c.getReferenceStation(), c, simTime));
					}
				}
			}

			Event event;
			while ((event = fel.poll()) != null && !ended) {
				simTime = event.getTime();
				var events = event.getReferenceStation().manageEvent(event,
						getActivityStream(event.getReferenceStation()),
						getRoutingStream(event.getReferenceStation()));
				fel.addAll(events);
				ended = network.getNode(referenceStation).getNumerOfDepartures() > maxNumOfDeparture;
			}

			network.generateReport();
		});

	}

	private RandomGenerator getRoutingStream(Node referenceStation) {
		var rng = routingStreams.get(referenceStation.getName());
		if (rng == null) {
			rng = streams.get(usedStreams);
			usedStreams++;
			routingStreams.put(referenceStation.getName(), rng);
		}
		return rng;
	}

	private RandomGenerator getActivityStream(Node referenceStation) {
		var rng = activityStreams.get(referenceStation.getName());
		if (rng == null) {
			rng = streams.get(usedStreams);
			usedStreams++;
			activityStreams.put(referenceStation.getName(), rng);
		}
		return rng;
	}

	public static QueueNetwork modello1() {
		Source source = new Source("source", new RandomRoutingStrategy());
		Queue q1 = new Queue("q1", 1, new RandomRoutingStrategy());
		Sink sink = new Sink("sink");

		OpenCustomerClass customerClass = new OpenCustomerClass("c", source);

		source.setServiceTimeDistribution(customerClass, new ExponentialDistribution(4.2));
		q1.setServiceTimeDistribution(customerClass, new PositiveNormalDistribution(3.2, 0.6));


		source.addOutput(q1);
		q1.addOutput(sink);

		return QueueNetwork
				.Builder
				.builder()
				.withNodes(source, q1, sink)
				.withClasses(customerClass)
				.build();
	}

	static QueueNetwork modello2() {
		Delay delay = new Delay("delay", new RandomRoutingStrategy());
		Queue q1 = new Queue("q1", 2, new RandomRoutingStrategy());

		ClosedCustomerClass customerClass = new ClosedCustomerClass("c", 10, delay);

		delay.setServiceTimeDistribution(customerClass, new ExponentialDistribution(4.2));
		q1.setServiceTimeDistribution(customerClass, new PositiveNormalDistribution(3.2, 0.6));


		delay.addOutput(q1);
		q1.addOutput(delay);

		return QueueNetwork
				.Builder
				.builder()
				.withNodes(delay, q1)
				.withClasses(customerClass)
				.build();
	}

	static QueueNetwork modello3() {
		var p = 0.7;
		Queue q1 = new Queue("q1", 1, new RandomRoutingStrategy());
		Queue q2 = new Queue("q2", 1, new RandomRoutingStrategy());
		Delay delay = new Delay("delay", new ProbabilityRoutingStrategy(Map.of(
				"q1", p,
				"q2", 1 - p
		)));

		ClosedCustomerClass customerClass = new ClosedCustomerClass("c", 10, delay);

		delay.setServiceTimeDistribution(customerClass, new ExponentialDistribution(4.2));
		q1.setServiceTimeDistribution(customerClass, new PositiveNormalDistribution(3.2, 0.6));
		q2.setServiceTimeDistribution(customerClass, new PositiveNormalDistribution(3.2, 0.6));


		delay.addOutput(q1);
		delay.addOutput(q2);
		q1.addOutput(delay);
		q2.addOutput(delay);

		return QueueNetwork
				.Builder
				.builder()
				.withNodes(delay, q1, q2)
				.withClasses(customerClass)
				.build();
	}
}
