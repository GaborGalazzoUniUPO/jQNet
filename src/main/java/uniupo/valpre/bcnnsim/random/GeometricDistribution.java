package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class GeometricDistribution extends Distribution{

    private final double p;

    public GeometricDistribution(double p) {
        this.p = p;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (Math.log(1.0 - stream.random()) / Math.log(p));
    }


    @Override
    public String toString()
    {
        return new Formatter(Locale.US).format("GeometricDistribution(p=%.4f)", p).toString();
    }
}
