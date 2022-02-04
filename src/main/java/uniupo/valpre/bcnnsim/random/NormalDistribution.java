package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

import static java.lang.Math.PI;

public class NormalDistribution extends Distribution
{
	private final double mean;
	private final double sigma;

	public NormalDistribution(double mean, double sigma)
	{
		this.mean = mean;
		this.sigma = sigma;
	}

	@Override
	public Double generate(Random stream)
	{
		double result;
		// get the job at the head of the queue
		while ((result = normal(stream, mean, sigma)) < 0) ;

		return result;
	}

	private int numNormals = 0;
	private double saveNormal;

	private double normal(Random rng, double mean, double sigma)
	{
		double result;
		// should we generate two normals?
		if (numNormals == 0)
		{
			double r1 = rng.nextDouble();
			double r2 = rng.nextDouble();
			double sqrt = Math.sqrt(-2 * Math.log(r1));
			result = sqrt * Math.cos(2 * PI * r2);
			saveNormal = sqrt * Math.sin(2 * PI * r2);
			numNormals = 1;
		} else
		{
			numNormals = 0;
			result = saveNormal;
		}
		return result * sigma + mean;
	}

	@Override
	public String toString()
	{
		return new Formatter(Locale.US).format("NormalDistribution(μ=%.4f,σ=%.4f)", mean, sigma).toString();
	}
}
