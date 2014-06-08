import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoCCounter {	
	
	/**
	 * Counts the lines of code in the file at the location specified by <b>path</b>.
	 * @param path - the path of the file to count lines of code in.
	 * @return The lines of code, comment and blank, wrapped in a <code>LoCResult</code> object.
	 */
	public static LoCResult count(String path) {
		
		BufferedReader is = null;
		int totalcount = 0, commcount = 0, blankcount = 0;
		boolean flag = false;

		try {		
			try {
				is = new BufferedReader(new FileReader(path));
				String l;

				while ((l = is.readLine()) != null) {
					
					totalcount++;

					if ((l.trim().equals("")) && !flag)
						blankcount++;
					
					if(flag)
						commcount++;			
					
					if (l.indexOf("//") != -1 && !flag)
						if (l.trim().split("//")[0].equals(""))
							commcount++;

					if (l.indexOf("/*") != -1 && !flag) {
						flag = true;
						if (l.trim().split("/*")[0].equals(""))
							commcount++;
					}

					if (l.indexOf("*/") != -1 && flag)
						flag = false;
				}
				
			return new LoCResult(totalcount, commcount, blankcount);	
						
			} finally {
				if (is != null)
					is.close();
			}
							
		} catch (IOException e) {
			//e.printStackTrace();		
		}
		
		return new LoCResult(true);	
	}	
	
	/**
	 * @param args		the file to count loc in
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out
				.println("Please give the path to a file to count loc in.\nUsage: java LinesOfCode <file>");
			return;
		}
		
		LoCResult res = count(args[0]);
		
		System.out.println(res.report());
		
	}	
}
