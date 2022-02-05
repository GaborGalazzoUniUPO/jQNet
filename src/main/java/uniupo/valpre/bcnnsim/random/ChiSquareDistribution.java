package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class ChiSquareDistribution extends Distribution {
    private final long freedomDegree;
    private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;

    public ChiSquareDistribution(long freedomDegree) {
        this.freedomDegree = freedomDegree;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        long i;
        double z, x = 0.0;

        for (i = 0; i < freedomDegree; i++) {
            z = normalDistribution.generate(stream);
            x += z * z;
        }
        return (x);
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("ChiSquareDistribution(freedomDegree=%d)", freedomDegree).toString();
    }
}
