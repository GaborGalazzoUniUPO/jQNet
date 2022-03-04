package uniupo.valpre.bcnnsim.network.node;


import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Event;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.routing.RoutingStrategy;
import uniupo.valpre.bcnnsim.random.distribution.Distribution;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.serializer.JsonSerializable;

import java.util.*;

public abstract class Node extends JsonSerializable {
	private final String name;
	private final HashMap<String, Node> inputs = new HashMap<>();
	private final HashMap<String, Node> outputs = new HashMap<>();
	private final RoutingStrategy routingStrategy;
	private final HashMap<String, Distribution> serviceTimeDistributions = new HashMap<>();
	protected long numerOfDepartures = 0;

	public Node(String name, RoutingStrategy routingStrategy) {
		this.name = name;
		this.routingStrategy = routingStrategy;
	}

	public Distribution setServiceTimeDistribution(CustomerClass customerClass, Distribution distribution) {
		return serviceTimeDistributions.put(customerClass.getName(), distribution);
	}

	public Distribution getServiceTimeDistribution(CustomerClass customerClass) {
		return serviceTimeDistributions.get(customerClass.getName());
	}

	public Distribution getServiceTimeDistribution(String customerClass) {
		return serviceTimeDistributions.get(customerClass);
	}

	public Set<Map.Entry<String, Distribution>> getServiceTimeDistributions() {
		return serviceTimeDistributions.entrySet();
	}

	public String getName() {
		return name;
	}

	Node addInput(Node node) {
		node.outputs.put(name, this);
		return inputs.put(node.name, node);
	}

	public Node removeInput(Node node) {
		node.outputs.remove(name);
		return inputs.remove(node.name);
	}

	private Node removeInput(String name) {
		inputs.get(name).outputs.remove(name);
		return inputs.remove(name);
	}

	public Collection<Node> getInputs() {
		return inputs.values();
	}

	public Node addOutput(Node node) {
		node.inputs.put(name, this);
		return outputs.put(node.name, node);
	}

	public Node removeOutput(Node node) {
		node.inputs.remove(name);
		return outputs.remove(node.name);
	}

	private Node removeOutput(String name) {
		outputs.get(name).inputs.remove(name);
		return outputs.remove(name);
	}

	public Collection<Node> getOutputs() {
		return outputs.values();
	}

	public abstract boolean isValid();


	public abstract List<Event> manageEvent(Event event, RandomGenerator activityStream, RandomGenerator routingStream);

	public RoutingStrategy getRoutingStrategy() {
		return routingStrategy;
	}

	public void generateReport() {

	}

	public long getNumerOfDepartures() {
		return numerOfDepartures;
	}

	public JsonObject jsonSerialize() {
		var jNode = super.jsonSerialize();
		jNode.addProperty("name", this.name);
		if (this.routingStrategy != null)
			jNode.add("routingStrategy", this.routingStrategy.jsonSerialize());
		return jNode;
	}

	@SneakyThrows
	public Node(JsonObject json, Map<String, Node> memory) {
		this.name = json.get("name").getAsString();
		this.routingStrategy = (RoutingStrategy) Utils.deserializeJson(json, "routingStrategy", memory);
	}
}
