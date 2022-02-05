package uniupo.valpre.bcnnsim.random;

public class BinomialDistribution extends Distribution{

    private final int n;
    private final double p;

    private final BernoulliDistribution bernoulli;

    public BinomialDistribution(int n, double p) {
        this.n = n;
        this.p = p;
        bernoulli = new BernoulliDistribution(p);
    }

    @Override
    public Double generate(RandomGenerator stream) {
        double i, x = 0;
        for (i = 0; i < n; i++)
            x += bernoulli.generate(stream);
        return x;
    }
}
