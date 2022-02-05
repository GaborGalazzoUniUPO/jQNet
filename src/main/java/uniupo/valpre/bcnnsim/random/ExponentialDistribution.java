package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

public class ExponentialDistribution extends Distribution
{
	public static final ExponentialDistribution DEFAULT = new ExponentialDistribution(1);
	private final double mean;

	public ExponentialDistribution(double mean)
	{
		super();
		this.mean = mean;
	}

	@Override
	public Double generate(RandomGenerator stream)
	{
		return -mean*Math.log( stream.random() );
	}

	@Override
	public String toString()
	{
		return new Formatter(Locale.US).format("ExponentialDistribution(μ=%.4f)", mean).toString();
	}
}
