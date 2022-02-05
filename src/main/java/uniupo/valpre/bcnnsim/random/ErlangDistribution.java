package uniupo.valpre.bcnnsim.random;

public class ErlangDistribution extends Distribution {

    private final long n;
    private final ExponentialDistribution exponential;

    public ErlangDistribution(long n, double mean) {
        this.n = n;
        exponential = new ExponentialDistribution(mean);
    }

    @Override
    public Double generate(RandomGenerator stream) {
        long   i;
        double x = 0.0;
        for (i = 0; i < n; i++)
            x += exponential.generate(stream);
        return (x);
    }
}
