package ravi.contest.ants.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.Constants;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.movement.NavigationAdvisor;

public class ShortestPathCalculator {
	private Walk2 getApproxShortestPathByNavAdvisor(LocalMap map, Point source, Point destination) {
		Walk2 walk = new Walk2(); // this is the returned object.
		walk.visit(source);

		NavigationAdvisor advisor = new NavigationAdvisor();

		// Have we reached our destination?
		boolean destinationReached = false;

		// We are going to experiment with the navigation advisor
		// for a maximum of twice the distance between the 2 points.
		// (This is how far down the rabbit hole we are willing to go
		// before we call it quits with this approach.)
		// This should be more than enough for most easy topologies.
		int maxAllowedDistance = 2 * MapUtils.getDistance(source.x, source.y, destination.x, destination.y);

		Point location = source;
		for (int i=0; (!destinationReached) && (i<maxAllowedDistance); i++) {
			if (location.equals(destination)) {
				// We have reached our destination.
				destinationReached= true;
			} else {
				Direction dir = advisor.advise(location, destination, map, walk);
				if ((dir == null) || (dir == Direction.here)) {
					// We have not reached our destination.
					// Yet the advise is to not move.
					// That can't be right. We are done, unsuccessfully.
					walk = null;
					break;
				}
				// We have a valid advise. Follow it.
				Point newLocation = new Point(location.x, location.y);
				newLocation.move(dir);
				walk.visit(newLocation);
				location = newLocation;
			}
		}

		// Return the walk we have taken, null if not a valid walk.
		return destinationReached ? walk : null;
	}

