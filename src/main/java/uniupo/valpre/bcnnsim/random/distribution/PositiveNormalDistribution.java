package uniupo.valpre.bcnnsim.random.distribution;

import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.node.Node;
import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.random.distribution.NormalDistribution;

import java.util.Map;

public class PositiveNormalDistribution extends NormalDistribution {
    public PositiveNormalDistribution(double mean, double sigma) {
        super(mean, sigma);
    }

    public PositiveNormalDistribution(JsonObject jsonObject, Map<String, Node> memory) {
        super(jsonObject, memory);
    }

    @Override
    public Double generate(RandomGenerator rng) {
        double result;
        // get the job at the head of the queue
        while ((result = super.generate(rng)) < 0) ;

        return result;
    }
}
