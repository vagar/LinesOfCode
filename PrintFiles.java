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

		DirectoryStream.Filter<Path> filter = new CodeFileFilter(knownExtensions);
		
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

	// Print information about
	// each type of file.
	/*@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
		
		files.add(file.toAbsolutePath());		
		return FileVisitResult.CONTINUE;
	}*/

	// If there is some error accessing
	// the file, let the user know.
	// If you don't override this method
	// and an error occurs, an IOException
	// is thrown.
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return FileVisitResult.CONTINUE;
	}
	
	public ArrayList<Path> getFiles(){
		return files;
	}
	
}