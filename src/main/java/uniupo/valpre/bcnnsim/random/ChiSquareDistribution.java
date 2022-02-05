package uniupo.valpre.bcnnsim.random;

public class ChiSquareDistribution  extends Distribution{
    private final long n;
    private final NormalDistribution normalDistribution;

    public ChiSquareDistribution(long n) {
        this.n = n;
        this.normalDistribution = NormalDistribution.DEFAULT;

    }

    @Override
    public Double generate(RandomGenerator stream) {
        long   i;
        double z, x = 0.0;

        for (i = 0; i < n; i++) {
            z  = normalDistribution.generate(stream);
            x += z * z;
        }
        return (x);
    }
}
