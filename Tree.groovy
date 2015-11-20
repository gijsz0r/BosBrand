package BosBrand

public class Tree {

	private int treeHP;
	private boolean isBurning;
	private boolean isRaining;

	public Tree(TreeType treeType) {
		this.treeHP = TreeType.getHP(treeType)*BosBrandConstants.TREE_HP_MODIFIER;
		this.isBurning = false;
		this.isRaining = false;
	}
}
