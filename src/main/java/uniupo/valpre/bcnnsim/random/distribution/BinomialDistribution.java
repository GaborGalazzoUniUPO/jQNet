package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class BinomialDistribution extends Distribution{

    private final int numSamples;

    private final BernoulliDistribution bernoulli;

    public BinomialDistribution(int numSamples, double winRate) {
        this.numSamples = numSamples;
        bernoulli = new BernoulliDistribution(winRate);
    }

    @SneakyThrows
    public BinomialDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.numSamples = jsonObject.get("numSamples").getAsInt();
        this.bernoulli = (BernoulliDistribution) Utils.deserializeJson(jsonObject, "numSamples", memory);
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("numSamples", this.numSamples);
        json.add("bernoulli", this.bernoulli.jsonSerialize());
        return json;
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
