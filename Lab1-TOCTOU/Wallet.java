import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Wallet {
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
	 * @param newBalance new balance to write in the wallet
	 */
	public void setBalance(int newBalance) throws Exception {
		this.file.setLength(0);
		String str = Integer.valueOf(newBalance).toString() + '\n';
		this.file.writeBytes(str);
	}

	// task 2: Fix the API
	/**
	 * Withdraws a set value from the wallet balance, if possible; otherwise throws
	 * an Exception
	 * 
	 * @param valueToWithdraw self-explanatory
	 * @throws Exception if balance is insufficient
	 */
	public synchronized void safeWithdraw(int valueToWithdraw) throws Exception {
		BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
		FileChannel fileChannel = file.getChannel();
		System.out.println("asking for lock for file " + file.toString());
		FileLock lock = fileChannel.tryLock();
		while (true) {
			if (lock == null) {
				System.out.println("the file is not available");
				System.out.println("press [A] to acquire lock again");
				System.out.println("press [R] to release lock");
				lock = fileChannel.tryLock();
			} else if (lock.isValid()) {
				System.out.println("the file is available for read/write operations");
				System.out.println("press [R] to release lock");
			} else {
				System.out.println("the lock is not valid");
				System.out.println("press [A] to acquire lock again");
				lock.close();
				lock = fileChannel.tryLock();
			}
			System.out.println("press [X] to exit");
			String input = scanner.readLine().trim().toUpperCase();
			if ("A".equals(input)) {
				if( lock != null ) {
					lock.close();
				}
				lock = fileChannel.tryLock();
			} else if ("R".equals(input)) {
				if( lock != null ) {
					lock.close();
				}
			} else if ("X".equals(input)) {
				return;
			} else {
				System.out.println("Unknown command " + input);
			}
		}

//		FileChannel fileChannel = file.getChannel();
//		System.out.println("asking for lock");
//		FileLock lock = fileChannel.tryLock();
//		while (true) {
//			if (lock == null || !lock.isValid()) {
//				System.out.println("Another instance is already running");
//				lock = fileChannel.tryLock();
//			} else {
//				break;
//			}
//		}
//		// read balance from file
//		System.out.println("acquired lock");
//		int balance = getBalance();
//		Thread.sleep(10000);
//		// verify the amount
//		if (balance < valueToWithdraw) {
//			// exception if balance is low
//			throw new Exception("Insufficient balance");
//		} else {
//			int newBalance = balance - valueToWithdraw;
//			// update balance otherwise
//			try {
//				setBalance(newBalance);
//			} finally {
//				if (lock != null) {
//					lock.release();
//				}
//			}
//		}
	}

	/**
	 * Closes the RandomAccessFile in this.file
	 */
	public void close() throws Exception {
		this.file.close();
	}
}
