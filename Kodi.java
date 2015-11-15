import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Kodi {

	static String hostname = "192.168.1.61";
	static int port = 9090;

	static String matcher = ".+\"label\":\"\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d-\\d\\d:\\d\\d.+";


	public static void main(String[] args) throws Exception {

		System.out.println(isPlaying());
		System.out.println(waitTime());

	}


	public static boolean isPlaying() throws Exception {
		JSONObject jsobject = new JSONObject();
		JSONObject p = new JSONObject();

		p.put("playerid", 1);
		jsobject.put("jsonrpc", "2.0");
		jsobject.put("id", 1);
		jsobject.put("method", "Player.GetItem");
		jsobject.put("params", p);

		Socket client = new Socket(hostname, port);

		Writer w = new OutputStreamWriter(client.getOutputStream());
		// Scanner sc = new Scanner(client.getInputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		jsobject.write(w);
		w.flush();

		char x;
		StringBuilder str = new StringBuilder();
		while ((x = (char) in.read()) != '}') {
			str.append(x);
		}

		client.close();

		return str.toString().matches(matcher);

	}


	public static long waitTime() throws Exception {

		JSONObject jsobject = new JSONObject();
		JSONObject p = new JSONObject();

		p.put("timerid", getTimerId());
		jsobject.put("jsonrpc", "2.0");
		jsobject.put("id", 1);
		jsobject.put("method", "PVR.GetTimerDetails");
		jsobject.put("params", p);

		Socket client = new Socket(hostname, port);

		Writer w = new OutputStreamWriter(client.getOutputStream());
		// Scanner sc = new Scanner(client.getInputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		jsobject.write(w);
		w.flush();

		char x;
		StringBuilder str = new StringBuilder();
		while ((x = (char) in.read()) != '}') {
			str.append(x);
		}

		client.close();

		String strang = str.toString();
		strang = strang.substring(strang.indexOf('~') + 1, strang.lastIndexOf('-'));

		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
		Date d = df.parse(strang);
		long epoch = d.getTime();
		return epoch - System.currentTimeMillis() + 3300000;

	}


	public static int getTimerId() throws Exception {

		JSONObject js = new JSONObject();
		JSONObject p = new JSONObject();

		js.put("jsonrpc", "2.0");
		js.put("id", 1);
		js.put("method", "PVR.GetTimers");

		Socket client = new Socket(hostname, port);

		Writer w = new OutputStreamWriter(client.getOutputStream());
		// Scanner sc = new Scanner(client.getInputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		js.write(w);
		w.flush();

		char x;
		StringBuilder str = new StringBuilder();
		while ((x = (char) in.read()) != ']') {
			str.append(x);
		}

		client.close();
		String strang = str.toString();
		int y = strang.indexOf("\"timerid\":") + 10;
		strang = strang.substring(y, y + 2);
		return Integer.parseInt(strang);

	}


}