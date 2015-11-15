package BosBrand;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class FireFighter {

	private Grid<Object> grid;
	private boolean moved;
	private static int lookingDistance = 2;
	public boolean fightingFire;

	public FireFighter(Grid<Object> grid) {
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		// Get the grid location of this FireFighter
		GridPoint pt = grid.getLocation(this);

		// SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		// Make a list with all cells containing allies
		GridCellNgh<FireFighter> allyNeighbourHoodCreator = new GridCellNgh<FireFighter>(
				grid, pt, FireFighter.class, lookingDistance, lookingDistance);
		List<GridCell<FireFighter>> allyGridCells = allyNeighbourHoodCreator
				.getNeighborhood(false);
		// Check if any ally is currently fighting fire
		List<GridCell<FireFighter>> fightingAllies = allyGridCells.stream()
				.filter(i -> i.items().iterator().next().fightingFire)
				.collect(Collectors.toList());
		// If any
		if (fightingAllies.size() > 0) {
			GridCell<FireFighter> ally = null;
			// Debug
			System.out.println(String.format(
					"Found %d nearby allies fighting fire!",
					fightingAllies.size()));
			// Sense closest
			OptionalInt minDistance = fightingAllies.stream()
					.mapToInt(i -> (int) grid.getDistance(pt, i.getPoint()))
					.min();
			// Debug
			if (minDistance.isPresent()) {
				System.out.println(String.format(
						"Nearest fighting ally distance: %d",
						minDistance.getAsInt()));
				// Get the point of the closest fighting ally
				Optional<GridCell<FireFighter>> close = fightingAllies
						.stream()
						.filter(i -> (int) grid.getDistance(pt, i.getPoint()) == minDistance
								.getAsInt()).findFirst();
				if (close.isPresent()) {
					ally = close.get();
				}
			} else {
				System.out
						.println("No distance to nearest fighting ally found.");
			}

			// Move towards the closest fighting ally
			if (ally != null) {
				moveTowards(ally.getPoint());
			}
		} else {
			// Make a list with all cells containing fire
			GridCellNgh<Fire> fireNeighbourhoodCreator = new GridCellNgh<Fire>(
					grid, pt, Fire.class, lookingDistance, lookingDistance);
			List<GridCell<Fire>> fireGridCells = fireNeighbourhoodCreator
					.getNeighborhood(true);
			// Check if any fires were detected
			if (fireGridCells.size() > 0) {
				GridCell<Fire> fire = null;
				// Debug
				System.out.println(String.format("Found %d nearby fires!",
						fireGridCells.size()));
				// Check which fire is closest
				OptionalInt minDistance = fireGridCells
						.stream()
						.mapToInt(i -> (int) grid.getDistance(pt, i.getPoint()))
						.min();
				if (minDistance.isPresent()) {
					System.out
							.println(String.format("Nearest fire distance: %d",
									minDistance.getAsInt()));
					// Get the point of the closest fire
					Optional<GridCell<Fire>> close = fireGridCells
							.stream()
							.filter(i -> (int) grid.getDistance(pt,
									i.getPoint()) == minDistance.getAsInt())
							.findFirst();
					if (close.isPresent()) {
						fire = close.get();
					}
				} else {
					System.out.println("No distance found to closest fire.");
				}

				// Move towards the closest fire
				if (fire != null) {
					moveTowards(fire.getPoint());
				}
			}
			// No fire found, patrol
		}
	}

	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			GridPoint myPoint = grid.getLocation(this);
			GridPoint otherPoint = new GridPoint(pt.getX(), pt.getY());

			Direction angle = Direction.getDirection(myPoint, otherPoint);

			// TODO: check speed
			int mySpeed = 1;

			switch (angle) {
			case NORTH:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.NORTH);
			case NORTHEAST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.NORTH);
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.EAST);
			case EAST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.EAST);
			case SOUTHEAST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.EAST);
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.SOUTH);
			case SOUTH:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.SOUTH);
			case SOUTHWEST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.SOUTH);
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.WEST);
			case WEST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.WEST);
			case NORTHWEST:
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.WEST);
				grid.moveByVector(this, mySpeed,
						repast.simphony.space.Direction.NORTH);
			default:
				// TODO: handle this (error?)
			}

			myPoint = grid.getLocation(this);

			moved = true;
		}
	}
}