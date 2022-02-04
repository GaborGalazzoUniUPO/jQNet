package uniupo.valpre.bcnnsim.network.classes;

import uniupo.valpre.bcnnsim.network.node.Source;
import uniupo.valpre.bcnnsim.random.Distribution;
import uniupo.valpre.bcnnsim.random.ExponentialDistribution;

public class OpenCustomerClass extends CustomerClass
{
	private final Distribution interArrivalTimeDistribution;

	public OpenCustomerClass(String name, long priority, Distribution interArrivalTimeDistribution, Source referenceStation)
	{
		super(name, priority, referenceStation);
		this.interArrivalTimeDistribution = interArrivalTimeDistribution;
	}

	public OpenCustomerClass(String name, Source referenceStation)
	{
		super(name, 0L, referenceStation);
		this.interArrivalTimeDistribution = ExponentialDistribution.DEFAULT;
	}


	public Distribution getInterArrivalTimeDistribution()
	{
		return interArrivalTimeDistribution;
	}
}
