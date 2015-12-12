package BosBrand;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.IndexedIterable;

public class Rain {

	private Grid<Object> grid;
	private int forestWidth;
	private int forestHeight;
	private int myRemainingTime = 0;

	public Rain(Grid<Object> grid, int forestWidth, int forestHeight) {
		this.grid = grid;
		this.forestWidth = forestWidth;
		this.forestHeight = forestHeight;
		this.myRemainingTime = BosBrandConstants.RAIN_LIFETIME;
	}

	public void step(Context<Object> context) {
		// Get the grid location of this Rain
		GridPoint oldLocation = grid.getLocation(this);

		// To check the wind direction, we need to access the environment
		IndexedIterable<Object> possibleEnvironments = context.getObjects(Environment.class);
		if (possibleEnvironments.size() >= 1) {
			// Check if I still have any lifetime remaining
			if (myRemainingTime > 0) {
				// Decrease lifetime
				myRemainingTime--;
				// Get the current direction of the wind
				Direction windDirection = ((Environment) possibleEnvironments.get(0)).getWindDirection();
				// Move in that direction
				if (Direction.canIMoveInDirection(oldLocation, windDirection, forestWidth, forestHeight)) {
					// Execute move
					GridPoint newLocation = moveTowards(windDirection);
					// Toggle rain on new location
					Environment.toggleRainOnLocation(newLocation, grid);
					// Toggle rain on old location
					Environment.toggleRainOnLocation(oldLocation, grid);
					return;
				} else {
					// This means we are moving outside of the grid, so we want to remove ourself
					if (!context.remove(this)) {
						// Debug
						System.out.println(String.format("Failed to remove Rain object from (%d,%d) because moving outside of grid", oldLocation.getX(), oldLocation.getY()));
					}
					return;
				}
			} else {
				// We poured out all of our water, so we can be removed
				if (!context.remove(this)) {
					// Debug
					System.out.println(String.format("Failed to remove Rain object from (%d,%d) because it died", oldLocation.getX(), oldLocation.getY()));
				}
				// Toggle rain on old location
				Environment.toggleRainOnLocation(oldLocation, grid);
				return;
			}
		} else {
			// Still decrease our lifetime
			myRemainingTime--;
		}
	}

	private GridPoint moveTowards(Direction direction) {
		// Debug
		// System.out.println(String.format("Moving in direction: %s", direction.toString()));
		// Debug
		// System.out.println(String.format("Location before move: %d,%d", grid.getLocation(this).getX(), grid.getLocation(this).getY()));

		// This should be self explanatory
		switch (direction) {
		case NORTH:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
			break;
		case NORTHEAST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.EAST);
			break;
		case EAST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.EAST);
			break;
		case SOUTHEAST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.EAST);
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
			break;
		case SOUTH:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
			break;
		case SOUTHWEST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.WEST);
			break;
		case WEST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.WEST);
			break;
		case NORTHWEST:
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.WEST);
			grid.moveByVector(this, BosBrandConstants.RAIN_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
			break;
		default:
			// TODO: handle this (error?)
			System.out.println("Error in Rain.moveTowards(Direction)");
			break;
		}

		// Save our new location
		GridPoint newLocation = grid.getLocation(this);
		// Debug
		// System.out.println(String.format("Location after move: %d,%d", newLocation.getX(), newLocation.getY()));
		// Return that we successfully moved
		return newLocation;
	}
}
