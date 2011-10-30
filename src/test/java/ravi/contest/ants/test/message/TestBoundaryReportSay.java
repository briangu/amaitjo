package ravi.contest.ants.test.message;

import ravi.contest.ants.message.BoundaryReportSay;
import junit.framework.TestCase;

public class TestBoundaryReportSay extends TestCase {
	public void testDeserialization() {
		BoundaryReportSay say = BoundaryReportSay.deserialize("BOUNDARY_REPORT[id=5,x=30,y=-75]");
		assertEquals(5, say.id);
		assertEquals(30, say.x);
		assertEquals(-75, say.y);

		BoundaryReportSay say2 = BoundaryReportSay.deserialize("BOUNDARY_REPORT[id=5,x=-30,y=-75]");
		assertEquals(5, say2.id);
		assertEquals(-30, say2.x);
		assertEquals(-75, say2.y);

		BoundaryReportSay say3 = BoundaryReportSay.deserialize("BOUNDARY_REPORT[id=5,x=0,y=-75]");
		assertEquals(5, say3.id);
		assertEquals(0, say3.x);
		assertEquals(-75, say3.y);

		BoundaryReportSay say4 = BoundaryReportSay.deserialize("BOUNDARY_REPORT[id=0,x=50,y=0]");
		assertEquals(0, say4.id);
		assertEquals(50, say4.x);
		assertEquals(0, say4.y);
	}
	
	public void testSerialization() {
		BoundaryReportSay say = new BoundaryReportSay();
		say.id = 0;
		say.x = 89;
		say.y = -250;
		assertEquals("BOUNDARY_REPORT[id=0,x=89,y=-250]", say.serialize());
		
		say.id = 40;
		say.x = 0;
		say.y = 138;
		assertEquals("BOUNDARY_REPORT[id=40,x=0,y=138]", say.serialize());
		
		say.id = 40;
		say.x = -50;
		say.y = 0;
		assertEquals("BOUNDARY_REPORT[id=40,x=-50,y=0]", say.serialize());
		
		say.id = 40;
		say.x = -78;
		say.y = 138;
		assertEquals("BOUNDARY_REPORT[id=40,x=-78,y=138]", say.serialize());
	}
}
