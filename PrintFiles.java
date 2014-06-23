import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import javax.json.JsonObject;

public class PrintFiles extends SimpleFileVisitor<Path> {

	private ArrayList<Path> files = new ArrayList<Path>();
	private JsonObject knownExtensions;

	public PrintFiles(JsonObject knownExtensions) {
		super();
		this.knownExtensions = knownExtensions;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {

		DirectoryStream.Filter<Path> filter = new CodeFileFilter(
				knownExtensions);

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
				filter)) {
			for (Path path : stream) {
				files.add(path.toAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return FileVisitResult.CONTINUE;
	}

	public ArrayList<Path> getFiles() {
		return files;
	}

}