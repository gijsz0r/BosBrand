package BosBrand;

import java.util.List;

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

	public FireFighter(Grid<Object> grid) {
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		// Get the grid location of this FireFighter
		GridPoint pt = grid.getLocation(this);

		GridCellNgh<Fire> neighbourhoodCreator = new GridCellNgh<Fire>(grid,
				pt, Fire.class, lookingDistance, lookingDistance);

		List<GridCell<Fire>> gridCells = neighbourhoodCreator
				.getNeighborhood(true);
		// SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		// Make a list with all cells containing allies
		// Check if any ally is currently fighting fire
		// If any, maybe communicate?
		// Maybe sense closest fire?

		// Make a list with all cells containing fire
		// Check which fire is closest
		// Move there

		// moveTowards(pointWithMostFire);
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