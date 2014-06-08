public class LoCResult {

	private final int totalLines, commentLines, blankLines;
	private boolean err = false;

	public LoCResult(boolean err) {
		this(0, 0, 0);
		this.err = err;
	}

	public LoCResult(int totalLines, int commentLines, int blankLines) {
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

	public boolean error() {
		return err;
	}

	public String report() {
		return err?"Error flag set, no count was done.":"Lines of Code: " + getCodeLines() + " (" + getCommentLines()
				+ " comment, " + getBlankLines() + " blank)";
	}

}
