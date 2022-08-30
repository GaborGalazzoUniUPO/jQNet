package uniupo.valpre.bcnnsim.network.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uniupo.valpre.bcnnsim.network.classes.CustomerClass;
import uniupo.valpre.bcnnsim.network.node.Node;

@AllArgsConstructor
@Getter
public abstract class Event implements Comparable<Event>
{
	private final Node referenceStation;
	private final CustomerClass customerClass;
	private final double time;


	@Override
	public int compareTo(Event o)
	{
		return Double.compare(time, o.getTime());
	}
}
