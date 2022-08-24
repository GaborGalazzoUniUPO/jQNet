package uniupo.valpre.bcnnsim.sim;

import java.util.HashMap;
import java.util.Map;

public class NetworkReport {

	private final HashMap<String, NodeReport> report = new HashMap<>();
	public void put(String name, NodeReport value) {
		report.put(name, value);
	}

	public String  toString() {
		StringBuilder str = new StringBuilder();
		for (Map.Entry<String, NodeReport> e : report.entrySet()) {
			str.append("Node: ");
			str.append(e.getKey());
			str.append("\n");
			str.append(e.getValue().toString("\t"));
			str.append("\n");
		}
		return str.toString();
	}

	public HashMap<String, NodeReport> getNodeReports() {
		return new HashMap<>(report);
	}
}
