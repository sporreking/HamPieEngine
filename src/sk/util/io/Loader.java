package sk.util.io;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class Loader {
	
	public static final String loadSource(String path) {
		StringBuilder sb = new StringBuilder();
		
		try (Scanner scanner = new Scanner(new File(path))) {
			while(scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				if(scanner.hasNextLine())
					sb.append('\n');
			}
			
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
}