package BosBrand;

public class Tree {

	private int treeHP;
	private boolean isBurning;
	private boolean isRaining;

	public Tree(TreeType treeType) {
		this.treeHP = TreeType.getHP(treeType)*BosBrandConstants.TREE_HP_MODIFIER;
		this.isBurning = false;
		this.isRaining = false;
	}

	public int getCurrentHP() {
		return treeHP;
	}

	public void setCurrentHP(int hp) {
		if (hp < 0) hp = 0;
		treeHP = hp;
	}

	public boolean getIsBurning() {
		return isBurning;
	}

	public void toggleBurning() {
		isBurning = !isBurning;
	}

	public boolean getIsRaining() {
		return isRaining;
	}

	public void toggleRaining() {
		isRaining = !isRaining;
	}
}
