package uniupo.valpre.bcnnsim.random;

public class BernoulliDistribution  extends Distribution{

    private final double p;
    public BernoulliDistribution(double p) {
        this.p = p;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return ((stream.random() < (1.0 - p)) ? 0.0 : 1.0);
    }
}
