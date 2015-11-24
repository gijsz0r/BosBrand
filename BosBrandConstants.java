package BosBrand;

import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.space.grid.StrictBorders;

public class BosBrandConstants {

	// These constants define the dimensions of the grid
	public static final int FOREST_WIDTH = 25;
	public static final int FOREST_HEIGHT = 25;
	// This constant defines the amount of HP tree will have
	public static final int TREE_HP_MODIFIER = 100;
	// These constants define environmental probabilities
	public static final double CHANCE_OF_RAIN = 0.2;
	public static final double CHANCE_OF_WIND_TURNING = 0.2;
	public static final double CHANCE_OF_FIRE_SPREADING = 0.05;
	public static final double TREE_CHANCE_OF_COMBUSTION = 0.001;
	public static final double RAIN_MODIFIER = 0.50;
	public static final double WIND_MODIFIER = 2.0;
	// These constants define the amount of agents at the start of the
	// simulation
	public static final int INITIAL_FIREFIGHTERS = 1;
	public static final int INITIAL_FIRES = 1;
	// These constants define the speed at which agents move, or trees burn
	public static final double WILDFIRE_SPEED_MIN = 0.25;
	public static final double WILDFIRE_SPEED_MAX = 0.75;
	public static final int FIREFIGHTER_MOVE_SPEED = 1;
	public static final int TREE_BURNING_SPEED = 1;
	// This constant defines the distance FireFighters can sense
	public static final int FIREFIGHTER_LOOKING_DISTANCE = 3;
	// This constant defines the type of borders used in the simulation
	public static final GridPointTranslator BORDER_TYPE = new StrictBorders();
}
