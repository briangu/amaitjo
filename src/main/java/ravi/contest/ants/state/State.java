package ravi.contest.ants.state;

public enum State {
	START,
	TERRITORY_SEARCH, // first attempt at migrating to allotted territory
	BOUNDARY_REPORT, // if first attempt at allotted territory hits a boundary, need to report back to nest.
	WAIT_FOR_FULL_BOUNDARY_REPORT, // partial boundary info available, need to wait for full information.
	TERRITORY_MIGRATION, // with full boundary info, ant can now head out to territory
	FORAGE, // in self territory, collect food in a designated place.
	TRANSPORT_FOOD, // move food from territory to nest.
	
	ORACLE, // located permanently at the nest.
	ORACLE_AWAIT_BOUNDARY_REPORTS, // awaiting boundary reports
	;
}
