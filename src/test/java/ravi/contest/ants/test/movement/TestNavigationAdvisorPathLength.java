package ravi.contest.ants.test.movement;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.movement.MovementUtils;
import ravi.contest.ants.movement.NavigationAdvisor;


public class TestNavigationAdvisorPathLength {
	public static void main(String[] args) {
		for (int i=10; i<56; i++) {
			double obstacleDensity = i * 1.0 / 100;
			simulate(512, obstacleDensity, 10000);
		}
	}
	
	private static void simulate(int squareSize, double obstacleDensity, int numIterations) {
		Map<Integer, Long> distanceWalkLengths = new HashMap<Integer, Long>();
		Map<Integer, Integer> distanceNumRuns = new HashMap<Integer, Integer>();
		Map<Integer, Integer> distanceUnreachable = new HashMap<Integer, Integer>();
		
		// Initialize map.
		LocalMap map = createEmptyMap(squareSize, squareSize);
		addObstacles(map, squareSize, squareSize, obstacleDensity);
		
		for (int i=0; i<numIterations; i++) {
			// Initialize source and destination.
			Point source = new Point((int) (Math.random() * (squareSize - 2)) + 1, (int) (Math.random() * (squareSize - 2)) + 1);
			map.setSquareType(source.x, source.y, LocalMap.SquareType.EMPTY);
			Point dest = new Point((int) (Math.random() * (squareSize - 2)) + 1, (int) (Math.random() * (squareSize - 2)) + 1);
			map.setSquareType(dest.x, dest.y, LocalMap.SquareType.EMPTY);

			//print(map, squareSize, squareSize, source, dest);

			// Compute distance and path.
			int distance = MapUtils.getDistance(source.x, source.y, dest.x, dest.y);
			int walkLength = getPathLength(map, source, dest);

			// Add to our statistics bank.
			addStats(distanceWalkLengths, distanceNumRuns, distanceUnreachable, distance, walkLength);
			//printStats(squareSize, obstacleDensity, distanceWalkLengths, distanceNumRuns, distanceUnreachable);
		}
		
		printStats(squareSize, obstacleDensity, distanceWalkLengths, distanceNumRuns, distanceUnreachable);
	}
	
	private static void addStats(Map<Integer, Long> walkLengths, Map<Integer, Integer> numRuns, Map<Integer, Integer> unreachable, int distance, int walkLength) {
		if (walkLength >= 0) {
			Long lengthSum = walkLengths.get(distance);
			if (lengthSum == null) {
				lengthSum = 0L;
			}
			walkLengths.put(distance, lengthSum + walkLength);
			
			Integer runs = numRuns.get(distance);
			if (runs == null) {
				runs = 0;
			}
			numRuns.put(distance, runs + 1);
		} else {
			Integer numUnreachableRuns = unreachable.get(distance);
			if (numUnreachableRuns == null) {
				numUnreachableRuns = 0;
			}
			unreachable.put(distance, numUnreachableRuns + 1);
		}
	}
	
	private static void printStats(int squareSize, double obstacleDensity, Map<Integer, Long> walkLengths, Map<Integer, Integer> numRuns, Map<Integer, Integer> unreachable) {
		SortedSet<Integer> sortedDistances = new TreeSet<Integer>(walkLengths.keySet());
		int overallNumRuns = 0;
		double overallTotalWalkLengthRatio = 0;
		for (Integer distance : sortedDistances) {
			long totalWalkLength = (walkLengths.containsKey(distance) ? walkLengths.get(distance) : 0);
			int numRunsForDistance = (numRuns.containsKey(distance) ? numRuns.get(distance) : 0);
			int numUnreachables = (unreachable.containsKey(distance) ? unreachable.get(distance) : 0);
			double avgWalkLength = numRunsForDistance > 0 ? (totalWalkLength * 1.0D / numRunsForDistance) : 0;
			double walkLengthRatio = (avgWalkLength / distance);
			double unreachableRatio = (numRunsForDistance + numUnreachables) > 0 ? (numUnreachables * 1.0D / (numRunsForDistance + numUnreachables)) : 0;
			//System.out.println(distance + "\t" + (numRunsForDistance + numUnreachables) + "\t" + walkLengthRatio + " [" + totalWalkLength + "/" + numRunsForDistance * distance + "]" + "\t" + unreachableRatio + " [" + numUnreachables + "]");
			
			overallNumRuns += numRunsForDistance;
			overallTotalWalkLengthRatio += walkLengthRatio * numRunsForDistance;
		}
		
		System.out.println(obstacleDensity + "\t" + (overallTotalWalkLengthRatio / overallNumRuns));
	}
	
	private static int getPathLength(LocalMap map, Point source, Point dest) {
		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(source.x, source.y);
		Walk2 walk = new Walk2();

		int distance = MapUtils.getDistance(source.x, source.y, dest.x, dest.y);
		int pathDistance = 0;
		boolean pathExists = false;
		
		for (int i=0; i<distance*10; i++) {
			walk.visit(location);
			Direction dir = advisor.advise(location, dest, map, walk);
			if (dir == null) {
//				System.out.println("No path to destination possible!");
				break;
			} else if (dir == Direction.here) {
//				System.out.println(dir);
//				System.out.println("Reached destination!");
				pathExists = true;
				break;
			} else {
//				System.out.println(dir);
			}
			location = MovementUtils.move(location, dir);
			pathDistance++;
		}
		
		if (pathExists) {
			return pathDistance;
		} else {
			return -1;
		}
	}

	private static LocalMap createEmptyMap(int xLength, int yLength) {
		LocalMap map = new LocalMap();

		// Lower x margin
		for (int i=0; i<xLength; i++) {
			map.setSquareType(i, 0, LocalMap.SquareType.OBSTACLE);
		}
		// Upper x margin
		for (int i=0; i<xLength; i++) {
			map.setSquareType(i, yLength-1, LocalMap.SquareType.OBSTACLE);
		}
		// Left y margin
		for (int i=0; i<yLength; i++) {
			map.setSquareType(0, i, LocalMap.SquareType.OBSTACLE);
		}
		// Right y margin
		for (int i=0; i<yLength; i++) {
			map.setSquareType(xLength-1, i, LocalMap.SquareType.OBSTACLE);
		}
		// Fill interior.
		for (int i=1; i<xLength-1; i++) {
			for (int j=1; j<yLength-1; j++) {
				map.setSquareType(i, j, LocalMap.SquareType.EMPTY);
			}
		}

		return map;
	}
	
	private static void addObstacles(LocalMap map, int xLength, int yLength, double obstacleDensity) {
		for (int i=1; i<xLength-1; i++) {
			for (int j=1; j<yLength-1; j++) {
				if (Math.random() < obstacleDensity) {
					map.setSquareType(i, j, LocalMap.SquareType.OBSTACLE);
				}
			}
		}
	}

	private static void print(LocalMap map, int xLength, int yLength, Point source, Point destination) {
		for (int j=yLength-1; j>=0; j--) {
			System.out.format("%03d: ", j);
			for (int i=0; i<xLength; i++) {
				if ((source.x == i) && (source.y == j)) {
					System.out.print("@");
				} else if ((destination.x == i) && (destination.y == j)) {
					System.out.print("$");
				} else if (map.getSquareType(i, j) == LocalMap.SquareType.OBSTACLE) {
					System.out.print("#");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}
}
