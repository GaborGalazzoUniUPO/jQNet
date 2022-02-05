package uniupo.valpre.bcnnsim.random;

public class GeometricDistribution extends Distribution{

    private final double p;

    public GeometricDistribution(double p) {
        this.p = p;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (Math.log(1.0 - stream.random()) / Math.log(p));
    }
}
