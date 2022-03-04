package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class EquilikelyDistribution  extends Distribution{
    private final long a;
    private final long b;

    public EquilikelyDistribution(long a, long b) {
        this.a = a;
        this.b = b;
    }

    @SneakyThrows
    public EquilikelyDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.a = jsonObject.get("a").getAsLong();
        this.b = jsonObject.get("b").getAsLong();
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("a", this.a);
        json.addProperty("b", this.b);
        return json;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (double) Math.round(a + ((b - a + 1) * stream.random()));
    }

    @Override
    public String toString() {
        return new Formatter(Locale.US).format("EquilikelyDistribution(a=%d,b=%d)", a,b).toString();
    }
}
