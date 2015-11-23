package BosBrand;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public class Environment {

	private Grid<Object> grid;
	private Random random = new Random();

	public Environment(Grid<Object> grid) {
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void step() {
		// Check wind direction change
		// TODO: implement wind
		// Check if rain moves
		// TODO: implement rain

		// Get the context this environment is in, and get all Tree objects
		Context<Object> context = ContextUtils.getContext(this);
		IndexedIterable<Object> trees = context.getObjects(Tree.class);
		// Loop over all trees
		for (Object obj : trees) {
			if (obj instanceof Tree) {
				Tree tree = (Tree) (obj);
				// Check if the tree is not dead
				if (tree.getCurrentHP() > 0) {
					// Check if the tree is already burning
					if (tree.getIsBurning()) {
						// Reduce tree HP
						tree.setCurrentHP(tree.getCurrentHP()
								- BosBrandConstants.TREE_BURNING_SPEED);
					} else {
						// Tree is not burning, check if the tree spontaneously
						// combusts
						if (random.nextDouble() <= BosBrandConstants.TREE_CHANCE_OF_COMBUSTION) {
							// Create a new Fire object
							Fire fire = new Fire(grid);
							// Add Fire to the context
							if (context.add(fire)) {
								// If the Fire was successfully added, set tree on
								// fire and put the Fire object on the grid
								GridPoint treeLocation = grid.getLocation(tree);
								grid.moveTo(fire, treeLocation.getX(),
										treeLocation.getY());
								tree.toggleBurning();
							}
						}
					}
				}
			}
		}
	}
}
