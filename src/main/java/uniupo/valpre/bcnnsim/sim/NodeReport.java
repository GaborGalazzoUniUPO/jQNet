package uniupo.valpre.bcnnsim.sim;

import java.util.HashMap;
import java.util.Map;

public class NodeReport {
	private final HashMap<String, Double> data = new HashMap<String, Double>();
	private final HashMap<String, Boolean> absolute = new HashMap<String, Boolean>();

	public void put(String name, double value) {
		put(name, value, false);
	}

	public void put(String name, double value, boolean isAbsolute) {
		data.put(name, value);
		absolute.put(name, isAbsolute);
	}


	public boolean isAbsolute(String name){
		return absolute.getOrDefault(name, false);
	}

	@Override
	public String toString() {
		return toString("");
	}

	public String toString(String prefix) {
		StringBuilder str = new StringBuilder();
		for (Map.Entry<String, Double> e : data.entrySet()) {
			str.append(prefix);
			str.append(e.getKey());
			str.append(": ");
			str.append(e.getValue());
			str.append("\n");
		}
		return str.toString();
	}

	public HashMap<String, Double> getData() {
		return new HashMap<>(data);
	}
}
