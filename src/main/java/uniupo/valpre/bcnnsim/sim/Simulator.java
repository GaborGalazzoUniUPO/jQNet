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
import uniupo.valpre.bcnnsim.random.MultipleStreamGenerator;
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
	private final MultipleStreamGenerator streamsGenerator = new MultipleLehmerStreamGenerator();
	private final List<RandomGenerator> streams;
	private final HashMap<String, RandomGenerator> activityStreams = new HashMap<>();
	private final HashMap<String, RandomGenerator> routingStreams = new HashMap<>();
	private int usedStreams = 0;
	private ArrayList<NetworkReport> allReports = new ArrayList<>();

	public Simulator() {
		this(LehmerGenerator.DEFAULT_SEED);

	}

	public Simulator(long seed) {
		streams = streamsGenerator.generateStreams(seed);
	}


	public void setSeed(long seed) {
		streams.clear();
		streams.addAll(streamsGenerator.generateStreams(seed));
	}


	public MultiRunNetworkReport runSimulation(QueueNetwork originalNetwork, int numRuns, String referenceStation, long maxNumOfDeparture,
											   Consumer<Long> progress) {
		var step = new AtomicLong(0);

		// Per ogni simulazione parallela
		var reports = IntStream.range(0, numRuns).parallel().mapToObj(num -> {
			boolean ended = false;
			double simTime = 0L;

			// Creo la FEL
			PriorityQueue<Event> fel = new PriorityQueue<Event>();

			// Clono la rete per ottenerne una pulita
			var network = originalNetwork.clone();

			// Inizzializzo la rete aggiungendo gli eventi di arrivo per le
			// stazioni di riferimento delle varie classi di clienti
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
			// Estraggo un evento dalla FEL
			while ((event = fel.poll()) != null && !ended) {
				var events =
						event
								// Ogni evento è associato alla stazione di riferimento
								.getReferenceStation()

								// invoco la funzione di gestione di Eventi per quella stazione (Nodo) e
								.manageEvent(

										// gli passo come parametro l'evento che contiene il tempo e la classe del cliente
										event,

										// La sequenza casuale relativa alle attività per quella stazione
										getActivityStream(event.getReferenceStation()),

										// La sequenza casuale relativa al routing per quella stazione
										getRoutingStream(event.getReferenceStation()));

				// La funzione di gestione restituisce una lista di Eventi Futuri che aggiungo alla FEL
				fel.addAll(events);

				// Incremento il contatore delle partenze relative alla run
				if (event instanceof DepartureEvent e) {
					if (e.getReferenceStation().getName().equals(referenceStation)) {
						progress.accept(step.incrementAndGet());

						// Controllo se ho raggiunto il numero di partenze desiderato per terminare la RUN
						var departures = network.getNode(referenceStation).getNumberOfDepartures();
						ended = departures > maxNumOfDeparture;
					}
				}
			}

			// Genero il Report
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
