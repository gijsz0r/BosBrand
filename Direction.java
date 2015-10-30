package BosBrand;

import java.awt.Point;

public enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
	
	public Direction getDirection(Point a, Point b) {
		int dX = b.x-a.x;
		int dY = b.y-a.y;
		
		if (dX < 0 && dY < 0) return NORTHWEST; //lower in both axes, so northwest
		//TODO: do the rest of the directions
		else return NORTH;
	}

}
