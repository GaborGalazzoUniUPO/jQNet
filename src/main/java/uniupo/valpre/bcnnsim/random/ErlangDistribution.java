package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class ErlangDistribution extends Distribution {

    private final long numSamples;
    private final ExponentialDistribution exponential;

    public ErlangDistribution(long numSamples, double mean) {
        this.numSamples = numSamples;
        exponential = new ExponentialDistribution(mean);
    }

    @Override
    public Double generate(RandomGenerator stream) {
        long   i;
        double x = 0.0;
        for (i = 0; i < numSamples; i++)
            x += exponential.generate(stream);
        return (x);
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("ErlangDistribution(numSamples=%d,mean=%.4f)", numSamples, exponential.getMean()).toString();
    }
}