	private Walk2 getApproxShortestPathByIntervalBFS(LocalMap map, Point source, Point destination) {
		// (1) Create graph of intervals for the map.
		List<GraphNode> prevRowNodes = null;
		GraphNode sourceNode = null;
		GraphNode destNode = null;
		
		for (int i=map.getBottomMargin(); i<=map.getTopMargin(); i++) {
			List<Interval> thisRowIntervals = getIntervalsForY(map, i);
			List<GraphNode> thisRowNodes = new ArrayList<ShortestPathCalculator.GraphNode>();
			for (Interval interval : thisRowIntervals) {
				// Create a graph node for this interval.
				GraphNode node = new GraphNode();
				node._interval = interval;
				// Determine our source node.
				if (interval.contains(source)) {
					sourceNode = node;
				}
				// Determine our destination node.
				if (interval.contains(destination)) {
					destNode = node;
				}
				// Add this graph node to this row's list of graph nodes.
				thisRowNodes.add(node);
				// Determine adjacency with the previous row's list of graph nodes.
				if (prevRowNodes != null) {
					// Check adjacency for each of tbe nodes in the previous row.
					for (GraphNode prevRowNode : prevRowNodes) {
						if (interval.isAdjacent(prevRowNode._interval)) {
							node._neighbors.add(prevRowNode);
							prevRowNode._neighbors.add(node);
						}
					}
				}
			}
			prevRowNodes = thisRowNodes;
		}
		
		// (2) Run BFS on the interval graph.
		boolean destinationReached = false;
		if ((sourceNode != null) && (destNode != null)) {
			// Create and initialize BFS queue.
			List<GraphNode> bfsQueue = new LinkedList<GraphNode>();
			bfsQueue.add(sourceNode);
			sourceNode._distance = 0;
			// Run BFS on our graph.
			while (!bfsQueue.isEmpty()) {
				GraphNode node = bfsQueue.remove(0);
				// Are we there yet?
				if (node == destNode) {
					// We have reached our destination.
					destinationReached = true;
					break;
				} else {
					// We haven't reached our destination.
					// Continue BFS by visiting our neighbors.
					for (GraphNode neighbor : node._neighbors) {
						// Have we visited this node before?
						if (neighbor._distance < 0) {
							// We have NOT visited it before. Enqueue it.
							neighbor._distance = node._distance + 1;
							bfsQueue.add(neighbor);
						}
					}
				}
			}
		} else {
			System.err.println("Source or destination not found in the map!");
		}
		
		// (3) Get a walk along the shortest path for intervals.
		Walk2 walk = null;
		if (destinationReached) {
			// Walk back from dest to source,
			// choosing the shortest interval nodes.
			List<GraphNode> intervalPath = new LinkedList<GraphNode>();
			intervalPath.add(destNode);
			
			for (int dist=destNode._distance-1; dist>=0; dist--) {
				GraphNode node = intervalPath.get(0);
				// Go through my neighbors with distance 1 less than mine.
				// Determine the one with the shortest interval.
				int shortestInterval = Constants.BOARD_SIZE;
				GraphNode shortestIntervalNode = null;
				for (GraphNode neighbor : node._neighbors) {
					if (neighbor._distance == node._distance - 1) {
						int interval = neighbor._interval.xEnd - node._interval.xStart + 1;
						if (interval < shortestInterval) {
							shortestIntervalNode = neighbor;
						}
					}
				}

				// Add this interval to the head of the queue,
				// since we are going from dest back to source.
				intervalPath.add(0, shortestIntervalNode);
			}
			
			// We now have a shortest path for intervals.
			// Walk along this path.
			walk = new Walk2();
			Point currentLocation = new Point(source.x, source.y);
			walk.visit(currentLocation); // Visit the source.

			// Here's the algorithm:
			// (i) If at the destination, go to step (vii).
			// (ii.a) If in destination's interval:
			//        (1) determine direction - east or west - towards destination.
			//        (2) travel along determined direction to destination.
			//        (3) Go to step (vii).
			// (ii.b) If NOT in destination's interval:
			// (iii.a) If adjacent to the next interval, go to step (v).
			// (iii.b) If not adjacent to the next interval,
			//       determine the direction - east or west - towards next interval.
			// (iv) In the current interval, go to the first point adjacent to the next interval.
			// (v) Go to a point in the next interval.
			// (vi) Go to (i).
			// (vii) Stop. Done!
			int currentIntervalDistFromSource = 0;
			while (!currentLocation.equals(destination)) {
				GraphNode currentIntervalNode = intervalPath.get(currentIntervalDistFromSource);
				if (currentIntervalNode._interval.contains(destination)) {
					// We are in destination's interval,
					// although not at the destination itself.
					// Determine direction towards destination.
					// Walk in this direction till we reach destination.
					if (currentLocation.x < destination.x) {
						// Move east.
						for (int i=currentLocation.x + 1; i <= destination.x; i++) {
							currentLocation = new Point(i, destination.y);
							walk.visit(currentLocation);
						}
					} else {
						// Move west.
						for (int i=currentLocation.x - 1; i >= destination.x; i--) {
							currentLocation = new Point(i, destination.y);
							walk.visit(currentLocation);
						}
					}
				} else {
					// We are not in destination's interval.
					// Determine where we are, relative to our next interval.
					GraphNode nextIntervalNode = intervalPath.get(currentIntervalDistFromSource + 1);
					Interval nextInterval = nextIntervalNode._interval;
					// If we are not adjacent to the next interval,
					// walk within our interval towards the next interval.
					if (currentLocation.x + 1 < nextInterval.xStart) {
						// Move towards east.
						for (int i=currentLocation.x+1; i<nextInterval.xStart; i++) {
							currentLocation = new Point(i, currentLocation.y);
							walk.visit(currentLocation);
						}
					} else if (nextInterval.xEnd + 1 < currentLocation.x) {
						// Move towards west.
						for (int i=currentLocation.x-1; i>nextInterval.xEnd; i--) {
							currentLocation = new Point(i, currentLocation.y);
							walk.visit(currentLocation);
						}
					}

					// We are now adjacent to the next interval.
					// Determine which endpoint of that interval we are closest to.
					if (MapUtils.getDistance(currentLocation.x, currentLocation.y, nextInterval.xStart, nextInterval.y) <= 1) {
						// We are adjacent to the next interval's endpoint.
						// Walk into the next interval.
						currentLocation = new Point(nextInterval.xStart, nextInterval.y);
						walk.visit(currentLocation);
					} else if (MapUtils.getDistance(currentLocation.x, currentLocation.y, nextInterval.xEnd, nextInterval.y) <= 1) {
						// We are adjacent to the next interval's endpoint.
						// Walk into the next interval.
						currentLocation = new Point(nextInterval.xEnd, nextInterval.y);
						walk.visit(currentLocation);
					} else {
						// We are not adjacent to the next interval's endpoints.
						// However, we can just walk into it.
						currentLocation = new Point(currentLocation.x, nextInterval.y);
						walk.visit(currentLocation);
					}
					
					// We have now entered into the next interval.
					currentIntervalDistFromSource++;
				}
			}
		}
		
		return walk;
	}

