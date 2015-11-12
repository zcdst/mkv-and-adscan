import java.io.*;
import java.util.*;

public class Main{


	static HashSet<String> recs = new HashSet<String>();
	static String comskippath = "\"C:\\Program Files (x86)\\Comskip\\comskip.exe\"";


	public static void main(String[] args) {

		File[] root = new File("P:\\").listFiles();

		walk(root);


		for (String x : recs) {
			System.out.println(x);
			String z = comskippath + " " + x + ".ts";
			System.out.println(z);
			try {
				Process p = Runtime.getRuntime().exec(z);
				p.waitFor();
			} catch (Exception e) {
				System.err.println("process creation failed");
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
				if (x.endsWith(".ts") || x.endsWith(".log")) {
					String y = x.substring(0, x.lastIndexOf('.'));
					if (recs.contains(y)) {
						recs.remove(y);
					} else {
						recs.add(y);
					}
				}
			}
		}
	}



}

