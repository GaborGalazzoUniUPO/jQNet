package uniupo.valpre.bcnnsim.network.classes;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Delay;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.network.node.Queue;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public JsonObject jsonSerialize() {
		var json =  super.jsonSerialize();
		json.addProperty("name", this.getName());
		json.addProperty("numCustomer", this.getNumCustomer());
		json.addProperty("priority", this.getPriority());
		json.addProperty("referenceStation", this.getReferenceStation().getName());
		return json;
	}

	protected ClosedCustomerClass(JsonObject jsonObject, Map<String, Node> memory) {
		super(jsonObject, memory);
		this.numCustomer =	jsonObject.get("numCustomer").getAsInt();
	}
}
