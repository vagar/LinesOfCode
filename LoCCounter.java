import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class LoCCounter {	
	
	public static final JsonObject KNOWN_FILE_EXTENSIONS;

	static {
		try {
			JsonReader reader = Json.createReader(new FileInputStream(
					"knownExtensions.json"));

			KNOWN_FILE_EXTENSIONS = reader.readObject();

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Initialization of known file extensions failed", e);
		}
	}

	/**
	 * Counts the lines of code in the file at the location specified by <b>path</b>.
	 * @param path - the path of the file to count lines of code in.
	 * @return The lines of code, comment and blank, wrapped in a <code>LoCResult</code> object.
	 */
	public static LoCResult countFile(Path path) {
				
		BufferedReader is = null;
		int totalcount = 0, commcount = 0, blankcount = 0;
		boolean flag = false;

		try {		
			try {
				is = Files.newBufferedReader(path, Charset.defaultCharset()); //new BufferedReader(new FileReader(path));
				String l;

				while ((l = is.readLine()) != null) {
					
					totalcount++;

					if ((l.trim().equals("")) && !flag)
						blankcount++;
					
					if(flag)
						commcount++;			
					
					if (l.indexOf("//") != -1 && !flag)
						if (l.trim().split("//").length == 0 || l.trim().split("//")[0].equals(""))
							commcount++;

					if (l.indexOf("/*") != -1 && !flag) {
						flag = true;
						if (l.trim().split("/*").length == 1 || l.trim().split("/*")[0].equals(""))
							commcount++;
					}

					if (l.indexOf("*/") != -1 && flag)
						flag = false;
				}
				
			return new LoCResult(path, totalcount, commcount, blankcount);	
						
			} finally {
				if (is != null)
					is.close();
			}
							
		} catch (IOException e) {
			//e.printStackTrace();
			//System.out.println(path);
		}
		
		return new LoCResult(true);	
	}
	
	public static ArrayList<LoCResult> count(Path path) {
		
		ArrayList<LoCResult> results = new ArrayList<LoCResult>();
		
		if(Files.isRegularFile(path)){	
			results.add(countFile(path));
			return results;
		}	
		
		PrintFiles visitor = new PrintFiles(KNOWN_FILE_EXTENSIONS);
		try {
			Files.walkFileTree(path, visitor);
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		ArrayList<Path> files = visitor.getFiles();
		
		for(Path p: files){
			results.add(countFile(p));
		}
		
		return results;
	}
	
	/**
	 * @param args - the file to count loc in
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out
				.println("Please give the path to a file to count loc in.\nUsage: java LoCCounter <file>");
			return;
		}
		
		ArrayList<LoCResult> results = count(Paths.get(args[0]));
		
		for (LoCResult res : results) {
			System.out.println(res.report());
		}		
		
	}	
}
