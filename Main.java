import java.io.*;
import java.util.*;

public class Main{


	static HashSet<Recording> recs = new HashSet<Recording>();
	static String comskip = "\"C:\\Program Files (x86)\\Comskip\\comskip.exe\"";
	static String mkvmerge = "\"C:\\Program Files\\MKVToolNix\\mkvmerge.exe\"";
	// static String mkvmerge = "\"C:\\Program Files\\MKVToolNix\\mmg.exe\"";


	public static void main(String[] args) throws Exception {

		// File[] root = new File("P:\\").listFiles();
		File[] root = new File("E:\\comskiptest\\testcase\\").listFiles();

		walk(root);


		for (Recording x : recs) {
			if (!x.processed) {	// remux the file
				System.out.println("Re-muxing: " + x.recording);
				String tmp = x.recording + "_mux.mkv";
				ProcessBuilder p = new ProcessBuilder(mkvmerge, "-o", tmp, x.recording); 
				Process pr = p.start();

				printOutput(pr.getInputStream(), x.recording);

				System.out.println("---Muxing Complete!---");

				File o = new File(x.recording);
				File n = new File(tmp);

				if (n.length() > 0.75 * o.length()) {
					System.out.println("Output seems OK, deleting " + x.recording + " !");
				} else {
					n.delete();
					continue;
				}

				o.delete();

				Thread.sleep(2000);

				n.renameTo(new File(x.recording));


				Thread.sleep(2000);

				p = new ProcessBuilder(comskip, x.recording);
				pr = p.start();

				printOutput(pr.getInputStream(), x.recording);

				String m = x.recording.substring(0, x.recording.lastIndexOf('.'));
				String d = x.recording.substring(0, x.recording.lastIndexOf('\\') + 1);
				File comskipout = new File(m + ".vdr");
				File marks = new File(d + "marks");


				if (!marks.exists()) {
					comskipout.renameTo(marks);
				}




				System.out.println("below--");
				System.out.println(d);

			}

		}

		System.out.println(recs.size());

	}


	public static void walk(File[] root) {
		for (File f : root) {
			if (f.isDirectory()) {
				walk(f.listFiles());
			} else {
				String x = f.toString();
				if (x.endsWith(".ts") && checkTime(f)) {
					File tmp = new File(x.replace(".ts", ".log"));
					Recording r = new Recording(x, tmp.exists());
					recs.add(r);
				}
			}
		}
	}



	public static boolean checkTime(File f) {		// true if file has not been written to in last 5 min
		if (System.currentTimeMillis() - f.lastModified() > 300000) {
			return true;
		} else {
			return false;
		}
	}


	public static void printOutput(InputStream in, String s) throws Exception {

		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		String line = null;
		while ((line = r.readLine()) != null) {
			System.out.println(line);
			// System.out.println("Currently processing: " + s);
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


// scann all directories