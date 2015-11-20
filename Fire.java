package BosBrand;

import java.util.Random;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Fire {

	private Grid<Object> grid;

	public Fire(Grid<Object> grid) {
		this.grid = grid;
	}

	public Fire() {

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		// Check if any trees spontaneously combust
		// TODO: implement

		// Chance of Fire moving = Base Chance + Rain Modifier + Wind Modifier
		Random r = new Random();

		// Get the grid location of this Fire
		GridPoint pt = grid.getLocation(this);

		// Loop through all directions
		for (Direction direction : Direction.getAllDirections()) {
			// Loop through all objects in the neighbouring cells
			Iterable<Object> cellObjects = Direction
					.getObjectsInAdjacentDirection(direction, grid, pt);
			// Check if the tree has any health left, otherwise spreading there
			// can't happen
			// Check if there is fire already on the cell
			// Calculate chance of spreading
			// TODO: implement
		}
	}

	public GridPoint getLocation() {
		return grid.getLocation(this);
	}

}
