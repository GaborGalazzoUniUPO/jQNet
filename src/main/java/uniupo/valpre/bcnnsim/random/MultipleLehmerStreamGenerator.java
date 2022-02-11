package uniupo.valpre.bcnnsim.random;

import java.util.ArrayList;
import java.util.List;

public class MultipleLehmerStreamGenerator implements MultipleStreamGenerator {

	private final long nStreams;
	private final long jump;
	private final long multiplier;
	private final long modulus;


	private static final long DEFAULT_N_STREAMS = 256;
	private static final long DEFAULT_JUMP = 22925;


	public MultipleLehmerStreamGenerator(long multiplier, long modulus, long nStreams, long jump) {
		this.multiplier = multiplier;
		this.modulus = modulus;
		this.nStreams = nStreams;
		this.jump = jump;
	}

	public MultipleLehmerStreamGenerator(long multiplier, long modulus) {
		this(multiplier, modulus, DEFAULT_N_STREAMS, DEFAULT_JUMP);
	}

	public MultipleLehmerStreamGenerator() {
		this(LehmerGenerator.DEFAULT_MULTIPLIER, LehmerGenerator.DEFAULT_MODULE);
	}

	@Override
	public List<RandomGenerator> generateStreams(long seed) {
		ArrayList<RandomGenerator> streams = new ArrayList<>();
		long Q = modulus / jump;
		long R = modulus % jump;
		int j;
		streams.add(new LehmerGenerator(multiplier, modulus, seed));
		for (j = 1; j < nStreams; j++) {
			seed = jump * (streams.get(j - 1).getSeed() % Q) - R * (streams.get(j - 1).getSeed() / Q);
			if (seed > 0)
				streams.add(new LehmerGenerator(multiplier, modulus, seed));
			else
				streams.add(new LehmerGenerator(multiplier, modulus, seed + modulus));
		}
		return streams;
	}

	public long getNStreams() {
		return nStreams;
	}

	public long getJump() {
		return jump;
	}

	public long getMultiplier() {
		return multiplier;
	}

	public long getModulus() {
		return modulus;
	}
}
