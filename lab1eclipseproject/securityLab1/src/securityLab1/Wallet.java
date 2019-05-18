package securityLab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Logger;

public class Wallet {

    //	private static Logger log = Logger.getLogger(Wallet.class.getSimpleName());

	/**
	 * The RandomAccessFile of the wallet file
	 */
	private RandomAccessFile file;

	/**
	 * Creates a Wallet object
	 * 
	 * A Wallet object interfaces with the wallet RandomAccessFile
	 */
	public Wallet() throws Exception {
		this.file = new RandomAccessFile(new File("wallet.txt"), "rw");
	}

	/**
	 * Gets the wallet balance.
	 * 
	 * @return The content of the wallet file as an integer
	 */
	public int getBalance() throws IOException {
		this.file.seek(0);
		return Integer.parseInt(this.file.readLine());
	}

	/**
	 * Sets a new balance in the wallet
	 * 
	 * @param newBalance
	 *            new balance to write in the wallet
	 */
	public void setBalance(int newBalance) throws Exception {
		this.file.setLength(0);
		String str = Integer.valueOf(newBalance).toString() + '\n';
		this.file.writeBytes(str);
	}

	// task 2: Fix the API
	/**
	 * Withdraws a set value from the wallet balance, if possible; otherwise
	 * throws an Exception
	 * 
	 * @param valueToWithdraw
	 *            self-explanatory
	 * @throws Exception
	 *             if balance is insufficient
	 */
    public synchronized void safeWithdraw(int valueToWithdraw) throws Exception {
	//		log.info("asking for lock; this operation is blocking");
	FileLock lock = file.getChannel().lock();
	//	log.info("lock acquired; program has now exclusive access to wallet.txt");
	Thread.sleep(3000);// testing purposes
	try {
	    // read balance from file
	    int balance = getBalance();
	    // verify the amount
	    if (balance < valueToWithdraw) {
		// exception if balance is low
		throw new Exception("Insufficient balance");
	    } else {
		// update balance otherwise
		setBalance(balance - valueToWithdraw);
	    }
	} finally {
	    lock.close();
	}
    }
    
	/**
	 * Closes the RandomAccessFile in this.file
	 */
	public void close() throws Exception {
		this.file.close();
	}
}
