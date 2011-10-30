package ravi.contest.ants.test.movement;

public class TestWalkBloatGraph {
	public static void main(String[] args) {
		for (int i=10; i<75; i++) {
			double obstacleDensity = i * 1.0 / 100;
			// e^{(2*x)^5} = e^{32*x^5} - appears to approximate empirical data.
			System.out.println(obstacleDensity + "\t" + Math.pow(Math.E, Math.pow(2*obstacleDensity, 5)));
		}
	}
}
