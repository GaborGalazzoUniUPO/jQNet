package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class UniformDistribution extends Distribution{

    private final long min;
    private final long max;

    public UniformDistribution(long min, long max) {
        this.min = min;
        this.max = max;
    }


    @SneakyThrows
    public UniformDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.min = jsonObject.get("min").getAsLong();
        this.max = jsonObject.get("max").getAsLong();
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("min", this.min);
        json.addProperty("max", this.max);
        return json;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (stream.random() * max) + min;
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("UniformDistribution(min=%d,max=%d)", min, max).toString();
    }
}
