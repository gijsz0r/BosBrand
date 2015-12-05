package BosBrand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.IteratorUtils;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public class Environment {

	private Grid<Object> grid;
	private Direction windDirection;
	private Random rng = new Random();
	private double evalScore = 0;
	private int deadTreeCount = 0;
	private int totalTreeCount = BosBrandConstants.FOREST_HEIGHT * BosBrandConstants.FOREST_WIDTH;
	private int initialFireFighterCount = 0;
	private int rainIntensity = 0;

	public Environment(Grid<Object> grid, int initialFireFighters, int rainIntensity) {
		this.grid = grid;
		this.windDirection = Direction.NORTH;
		this.initialFireFighterCount = initialFireFighters;
		this.rainIntensity = rainIntensity;
	}

	@SuppressWarnings("unchecked")
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void step() {
		// Get the context this environment is in, we need it to access objects
		Context<Object> context = ContextUtils.getContext(this);

		// Check wind direction change
		if (rng.nextDouble() <= BosBrandConstants.CHANCE_OF_WIND_TURNING) {
			// Get all directions adjacent to our current wind direction
			ArrayList<Direction> directions = Direction.getAdjacentDirections(this.windDirection);
			// Debug
			// System.out.println(String.format("Found %d adjacent directions", directions.size()));
			this.windDirection = directions.get(rng.nextInt(directions.size()));
			// Debug
			// System.out.println(String.format("Wind direction changed to %s", this.windDirection));

			// TODO: Research wind direction changed event in Context
		}

		// Loop over all rain
		IndexedIterable<Object> rains = context.getObjects(Rain.class);
		List<Object> rainList = IteratorUtils.toList(rains.iterator());
		// Call step method on each Rain object
		rainList.stream().filter(i -> i instanceof Rain).forEach(j -> ((Rain) j).step(context));

		// Loop over all trees
		IndexedIterable<Object> trees = context.getObjects(Tree.class);
		for (Object obj : trees) {
			if (obj instanceof Tree) {
				Tree tree = (Tree) obj;
				// Check if the tree is not dead
				if (tree.getCurrentHP() > 0) {
					// Check if the tree is already burning
					if (tree.getIsBurning()) {
						// Reduce tree HP
						tree.setCurrentHP(tree.getCurrentHP() - BosBrandConstants.TREE_BURNING_SPEED);
						// Check if the Tree died
						if (tree.getCurrentHP() == 0) {
							// Fire
							tree.toggleBurning();
							// Remove the Fire object on this cell
							GridPoint treeLocation = grid.getLocation(tree);
							removeFire(treeLocation);
							// Increase deadTreeCount
							deadTreeCount++;
							// Add a DeadTreeDummy to visually indicate that this tree died
							DeadTreeDummy dummy = new DeadTreeDummy();
							if (context.add(dummy)) {
								grid.moveTo(dummy, treeLocation.getX(), treeLocation.getY());
							}
						}
					} else {
						// Tree is not burning, check if the tree ignites
						if (rng.nextDouble() <= BosBrandConstants.TREE_CHANCE_OF_COMBUSTION) {
							// Create a new Fire object
							Fire fire = new Fire(grid);
							// Add Fire to the context
							if (context.add(fire)) {
								// If the Fire was successfully added, set tree on fire and put the Fire object on the grid
								GridPoint treeLocation = grid.getLocation(tree);
								grid.moveTo(fire, treeLocation.getX(), treeLocation.getY());
								tree.toggleBurning();
							}
						}
					}
				}
			}
		}

		// Spawn rain objects, only if there are not enough rain objects
		rains = context.getObjects(Rain.class);
		if (rains.size() < rainIntensity) {
			// Keep track of locations we spawned rain at
			ArrayList<GridPoint> spawnLocations = new ArrayList<GridPoint>();
			// Not enough rain, spawn new ones equal to the difference
			for (int i = 0; i < (rainIntensity - rains.size()); i++) {
				// Find a good location to spawn our rain
				boolean foundNewLocation = false;
				GridPoint spawnLocation = null;
				do {
					// Create a random location
					int x = rng.nextInt(BosBrandConstants.FOREST_WIDTH);
					int y = rng.nextInt(BosBrandConstants.FOREST_HEIGHT);
					// Set our spawn point to these coordinates
					spawnLocation = new GridPoint(x, y);
					// Check if the coordinates are not the coordinates of a spawn point that has already
					foundNewLocation = !spawnLocations.stream().anyMatch(j -> j.getX() == x && j.getY() == y);
					// Do this until we find a good location
				} while (!foundNewLocation);
				// Create new Rain object
				Rain rain = new Rain(grid);
				// Add the Rain to the context
				if (context.add(rain)) {
					// Move the object to the spawn location
					grid.moveTo(rain, spawnLocation.getX(), spawnLocation.getY());
					// Toggle rain on the new spawn location
					toggleRainOnLocation(spawnLocation, grid);
				}
			}
		}
	}

	public Direction getWindDirection() {
		return this.windDirection;
	}

	@SuppressWarnings("unchecked")
	private void removeFire(GridPoint pt) {
		// Loop through the objects at the location
		Iterable<Object> fireIterator = grid.getObjectsAt(pt.getX(), pt.getY());
		for (Object obj : fireIterator) {
			// Check if a Fire object is found
			if (obj instanceof Fire) {
				// Debug
				// System.out.println(String.format("Trying to remove Fire at: %d,%d", pt.getX(), pt.getY()));
				// Remove the Fire object from the context
				Context<Object> context = ContextUtils.getContext(obj);
				if (context.remove(obj)) {
					// Debug
					// System.out.println("Fire successfully removed from context");
					break;
				}
			}
		}
	}

	public static void toggleRainOnLocation(GridPoint pt, Grid<Object> grid) {
		// Loop through all the objects on our old cell
		Iterable<Object> cellObjects = grid.getObjectsAt(pt.getX(), pt.getY());
		for (Object obj : cellObjects) {
			if (obj instanceof Tree) {
				Tree tree = (Tree) obj;
				// Toggle off
				tree.toggleRaining();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void evaluate() {
		// Get the context this environment is in, we need it to access objects
		Context<Object> context = ContextUtils.getContext(this);
		// Get firefighters
		IndexedIterable<Object> fireFighters = context.getObjects(FireFighter.class);
		List<Object> fireFighterList = IteratorUtils.toList(fireFighters.iterator());
		// Calculate how many firefighters have died (they have been removed from the context, so we can't find them anymore)
		int deadFireFighters = initialFireFighterCount - fireFighterList.size();
		// Calculate the average bounty of the currently alive firefighters
		int sumBounty = 0;
		for (int i = 0; i < fireFighterList.size(); i++) {
			sumBounty = sumBounty + ((FireFighter) fireFighterList.get(i)).getBounty();
		}
		// Debug
		// System.out.println(String.format("Evaluating (totalTreeCount:%d) (deadTreeCount:%d) (sumBounty:%d) (deadFireFighters:%d)", totalTreeCount, deadTreeCount, sumBounty, deadFireFighters));
		// Determine current evaluation value
		// (fraction of alive trees * average bounty) / (fraction of alive firefighters)
		evalScore = (((totalTreeCount - deadTreeCount) / (totalTreeCount * 1.0)) * (sumBounty / (initialFireFighterCount - deadFireFighters) * 1.0)) * ((initialFireFighterCount - deadFireFighters) / (initialFireFighterCount * 1.0));
		// Debug
		// System.out.println(String.format("Current evaluation score: %1$,.2f", evalScore));
	}

	public double getEvaluationScore() {
		return evalScore;
	}
}
