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
		// Set ID for the context. Note: should be same as package
		context.setId("BosBrand");
		// Create a grid. Note: name should be 'grid' for some reason
		// Parameters are type of border handling, how to add items to the grid and the dimensions of the grid
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(BosBrandConstants.BORDER_TYPE, new SimpleGridAdder<Object>(), true, BosBrandConstants.FOREST_WIDTH, BosBrandConstants.FOREST_HEIGHT));

		// Set the tree type for this run
		TreeType treeType = TreeType.PALM;
		// We are making a tree on each cell
		for (int i = 0; i < BosBrandConstants.FOREST_WIDTH * BosBrandConstants.FOREST_HEIGHT; i++) {
			// Add the Tree objects to the context
			context.add(new Tree(treeType));
		}
		// Place each created Tree on a location in the grid
		int placer = 0;
		// Note that we only have Tree objects in the context so far
		for (Object tree : context) {
			// The X-coordinate for this Tree will be the modulus of the width of the forest
			int x = placer % BosBrandConstants.FOREST_WIDTH;
			// The Y-coordinate for this Tree will be the floor of dividing placer by the width of the forest
			int y = (int) (placer / BosBrandConstants.FOREST_WIDTH);
			// Place the Tree in the correct location on the grid
			grid.moveTo(tree, x, y);
			// Tell the tree to save it's location
			((Tree) tree).setLocation(x, y);
			// Increase the index
			placer++;
		}

		// Add a bunch of FireFighters
		// int fireFighterCount = BosBrandConstants.INITIAL_FIREFIGHTERS;
		// Read the amount of firefighters we need to make from the parameters file
		Parameters params = RunEnvironment.getInstance().getParameters();
		int fireFighterCount = (Integer) params.getValue("firefighter_count");
		for (int i = 0; i < fireFighterCount; i++) {
			context.add(new FireFighter(grid));
		}

		// Add a bunch of Fires
		int fireCount = BosBrandConstants.INITIAL_FIRES;
		for (int i = 0; i < fireCount; i++) {
			context.add(new Fire(grid));
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
					int x = r.nextInt(BosBrandConstants.FOREST_WIDTH);
					int y = r.nextInt(BosBrandConstants.FOREST_HEIGHT);
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

		// We add the environment that controls various environmental elements
		context.add(new Environment(grid, fireFighterCount));
		// Debug
		System.out.println("Environment created!");

		// Tell the RunEnvironment we want to stop the run after X ticks
		RunEnvironment.getInstance().endAt(BosBrandConstants.RUN_TICKS);

		// Return the created context
		return context;
	}

}
