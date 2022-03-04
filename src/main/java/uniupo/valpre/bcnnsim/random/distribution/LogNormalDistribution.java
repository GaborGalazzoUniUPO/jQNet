package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class LogNormalDistribution extends Distribution
{

	private final NormalDistribution normalDistribution = NormalDistribution.DEFAULT;
	private final double a;
	private final double b;

	public LogNormalDistribution(double a, double b) {
		this.a = a;
		this.b = b;
	}


	@SneakyThrows
	public LogNormalDistribution(JsonObject jsonObject, Map<String, Node> memory){
		this.a = jsonObject.get("a").getAsDouble();
		this.b = jsonObject.get("b").getAsDouble();
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
		return  (Math.exp(a + b * normalDistribution.generate(stream)));
	}

	@Override
	public String toString() {
		return new Formatter(Locale.US).format("LogNormalDistribution(a=%.4f,b=%.4f)", a,b).toString();
	}
}
