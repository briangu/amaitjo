package ravi.contest.ants.oracle;

import java.util.Map;

import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.message.TopologySay;

public class OracleUtils {
	public static void updateTopology(Knowledge knowledge, TopologySay say) {
		if (say != null) {
			Map<Point, SquareType> topology = say.getTopology();
			System.out.println("Consuming Topology report - " + say.size() + " points.");
			if (topology != null) {
				for (Map.Entry<Point, SquareType> entry : topology.entrySet()) {
					Point p = entry.getKey();
					knowledge.getMap().setSquareType(p.x, p.y, entry.getValue());
				}
			}
		} else {
			System.out.println("Topology report is null!");
		}
	}
}
