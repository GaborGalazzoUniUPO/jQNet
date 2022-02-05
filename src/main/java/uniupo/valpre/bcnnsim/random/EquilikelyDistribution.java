package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class EquilikelyDistribution  extends Distribution{
    private final long a;
    private final long b;

    public EquilikelyDistribution(long a, long b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (double) Math.round(a + ((b - a + 1) * stream.random()));
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("EquilikelyDistribution(a=%d,b=%d)", a,b).toString();
    }
}
