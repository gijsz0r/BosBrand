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

		Context<Object> context = ContextUtils.getContext(this);
		IndexedIterable<Object> trees = context.getObjects(Tree.class);
		// loop over all trees
		for (Object obj : trees) {
			if (obj instanceof Tree) {
				Tree tree = (Tree) (obj);
				// check if tree is not dead
				if (tree.getCurrentHP() > 0) {
					// check if tree is already burning
					if (tree.getIsBurning()) {
						// Reduce Tree HP
						tree.setCurrentHP(tree.getCurrentHP()
								- BosBrandConstants.TREE_BURNING_SPEED);
					} else {
						// if it is not burning, check if the Tree spontaneously
						// combusts
						if (random.nextDouble() <= BosBrandConstants.TREE_CHANCE_OF_COMBUSTION) {
							// create new fire
							Fire fire = new Fire(grid);
							// add fire to context
							if (context.add(fire)) {
								// if fire was successfully added, set tree on
								// fire and put the fire in the grid
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
