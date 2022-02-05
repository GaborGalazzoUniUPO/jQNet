package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class UniformDistribution extends Distribution{

    private final long min;
    private final long max;

    public UniformDistribution(long min, long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (stream.random() * max) + min;
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("UniformDistribution(min=%d,max=%d)", min, max).toString();
    }
}
