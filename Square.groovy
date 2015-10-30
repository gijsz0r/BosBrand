package BosBrand

public class Square {
	
	private int treeHP;
	private boolean isBurning;
	private boolean isRaining;
		
	public Square(TreeType treeType) {
		this.treeHP = TreeType.getHP(treeType)*TREE_HP_MODIFIER;
		this.isBurning = false;
		this.isRaining = false;
	}
			
}
