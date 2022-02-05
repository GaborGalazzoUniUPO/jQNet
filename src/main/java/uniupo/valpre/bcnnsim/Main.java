package uniupo.valpre.bcnnsim;

import uniupo.valpre.bcnnsim.random.LehmerGenerator;

public class Main
{
	public static void main(String[] args)
	{

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
