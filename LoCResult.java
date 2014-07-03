import java.nio.file.Path;

import javax.json.Json;
import javax.json.JsonObject;

public class LoCResult {

	private final Path path;
	private final String lang;
	private final int totalLines, commentLines, blankLines;
	private boolean err = false;

	public LoCResult(boolean err) {
		this(null, "", 0, 0, 0);
		this.err = err;
	}

	public LoCResult(Path path, String lang, int totalLines, int commentLines,
			int blankLines) {
		this.path = path;
		this.lang = lang;
		this.totalLines = totalLines;
		this.commentLines = commentLines;
		this.blankLines = blankLines;
	}

	public int getTotalLines() {
		return totalLines;
	}

	public int getCommentLines() {
		return commentLines;
	}

	public int getBlankLines() {
		return blankLines;
	}

	public int getCodeLines() {
		return totalLines - commentLines - blankLines;
	}

	public Path getPath() {
		return path;
	}

	public String getLanguage() {
		return lang;
	}

	public boolean error() {
		return err;
	}
	
	public JsonObject toJsonObject(){
		return Json.createObjectBuilder()
		.add("path",path.toAbsolutePath().toString())
		.add("lang",lang)
		.add("totalLines",totalLines)
		.add("commentLines",commentLines)
		.add("blankLines", blankLines).build();
	}

	public String report() {
		return err ? "Error flag set, no count was done." : path + "(" + lang
				+ "): " + getCodeLines() + "lines of code ("
				+ getCommentLines() + " comment, " + getBlankLines()
				+ " blank)";
	}

}
