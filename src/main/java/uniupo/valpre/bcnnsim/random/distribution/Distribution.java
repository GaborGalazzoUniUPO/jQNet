package uniupo.valpre.bcnnsim.random.distribution;

import uniupo.valpre.bcnnsim.random.RandomGenerator;
import uniupo.valpre.bcnnsim.serializer.JsonSerializable;

import java.util.Random;

public abstract class Distribution extends JsonSerializable
{
	public abstract Double generate(RandomGenerator stream);
}
