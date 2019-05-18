package boku.no.nopermission;

public class RunBrowser implements Runnable {
	private String data;
	private NoPermissionsActivity parent;

	RunBrowser(NoPermissionsActivity parent, String data) {
		super();
		this.data = data;
		this.parent = parent;
	}
	
	public void run() {
		parent.sendByBrowser(data);
	}

}
