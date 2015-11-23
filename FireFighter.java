package BosBrand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class FireFighter {

	private Grid<Object> grid;
	public boolean fightingFire;

	public FireFighter(Grid<Object> grid) {
		this.grid = grid;
	}

	public FireFighter() {

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		// Get the grid location of this FireFighter
		GridPoint pt = grid.getLocation(this);

		// Check if we are next to a fire
		GridPoint firePoint = checkForFire(pt);
		if (firePoint != null) {
			// Check if the fire is next to us
			double x = grid.getDistance(pt, firePoint);
			// Debug
			System.out.println(String.format("I can see a fire %1$,.2f", x));
			if (Math.floor(x) <= 1) {
				// We are now adjacent to a Fire
				extinguishFire(firePoint);
				// Can't do anything else this turn, return
				return;
			}
		}
		
		// Make a list with all cells containing allies
		GridCellNgh<FireFighter> allyNeighbourHoodCreator = new GridCellNgh<FireFighter>(
				grid, pt, FireFighter.class, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE);
		List<GridCell<FireFighter>> gridCells = allyNeighbourHoodCreator
				.getNeighborhood(true);
		// Debug
		// System.out.println(String.format("Amount of cells found: %d",
		// gridCells.size()));
		// Filter on GridCells containing at least one item
		List<GridCell<FireFighter>> allyGridCells = gridCells.stream()
				.filter(i -> i.items().iterator().hasNext())
				.collect(Collectors.toList());
		// Check if any cells were found at all
		if (allyGridCells.size() > 0) {
			// Check if any ally is currently fighting fire
			List<GridCell<FireFighter>> fightingAllies = allyGridCells.stream()
					.filter(i -> i.items().iterator().hasNext())
					.filter(j -> j.items().iterator().next().fightingFire)
					.collect(Collectors.toList());
			// If any
			if (fightingAllies.size() > 0) {
				GridCell<FireFighter> ally = null;
				// Debug
				System.out.println(String.format(
						"Found %d nearby allies fighting fire!",
						fightingAllies.size()));
				// Sense closest
				OptionalInt minDistance = fightingAllies
						.stream()
						.mapToInt(i -> (int) grid.getDistance(pt, i.getPoint()))
						.min();
				if (minDistance.isPresent()) {
					// Debug
					System.out.println(String.format(
							"Nearest fighting ally distance: %d",
							minDistance.getAsInt()));
					// Get the point of the closest fighting ally
					Optional<GridCell<FireFighter>> close = fightingAllies
							.stream()
							.filter(i -> (int) grid.getDistance(pt,
									i.getPoint()) == minDistance.getAsInt())
							.findFirst();
					if (close.isPresent()) {
						ally = close.get();
					}
				} else {
					// Debug
					System.out
							.println("No distance to nearest fighting ally found.");
				}

				if (ally != null) {
					// Move towards the closest fighting ally
					moveTowards(ally.getPoint());
					return;
				} else {
					patrol();
					return;
				}
			} else {
				// This means there are allies close, but none are fighting fire
				if (firePoint != null) {
					// Move towards the closest fire
					moveTowards(firePoint);
					return;
				} else {
					patrol();
					return;
				}
			}
		} else {
			// If we have a fire nearby, move there
			if (firePoint != null) {
				// Move towards the closest fire
				moveTowards(firePoint);
				return;
			} else {
				patrol();
				return;
			}
		}
	}

	public GridPoint checkForFire(GridPoint pt) {
		// Make a list with all cells containing fire
		GridCellNgh<Fire> fireNeighbourhoodCreator = new GridCellNgh<Fire>(
				grid, pt, Fire.class, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE);
		List<GridCell<Fire>> gridCells = fireNeighbourhoodCreator
				.getNeighborhood(false);
		// Filter on GridCells containing at least one item
		List<GridCell<Fire>> fireGridCells = gridCells.stream()
				.filter(i -> i.items().iterator().hasNext())
				.collect(Collectors.toList());
		// Check if any fires were detected
		if (fireGridCells.size() > 0) {
			GridCell<Fire> fire = null;
			// Debug
			System.out.println(String.format("Found %d nearby fires!",
					fireGridCells.size()));
			// Check which fire is closest
			OptionalInt minDistance = fireGridCells.stream()
					.mapToInt(i -> (int) grid.getDistance(pt, i.getPoint()))
					.min();
			if (minDistance.isPresent()) {
				// Debug
				System.out.println(String.format("Nearest fire distance: %d",
						minDistance.getAsInt()));
				// Get the point of the closest fire
				Optional<GridCell<Fire>> close = fireGridCells
						.stream()
						.filter(i -> (int) grid.getDistance(pt, i.getPoint()) == minDistance
								.getAsInt()).findFirst();
				if (close.isPresent()) {
					fire = close.get();
				}
			} else {
				// Debug
				System.out.println("No distance found to closest fire.");
			}

			if (fire != null) {
				// Move towards the closest fire
				return fire.getPoint();
			}
		}
		// No fire found, return null
		return null;
	}

	public void extinguishFire(GridPoint pt) {
		// Loop through the objects at the location
		Iterable<Object> fireIterator = grid.getObjectsAt(pt.getX(), pt.getY());
		for (Object obj : fireIterator) {
			// Check if a Fire object is found
			if (obj instanceof Fire) {
				// Debug
				System.out
						.println(String.format(
								"Trying to remove Fire at: %d,%d", pt.getX(),
								pt.getY()));
				// Remove the Fire object from the context
				Context<Object> context = ContextUtils.getContext(obj);
				if (context.remove(obj)) {
					// Debug
					System.out
							.println("Fire successfully removed from context");
					// Find the tree on this cell
					Iterable<Object> treeIterator = grid.getObjectsAt(
							pt.getX(), pt.getY());
					for (Object obj2 : treeIterator) {
						// Extinguish the fire
						if (obj2 instanceof Tree) {
							((Tree) obj2).toggleBurning();
						}
					}
					break;
				}
			}
		}
	}

	public void moveTowards(GridPoint pt) {
		// Only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			GridPoint myPoint = grid.getLocation(this);
			GridPoint otherPoint = new GridPoint(pt.getX(), pt.getY());
			// Determine the direction we want to move in
			Direction direction = Direction.getDirection(myPoint, otherPoint);
			// Execute move
			moveTowards(direction);
		}
	}

	public void moveTowards(Direction direction) {
		// Debug
		System.out.println(String.format("Moving in direction: %s",
				direction.toString()));
		// Debug
		System.out.println(String.format("Location before move: %d,%d", grid
				.getLocation(this).getX(), grid.getLocation(this).getY()));

		// This should be self explanatory
		switch (direction) {
		case NORTH:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.NORTH);
			break;
		case NORTHEAST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.NORTH);
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.EAST);
			break;
		case EAST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.EAST);
			break;
		case SOUTHEAST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.EAST);
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.SOUTH);
			break;
		case SOUTH:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.SOUTH);
			break;
		case SOUTHWEST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.SOUTH);
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.WEST);
			break;
		case WEST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.WEST);
			break;
		case NORTHWEST:
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.WEST);
			grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED,
					repast.simphony.space.Direction.NORTH);
			break;
		default:
			// TODO: handle this (error?)
			System.out.println("Error in FireFighter.moveTowards(Direction)");
			break;
		}
		// Debug
		System.out.println(String.format("Location after move: %d,%d", grid
				.getLocation(this).getX(), grid.getLocation(this).getY()));
	}

	public GridPoint getLocation() {
		return grid.getLocation(this);
	}

	public void patrol() {
		// Do random move
		ArrayList<Direction> directions = Direction.getAllDirections();
		Random r = new Random();
		int choice = r.nextInt(directions.size());
		Direction chosenDirection = directions.get(choice);
		// Debug
		System.out.println(String.format("Patrolling in direction: %s",
				chosenDirection.toString()));
		// Execute move
		moveTowards(chosenDirection);
	}
}