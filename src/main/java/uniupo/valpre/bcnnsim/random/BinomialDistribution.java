package uniupo.valpre.bcnnsim.random;

import java.util.Formatter;
import java.util.Locale;

public class BinomialDistribution extends Distribution{

    private final int numSamples;

    private final BernoulliDistribution bernoulli;

    public BinomialDistribution(int numSamples, double winRate) {
        this.numSamples = numSamples;
        bernoulli = new BernoulliDistribution(winRate);
    }

    @Override
    public Double generate(RandomGenerator stream) {
        double i, x = 0;
        for (i = 0; i < numSamples; i++)
            x += bernoulli.generate(stream);
        return x;
    }


    @Override
    public String toString() {
        return new Formatter(Locale.US).format("BinomialDistribution(samples=%d,winRate=%.4f)", numSamples,  bernoulli.getWinRate()).toString();
    }
}
