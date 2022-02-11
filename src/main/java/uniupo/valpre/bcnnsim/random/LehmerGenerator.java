package uniupo.valpre.bcnnsim.random;

import java.util.Date;

public class LehmerGenerator implements RandomGenerator {


	public static final long DEFAULT_MULTIPLIER = 48271;
	public static final long DEFAULT_MODULE = (long) (Math.pow(2, 31) - 1);
	public static final long DEFAULT_SEED = 123456789L; /* initial seed, use 0 < DEFAULT < MODULUS   */

	private final long multiplier;
	private final long modulus;
	private final long q;
	private final long r;
	private long state;


	public LehmerGenerator(long multiplier, long modulus, long seed) {
		this.multiplier = multiplier;
		this.modulus = modulus;
		q = modulus / multiplier;
		r = modulus % multiplier;
		this.state = seed % modulus;
	}

	public void setSeed(long seed) {
		this.state = seed % modulus;
	}

	public LehmerGenerator(long a, long m) {
		this(a, m, new Date().getTime());
	}

	public LehmerGenerator(long seed) {
		this(DEFAULT_MULTIPLIER, DEFAULT_MODULE, seed);
	}

	public LehmerGenerator() {
		this(DEFAULT_SEED);
	}

	public double random() {
		long t = multiplier * (state % q) - r * (state / q);
		if (t > 0) {
			state = t;
		} else {
			state = t + modulus;
		}
		return ((double) state) / modulus;
	}

	@Override
	public long getSeed() {
		return state;
	}
}
