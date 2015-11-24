package BosBrand;

import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Fire {

	private Grid<Object> grid;
	private Random r = new Random();

	public Fire(Grid<Object> grid) {
		this.grid = grid;
	}

	public Fire() {
		System.out
				.println("Careful! You're using the empty constructor for fire! if you don't want to do this, turn back now");
	}

	public GridPoint getLocation() {
		return grid.getLocation(this);
	}

	@SuppressWarnings("unchecked")
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		// Get the grid location of this Fire
		GridPoint pt = grid.getLocation(this);

		// Loop through all directions
		for (Direction direction : Direction.getAllDirections()) {
			// Check if this direction does not cross a grid border
			if (Direction.canIMoveInDirection(pt, direction)) {
				// Loop through all objects in the neighbouring cells
				Iterable<Object> cellObjects = Direction
						.getObjectsInAdjacentDirection(direction, grid, pt);
				// Convert iterable to a list
				List<Object> cellObjectsList = StreamSupport.stream(
						Spliterators.spliteratorUnknownSize(
								cellObjects.iterator(), Spliterator.ORDERED),
						false).collect(Collectors.toList());
				// Get the Tree object on the cell (should always be 1)
				if (cellObjectsList.stream().anyMatch(i -> i instanceof Tree)) {
					Tree neighbourTree = (Tree) cellObjectsList.stream()
							.filter(i -> i instanceof Tree).findFirst().get();
					// Check if the tree has any health left and is not already
					// burning
					if (neighbourTree.getCurrentHP() > 0
							&& !neighbourTree.getIsBurning()) {
						// Check if we can add the wind modifier
						// TODO: calculate wind modifier
						// Check if we can add the rain modifier
						// TODO: calculate rain modifier
						// Chance of Fire moving = Base Chance + Rain Modifier +
						// Wind Modifier
						if (r.nextDouble() <= BosBrandConstants.CHANCE_OF_FIRE_SPREADING) {
							// Create a new Fire object
							Fire fire = new Fire(grid);
							// Add the Fire to the context
							Context<Object> context = ContextUtils
									.getContext(this);
							if (context.add(fire)) {
								// If the Fire was successfully added, set the
								// tree on
								// cell on fire and place the Fire on the grid
								GridPoint treeLocation = grid
										.getLocation(neighbourTree);
								grid.moveTo(fire, treeLocation.getX(),
										treeLocation.getY());
								neighbourTree.toggleBurning();
							} else {
								// Failed to add Fire object to context
								// TODO: handle error
							}
						}
					} else {
						// Tree is dead, or already burning
					}
				} else {
					// No tree found on this cell
					// TODO: Debug
				}
			}
		}
	}
}
