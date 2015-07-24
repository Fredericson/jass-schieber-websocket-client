package ch.jass.connection.mapping;

import ch.jass.model.schieber.table.Team;

public class TeamMapper {

	public static Team getTeam(final String name) {
		if ("Team 1".equals(name)) {
			return Team.TEAM_1;
		} else if ("Team 2".equals(name)) {
			return Team.TEAM_2;
		}
		throw new IllegalArgumentException("Team Name not available! " + name);
	}

}
