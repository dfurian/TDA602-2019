package the.least.permissions;

public class RunBrowser implements Runnable {
	private String data;
	private MainActivity parent;

	RunBrowser(MainActivity parent, String data) {
		super();
		this.data = data;
		this.parent = parent;
	}
	
	public void run() {
		parent.prepareForUpload(data);
	}

}
