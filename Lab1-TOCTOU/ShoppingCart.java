import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShoppingCart {
	// constants
	private static String UI = "                   ### TDA602 Language-based security ###\n"
			+ "                  ### Lab 1: TOCTOU                    ###\n"
			+ "                 ### Shopping Cart                      ###\n";
	private static int LENGTH = 80;

	// aux methods
	private static void printLine(int length) {
		while (length-- > 0) {
			System.out.print('_');
		}
		System.out.println('\n');
	}

	// displays current balance and list of items
	private static void displayStore(int balance) {
		// print balance
		System.out.println(new StringBuilder().append("Current balance: ").append(balance).toString());
		printLine(LENGTH);
		// print items
		System.out.print(Store.asString());
		printLine(LENGTH);
	}

	// lets the user select an item to purchase and returns the name
	private static String chooseItem() throws IllegalArgumentException, IOException {
		// select an item
		System.out.print("Specify an item to purchase:\t");
		BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
		String itemToBuy = scanner.readLine().trim();
		printLine(LENGTH);
		// return name of selected item
		return itemToBuy;
	}

	// actual programme
	private static void printUI() throws IOException {
		System.out.print(UI);
		printLine(LENGTH);
	}

	// task 0: The scenario and base program
	private static void badImplementation(Wallet wallet, Pocket pocket)
			throws IOException, UnsupportedOperationException, Exception {
		int balance = wallet.getBalance();
		System.out.println("### BAD IMPLEMENTATION ###");
		displayStore(balance);
		String itemToBuy = chooseItem();
		int price = Store.getProductPrice(itemToBuy);
		int newBalance = balance - price;
		if (newBalance < 0) {
			throw new UnsupportedOperationException();
		}
		wallet.setBalance(newBalance);
		pocket.addProduct(itemToBuy + "\r\n");
	}

	// task 2: API fixed (using Wallet.safeWithdraw)
	private static void correctImplementation(Wallet wallet, Pocket pocket)
			throws IOException, UnsupportedOperationException, Exception {
		int balance = wallet.getBalance();
		System.out.println("### CORRECT IMPLEMENTATION ###");
		displayStore(balance);
		String itemToBuy = chooseItem();
		int price = Store.getProductPrice(itemToBuy);
		wallet.safeWithdraw(price);
		pocket.addProduct(itemToBuy + "\r\n");
	}

	// main method
	public static void main(String[] args) throws IOException {
		printUI();
		try {
			// init client attributes
			Pocket pocket = new Pocket();
			Wallet wallet = new Wallet();
			System.out.println("Choose an implementation:");
			System.out.println("\t- enter [1] for the bad implementation;");
			System.out.println("\t- enter anything else for the good implementation.");
			if ("1".equals(new BufferedReader(new InputStreamReader(System.in)).readLine().trim())) {
				// bad: read balance once
				badImplementation(wallet, pocket);
			} else {
				// good: keep track of balance
				correctImplementation(wallet, pocket);
			}
			// add item to client pocket
			System.out.println("Your new balance: " + wallet.getBalance());
			System.out.println("Thank you for your business!");
			wallet.close();
			pocket.close();
		} catch (IllegalArgumentException e) {
			System.out.println("The item specified is not available.");
		} catch (UnsupportedOperationException e) {
			System.out.println("Insufficient balance.");
		} catch (Exception e) {
			System.out.println("An error has occurred: " + e.getMessage());
		}
	}
}
