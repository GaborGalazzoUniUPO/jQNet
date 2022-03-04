package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class BernoulliDistribution  extends Distribution{

    private final double winRate;
    public BernoulliDistribution(double winRate) {
        this.winRate = winRate;
    }

    public BernoulliDistribution(JsonObject jsonObject, Map<String, Object> memory){
        this.winRate = jsonObject.get("winRate").getAsDouble();
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("winRate", this.winRate);
        return json;
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
