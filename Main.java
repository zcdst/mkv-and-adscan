import java.io.*;
import java.util.*;

public class Main {


	static HashSet<Recording> recs = new HashSet<Recording>();
	static final String COMSKIP = "\"C:\\Program Files (x86)\\Comskip\\comskip.exe\"";
	static final String MKVMERGE = "\"C:\\Program Files\\MKVToolNix\\mkvmerge.exe\"";
	static final String DIR = "P:\\";
	static boolean force = false;	// default behaviour is to do nothing if a recording is active; setting this to true ignores active recordings
	static boolean end = false;
	static boolean background = false;

	Set<String> ignore = new HashSet<String>(Arrays.asList("a", "b"));


	public static void main(String[] args) throws Exception {


		if (args.length > 0) {
		 	if (args[0].contains("force")) {
				force = true;
			} else if (args[0].contains("background")) {
				background = true;
			}
		}


		do {

			File[] root = new File(DIR).listFiles();

			walk(root);

			int count = 1;

			if (recs.size() > 0) {
				for (Recording x : recs) {
					System.out.println("------Re-muxing!-------");
					System.out.println("File " + count + " of " + (recs.size()));
					System.out.println("Re-muxing: " + x.recording);
					String tmp = x.recording + "_mux.mkv";

					ProcessBuilder cspb = new ProcessBuilder(MKVMERGE, "-o", tmp, x.recording); 	// run mkvmerge
					cspb.inheritIO();
					Process csp = cspb.start();
					csp.waitFor();

					System.out.println("------Muxing Complete!------");

					File o = new File(x.recording);
					File n = new File(tmp);

					if (n.length() > 0.5 * o.length()) {	// check if muxed correctly
						System.out.println("Output seems OK, deleting " + x.recording + " !");
					} else {
						System.out.println("Output too small, deleting muxed file!");
						n.delete();
						continue;
					}

					o.delete();	// delete original file, rename muxed mkv to original ts
					Thread.sleep(2000);
					n.renameTo(new File(x.recording));

					Thread.sleep(2000);

					ProcessBuilder mkpb = new ProcessBuilder(COMSKIP, x.recording);	// run comskip
					mkpb.inheritIO();
					Process mkp = mkpb.start();
					mkp.waitFor();

					System.out.println("------Scanning for ads!------");
					System.out.println("File " + count + " of " + (recs.size()));
					System.out.println("Scanning: " + x.recording);

					String m = x.recording.substring(0, x.recording.lastIndexOf('.'));	// rename vdr file
					String d = x.recording.substring(0, x.recording.lastIndexOf('\\') + 1);
					File comskipout = new File(m + ".vdr");
					File marks = new File(d + "marks");
					if (!marks.exists()) {	// don't replace marks file if exists
						comskipout.renameTo(marks);
					}

					count++;

				}
			} else if (end) {
				System.out.println("Recording in progress (or recently completed)... aborting!");
			} else {
				System.out.println("No recordings to process!");
				// System.exit(-1);
			}

			System.out.println("------------------");
		
			for (Recording x : recs) {
				System.out.println("Processed: " + x.recording);
			}

			if (background) {
				recs.clear();
				System.out.println("Re-starting in 10 minutes...");
				Thread.sleep(900000);
			}
		} while (background);

	}


	public static void walk(File[] root) {
		for (File f : root) {
			String fs = f.toString();
			if (f.isDirectory() && !fs.contains(".del")) {
				walk(f.listFiles());
			} else {
				String x = f.toString();
				if (x.endsWith(".ts") && checkTime(f)) {
					File tmp = new File(x.replace(".ts", ".log"));
					if (!tmp.exists()) {
						Recording r = new Recording(x);
						recs.add(r);
					}
				}
			}
		}
		if (end) {
			recs.clear();
			end = false;
		}
	}



	public static boolean checkTime(File f) {		// true if file has not been written to in last 5 min
		if (System.currentTimeMillis() - f.lastModified() > 600000) {
			return true;
		} else if (force) {	// ignore this recording, but continue adding others
			return false;
		} else {
			// System.exit(-1);
			end = true;
			return false;
		}
	}






}



/*
try {
	
	for (int i = 0; i < 3; i += 1) {
		Process p =	Runtime.getRuntime().exec("notepad.exe");
		p.waitFor();
	}
} catch (Exception e) {
	System.err.println("nope");
}
*/



/*	public static void printOutput(InputStream in, String s) throws Exception {

		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		String line = null;
		while ((line = r.readLine()) != null) {
			System.out.println(line);
			// System.out.println("Currently processing: " + s);
		}
	}*/