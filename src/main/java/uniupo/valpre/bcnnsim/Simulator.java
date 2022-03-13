package uniupo.valpre.bcnnsim;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.classes.ClosedCustomerClass;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.node.*;
import uniupo.valpre.bcnnsim.random.LehmerGenerator;
import uniupo.valpre.bcnnsim.random.MultipleLehmerStreamGenerator;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Simulator {
	private MultipleLehmerStreamGenerator streamsGenerator = new MultipleLehmerStreamGenerator();
	private List<RandomGenerator> streams = streamsGenerator.generateStreams(LehmerGenerator.DEFAULT_SEED);
	private HashMap<String, RandomGenerator> activityStreams = new HashMap<>();
	private HashMap<String, RandomGenerator> routingStreams = new HashMap<>();
	private int usedStreams = 0;


	public void runSimulation(QueueNetwork originalNetwork, int numRuns, String referenceStation, long maxNumOfDeparture) {
		var progressBar = new ProgressBar("Job", maxNumOfDeparture * numRuns);
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
						progressBar.step();
					}
				}
				var departures = network.getNode(referenceStation).getNumerOfDepartures();
				ended = departures > maxNumOfDeparture;
			}
			return network.generateReport();
		}).toList();
		progressBar.close();
		reports.forEach(System.out::println);
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
