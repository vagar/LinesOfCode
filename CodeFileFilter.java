import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

import javax.json.JsonObject;

public class CodeFileFilter implements Filter<Path> {
	
	private JsonObject knownExtensions;
	
	public CodeFileFilter(JsonObject knownExtensions) {
		this.knownExtensions = knownExtensions;
	}

	@Override
	public boolean accept(Path entry) throws IOException {
		
		for (String s : knownExtensions.keySet()) {
			if(entry.getFileName().toString().endsWith("."+s))
				return true;
		}	
		return false;
	}

}
