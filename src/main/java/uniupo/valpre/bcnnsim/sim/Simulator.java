package uniupo.valpre.bcnnsim.sim;

import me.tongfei.progressbar.ProgressBar;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.classes.ClosedCustomerClass;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.event.ArrivalEvent;
import uniupo.valpre.bcnnsim.network.event.DepartureEvent;
import uniupo.valpre.bcnnsim.network.event.Event;
import uniupo.valpre.bcnnsim.network.node.*;
import uniupo.valpre.bcnnsim.random.LehmerGenerator;
import uniupo.valpre.bcnnsim.random.MultipleLehmerStreamGenerator;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Simulator {
	private final MultipleLehmerStreamGenerator streamsGenerator = new MultipleLehmerStreamGenerator();
	private final List<RandomGenerator> streams = streamsGenerator.generateStreams(LehmerGenerator.DEFAULT_SEED);
	private final HashMap<String, RandomGenerator> activityStreams = new HashMap<>();
	private final HashMap<String, RandomGenerator> routingStreams = new HashMap<>();
	private int usedStreams = 0;
	private ArrayList<NetworkReport> allReports = new ArrayList<>();


	public MultiRunNetworkReport runSimulation(QueueNetwork originalNetwork, int numRuns, String referenceStation, long maxNumOfDeparture,
											   Consumer<Long> progress) {
		var step = new AtomicLong(0);
		var reports = IntStream.range(0, numRuns).parallel().mapToObj(num -> {
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
				if (event instanceof DepartureEvent e) {
					if (e.getReferenceStation().getName().equals(referenceStation)) {
						progress.accept(step.incrementAndGet());
					}
				}
				var departures = network.getNode(referenceStation).getNumberOfDepartures();
				ended = departures > maxNumOfDeparture;
			}
			return network.generateNetworkReport();
		}).toList();
		allReports.addAll(reports);
		return new MultiRunNetworkReport(numRuns, allReports);
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
}
