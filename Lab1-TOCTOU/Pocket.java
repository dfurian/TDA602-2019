import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class Pocket {
	/**
	 * The RandomAccessFile of the pocket file
	 */
	private RandomAccessFile file;

	/**
	 * Creates a Pocket object
	 * 
	 * A Pocket object interfaces with the pocket RandomAccessFile.
	 */
	public Pocket() throws Exception {
		this.file = new RandomAccessFile(new File("pocket.txt"), "rw");
	}

	/**
	 * Adds a product to the pocket. This method was upgraded to be mutual
	 * exclusive, we didn't make a new safeAddProduct since it still won't be worth
	 * anything if the ShoppingCart uses the unsafe withdrawal method...
	 *
	 * @param product product name to add to the pocket (e.g. "car")
	 */
	public void addProduct(String product) throws Exception {
		FileLock fileLock = file.getChannel().lock();
		// now that we are inside the pocket, we can add the product
		this.file.seek(this.file.length());
		this.file.writeBytes('\n' + product);
		// we're done writing: we can release the lock
		fileLock.channel().close();
	}

	/**
	 * Closes the RandomAccessFile in this.file
	 */
	public void close() throws Exception {
		this.file.close();
	}
}
