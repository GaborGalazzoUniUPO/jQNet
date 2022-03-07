package uniupo.valpre.bcnnsim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.network.node.Queue;
import uniupo.valpre.bcnnsim.network.node.Sink;
import uniupo.valpre.bcnnsim.network.node.Source;
import uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy;
import uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution;
import uniupo.valpre.bcnnsim.random.distribution.PositiveNormalDistribution;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main
{
	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		var scanner = new Scanner(System.in);

		System.out.println("ValPre QNet Simulator");
		System.out.print("Specifica il file in input\n>");
		var file = scanner.next();
		var jsonString = Files.readString(Paths.get(file));
		var json = gson.fromJson(jsonString, JsonObject.class);

		var q = new QueueNetwork(json);

		System.out.print("Seleziona una stazione di riferimento per la terminazione [" +  q.getNodes().stream().map(Node::getName).collect(Collectors.joining(","))+ "]\n>");

		var referenceStation = scanner.next();

		System.out.print("Numero di partenze per terminazione\n>");
		var maxNumOfDeparture = scanner.nextLong();

		System.out.print("Numero di partenze run\n>");
		var numRun = scanner.nextInt();


		var simulator = new Simulator();
		System.out.println("Simulazione in corso...");
		simulator.runSimulation(q, numRun, referenceStation, maxNumOfDeparture);


		/*
		Simulator simulator = new Simulator();
		simulator.init();
		simulator.runSimulation();
		/*
		var rng = new LehmerGenerator(48271, (long) (Math.pow(2,31) - 1));
		for (int i = 0; i<100; i++){
			System.out.println(rng.random());
		}*/
	}
}
