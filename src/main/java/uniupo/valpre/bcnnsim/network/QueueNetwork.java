package uniupo.valpre.bcnnsim.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.distribution.Distribution;
import uniupo.valpre.bcnnsim.serializer.JsonSerializable;
import uniupo.valpre.bcnnsim.sim.NetworkReport;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class QueueNetwork extends JsonSerializable {
	private final HashMap<String, Node> nodes = new HashMap<String, Node>();
	private final HashMap<String, CustomerClass> classes = new HashMap<String, CustomerClass>();

	public Node addNode(Node node) {
		return nodes.put(node.getName(), node);
	}

	public Collection<Node> addNodes(Node... node) {
		var addedNodes = new ArrayList<Node>();
		for (Node n : node) {
			addedNodes.add(nodes.put(n.getName(), n));
		}
		return addedNodes;
	}

	public Collection<Node> addNodes(Collection<Node> nodes) {
		var addedNodes = new ArrayList<Node>();
		for (Node n : nodes) {
			addedNodes.add(this.nodes.put(n.getName(), n));
		}
		return addedNodes;
	}

	public Node getNode(String name) {
		return nodes.get(name);
	}

	public Collection<Node> getNodes() {
		return nodes.values();
	}

	public CustomerClass getCustomerClass(String name) {
		return classes.get(name);
	}

	public Collection<CustomerClass> getClasses() {
		return classes.values();
	}

	public void addClass(CustomerClass customerClass) {
		classes.put(customerClass.getName(), customerClass);
	}

	public Collection<CustomerClass> addClasses(CustomerClass... classes) {
		var addedClasses = new ArrayList<CustomerClass>();
		for (CustomerClass c : classes) {
			addedClasses.add(this.classes.put(c.getName(), c));
		}
		return addedClasses;
	}

	public Collection<CustomerClass> addClasses(Collection<CustomerClass> classes) {
		var addedClasses = new ArrayList<CustomerClass>();
		for (CustomerClass c : classes) {
			addedClasses.add(this.classes.put(c.getName(), c));
		}
		return addedClasses;
	}

	public NetworkReport generateNetworkReport() {
		NetworkReport networkReport = new NetworkReport();
		for (Node value : nodes.values()) {
			networkReport.put(value.getName(),value.generateNodeReport());
		}
		return networkReport;
	}

	public static class Builder {

		private final QueueNetwork queueNetwork;

		private Builder() {
			queueNetwork = new QueueNetwork();
		}

		public static Builder builder() {
			return new Builder();
		}

		public QueueNetwork build() {
			assert !queueNetwork.nodes.isEmpty();
			assert !queueNetwork.classes.isEmpty();
			return queueNetwork;
		}

		public Builder withNodes(Node... node) {
			queueNetwork.addNodes(node);
			return this;
		}

		public Builder withNodes(Collection<Node> node) {
			queueNetwork.addNodes(node);
			return this;
		}

		public Builder withClasses(CustomerClass... customerClass) {
			queueNetwork.addClasses(customerClass);
			return this;
		}

		public Builder withClasses(Collection<CustomerClass> customerClass) {
			queueNetwork.addClasses(customerClass);
			return this;
		}
	}

	public JsonObject jsonSerialize() {
		var json = super.jsonSerialize();

		var nodes = new JsonArray();
		var serviceTimeDistributions = new JsonArray();
		var links = new JsonObject();
		for (var node : this.getNodes()) {
			nodes.add(node.jsonSerialize());
			var jStDistribution = new JsonObject();
			node.getServiceTimeDistributions().forEach(e -> {
				jStDistribution.addProperty("node", node.getName());
				jStDistribution.addProperty("class", e.getKey());
				jStDistribution.add("distribution", e.getValue().jsonSerialize());
				serviceTimeDistributions.add(jStDistribution);

			});
			var outputs = new JsonArray();
			node.getOutputs().forEach(output -> {
				outputs.add(output.getName());
			});
			links.add(node.getName(), outputs);
		}
		json.add("serviceTimeDistributions", serviceTimeDistributions);
		json.add("links", links);
		json.add("nodes", nodes);
		var classes = new JsonArray();
		for (var cls : this.getClasses()) {
			classes.add(cls.jsonSerialize());
		}
		json.add("classes", classes);
		return json;
	}

	private QueueNetwork() {
		super();
	}

	public QueueNetwork(JsonObject json) {
		json.get("nodes").getAsJsonArray().forEach(jsonElement -> {
			try {
				var node = (Node) Utils.deserializeJson(jsonElement.getAsJsonObject(), nodes);
				nodes.put(node.getName(), node);
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});

		json.get("classes").getAsJsonArray().forEach(jsonElement -> {
			try {
				var cls = (CustomerClass) Utils.deserializeJson(jsonElement.getAsJsonObject(), nodes);
				classes.put(cls.getName(), cls);
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});

		json.get("serviceTimeDistributions").getAsJsonArray().forEach(jsonElement -> {
			try {
				nodes.get(jsonElement.getAsJsonObject().get("node").getAsString())
						.setServiceTimeDistribution(
								classes.get(jsonElement.getAsJsonObject().get("class").getAsString()),
								(Distribution) Utils.deserializeJson(jsonElement.getAsJsonObject(), "distribution", nodes)
						);
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});

		json.get("links").getAsJsonObject().entrySet().forEach(e -> {
			var node = nodes.get(e.getKey());
			e.getValue().getAsJsonArray().forEach(output -> {
				node.addOutput(nodes.get(output.getAsString()));
			});
		});


	}

	@Override
	public QueueNetwork clone() {
		return new QueueNetwork(jsonSerialize());
	}
}
