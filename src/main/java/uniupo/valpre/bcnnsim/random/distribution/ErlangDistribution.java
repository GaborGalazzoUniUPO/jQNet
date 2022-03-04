package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class ErlangDistribution extends Distribution {

    private final long numSamples;
    private final ExponentialDistribution exponential;

    public ErlangDistribution(long numSamples, double mean) {
        this.numSamples = numSamples;
        exponential = new ExponentialDistribution(mean);
    }


    @SneakyThrows
    public ErlangDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.numSamples = jsonObject.get("numSamples").getAsLong();
        this.exponential = (ExponentialDistribution) Utils.deserializeJson(jsonObject,"exponential",memory);
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("numSamples", this.numSamples);
        json.add("exponential", exponential.jsonSerialize());
        return json;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        long   i;
        double x = 0.0;
        for (i = 0; i < numSamples; i++)
            x += exponential.generate(stream);
        return (x);
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("ErlangDistribution(numSamples=%d,mean=%.4f)", numSamples, exponential.getMean()).toString();
    }
}
