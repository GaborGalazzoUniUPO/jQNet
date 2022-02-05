package uniupo.valpre.bcnnsim.random;

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
}
