package BosBrand;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public enum Direction {
	NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST;

	public static ArrayList<Direction> getAllDirections() {
		return new ArrayList<Direction>(Arrays.asList(NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST));
	}

	public static ArrayList<Direction> getAllDirectionsShuffled() {
		// Get all directions
		ArrayList<Direction> tempList = getAllDirections();
		// Shuffle them
		SimUtilities.shuffle(tempList, RandomHelper.getUniform());
		// Return the shuffled list
		return tempList;
	}

	public static ArrayList<Direction> getAdjacentDirections(Direction direction) {
		return getAdjacentDirections(direction, false);
	}

	public static ArrayList<Direction> getAdjacentDirections(Direction direction, boolean includeSelf) {
		ArrayList<Direction> directions = new ArrayList<Direction>();
		// Return the directions next to the provided direction
		switch (direction) {
		case NORTHWEST:
			directions.add(WEST);
			directions.add(NORTH);
			break;
		case NORTH:
			directions.add(NORTHWEST);
			directions.add(NORTHEAST);
			break;
		case NORTHEAST:
			directions.add(NORTH);
			directions.add(EAST);
			break;
		case EAST:
			directions.add(NORTHEAST);
			directions.add(SOUTHEAST);
			break;
		case SOUTHEAST:
			directions.add(EAST);
			directions.add(SOUTH);
			break;
		case SOUTH:
			directions.add(SOUTHEAST);
			directions.add(SOUTHWEST);
			break;
		case SOUTHWEST:
			directions.add(SOUTH);
			directions.add(WEST);
			break;
		case WEST:
			directions.add(SOUTHWEST);
			directions.add(NORTHWEST);
			break;
		default:
			break;
		}
		// Include our own direction if requested
		if (includeSelf)
			directions.add(direction);
		return directions;
	}

	public static Direction getOppositeDirection(Direction direction) {
		switch (direction) {
		case NORTHWEST:
			return SOUTHEAST;
		case NORTH:
			return SOUTH;
		case NORTHEAST:
			return SOUTHWEST;
		case EAST:
			return WEST;
		case SOUTHEAST:
			return NORTHWEST;
		case SOUTH:
			return NORTH;
		case SOUTHWEST:
			return NORTHEAST;
		case WEST:
			return EAST;
		default:
			System.out.println("Unknown Direction value found in Direction.getOppositeDirection");
			return null; // TODO: handle this error
		}
	}

	public static Direction getDirection(Point from, Point to) {
		// We assume here that the X-axis is along the West-East line and the Y-axis is along the North-South line
		// Also, in RePast the 0,0 cell is at the bottom left of the grid
		int dX = to.x - from.x;
		int dY = to.y - from.y;
		// Debug
		// System.out.println(String.format("dX: %d ; dY: %d", dX, dY));

		if (dX < 0 && dY < 0)
			return SOUTHWEST; // lower in both axes: SouthWest
		if (dX == 0 && dY < 0)
			return SOUTH; // equal west-east, lower north-south: South
		if (dX > 0 && dY < 0)
			return SOUTHEAST; // higher west-east, lower north-south: SouthEast
		if (dX > 0 && dY == 0)
			return EAST; // higher west-east, equal north-south: East
		if (dX > 0 && dY > 0)
			return NORTHEAST; // higher west-east, higher north-south: NorthEast
		if (dX == 0 && dY > 0)
			return NORTH; // equal west-east, higher north-south: North
		if (dX < 0 && dY > 0)
			return NORTHWEST; // lower west-east, higher north-south: NorthWest
		if (dX < 0 && dY == 0)
			return WEST; // lower west-east, equal north-south: West
		else {
			// TODO: handle this error
			System.out.println(String.format("Unknown relative coordinates in Direction.getDirection", dX, dY));
			return null;
		}
	}

	public static Direction getDirection(GridPoint from, GridPoint to) {
		Point newFrom = new Point(from.getX(), from.getY());
		Point toFrom = new Point(to.getX(), to.getY());
		// Call the getDirection method with the newly created Point objects
		return getDirection(newFrom, toFrom);
	}

	public static Iterable<Object> getObjectsInAdjacentDirection(Direction direction, Grid<Object> grid, GridPoint origin) {
		// We assume here that the X-axis is along the West-East line and the Y-axis is along the North-South line
		// Also, in RePast the 0,0 cell is at the bottom left of the grid
		switch (direction) {
		case NORTHWEST:
			// North west: 1 index lower in X and 1 index higher in Y
			return grid.getObjectsAt(origin.getX() - 1, origin.getY() + 1);
		case NORTH:
			// North: equal in X and 1 index higher in Y
			return grid.getObjectsAt(origin.getX(), origin.getY() + 1);
		case NORTHEAST:
			// North east: 1 index higher in X and 1 index lower in Y
			return grid.getObjectsAt(origin.getX() + 1, origin.getY() + 1);
		case EAST:
			// East: 1 index higher in X and equal in Y
			return grid.getObjectsAt(origin.getX() + 1, origin.getY());
		case SOUTHEAST:
			// South east: 1 index higher in X and 1 index higher in Y
			return grid.getObjectsAt(origin.getX() + 1, origin.getY() - 1);
		case SOUTH:
			// South: equal in X and 1 index higher in Y
			return grid.getObjectsAt(origin.getX(), origin.getY() - 1);
		case SOUTHWEST:
			// South west: 1 index lower in X and 1 index higher in Y
			return grid.getObjectsAt(origin.getX() - 1, origin.getY() - 1);
		case WEST:
			// West: 1 index lower in X and equal in Y
			return grid.getObjectsAt(origin.getX() - 1, origin.getY());
		default:
			System.out.println("Unknown Direction value found in Direction.getObjects");
			return null; // TODO: handle this error
		}
	}

	public static boolean canIMoveInDirection(GridPoint point, Direction direction) {
		// Check if we are using WrapAroundBorders, because in that case we can just return true
		if (BosBrandConstants.BORDER_TYPE instanceof repast.simphony.space.grid.WrapAroundBorders)
			return true;
		// Determine if the coordinates are on any of the borders of the grid
		boolean notOnNorthBorder = point.getY() < BosBrandConstants.FOREST_HEIGHT - 1;
		boolean notOnEastBorder = point.getX() < BosBrandConstants.FOREST_WIDTH - 1;
		boolean notOnWestBorder = point.getX() > 0;
		boolean notOnSouthBorder = point.getY() > 0;
		boolean movementAllowed = false;
		// For each direction we check if the current X and Y coordinates
		switch (direction) {
		case NORTHWEST:
			movementAllowed = notOnNorthBorder && notOnWestBorder;
			break;
		case NORTH:
			movementAllowed = notOnNorthBorder;
			break;
		case NORTHEAST:
			movementAllowed = notOnNorthBorder && notOnEastBorder;
			break;
		case EAST:
			movementAllowed = notOnEastBorder;
			break;
		case SOUTHEAST:
			movementAllowed = notOnSouthBorder && notOnEastBorder;
			break;
		case SOUTH:
			movementAllowed = notOnSouthBorder;
			break;
		case SOUTHWEST:
			movementAllowed = notOnSouthBorder && notOnWestBorder;
			break;
		case WEST:
			movementAllowed = notOnWestBorder;
			break;
		default:
			movementAllowed = false;
			break;
		}
		// Debug
		// if (!movementAllowed) System.out.println(String.format("Impossible move: from (%d,%d) in %s direction", point.getX(), point.getY(), direction));
		return movementAllowed;
	}
}
