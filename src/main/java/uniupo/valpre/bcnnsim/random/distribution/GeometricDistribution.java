package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class GeometricDistribution extends Distribution{

    private final double p;

    public GeometricDistribution(double p) {
        this.p = p;
    }

    public GeometricDistribution(JsonObject jsonObject, Map<String, Object> memory){
        this.p = jsonObject.get("p").getAsDouble();
    }

    @Override
    public JsonObject jsonSerialize() {
        var json =  super.jsonSerialize();
        json.addProperty("p", this.p);
        return json;
    }

    @Override
    public Double generate(RandomGenerator stream) {
        return (Math.log(1.0 - stream.random()) / Math.log(p));
    }


    @Override
    public String toString()
    {
        return new Formatter(Locale.US).format("GeometricDistribution(p=%.4f)", p).toString();
    }
}
