package BosBrand;

import java.util.ArrayList;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;

public class BosBrandBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		// Reset the blackboard
		BlackBoard.resetBlackBoard();
		
		// Read the simulation's parameters, as defined in the parameters file
		Parameters params = RunEnvironment.getInstance().getParameters();

		// Create some parameters
		int forestWidth = params.getInteger("forest_width");
		int forestHeight = params.getInteger("forest_height");

		// Set ID for the context. Note: should be same as package
		context.setId("BosBrand");
		// Create a grid. Note: name should be 'grid' for some reason
		// Parameters are type of border handling, how to add items to the grid and the dimensions of the grid
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(BosBrandConstants.BORDER_TYPE, new SimpleGridAdder<Object>(), true, forestWidth, forestHeight));

		// Set the tree type for this run
		TreeType treeType = TreeType.PALM;
		// Get the tree HP modifier
		int treeHPModifier = params.getInteger("tree_hp_modifier");
		// We are making a tree on each cell
		for (int i = 0; i < forestWidth * forestHeight; i++) {
			// Add the Tree objects to the context
			context.add(new Tree(treeType, treeHPModifier));
		}
		// Place each created Tree on a location in the grid
		int placer = 0;
		// Note that we only have Tree objects in the context so far
		for (Object tree : context) {
			// The X-coordinate for this Tree will be the modulus of the width of the forest
			int x = placer % forestWidth;
			// The Y-coordinate for this Tree will be the floor of dividing placer by the width of the forest
			int y = (int) (placer / forestWidth);
			// Place the Tree in the correct location on the grid
			grid.moveTo(tree, x, y);
			// Tell the tree to save it's location
			((Tree) tree).setLocation(x, y);
			// Increase the index
			placer++;
		}

		// Get the amount of firefighters
		int fireFighterCount = params.getInteger("firefighter_count");
		int fireFighterLookingDistance = params.getInteger("firefighter_looking_distance");
		double fireFighterFireCoefficient = params.getDouble("firefighter_fire_coefficient");
		double fireFighterFireFighterCoefficient = params.getDouble("firefighter_firefighter_coefficient");
		// Add a bunch of FireFighters
		for (int i = 0; i < fireFighterCount; i++) {
			context.add(new FireFighter(grid, i, fireFighterLookingDistance, fireFighterCount, forestWidth, forestHeight, fireFighterFireCoefficient, fireFighterFireFighterCoefficient));
		}

		// Get the amount of initial fires
		int fireCount = params.getInteger("initial_fire_count");
		double fireSpreadChance = params.getDouble("environment_fire_spread_chance");
		// Add a bunch of Fires
		for (int i = 0; i < fireCount; i++) {
			context.add(new Fire(grid, forestWidth, forestHeight, fireSpreadChance));
		}

		// Create a list of locations that have something spawned in them
		ArrayList<GridPoint> spawnLocations = new ArrayList<GridPoint>();
		Random r = new Random();
		for (Object obj : context) {
			if (obj instanceof Tree) {
				// Skip Trees here
			} else {
				// Any other type is either a FireFighter or a Fire, which we will need to spawn on a location in the grid
				boolean foundNewLocation = false;
				GridPoint spawnLocation = null;
				do {
					// Create a random location
					int x = r.nextInt(forestWidth);
					int y = r.nextInt(forestHeight);
					// Set our spawn point to these coordinates
					spawnLocation = new GridPoint(x, y);
					// Check if the coordinates are not the coordinates of a spawn point that has already
					foundNewLocation = !spawnLocations.stream().anyMatch(i -> i.getX() == x && i.getY() == y);
					// Do this until we find a good location
				} while (!foundNewLocation);
				// Move the object to the spawn location
				grid.moveTo(obj, spawnLocation.getX(), spawnLocation.getY());
			}
		}

		// Get the environment parameters for this run
		int rainIntensity = params.getInteger("rain_intensity");
		double fireSpawnChance = params.getDouble("environment_fire_spawn_chance");
		// We add the environment that controls various environmental elements
		context.add(new Environment(grid, forestWidth, forestHeight, fireFighterCount, rainIntensity, fireSpawnChance, fireSpreadChance));
		// Debug
		System.out.println("Environment created!");

		// Tell the RunEnvironment we want to stop the run after X ticks
		int runtime = params.getInteger("runtime");
		RunEnvironment.getInstance().endAt(runtime);

		// Return the created context
		return context;
	}

}
