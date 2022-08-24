package uniupo.valpre.bcnnsim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.sim.MultiRunNetworkReport;
import uniupo.valpre.bcnnsim.sim.NetworkReport;
import uniupo.valpre.bcnnsim.sim.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) throws IOException {


		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		var scanner = new Scanner(System.in);

		System.out.println("ValPre QNet Simulator");
		System.out.print("Specifica il file in input\n>");
		var file = scanner.next();
		var jsonString = Files.readString(Paths.get(file));
		var json = gson.fromJson(jsonString, JsonObject.class);

		var q = new QueueNetwork(json);

		System.out.print("Seleziona una stazione di riferimento per la terminazione [" + q.getNodes().stream().map(Node::getName).collect(Collectors.joining(",")) + "]\n>");

		var referenceStation = scanner.next();

		System.out.print("Numero di partenze per terminazione\n>");
		var maxNumOfDeparture = scanner.nextLong();

		System.out.print("Numero di run\n>");
		var inputNumRun = scanner.nextInt();
		var numRun = inputNumRun;

		System.out.print("Alpha level\n>");
		var alphaLevel = scanner.nextDouble();

		System.out.print("Precision Type\n>");
		var precisionType = scanner.next();

		System.out.print("Precision\n>");
		var precision = scanner.nextDouble();

		var simulator = new Simulator();
		System.out.println("Simulazione in corso...");
		if (inputNumRun < 1) {
			numRun = 100;
		}

		var totalRuns = 0;

		var endSym = false;
		while (!endSym) {
			var report = simulator.runSimulation(q, numRun, referenceStation, maxNumOfDeparture);
			var accReport = report.checkAccuracy(alphaLevel,
					precisionType.equals("a") ? MultiRunNetworkReport.PrecisionType.Absolute : MultiRunNetworkReport.PrecisionType.Relative,
					precision
			);
			totalRuns += 100;

			if (inputNumRun < 1) {
				endSym = true;
				for (Map.Entry<String, HashMap<String, MultiRunNetworkReport.ValueStream>> e : accReport.entrySet()) {
					for (Map.Entry<String, MultiRunNetworkReport.ValueStream> ee : e.getValue().entrySet()) {
						var m = ee.getValue().getMetricStatistics(alphaLevel,
								precisionType.equals("a") ? MultiRunNetworkReport.PrecisionType.Absolute : MultiRunNetworkReport.PrecisionType.Relative,
								precision);
						if (m.mss() != null)
							endSym = endSym && (m.mss() < totalRuns);
					}
				}
			} else {
				System.out.print("Proseguire con la simulazione [y/n]\n>");
				endSym = !scanner.next().equalsIgnoreCase("y");
			}
		}


	}
}
