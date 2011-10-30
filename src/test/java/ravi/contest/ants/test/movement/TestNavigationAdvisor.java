package ravi.contest.ants.test.movement;


import junit.framework.TestCase;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.movement.MovementUtils;
import ravi.contest.ants.movement.NavigationAdvisor;


public class TestNavigationAdvisor extends TestCase {
	public void testOne() {
		NavigationAdvisor advisor = new NavigationAdvisor();

		LocalMap map = createEmptyMap(8, 8);
		Point location = new Point(1, 1);
		Point dest = new Point(6,6);
		Walk2 walk = new Walk2();

		for (int i=0; i<5; i++)  {
			Direction dir = advisor.advise(location, dest, map, walk);
			assertEquals(Direction.northeast, dir);
			walk.visit(location);
			location = MovementUtils.move(location, dir);
		}
		
		Direction dir = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.here, dir);
	}
	
	/**
	 * ########
	 * #..@...#
	 * #.##..##
	 * ##.##..#
	 * #....#.#
	 * #.#.#..#
	 * #.$..#.#
	 * ########
	 * Board drawing above is messed up.
	 * Need to switch X and Y co-ordinates.
	 * Assertions remain valid though.
	 */
	public void testTwo() {
		LocalMap map = createEmptyMap(8, 8);
		map.setSquareType(1, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 6, LocalMap.SquareType.OBSTACLE);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(6, 3);
		Point dest = new Point(1, 2);
		Walk2 walk = new Walk2();
		
		Direction dir1 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.northwest, dir1);
		walk.visit(location);
		location = MovementUtils.move(location, dir1);

		Direction dir2 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.northwest, dir2);
		walk.visit(location);
		location = MovementUtils.move(location, dir2);

		Direction dir3 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir3);
		walk.visit(location);
		location = MovementUtils.move(location, dir3);

		Direction dir4 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir4);
		walk.visit(location);
		location = MovementUtils.move(location, dir4);

		Direction dir5 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir5);
		walk.visit(location);
		location = MovementUtils.move(location, dir5);

		Direction dir6 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.here, dir6);
	}
	
	/**
	 * 7 ########
	 * 6 #..@...#
	 * 5 #.###.##
	 * 4 ##.##..#
	 * 3 #....#.#
	 * 2 #.#.#..#
	 * 1 #.$..#.#
	 * 0 ########
	 *   01234567 (X)
	 */
	public void testThree() {
		LocalMap map = createEmptyMap(8, 8);
		map.setSquareType(1, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 5, LocalMap.SquareType.OBSTACLE);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(3, 6);
		Point dest = new Point(2, 1);
		Walk2 walk = new Walk2();
		
		Direction dir1 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir1);
		walk.visit(location);
		location = MovementUtils.move(location, dir1);

		Direction dir2 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir2);
		walk.visit(location);
		location = MovementUtils.move(location, dir2);

		Direction dir3 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southeast, dir3);
		walk.visit(location);
		location = MovementUtils.move(location, dir3);

		Direction dir4 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.south, dir4);
		walk.visit(location);
		location = MovementUtils.move(location, dir4);

		Direction dir5 = advisor.advise(location, dest, map, walk);
		//assertEquals(Direction.southeast, dir5); // No assert needed here. Either path is okay.
		walk.visit(location);
		location = MovementUtils.move(location, dir5);

		Direction dir6 = advisor.advise(location, dest, map, walk);
		//assertEquals(Direction.southwest, dir6); // No assert needed here. Either path is okay.
		walk.visit(location);
		location = MovementUtils.move(location, dir6);

		Direction dir7 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.here, dir7);
	}
	
	/**
	 * 7 ########
	 * 6 #..@...#
	 * 5 #####.##
	 * 4 ##.##..#
	 * 3 #....#.#
	 * 2 #.#.#..#
	 * 1 #.$..#.#
	 * 0 ########
	 *   01234567 (X)
	 */
	public void testFour() {
		LocalMap map = createEmptyMap(8, 8);
		map.setSquareType(1, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 5, LocalMap.SquareType.OBSTACLE);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(3, 6);
		Point dest = new Point(2, 1);
		Walk2 walk = new Walk2();
		walk.visit(location);

		Direction dir1 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir1);
		location = MovementUtils.move(location, dir1);
		walk.visit(location);

		Direction dir2 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir2);
		location = MovementUtils.move(location, dir2);
		walk.visit(location);

		Direction dir3 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir3);
		location = MovementUtils.move(location, dir3);
		walk.visit(location);

		Direction dir4 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir4);
		location = MovementUtils.move(location, dir4);
		walk.visit(location);

		Direction dir5 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir5);
		location = MovementUtils.move(location, dir5);
		walk.visit(location);

		Direction dir6 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southeast, dir6);
		location = MovementUtils.move(location, dir6);
		walk.visit(location);

		Direction dir7 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.south, dir7);
		location = MovementUtils.move(location, dir7);
		walk.visit(location);

		Direction dir8 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir8);
		location = MovementUtils.move(location, dir8);
		walk.visit(location);

		Direction dir9 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir9);
		location = MovementUtils.move(location, dir9);
		walk.visit(location);

		Direction dir10 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir10);
		location = MovementUtils.move(location, dir10);
		walk.visit(location);

		Direction dir11 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.here, dir11);
	}

	/**
	 * 7 ########
	 * 6 #..@...#
	 * 5 #####.##
	 * 4 #....#.#
	 * 3 #####..#
	 * 2 #.#.#..#
	 * 1 #.$..#.#
	 * 0 ########
	 *   01234567 (X)
	 */
	public void testFive() {
		LocalMap map = createEmptyMap(8, 8);
		map.setSquareType(1, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 5, LocalMap.SquareType.OBSTACLE);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(3, 6);
		Point dest = new Point(2, 1);
		Walk2 walk = new Walk2();
		walk.visit(location);

		Direction dir1 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir1);
		location = MovementUtils.move(location, dir1);
		walk.visit(location);

		Direction dir2 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir2);
		location = MovementUtils.move(location, dir2);
		walk.visit(location);

		Direction dir3 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir3);
		location = MovementUtils.move(location, dir3);
		walk.visit(location);

		Direction dir4 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir4);
		location = MovementUtils.move(location, dir4);
		walk.visit(location);

		Direction dir5 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir5);
		location = MovementUtils.move(location, dir5);
		walk.visit(location);

		Direction dir6 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southeast, dir6);
		location = MovementUtils.move(location, dir6);
		walk.visit(location);

		Direction dir7 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir7);
		location = MovementUtils.move(location, dir7);
		walk.visit(location);

		Direction dir8 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir8);
		location = MovementUtils.move(location, dir8);
		walk.visit(location);

		Direction dir9 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir9);
		location = MovementUtils.move(location, dir9);
		walk.visit(location);

		Direction dir10 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir10);
		location = MovementUtils.move(location, dir10);
		walk.visit(location);

		Direction dir11 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir11);
		location = MovementUtils.move(location, dir11);
		walk.visit(location);

		Direction dir12 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir12);
		location = MovementUtils.move(location, dir12);
		walk.visit(location);

		Direction dir13 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.east, dir13);
		location = MovementUtils.move(location, dir13);
		walk.visit(location);

		Direction dir14 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southeast, dir14);
		location = MovementUtils.move(location, dir14);
		walk.visit(location);

		Direction dir15 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.south, dir15);
		location = MovementUtils.move(location, dir15);
		walk.visit(location);

		Direction dir16 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.southwest, dir16);
		location = MovementUtils.move(location, dir16);
		walk.visit(location);

		Direction dir17 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir17);
		location = MovementUtils.move(location, dir17);
		walk.visit(location);

		Direction dir18 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.west, dir18);
		location = MovementUtils.move(location, dir18);
		walk.visit(location);

		Direction dir19 = advisor.advise(location, dest, map, walk);
		assertEquals(Direction.here, dir19);
	}

	/**
	 * 4: ######
	 * 3: #.@.##
	 * 2: #.###$
	 * 1: #....#
	 * 0: ######
	 *    012345
	 */
	public void testSix() {
		LocalMap map = createEmptyMap(6, 5);
		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 3, LocalMap.SquareType.OBSTACLE);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(2, 3);
		Point dest = new Point(5, 2);
		Walk2 walk = new Walk2();

		for (int i=0; i<10; i++) {
			walk.visit(location);
			Direction dir = advisor.advise(location, dest, map, walk);
			if (dir != null) {
//				System.out.println(dir);
				location = MovementUtils.move(location, dir);
			}
		}
	}
	
	/**
	 * 019: ####################
	 * 018: ##.##.#####.########
	 * 017: #####...###.##.#..##
	 * 016: ###.#####.#..#....##
	 * 015: #...###..#.##.######
	 * 014: #.###..#.##...######
	 * 013: ##.#######.###.#####
	 * 012: #.##..###..@##.#.###
	 * 011: #########..#.##$####
	 * 010: ###..#.######.##.###
	 * 009: ##.##..###..##.##.##
	 * 008: ###..####.##..#.##.#
	 * 007: ##..####..##########
	 * 006: #.##...##.#.#.###.##
	 * 005: ###.####.#..####..##
	 * 004: #.#####.########.###
	 * 003: ##..########.####..#
	 * 002: #########.##.#####.#
	 * 001: ##..##.#.#.#.#######
	 * 000: ####################
	 *      01234567890123456789
	 */
	public void testSeven() {
		LocalMap map = createEmptyMap(20, 20);

		map.setSquareType(18, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(18, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(17, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(17, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(16, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(16, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(15, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(15, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(14, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(14, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(13, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(13, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(12, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(12, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(11, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(11, 15, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(10, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(10, 18, LocalMap.SquareType.OBSTACLE);
		
		map.setSquareType(9, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(9, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(8, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(8, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(7, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(7, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(6, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(6, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(5, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(5, 16, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(4, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 15, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(4, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(3, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(3, 18, LocalMap.SquareType.OBSTACLE);

		map.setSquareType(2, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 4, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 6, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 12, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 14, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(2, 17, LocalMap.SquareType.OBSTACLE);
		
		map.setSquareType(1, 1, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 2, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 3, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 5, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 7, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 8, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 9, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 10, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 11, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 13, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 16, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 17, LocalMap.SquareType.OBSTACLE);
		map.setSquareType(1, 18, LocalMap.SquareType.OBSTACLE);

		// Set source.
		Point source = new Point(11, 12);
		map.setSquareType(source.x, source.y, LocalMap.SquareType.EMPTY);

		// Set destination.
		Point dest = new Point(15, 11);
		map.setSquareType(dest.x, dest.y, LocalMap.SquareType.EMPTY);

		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(source.x, source.y);
		Walk2 walk = new Walk2();
		
//		print(map, 20, 20, source, dest);

		for (int i=0; i<100; i++) {
			walk.visit(location);
//			System.out.println((i+1) + ": " + location);
			Direction dir = advisor.advise(location, dest, map, walk);
			if (dir == null) {
//				System.out.println("No path to destination possible!");
				break;
			} else if (dir == Direction.here) {
//				System.out.println(dir);
//				System.out.println("Reached destination!");
				break;
			} else {
//				System.out.println(dir);
			}
			location = MovementUtils.move(location, dir);
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
	
	public void testRandomOne() {
		int SIZE = 20;
		LocalMap map = createEmptyMap(SIZE, SIZE);
		addObstacles(map, SIZE, SIZE, 0.40D);
		// Set source.
		Point source = new Point(1, 1);
		map.setSquareType(1, 1, LocalMap.SquareType.EMPTY);
		// Set destination.
		Point dest = new Point(SIZE-2, SIZE-2);
		map.setSquareType(SIZE-2, SIZE-2, LocalMap.SquareType.EMPTY);
//		print(map, SIZE, SIZE, source, dest);
		
		NavigationAdvisor advisor = new NavigationAdvisor();
		Point location = new Point(source.x, source.y);
		Walk2 walk = new Walk2();

		for (int i=0; i<100; i++) {
			walk.visit(location);
//			System.out.println((i+1) + ": " + location);
			Direction dir = advisor.advise(location, dest, map, walk);
			if (dir == null) {
//				System.out.println("No path to destination possible!");
				break;
			} else if (dir == Direction.here) {
//				System.out.println(dir);
//				System.out.println("Reached destination!");
				break;
			} else {
//				System.out.println(dir);
			}
			location = MovementUtils.move(location, dir);
		}
	}
	
	public void testRandomTwo() {
		int SIZE = 50;
		double OBSTACLE_RATIO = 0.15 + 0.35 * Math.random(); // constrain ratio between 0.15 and 0.5.
		LocalMap map = createEmptyMap(SIZE, SIZE);
		addObstacles(map, SIZE, SIZE, OBSTACLE_RATIO);

		// Set source.
		Point source = new Point((int) (Math.random() * SIZE), (int) (Math.random() * SIZE));
		map.setSquareType(source.x, source.y, LocalMap.SquareType.EMPTY);

		// Set destination.
		Point dest = new Point((int) (Math.random() * SIZE), (int) (Math.random() * SIZE));
		map.setSquareType(dest.x, dest.y, LocalMap.SquareType.EMPTY);
		print(map, SIZE, SIZE, source, dest);
		
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
				System.out.println("No path to destination possible!");
				break;
			} else if (dir == Direction.here) {
				System.out.println(dir);
				System.out.println("Reached destination!");
				pathExists = true;
				break;
			} else {
				System.out.println(dir);
			}
			location = MovementUtils.move(location, dir);
			pathDistance++;
		}
		
		if (pathExists) {
			System.out.println(OBSTACLE_RATIO + ": " + (double)(pathDistance * 1.0/distance) + " [" + pathDistance + "/" + distance + "]");
		} else {
			System.out.println(OBSTACLE_RATIO + ": 0 [" + pathDistance + "/" + distance + "]");
		}
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
