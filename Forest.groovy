package BosBrand

public class Forest {

	private Square[][] Map;
	
	public Forest() {
		//Set tree type
		TreeType treeType = TreeType.PALM;
		//Create a new Map
		Map = new Square[BosBrandConstants.FOREST_WIDTH][BosBrandConstants.FOREST_HEIGHT];
		//Create new Squares
		for (int i = 0; i < BosBrandConstants.FOREST_WIDTH; i++) {
			for (int j = 0; j < BosBrandConstants.FOREST_HEIGHT; j++) {
				Map[i][j] = new Square(treeType);
			}
		}
		System.out.println("Initiated Squares");
	}
	
}
