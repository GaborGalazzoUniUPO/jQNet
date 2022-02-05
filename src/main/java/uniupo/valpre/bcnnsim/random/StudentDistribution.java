package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class StudentDistribution extends Distribution {
    private final long freedomDegree;
    private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;
    private final ChiSquareDistribution chiSquareDistribution;

    public StudentDistribution(long freedomDegree) {
        this.freedomDegree = freedomDegree;
        chiSquareDistribution = new ChiSquareDistribution(freedomDegree);

    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (normalDistribution.generate(stream) / Math.sqrt(chiSquareDistribution.generate(stream) / freedomDegree));
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("StudentDistribution(freedomDegree=%d)", freedomDegree).toString();
    }
}
