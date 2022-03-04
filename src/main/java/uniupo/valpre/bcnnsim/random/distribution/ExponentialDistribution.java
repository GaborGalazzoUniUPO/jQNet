package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.random.RandomGenerator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class ExponentialDistribution extends Distribution
{
	public static final ExponentialDistribution DEFAULT = new ExponentialDistribution(1);
	private final double mean;

	public ExponentialDistribution(double mean)
	{
		super();
		this.mean = mean;
	}

	public ExponentialDistribution(JsonObject jsonObject, Map<String, Object> memory){
		this.mean = jsonObject.get("mean").getAsDouble();
	}

	@Override
	public JsonObject jsonSerialize() {
		var json =  super.jsonSerialize();
		json.addProperty("mean", this.mean);
		return json;
	}

	@Override
	public Double generate(RandomGenerator stream)
	{
		return -mean*Math.log( stream.random() );
	}

	@Override
	public String toString()
	{
		return new Formatter(Locale.US).format("ExponentialDistribution(μ=%.4f)", mean).toString();
	}

	public double getMean() {
		return mean;
	}
}
