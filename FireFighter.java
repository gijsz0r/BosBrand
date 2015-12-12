package BosBrand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
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
	private int id;
	private int HP;
	private int bounty;
	private int bountyModifier;
	private int startingTeamMates;

	public FireFighter(Grid<Object> grid, int id, int startingTeamMates) {
		this.grid = grid;
		this.id = id;
		this.HP = BosBrandConstants.FIREFIGHTER_STARTING_HEALTH;
		this.bountyModifier = BosBrandConstants.FIREFIGHTER_DEFAULT_BOUNTY_MODIFIER;
		this.startingTeamMates = startingTeamMates;
	}

	public FireFighter() {
		System.out.println("Careful! You're using the empty constructor for FireFighter! If you don't want to do this, turn back now");
	}

	@SuppressWarnings("unchecked")
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		// Check if our HP is 0
		if (HP == 0) {
			// Make firefighter die, by removing it from the context
			Context<Object> context = ContextUtils.getContext(this);
			if (!context.remove(this)) {
				// Debug
				System.out.println("Error! Tried removing a firefighter but couldn't");
				// Can't continue with this method, since the firefighter should be dead and removed
				return;
			}
			// Debug
			System.out.println("A firefighter died!");
			// Dead firefighters can't fight fire
			return;
		}

		// Get the grid location of this FireFighter
		GridPoint pt = grid.getLocation(this);

		int tickCount = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		// Signal that we are alive to the blackboard
		BlackBoard.signalAlive(this.id, tickCount, pt);

		// Check if we are next to a fire
		GridPoint firePoint = checkForFire(pt);
		if (firePoint != null) {
			// Get the distance to the closest fire
			double x = grid.getDistance(pt, firePoint);
			// Debug
			// System.out.println(String.format("I can see a fire %1$,.2f", x));
			// If firefighter is on a fire, lose health
			if (Math.floor(x) == 0) {
				HP--;
			}
			// Here we check whether we are surrounded by fire. If we are, there is a severe punishment
			// Start by finding all fires within a distance of 1.
			List<GridCell<Fire>> fires = getFiresInDistance(pt, 1);
			// Remove the possible fire underneath the firefighter from the list, then check if the size of the list is 8 (implying that there is fire everywhere around the firefighter)
			if (fires.stream().filter(i -> Math.floor(grid.getDistance(pt, i.getPoint())) == 1).count() == 8) {
				// Remove half of the starting health of the firefighter
				int healthToRemove = (int) Math.floor(BosBrandConstants.FIREFIGHTER_STARTING_HEALTH / 2.0);
				// Debug
				System.out.println(String.format("Surrounded by fire, removing %d HP", healthToRemove));
				HP -= healthToRemove;
			}
			if (Math.floor(x) <= 1) {
				// We are now adjacent to a Fire
				extinguishFire(firePoint);
				// Can't do anything else this turn, return
				return;
			}
		}

		moveTowards(patrolSmart(pt, tickCount));

		// // Make a list with all cells containing allies
		// GridCellNgh<FireFighter> allyNeighbourHoodCreator = new GridCellNgh<FireFighter>(grid, pt, FireFighter.class, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE);
		// List<GridCell<FireFighter>> gridCells = allyNeighbourHoodCreator.getNeighborhood(true);
		// // Debug
		// // System.out.println(String.format("Amount of cells found: %d", gridCells.size()));
		// // Filter on GridCells containing at least one item
		// List<GridCell<FireFighter>> allyGridCells = gridCells.stream().filter(i -> i.items().iterator().hasNext()).collect(Collectors.toList());
		// // Check if any cells were found at all
		// if (allyGridCells.size() > 0) {
		// // Check if any ally is currently fighting fire
		// List<GridCell<FireFighter>> fightingAllies = allyGridCells.stream().filter(i -> i.items().iterator().hasNext()).filter(j -> j.items().iterator().next().fightingFire).collect(Collectors.toList());
		// // If any
		// if (fightingAllies.size() > 0) {
		// GridCell<FireFighter> ally = null;
		// // Debug
		// // System.out.println(String.format("Found %d nearby allies fighting fire!", fightingAllies.size()));
		// // Sense closest
		// OptionalInt minDistance = fightingAllies.stream().mapToInt(i -> (int) grid.getDistance(pt, i.getPoint())).min();
		// if (minDistance.isPresent()) {
		// // Debug
		// // System.out.println(String.format("Nearest fighting ally distance: %d", minDistance.getAsInt()));
		// // Get the point of the closest fighting ally
		// Optional<GridCell<FireFighter>> close = fightingAllies.stream().filter(i -> (int) grid.getDistance(pt, i.getPoint()) == minDistance.getAsInt()).findFirst();
		// if (close.isPresent()) {
		// ally = close.get();
		// }
		// } else {
		// // Debug
		// // System.out.println("No distance to nearest fighting ally found.");
		// }
		//
		// if (ally != null) {
		// // Move towards the closest fighting ally
		// moveTowards(ally.getPoint());
		// return;
		// } else {
		// patrol();
		// return;
		// }
		// } else {
		// // This means there are allies close, but none are fighting fire
		// if (firePoint != null) {
		// // Move towards the closest fire
		// moveTowards(firePoint);
		// return;
		// } else {
		// patrol();
		// return;
		// }
		// }
		// } else {
		// // If we have a fire nearby, move there
		// if (firePoint != null) {
		// // Move towards the closest fire
		// moveTowards(firePoint);
		// return;
		// } else {
		// patrol();
		// return;
		// }
		// }
	}

	public List<GridCell<Fire>> getFiresInDistance(GridPoint pt, int distance) {
		// Make a list with all cells containing fire
		GridCellNgh<Fire> fireNeighbourhoodCreator = new GridCellNgh<Fire>(grid, pt, Fire.class, distance, distance);
		List<GridCell<Fire>> gridCells = fireNeighbourhoodCreator.getNeighborhood(true);
		// Return GridCells containing at least one item
		return gridCells.stream().filter(i -> i.items().iterator().hasNext()).collect(Collectors.toList());
	}

	public GridPoint checkForFire(GridPoint pt) {
		// Get all the Cells that contain Fire within the distance we can see
		List<GridCell<Fire>> fireGridCells = getFiresInDistance(pt, BosBrandConstants.FIREFIGHTER_LOOKING_DISTANCE);
		// Check if any fires were detected
		if (fireGridCells.size() > 0) {
			// System.out.println("FireFighter " + id + " is reporting fires");
			// Report each fire location to the blackboard
			// fireGridCells.stream().forEach(i -> BlackBoard.reportLocationStatus(i.getPoint(), true));

			for (int i = 0; i < fireGridCells.size(); i++) {
				BlackBoard.reportLocationStatus(fireGridCells.get(i).getPoint(), true);
			}

			GridCell<Fire> fire = null;
			// Debug
			// System.out.println(String.format("Found %d nearby fires!", fireGridCells.size()));
			// Check which fire is closest
			OptionalInt minDistance = fireGridCells.stream().mapToInt(i -> (int) grid.getDistance(pt, i.getPoint())).min();
			if (minDistance.isPresent()) {
				// Debug
				// System.out.println(String.format("Nearest fire distance: %d", minDistance.getAsInt()));
				// Get the point of the closest fire
				Optional<GridCell<Fire>> close = fireGridCells.stream().filter(i -> (int) grid.getDistance(pt, i.getPoint()) == minDistance.getAsInt()).findFirst();
				if (close.isPresent()) {
					fire = close.get();
				}
			} else {
				// Debug
				// System.out.println("No distance found to closest fire.");
			}

			if (fire != null) {
				// Move towards the closest fire
				return fire.getPoint();
			}
		}
		// No fire found, return null
		return null;
	}

	@SuppressWarnings("unchecked")
	public void extinguishFire(GridPoint pt) {
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
					// Report that a fire has been removed
					BlackBoard.reportLocationStatus(pt, false);
					// Find the tree on this cell
					Iterable<Object> treeIterator = grid.getObjectsAt(pt.getX(), pt.getY());
					for (Object obj2 : treeIterator) {
						// Extinguish the fire
						if (obj2 instanceof Tree) {
							((Tree) obj2).toggleBurning();
						}
					}
					// Award the firefighter a bounty
					bounty += bountyModifier * BosBrandConstants.FIREFIGHTER_DEFAULT_EXTINGUISH_BOUNTY;
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

	public boolean moveTowards(Direction direction) {
		// Debug
		// System.out.println(String.format("Moving in direction: %s", direction.toString()));
		// Debug
		// System.out.println(String.format("Location before move: %d,%d", grid.getLocation(this).getX(), grid.getLocation(this).getY()));

		// Check if we can even more in the desired direction
		if (Direction.canIMoveInDirection(this.getLocation(), direction)) {
			// This should be self explanatory
			switch (direction) {
			case NORTH:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
				break;
			case NORTHEAST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.EAST);
				break;
			case EAST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.EAST);
				break;
			case SOUTHEAST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.EAST);
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
				break;
			case SOUTH:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
				break;
			case SOUTHWEST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.SOUTH);
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.WEST);
				break;
			case WEST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.WEST);
				break;
			case NORTHWEST:
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.WEST);
				grid.moveByVector(this, BosBrandConstants.FIREFIGHTER_MOVE_SPEED, repast.simphony.space.Direction.NORTH);
				break;
			default:
				// TODO: handle this (error?)
				System.out.println("Error in FireFighter.moveTowards(Direction)");
				break;
			}
			// Debug
			// System.out.println(String.format("Location after move: %d,%d", grid.getLocation(this).getX(), grid.getLocation(this).getY()));
			// Return that we successfully moved
			return true;
		} else {
			// Debug
			// System.out.println(String.format("Could not move in direction %s; current location: %d,%d", direction, grid.getLocation(this).getX(), grid.getLocation(this).getY()));
			// Could not move in the direction we wanted
			return false;
		}

	}

	public GridPoint getLocation() {
		return grid.getLocation(this);
	}

	public void setBounty(int bounty) {
		this.bounty = bounty;
	}

	public int getBounty() {
		return bounty;
	}

	public void setBountyModifier(int bountyModifier) {
		this.bountyModifier = bountyModifier;
	}

	public int getBountyModifier() {
		return bountyModifier;
	}

	public int getId() {
		return this.id;
	}

	public void patrol() {
		Direction chosenDirection = null;
		ArrayList<Direction> directions = Direction.getAllDirections();
		Random r = new Random();
		do {
			// Do random move
			int choice = r.nextInt(directions.size());
			chosenDirection = directions.get(choice);
		} while (!Direction.canIMoveInDirection(this.getLocation(), chosenDirection));
		// Debug
		// System.out.println(String.format("Patrolling in direction: %s", chosenDirection.toString()));
		// Execute move
		moveTowards(chosenDirection);
	}

	private Direction patrolSmart(GridPoint location, int tickCount) {
		// create heatmap
		int initialValue = 0;
		int[][] fireFighterLayer = new int[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		int[][] fireLayer = new int[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		int[][] distanceLayer = new int[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		for (int i = 0; i < fireLayer.length; i++) {
			for (int j = 0; j < fireLayer[0].length; j++) {
				fireFighterLayer[i][j] = initialValue;
				fireLayer[i][j] = initialValue;
				distanceLayer[i][j] = initialValue;
			}
		}
		boolean[][] hasBeenFilled = new boolean[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		// fill heatmap with firefighters
		ArrayList<GridPoint> fireFighterList = BlackBoard.getFireFighterLocations(tickCount, startingTeamMates, this.id);

		// floodfill from firefighters, with decreasing strength (we do not want to go to a square if the square has a firefighter on it
		for (GridPoint f : fireFighterList) {
			int strength = 100;
			int decrease = 1;
			floodFill(strength, decrease, fireFighterLayer, hasBeenFilled, f);
			hasBeenFilled = new boolean[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		}

		ArrayList<GridPoint> fireList = BlackBoard.getReportedFires();
		// Debug
		// System.out.println("Hey hey hey hey hey hey! The size of the fire list is " + fireList.size());

		// go through the list of known fires, add heat to the heatmap for every one of them
		for (GridPoint f : fireList) {
			int strength = 100;
			int decrease = 1;
			floodFill(strength, decrease, fireLayer, hasBeenFilled, f);
			hasBeenFilled = new boolean[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		}

		int distanceStrength = 100;
		int distanceDecrease = 1;
		floodFill(distanceStrength, distanceDecrease, distanceLayer, hasBeenFilled, location);
		hasBeenFilled = new boolean[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];

		// create the final heatmap
		float[][] heatMap = new float[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];

		// find the maximum values of the layers, to normalize
		float fireMax = Math.max(1, Arrays.stream(fireLayer).flatMapToInt(Arrays::stream).max().getAsInt());
		float fireFighterMax = Math.max(1, Arrays.stream(fireFighterLayer).flatMapToInt(Arrays::stream).max().getAsInt());
		float distanceMax = Math.max(1, Arrays.stream(distanceLayer).flatMapToInt(Arrays::stream).max().getAsInt());

		// decide the coefficients of the layers to decide the importance of them
		float fireLayerCoefficient = 1.0f;
		float fireFighterLayerCoefficient = 0f;
		float distanceCoefficient = 0.0f;

		// fill every element of the heatmap with the separate layers
		for (int i = 0; i < heatMap.length; i++) {
			for (int j = 0; j < heatMap[0].length; j++) {
				heatMap[i][j] = (fireLayerCoefficient * (fireLayer[i][j] / fireMax)) + (-fireFighterLayerCoefficient * (fireFighterLayer[i][j] / fireFighterMax)) + (distanceCoefficient * distanceLayer[i][j] / distanceMax);
			}
		}

		// Debug
		// System.out.println("Heatmap!");
		// toStringArray(heatMap);
		 System.out.println("Fire Layer!");
		 toStringArray(fireLayer);
		// System.out.println("Firefighter Layer!");
		// toStringArray(fireFighterLayer);
		// System.out.println("Distance Layer!");
		// toStringArray(distanceLayer);

		// decide how to find where to go (best tile around firefighter, tile that leads to best tile on map)
		Random random = new Random();
		double epsilon = 0.000001;
		double bestHeat = Double.NEGATIVE_INFINITY;
		GridPoint bestPoint = null;
		int myX = location.getX();
		int myY = location.getY();
		for (int i = myX - 1; i <= myX + 1; i++) {
			for (int j = myY - 1; j <= myY + 1; j++) {
				if (i < 0 || j < 0 || i > heatMap.length - 1 || j > heatMap[0].length - 1) {
					continue;
				}
				if (i == myX && j == myY) {
					continue;
				}
				if (((double) (heatMap[i][j])) + epsilon * random.nextDouble() > bestHeat) {
					bestHeat = ((double) (heatMap[i][j])) + epsilon * random.nextDouble();
					bestPoint = new GridPoint(i, j);
				}
			}
		}

		return Direction.getDirection(location, bestPoint);
	}

	public void toStringArray(float[][] array) {
		System.out.println("Printing array! Size: " + array.length + " x " + array[0].length);
		String toWrite = "";
		for (int i = 0; i < array.length; i++) {
			toWrite += " \n | ";
			for (int j = 0; j < array[0].length; j++) {
				toWrite += (array[i][j] + " | ");
			}
			toWrite += "\n -------------------------------------------------------------------";
		}
		System.out.println(toWrite);
	}

	public void toStringArray(int[][] array) {
		System.out.println("Printing array! Size: " + array.length + " x " + array[0].length);
		String toWrite = "";
		for (int i = 0; i < array.length; i++) {
			toWrite += "\n | ";
			for (int j = 0; j < array[0].length; j++) {
				toWrite += (array[i][j] + " | ");
			}
			toWrite += "\n -------------------------------------------------------------------";
		}
		System.out.println(toWrite);
	}

	private void floodFill(int strength, int increase, int[][] heatMap, boolean[][] hasBeenFilled, GridPoint location) {
		// if there would be no increase, return
		if (strength <= 0)
			return;
		// this defines the decrease in strength of the heatmap
		int x = location.getX();
		int y = location.getY();
		if (heatMap[x][y] >= strength) {
			return;
		}
		heatMap[x][y] = strength;
		hasBeenFilled[x][y] = true;
		// decrease the strength to go to adjacent locations
		strength -= increase;
		strength = strength < 0 ? 0 : strength;
		if (strength > 0) {
			// Loop through all directions
			for (Direction dir : Direction.getAllDirectionsShuffled()) {
				// Get the location that is in this direction
				GridPoint locationToCheck = Direction.getPointInDirection(location, dir);
				// Check if this direction is accessible, and has not been filled yet
				if (Direction.canIMoveInDirection(locationToCheck, dir) && !hasBeenFilled[locationToCheck.getX()][locationToCheck.getY()]) {
					// Flood fill from that location
					floodFill(strength, increase, heatMap, hasBeenFilled, locationToCheck);
				}
			}
			// for (int i = x + 1; i >= x - 1; i--) {
			// for (int j = y + 1; j >= y - 1; j--) {
			// if (i >= 0 && j >= 0 && i < BosBrandConstants.FOREST_WIDTH && j < BosBrandConstants.FOREST_HEIGHT) {
			// if (!hasBeenFilled[i][j]) {
			// floodFill(strength, increase, heatMap, hasBeenFilled, new GridPoint(i, j));
			// }
			// }
			// }
			// }
		}

		return;

	}
}