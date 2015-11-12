public class Recording {


	public String recording;
	public boolean processed;
	public boolean locked = false;

	public Recording(String r, boolean p ) {

		this.recording = r;
		this.processed = p;

	}

}