package BosBrand;

import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.space.grid.StrictBorders;

public class BosBrandConstants {

	// These constants define the dimensions of the grid
	public static final int FOREST_WIDTH = 25;
	public static final int FOREST_HEIGHT = 25;
	// This constant defines the amount of HP tree will have
	public static final int TREE_HP_MODIFIER = 10;
	// This constant defines the amount of steps a Rain object can exist
	public static final int RAIN_LIFETIME = 10;
	// These constants define environmental probabilities
	public static final double CHANCE_OF_RAIN = 0.2;
	public static final double CHANCE_OF_WIND_TURNING = 0.1;
	public static final double CHANCE_OF_FIRE_SPREADING = 0.05;
	public static final double TREE_CHANCE_OF_COMBUSTION = 0.001;
	public static final double RAIN_MODIFIER = 0.50;
	public static final double WIND_MODIFIER = 1.0;
	// These constants define the amount of agents at the start of the simulation
	public static final int INITIAL_FIREFIGHTERS = 10;
	public static final int INITIAL_FIRES = 1;
	// This constant defines the intensity of rain on the map
	public static final int RAIN_INTENSITY = 10;
	// These constants define the speed at which agents move, or trees burn
	public static final int FIREFIGHTER_MOVE_SPEED = 1;
	public static final int RAIN_MOVE_SPEED = 1;
	public static final int TREE_BURNING_SPEED = 1;
	// This constant defines the distance FireFighters can sense
	public static final int FIREFIGHTER_LOOKING_DISTANCE = 8;
	// This constant defines the starting health of the fire fighters
	public static final int FIREFIGHTER_STARTING_HEALTH = 4;
	// This constant defines the type of borders used in the simulation
	public static final GridPointTranslator BORDER_TYPE = new StrictBorders();
	// This constant defines the default modifier for extinguishing fire
	public static final int FIREFIGHTER_DEFAULT_BOUNTY_MODIFIER = 1;
	// This constant defines the default bounty for extinguishing a fire
	public static final int FIREFIGHTER_DEFAULT_EXTINGUISH_BOUNTY = 1;
	// This constant defines the amount of ticks the run will last
	public static final double RUN_TICKS = 300;
}
