package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

import static java.lang.Math.PI;

public class NormalDistribution extends Distribution
{
	public static final NormalDistribution DEFAULT = new NormalDistribution(0.0, 1.0);
	private final double mean;
	private final double sigma;

	public NormalDistribution(double mean, double sigma)
	{
		this.mean = mean;
		this.sigma = sigma;
	}

	public Double generatePositive(RandomGenerator stream)
	{
		double result;
		// get the job at the head of the queue
		while ((result = generate(stream)) < 0) ;

		return result;
	}

	public Double generate(RandomGenerator rng)
	{
		final double p0 = 0.322232431088;     final double q0 = 0.099348462606;
		final double p1 = 1.0;                final double q1 = 0.588581570495;
		final double p2 = 0.342242088547;     final double q2 = 0.531103462366;
		final double p3 = 0.204231210245e-1;  final double q3 = 0.103537752850;
		final double p4 = 0.453642210148e-4;  final double q4 = 0.385607006340e-2;
		double u, t, p, q, z;

		u   = rng.random();
		if (u < 0.5)
			t = Math.sqrt(-2.0 * Math.log(u));
		else
			t = Math.sqrt(-2.0 * Math.log(1.0 - u));
		p   = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
		q   = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));
		if (u < 0.5)
			z = (p / q) - t;
		else
			z = t - (p / q);
		return (mean + sigma * z);
	}

	@Override
	public String toString()
	{
		return new Formatter(Locale.US).format("NormalDistribution(μ=%.4f,σ=%.4f)", mean, sigma).toString();
	}
}
