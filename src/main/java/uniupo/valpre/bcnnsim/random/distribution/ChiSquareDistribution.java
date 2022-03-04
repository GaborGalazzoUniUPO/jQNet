package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class ChiSquareDistribution extends Distribution {
    private final long freedomDegree;
    private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;

    public ChiSquareDistribution(long freedomDegree) {
        this.freedomDegree = freedomDegree;
    }

    @SneakyThrows
    public ChiSquareDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.freedomDegree = jsonObject.get("freedomDegree").getAsLong();
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("freedomDegree", this.freedomDegree);
        return json;
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
