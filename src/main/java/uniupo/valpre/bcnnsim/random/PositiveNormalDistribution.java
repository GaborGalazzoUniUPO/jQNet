package uniupo.valpre.bcnnsim.random;

public class PositiveNormalDistribution extends NormalDistribution{
    public PositiveNormalDistribution(double mean, double sigma) {
        super(mean, sigma);
    }

    @Override
    public Double generate(RandomGenerator rng) {
        double result;
        // get the job at the head of the queue
        while ((result = super.generate(rng)) < 0) ;

        return result;
    }
}
