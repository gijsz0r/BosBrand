package BosBrand;

import java.awt.Point;

public class Tree {

	private int treeHP;
	private boolean isBurning;
	private boolean isRaining;
	private int myX;
	private int myY;

	public Tree(TreeType treeType, int treeHPModifier) {
		this.treeHP = TreeType.getHP(treeType)*treeHPModifier;
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
	
	public void setLocation(int x, int y) {
		myX = x;
		myY = y;
	}
	
	public Point getLocation() {
		return new Point(myX, myY);
	}
}
