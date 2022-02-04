package uniupo.valpre.bcnnsim.network;

import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class QueueNetwork
{
	private final HashMap<String, Node> nodes = new HashMap<String, Node>();
	private final HashMap<String, CustomerClass> classes = new HashMap<String, CustomerClass>();

	public Node addNode(Node node){
		return nodes.put(node.getName(), node);
	}

	public Collection<Node> addNodes(Node... node){
		var addedNodes = new ArrayList<Node>();
		for (Node n : node)
		{
			addedNodes.add(nodes.put(n.getName(), n));
		}
		return addedNodes;
	}

	public Collection<Node> addNodes(Collection<Node> nodes){
		var addedNodes = new ArrayList<Node>();
		for (Node n : nodes)
		{
			addedNodes.add(this.nodes.put(n.getName(), n));
		}
		return addedNodes;
	}

	public Node getNode(String name){
		return nodes.get(name);
	}

	public CustomerClass getCustomerClass(String name){
		return classes.get(name);
	}

	public Collection<CustomerClass> getClasses(){
		return classes.values();
	}

	public void addClass(CustomerClass customerClass)
	{
		classes.put(customerClass.getName(), customerClass);
	}

	public Collection<CustomerClass> addClasses(CustomerClass... classes){
		var addedClasses = new ArrayList<CustomerClass>();
		for (CustomerClass c : classes)
		{
			addedClasses.add(this.classes.put(c.getName(), c));
		}
		return addedClasses;
	}

	public void generateReport()
	{
		for (Node value : nodes.values())
		{
			value.generateReport();
		}
	}

	public static class Builder {

		private final QueueNetwork queueNetwork;
		private Builder(){
			queueNetwork = new QueueNetwork();
		}
		public static Builder builder()
		{
			return new Builder();
		}

		public QueueNetwork build(){
			assert !queueNetwork.nodes.isEmpty();
			assert !queueNetwork.classes.isEmpty();
			return queueNetwork;
		}

		public Builder withNodes(Node... node)
		{
			queueNetwork.addNodes(node);
			return this;
		}

		public Builder withClasses(CustomerClass... customerClass)
		{
			queueNetwork.addClasses(customerClass);
			return this;
		}
	}
}
