package uniupo.valpre.bcnnsim.random;

public class HyperExponentialDistribution extends Distribution{

	private final double probability;
	private final ExponentialDistribution exponentialDistribution1;
	private final ExponentialDistribution exponentialDistribution2;

	public HyperExponentialDistribution(double p , double m1, double m2) {
		this.probability = p;
		this.exponentialDistribution1 = new ExponentialDistribution(m1);
		this.exponentialDistribution2 = new ExponentialDistribution(m2);
	}

	@Override
	public Double generate(RandomGenerator stream) {
		if(stream.random()<=probability){
			return this.exponentialDistribution1.generate(stream);
		}
		return this.exponentialDistribution2.generate(stream);
	}
}
