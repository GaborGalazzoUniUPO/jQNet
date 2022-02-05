package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class BernoulliDistribution  extends Distribution{

    private final double winRate;
    public BernoulliDistribution(double winRate) {
        this.winRate = winRate;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return ((stream.random() < (1.0 - winRate)) ? 0.0 : 1.0);
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("BernoulliDistribution(winRate=%.4f)", winRate).toString();
    }

    public Double getWinRate() {
        return winRate;
    }
}
