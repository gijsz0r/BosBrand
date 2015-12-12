package BosBrand;

import java.util.ArrayList;

import repast.simphony.space.grid.GridPoint;

public class BlackBoard {

	private static int[] blackboard = new int[20000];

	/* CHANNEL DEFINITIONS: [0..20000] */

	/**
	 * individual firefighter channels reserved from 1 to 600. channels contain:<br\>
	 * (+0) id of this firefighter;<br\>
	 * (+1) alive indicator;<br\>
	 * (+2) current position of this firefighter<br\>
	 */
	private static final int CHANNEL_FIRE_FIGHTER = 1;
	private static final int FIRE_FIGHTER_CHANNEL_COUNT = 3;
	private static final int FIRE_FIGHTER_CHANNEL_OFFSET_ID = 0;
	private static final int FIRE_FIGHTER_CHANNEL_OFFSET_ALIVE = 1;
	private static final int FIRE_FIGHTER_CHANNEL_OFFSET_LOCATION = 2;

	/**
	 * forest representation channels reserved from 601 to 10600. channels contain:<br\>
	 * (+0) whether or not there is fire on this location in the forest
	 */
	private static final int CHANNEL_FOREST = 601;
	private static final int FOREST_CHANNEL_COUNT = 1;
	private static final int FOREST_FIRE_OFFSET = 0;

	// ########### Methods ########################################

	/**
	 * returns the corresponding channel for the given firefighter without offset.<br\>
	 * Offsets are:<br\>
	 * (+0) id of this firefighter;<br\>
	 * (+1) alive indicator;<br\>
	 * (+2) current position of this soldier<br\>
	 * 
	 * @param fireFighterId
	 * @return
	 */
	private static int getFireFighterChannel(int fireFighterId) {
		return CHANNEL_FIRE_FIGHTER + fireFighterId * FIRE_FIGHTER_CHANNEL_COUNT;
	}

	/**
	 * for FireFighter. should be called at the beginning of each step. FireFighter broadcasts that he is still alive and his current GridPoint
	 * 
	 * @param fireFighterId
	 * @param pt
	 */
	public static void signalAlive(int fireFighterId, int tickCount, GridPoint pt) {
		// Get the firefighter's channel
		int c = getFireFighterChannel(fireFighterId);
		// Broadcast the information onto the blackboard
		blackboard[c + FIRE_FIGHTER_CHANNEL_OFFSET_ALIVE] = tickCount;
		blackboard[c + FIRE_FIGHTER_CHANNEL_OFFSET_LOCATION] = toInt(pt);
	}

	/**
	 * returns whether the firefighter is still alive
	 * 
	 * @param fireFighterId
	 * @return
	 */
	public static boolean isAlive(int fireFighterId, int tickCount) {
		// Get the firefighter's channel
		int c = getFireFighterChannel(fireFighterId);
		// Get the last alive tick written into the alive channel
		int lastSeenTick = blackboard[c + FIRE_FIGHTER_CHANNEL_OFFSET_ALIVE];
		// Check whether the last alive tick is the same as current tick (or one less, since firefighter's steps are called randomly)
		return lastSeenTick >= (tickCount - 1) && lastSeenTick > 0;
	}

	public static GridPoint getFireFighterLocation(int fireFighterId) {
		// Get the firefighter's channel
		int c = getFireFighterChannel(fireFighterId);
		// Return the converted location
		return toGridPoint(blackboard[c + FIRE_FIGHTER_CHANNEL_OFFSET_LOCATION]);
	}

	private static int getForestChannel(GridPoint location) {
		return CHANNEL_FOREST + toInt(location);
	}

	public static void reportLocationStatus(GridPoint location, boolean onFire) {
		int c = getForestChannel(location);
		blackboard[c + FOREST_FIRE_OFFSET] = onFire ? 1 : 0;
	}

	public static boolean getLocationStatus(GridPoint location) {
		int c = getForestChannel(location);
		return blackboard[c + FOREST_FIRE_OFFSET] == 1;
	}

	public static ArrayList<GridPoint> getFireFighterLocations(int tickCount, int startingTeamMates, int ID) {
		ArrayList<GridPoint> fireFighterList = new ArrayList<GridPoint>();
		for (int i = CHANNEL_FIRE_FIGHTER; i < CHANNEL_FIRE_FIGHTER + (FIRE_FIGHTER_CHANNEL_COUNT * startingTeamMates); i += FIRE_FIGHTER_CHANNEL_COUNT) {
			int fireFighterID = blackboard[i + FIRE_FIGHTER_CHANNEL_OFFSET_ID];
			if (ID != fireFighterID && isAlive(fireFighterID, tickCount)) {
				fireFighterList.add(toGridPoint(blackboard[i + FIRE_FIGHTER_CHANNEL_OFFSET_LOCATION]));
			}
		}

		return fireFighterList;
	}

	public static ArrayList<GridPoint> getReportedFires() {
		
		// Create a list to write to
		ArrayList<GridPoint> fireList = new ArrayList<GridPoint>();
		// Loop through the channels, starting at the point where the forests starts and continuing for as many items as there are in the forest
		for (int i = CHANNEL_FOREST; i < CHANNEL_FOREST + BosBrandConstants.FOREST_HEIGHT * BosBrandConstants.FOREST_WIDTH; i++) {
			// Check if the location is reported as being on fire
			if (blackboard[i] == 1) {
				// Add this location to the list
				fireList.add(toGridPoint(i - CHANNEL_FOREST));
			}
		}
		return fireList;
	}

	// ########### Conversion Methods #############################
	// Note that for this to work, the forest's dimensions need to be <= 99

	private static int toInt(GridPoint location) {
		return toInt(location.getX(), location.getY());
	}

	private static int toInt(int x, int y) {
		return x * 100 + y;
	}

	private static GridPoint toGridPoint(int encoded) {
		return new GridPoint(encoded / 100, encoded % 100);
	}

}
