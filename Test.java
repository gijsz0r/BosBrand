package BosBrand;

import java.io.File;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		try {
			Runtime.getRuntime().exec("cmd /c run_batch.bat", null, new File("C:\\Users\\Antonie\\Java\\workspace_repast\\BosBrand\\src\\BosBrand"));
			// TODO: Write output files to 'Test Runs' folder
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
