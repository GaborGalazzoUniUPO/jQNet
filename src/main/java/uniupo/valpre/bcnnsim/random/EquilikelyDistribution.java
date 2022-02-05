package uniupo.valpre.bcnnsim.random;

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
}