	public Walk2 getApproxShortestPath(LocalMap map, Point source, Point destination) {
		Walk2 walk = getApproxShortestPathByNavAdvisor(map, source, destination);
		if (walk == null) {
			// We were unable to find a path using navigation advisor.
			// Use interval BFS to determine shortest path.
			walk = getApproxShortestPathByIntervalBFS(map, source, destination);
		}
		return walk;
	}

	private List<Interval> getIntervalsForY(LocalMap map, int y) {
		// List of intervals, which is eventually returned by this method.
		List<Interval> intervals = new ArrayList<ShortestPathCalculator.Interval>();

		// Keeps track of the beginning of the current interval.
		int intervalStart = -Constants.BOARD_SIZE - 1; // doesn't matter what we set this to.
		// Keeps track of whether we are currently within an interval.
		boolean intervalStarted = false;
		
		for (int i=map.getLeftMargin(); i<=map.getRightMargin()+1; i++) {
			SquareType squareType = map.getSquareType(i, y);
			// Determine the start of the interval.
			if (!intervalStarted && (squareType != SquareType.UNKNOWN) && (squareType != SquareType.OBSTACLE)) {
				intervalStarted = true;
				intervalStart = i;
			}
			// Determine the end of the interval.
			if (intervalStarted && ((squareType == SquareType.UNKNOWN) || (squareType == SquareType.OBSTACLE))) {
				// Interval ended at the previous x co-ordinate.
				intervals.add(new Interval(intervalStart, i-1, y));
				intervalStarted = false;
				intervalStart = -Constants.BOARD_SIZE - 1; // doesn't matter what we set this to.
			}
		}
		
		// Return the fruit of our labor.
		return intervals;
	}

	private static class GraphNode {
		private Interval _interval; // interval representing this graph node.
		private List<GraphNode> _neighbors = new ArrayList<ShortestPathCalculator.GraphNode>(); // neighbors of this node.
		private int _distance = -1; // distance from a given source.
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("GraphNode[");
			sb.append(_interval);
			sb.append(",neighbors{");
			for (GraphNode neighbor : _neighbors) {
				sb.append(neighbor._interval);
			}
			sb.append("}]");
			return sb.toString();
		}
	}
	
	private static class Interval {
		public int xStart, xEnd, y;

		public Interval(int start, int end, int y) {
			xStart = Math.min(start, end);
			xEnd = Math.max(start, end);
			this.y = y;
		}

		public boolean isAdjacent(Interval interval) {
			// Check if points are adjacent along Y axis.
			if (Math.abs(y - interval.y) <= 1) {
				// Points are 0 or 1 unit away along Y axis.
				// The given interval must not intersect me.
				return !((xEnd < interval.xStart - 1) || (interval.xEnd < xStart - 1));
			} else {
				// Points are farther than 1 unit away,
				// along the Y axis. They cannot be adjacent.
				return false;
			}
		}
		
		public boolean contains(Point p) {
			return (xStart <= p.x) && (p.x <= xEnd) && (p.y == y);
		}
		
		public boolean equals(Object o) {
			if (o != null) {
				if (o instanceof Interval) {
					Interval that = (Interval) o;
					return (this.xStart == that.xStart) && (this.xEnd == that.xEnd) && (this.y == that.y);
				}
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return 17 * xStart + 31 * xEnd + 61 * y;
		}
		
		public String toString() {
			return "Interval[y=" + y + ",x=(" + xStart + "," + xEnd + ")]";
		}
	}
}
