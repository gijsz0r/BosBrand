package BosBrand;

import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.space.grid.StrictBorders;

public class BosBrandConstants {

	// This constant defines the amount of steps a Rain object can exist
	public static final int RAIN_LIFETIME = 10;
	// These constants define environmental probabilities
	public static final double CHANCE_OF_RAIN = 0.2;
	public static final double CHANCE_OF_WIND_TURNING = 0.1;
	public static final double RAIN_MODIFIER = 0.50;
	public static final double WIND_MODIFIER = 1.0;
	// These constants define the speed at which agents move, or trees burn
	public static final int FIREFIGHTER_MOVE_SPEED = 1;
	public static final int RAIN_MOVE_SPEED = 1;
	public static final int TREE_BURNING_SPEED = 1;
	// This constant defines the starting health of the fire fighters
	public static final int FIREFIGHTER_STARTING_HEALTH = 4;
	// This constant defines the type of borders used in the simulation
	public static final GridPointTranslator BORDER_TYPE = new StrictBorders();
	// This constant defines the default modifier for extinguishing fire
	public static final int FIREFIGHTER_DEFAULT_BOUNTY_MODIFIER = 1;
	// This constant defines the default bounty for extinguishing a fire
	public static final int FIREFIGHTER_DEFAULT_EXTINGUISH_BOUNTY = 1;
}
