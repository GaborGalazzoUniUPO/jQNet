package uniupo.valpre.bcnnsim.random;

import java.util.List;

public interface MultipleStreamGenerator {
	List<RandomGenerator> generateStreams(long seed);
}
