package uniupo.valpre.bcnnsim.random;

public interface RandomGenerator {

    public double random();

	long getSeed();
	public void setSeed(long seed);
}
