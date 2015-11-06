package BosBrand;

import java.awt.Point;

import repast.simphony.space.grid.GridPoint;

public enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
	
	public static Direction getDirection(Point from, Point to) {
		//We assume here that the X-axis is along the West-East line and the Y-axis is along the North-South line
		int dX = to.x-from.x;
		int dY = to.y-from.y;
		
		if (dX < 0 && dY < 0) return NORTHWEST; //lower in both axes: NorthWest
		if (dX == 0 && dY < 0) return NORTH; //equal west-east, lower north-south: North
		if (dX > 0 && dY < 0) return NORTHEAST; //higher west-east, lower north-south: NorthEast
		if (dX > 0 && dY == 0) return EAST; //higher west-east, equal north-south: East
		if (dX > 0 && dY > 0) return SOUTHEAST; //higher west-east, higher north-south: SouthEast
		if (dX == 0 && dY > 0) return SOUTH; //equal west-east, higher north-south: South
		if (dX < 0 && dY > 0) return SOUTHWEST; //lower west-east, higher north-south: SOUTHWEST
		if (dX < 0 && dY == 0) return WEST; //lower west-east, equal north-south: West
		else return NORTH; //TODO: handle equal coordinates case
	}

	public static Direction getDirection(GridPoint from, GridPoint to) {
		Point newFrom = new Point(from.getX(), from.getY());
		Point toFrom = new Point(to.getX(), to.getY());
		
		return getDirection(newFrom, toFrom);
	}
}
