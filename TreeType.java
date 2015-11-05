package BosBrand;

public enum TreeType {
	PALM, ELM, WILLOW, OAK, PINE;

	public int getHP(TreeType type) {
		switch(type) {
			case PALM:
				return 1;
			case ELM:
				return 2;
			case WILLOW:
				return 3;
			case OAK:
				return 4;
			case PINE:
				return 5;
			default:
				return 0;
		}
	}
}