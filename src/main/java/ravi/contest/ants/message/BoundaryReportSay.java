package ravi.contest.ants.message;

public class BoundaryReportSay implements MySay {
	// Id of the reporting ant.
	public int id;
	
	// X boundary to report, if any, relative to nest.
	// Note that this can be negative.
	// Also, note that 0 is not a valid value.
	public int x = 0;
	
	// Y boundary to report, if any, relative to nest.
	// Note that this can be negative.
	// Also, note that 0 is not a valid value.
	public int y = 0;

	public static BoundaryReportSay deserialize(String message) {
		if (message != null) {
			int index = message.indexOf(SayType.BOUNDARY_REPORT.name());
			if (index >= 0) {
				BoundaryReportSay say = new BoundaryReportSay();
				
				// Get id.
				int indexOfComma = message.indexOf(",");
				say.id = Integer.parseInt(message.substring(index + SayType.BOUNDARY_REPORT.name().length() + "[id=".length(), indexOfComma));
				
				// Get X value.
				int indexOfX = message.indexOf("x");
				indexOfComma = message.indexOf(",", indexOfComma + 1);
				say.x = Integer.parseInt(message.substring(indexOfX + 2, indexOfComma));
				
				// Get X value.
				int indexOfY = message.indexOf("y");
				say.y = Integer.parseInt(message.substring(indexOfY + 2, message.length() - 1));
				
				return say;
			}
		}
		return null;
	}

	@Override
	public SayType getType() {
		return SayType.BOUNDARY_REPORT;
	}

	@Override
	public String serialize() {
		// Examples:
		// BOUNDARY[id=23,x=-120,y=0] => y is unknown.
		// BOUNDARY[id=8,x=,y=73] => x is unknown.
		// BOUNDARY[id=30,x=90,y=-84] => both x and y are known.
		return SayType.BOUNDARY_REPORT.name()
		    + "[id=" + id
			+ ",x=" + x
			+ ",y=" + y
			+ "]";
	}
	
	@Override
	public String toString() {
		return serialize();
	}
}
