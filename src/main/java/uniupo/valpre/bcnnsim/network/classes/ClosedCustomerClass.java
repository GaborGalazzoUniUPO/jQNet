package uniupo.valpre.bcnnsim.network.classes;

import uniupo.valpre.bcnnsim.network.node.Delay;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.network.node.Queue;

public class ClosedCustomerClass extends CustomerClass
{

	private final int numCustomer;

	public ClosedCustomerClass(String name, int numCustomer, Delay referenceStation)
	{
		this(name,numCustomer, referenceStation, 0);
	}

	public ClosedCustomerClass(String name, int numCustomer, Delay referenceStation, int priority)
	{
		super(name, priority, referenceStation);
		this.numCustomer = numCustomer;
	}


	public ClosedCustomerClass(String name, int numCustomer, Queue referenceStation)
	{
		this(name,numCustomer, referenceStation, 0);
	}

	public ClosedCustomerClass(String name, int numCustomer, Queue referenceStation, int priority)
	{
		super(name, priority, referenceStation);
		this.numCustomer = numCustomer;
	}

	public int getNumCustomer()
	{
		return numCustomer;
	}
}
