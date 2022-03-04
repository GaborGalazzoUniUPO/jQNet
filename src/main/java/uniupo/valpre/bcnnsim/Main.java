package uniupo.valpre.bcnnsim;

import com.google.gson.GsonBuilder;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.node.Queue;
import uniupo.valpre.bcnnsim.network.node.Sink;
import uniupo.valpre.bcnnsim.network.node.Source;
import uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy;
import uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution;
import uniupo.valpre.bcnnsim.random.distribution.PositiveNormalDistribution;

import java.io.FileWriter;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException {

		var json = Simulator.modello3().jsonSerialize();
		var gson = new GsonBuilder().setPrettyPrinting().create();
		var fw = new FileWriter("model3.json");
		fw.write(gson.toJson(json));
		fw.close();



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
