package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import uniupo.valpre.bcnnsim.Utils;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Map;

public class HyperExponentialDistribution extends Distribution{

	private final double probability;
	private final ExponentialDistribution exponentialDistribution1;
	private final ExponentialDistribution exponentialDistribution2;

	public HyperExponentialDistribution(double p , double m1, double m2) {
		this.probability = p;
		this.exponentialDistribution1 = new ExponentialDistribution(m1);
		this.exponentialDistribution2 = new ExponentialDistribution(m2);
	}


	@SneakyThrows
	public HyperExponentialDistribution(JsonObject jsonObject, Map<String, Node> memory){
		this.probability = jsonObject.get("probability").getAsDouble();
		this.exponentialDistribution1 = (ExponentialDistribution) Utils.deserializeJson(jsonObject,"exponentialDistribution1",memory);
		this.exponentialDistribution2 = (ExponentialDistribution) Utils.deserializeJson(jsonObject,"exponentialDistribution2",memory);
	}

	@Override
	public JsonObject jsonSerialize() {
		var json =  super.jsonSerialize();
		json.addProperty("probability", this.probability);
		json.add("exponentialDistribution1", exponentialDistribution1.jsonSerialize());
		json.add("exponentialDistribution2", exponentialDistribution2.jsonSerialize());
		return json;
	}

	@Override
	public Double generate(RandomGenerator stream) {
		if(stream.random()<=probability){
			return this.exponentialDistribution1.generate(stream);
		}
		return this.exponentialDistribution2.generate(stream);
	}
}
