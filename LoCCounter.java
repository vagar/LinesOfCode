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
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class LoCCounter {

	public static final JsonObject KNOWN_FILE_EXTENSIONS;
	public static final JsonObject COMMENT_LITERALS;

	static {
		try {
			JsonReader reader = Json.createReader(new FileInputStream(
					"knownExtensions.json"));
			KNOWN_FILE_EXTENSIONS = reader.readObject();
			reader.close();

			JsonReader reader2 = Json.createReader(new FileInputStream(
					"commentLiterals.json"));
			COMMENT_LITERALS = reader2.readObject();
			reader2.close();

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Initialization of known file extensions failed", e);
		}
	}

	/**
	 * Counts the lines of code in the file at the location specified by
	 * <b>path</b>.
	 * 
	 * @param path
	 *            - the path of the file to count lines of code in.
	 * @return The lines of code, comment and blank, wrapped in a
	 *         <code>LoCResult</code> object.
	 */
	public static LoCResult countFile(Path path) {

		int totalcount = 0, commcount = 0, blankcount = 0;
		boolean flag = false;

		String ext = "";
		ArrayList<String> singleline = new ArrayList<String>(), blockstart = new ArrayList<String>(), blockend = new ArrayList<String>();

		for (String s : KNOWN_FILE_EXTENSIONS.keySet())
			if (path.getFileName().toString().endsWith("." + s)
					|| (s.length() > 5 && path.getFileName().toString()
							.endsWith(s)))
				ext = s;

		JsonObject lang = COMMENT_LITERALS.getJsonObject(KNOWN_FILE_EXTENSIONS
				.getString(ext));
		JsonArray literals = lang.getJsonArray("literals");

		for (JsonValue jv : literals) {
			switch (CommentType.valueOf(((JsonObject) jv).getString("type"))) {
			case SINGLE_LINE:
				singleline.add(((JsonObject) jv).getString("value"));
				break;
			case START_BLOCK:
				blockstart.add(((JsonObject) jv).getString("value"));
				break;
			case END_BLOCK:
				blockend.add(((JsonObject) jv).getString("value"));
				break;
			}
		}

		try (BufferedReader is = Files.newBufferedReader(path,
				Charset.defaultCharset())) {

			String l;

			while ((l = is.readLine()) != null) {

				totalcount++;

				if ((l.trim().equals("")) && !flag)
					blankcount++;

				if (flag)
					commcount++;

				if (singleline.size() > 0)
					for (String s : singleline) {
						if (l.indexOf(s) != -1 && !flag)
							if (l.trim().split(s).length == 0
									|| l.trim().split(s)[0].equals("")) {
								commcount++;
								break;
							}
					}

				if (blockstart.size() > 0)
					for (String s : blockstart) {
						if (l.indexOf(s) != -1 && !flag) {
							flag = true;
							if (l.trim().split(s).length == 1
									|| l.trim().split(s)[0].equals(""))
								commcount++;
						}

					}

				if (blockend.size() > 0)
					for (String s : blockend) {
						if (l.indexOf(s) != -1 && flag)
							flag = false;
					}

			}

			return new LoCResult(path, lang.getString("name"), totalcount,
					commcount, blankcount);

		} catch (IOException e) {
			// e.printStackTrace();
		}

		return new LoCResult(true);
	}

	/**
	 * Wrapper method for <code>countFile(Path)</code> that also supports
	 * recursively traversing a directory and visiting every code file in it. If
	 * a <b>path</b> to a file is given, <code>countFile(Path)</code> will be
	 * called directly.
	 * 
	 * @param path - path to the directory/file
	 * 
	 * @return An <code>ArrayList&lt;LoCResult&gt;</code> with the results of
	 *         all the evaluated files in no particular order.
	 */
	public static ArrayList<LoCResult> count(Path path) {

		ArrayList<LoCResult> results = new ArrayList<LoCResult>();

		if (Files.isRegularFile(path)) {
			results.add(countFile(path));
			return results;
		}

		PrintFiles visitor = new PrintFiles(KNOWN_FILE_EXTENSIONS);
		try {
			Files.walkFileTree(path, visitor);
		} catch (IOException e) {
			// e.printStackTrace();
		}

		ArrayList<Path> files = visitor.getFiles();

		for (Path p : files) {
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
