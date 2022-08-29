package uniupo.valpre.bcnnsim.sim;

import uniupo.valpre.bcnnsim.random.Student;

import java.io.PrintStream;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;

public class MultiRunNetworkReport {
	private final int runs;
	private final List<NetworkReport> reports;

	public MultiRunNetworkReport(int runs, List<NetworkReport> reports) {
		this.runs = runs;
		this.reports = reports;
	}

	public HashMap<String, HashMap<String, ValueStream>> getResults() {
		var all = new HashMap<String, HashMap<String, ValueStream>>();
		for (NetworkReport report : reports) {
			for (Map.Entry<String, NodeReport> e : report.getNodeReports().entrySet()) {
				var l = all.getOrDefault(e.getKey(), new HashMap<>());
				for (Map.Entry<String, Double> data : e.getValue().getData().entrySet()) {
					var vs = l.getOrDefault(data.getKey(), new ValueStream());
					vs.add(data.getValue());
					l.put(data.getKey(), vs);
					vs.setAbsolute(e.getValue().isAbsolute(data.getKey()));
				}
				all.put(e.getKey(), l);
			}
		}

		return all;

	}

	public static class ValueStream {
		private final List<Double> values = new ArrayList<>();
		private boolean isAbsolute = false;

		public void add(Double value) {
			values.add(value);
		}

		public MetricStatistics getMetricStatistics(Double alphaLevel, PrecisionType precisionType, Double precisionRequirement) {

			var sum = values.stream().reduce(Double::sum).orElseThrow();
			var mean = sum / values.size();
			sum = values.stream().reduce((a, v) -> Math.pow(v - mean, 2) + a).orElseThrow();
			var stdev = Math.sqrt(sum / (float) (values.size() - 1));

			if (isAbsolute) {
				return new MetricStatistics(mean, stdev, null, null, null, true);
			}

			var stdevOfMean = stdev / Math.sqrt(values.size() - 1);
			var tCritical = Student.getInstance().idfStudent(values.size() - 1, (1 + alphaLevel) * 0.5);
			var tc = tCritical * stdevOfMean;

			var mss = 0;
			if (precisionType == PrecisionType.Absolute) {
				mss = (int) Math.ceil(Math.pow(tCritical * stdev / precisionRequirement, 2));
			} else {
				mss = (int) Math.ceil(Math.pow(tCritical * stdev / (precisionRequirement * mean), 2));
			}

			return new MetricStatistics(mean, stdev, tc, mss, alphaLevel,  mss< values.size());
		}


		public boolean isAbsolute() {
			return isAbsolute;
		}

		public void setAbsolute(boolean absolute) {
			isAbsolute = absolute;
		}
	}

	public record MetricStatistics(Double mean, Double sd, Double tc, Integer mss, Double lv, boolean done) {
	}

	public enum PrecisionType {
		Absolute,
		Relative
	}
}
