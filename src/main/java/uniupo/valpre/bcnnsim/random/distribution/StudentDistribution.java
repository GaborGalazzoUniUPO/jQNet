package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class StudentDistribution extends Distribution {
    private final long freedomDegree;
    private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;
    private final ChiSquareDistribution chiSquareDistribution;

    public StudentDistribution(long freedomDegree) {
        this.freedomDegree = freedomDegree;
        chiSquareDistribution = new ChiSquareDistribution(freedomDegree);

    }

    @SneakyThrows
    public StudentDistribution(JsonObject jsonObject, Map<String, Node> memory){
        this.freedomDegree = jsonObject.get("freedomDegree").getAsInt();
        this.chiSquareDistribution = (ChiSquareDistribution) Utils.deserializeJson(jsonObject,"chiSquareDistribution",memory);
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("freedomDegree", this.freedomDegree);
        json.add("chiSquareDistribution", chiSquareDistribution.jsonSerialize());
        return json;
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
