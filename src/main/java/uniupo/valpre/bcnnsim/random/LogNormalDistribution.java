package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class LogNormalDistribution extends Distribution
{

	private final NormalDistribution normalDistribution;
	private final double a;
	private final double b;

	public LogNormalDistribution(double a, double b) {
		this.a = a;
		this.b = b;
		normalDistribution = NormalDistribution.DEFAULT;
	}

	@Override
	public Double generate(RandomGenerator stream) {
		return  (Math.exp(a + b * normalDistribution.generate(stream)));
	}

	@Override
	public String toString() {
		return new Formatter(Locale.US).format("LogNormalDistribution(a=%.4f,b=%.4f)", a,b).toString();
	}
}
