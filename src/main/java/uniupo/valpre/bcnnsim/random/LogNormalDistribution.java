package uniupo.valpre.bcnnsim.random;

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
}
