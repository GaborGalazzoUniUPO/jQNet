package uniupo.valpre.bcnnsim.random;

public interface RandomGenerator {

    double random();

	long getSeed();
	void setSeed(long seed);
}
