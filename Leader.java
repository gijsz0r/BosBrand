package BosBrand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

@Deprecated
public class Leader extends FireFighter {

	private Grid<Object> grid;
	private ArrayList<FireFighter> fireFighters;
	private ArrayList<Fire> fires;

	public Leader(Grid<Object> grid) {
		this.grid = grid;
	}

	// TODO: adjust these timings?
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void step() {
		// Check for idle FireFighters
		List<FireFighter> idleFireFighters = fireFighters.stream()
				.filter(i -> !i.fightingFire).collect(Collectors.toList());
		if (idleFireFighters.size() > 0) {
			// Check if there are any Fires
			if (fires.size() > 0) {
				// Send each FireFighter to it's closest Fire
				for (FireFighter agent : idleFireFighters) {
					Fire closestFire = null;
					double closestDistance = Double.MAX_VALUE;
					for (Fire fire : fires) {
						// Check the distance from the FireFighter to the Fire
						double distance = grid.getDistanceSq(
								agent.getLocation(), fire.getLocation());
						// If the new distance is lower than what we have
						// already, save it
						if (distance < closestDistance) {
							closestDistance = distance;
							closestFire = fire;
						}
					}
					// After checking all Fires, send the idle FireFighter to
					// whichever Fire is closest
					if (closestFire != null) {
						agent.moveTowards(closestFire.getLocation());
					}
				}
			}
			// If there are no Fires
			else {
				// Tell each FireFighter to patrol
				for (FireFighter agent : idleFireFighters) {
					agent.patrol();
				}
			}

		}
		// If there are no idle FireFighters
		else {
			// Do nothing
		}
	}
}
