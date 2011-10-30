package ravi.contest.ants.state;

import ravi.contest.ants.Constants;

public class StateUtils {
	public static boolean isOracle(int id) {
		return (id == Constants.NUM_ANTS - 1);
	}
}
