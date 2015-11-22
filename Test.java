package BosBrand;

public class Test {

	public static void main(String[] args) {
		Tree[][] forest = new Tree[BosBrandConstants.FOREST_HEIGHT][BosBrandConstants.FOREST_WIDTH];		
		TreeType treeType = TreeType.PALM;
		for (int i = 0; i < BosBrandConstants.FOREST_HEIGHT
				* BosBrandConstants.FOREST_WIDTH; i++) {
		
			// The X-coordinate for this Tree will be the modulus of the width
			// of the forest
			int x = i % BosBrandConstants.FOREST_WIDTH;
			// The Y-coordinate for this Tree will be the floor of dividing
			// placer by the width of the forest
			int y = (int) (i / BosBrandConstants.FOREST_WIDTH);
			// Place the Tree in the correct location on the grid
			
			System.out.println(String.format("Trying to place Tree on %d,%d", x, y));

			forest[x][y] = new Tree(treeType);
		}
	}

}
