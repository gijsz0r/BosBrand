package BosBrand;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

	public static ArrayList<Direction> getAllDirections() {
		return new ArrayList<Direction>(Arrays.asList(NORTH, NORTHEAST, EAST,
				SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST));
	}

	public static Direction getDirection(Point from, Point to) {
		// We assume here that the X-axis is along the West-East line and the
		// Y-axis is along the North-South line
		// Also, in RePast the 0,0 cell is at the bottom left of the grid
		int dX = to.x - from.x;
		int dY = to.y - from.y;
		System.out.println(String.format("dX: %d ; dY: %d", dX, dY));

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
		else
			System.out.println("Error in Direction...");
		return NORTH; // TODO: handle this error
	}

	public static Direction getDirection(GridPoint from, GridPoint to) {
		Point newFrom = new Point(from.getX(), from.getY());
		Point toFrom = new Point(to.getX(), to.getY());

		return getDirection(newFrom, toFrom);
	}

	public static Iterable<Object> getObjectsInAdjacentDirection(
			Direction direction, Grid<Object> grid, GridPoint origin) {
		// We assume here that the X-axis is along the West-East line and the
		// Y-axis is along the North-South line
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
			System.out.println("Error in (getObjects) Direction...");
			return null; // TODO: handle this error
		}
	}
}
