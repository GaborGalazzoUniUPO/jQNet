package uniupo.valpre.bcnnsim.random;

public class StudentDistribution extends Distribution {
    private final long n;
    private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;
    private final ChiSquareDistribution chiSquareDistribution;

    public StudentDistribution(long n) {
        this.n = n;
        chiSquareDistribution = new ChiSquareDistribution(n);

    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (normalDistribution.generate(stream) / Math.sqrt(chiSquareDistribution.generate(stream) / n));
    }
}
