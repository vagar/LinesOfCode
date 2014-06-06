import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LinesOfCode {

	/**
	 * @param args			the file to count loc in
	 * @throws IOException	thrown if io fails somehow
	 */
	public static void main(String[] args) throws IOException {

		BufferedReader is = null;

		int count = 0;
		boolean flag = false;

		if(args.length != 1){
			System.out.println("Please give the path to a file to count loc in.\nUsage: java LinesOfCode D:\\workspace\\project\\file.java");
			return;
		}

		try {
			is = new BufferedReader(new FileReader(args[0]));
			String l;

			while ((l = is.readLine()) != null) {

				if ((!l.trim().equals("")) && !flag)
					count++;

				if (l.indexOf("//") != -1)
					if (l.trim().split("//")[0].equals(""))
						count--;

				if (l.indexOf("/*") != -1) {
					flag = true;
					if (l.trim().split("/*")[0].equals(""))
						count--;
				}

				if (l.indexOf("*/") != -1)
					flag = false;
			}
		} finally {
			if (is != null)
				is.close();
		}
		System.out.println("Lines of Code: " + count);
	}
}