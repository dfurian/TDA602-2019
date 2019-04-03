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
    while (length -- > 0) {
      System.out.print('_');
    }
    System.out.println('\n');
  }
  
  // actual programme
  private static void printUI() throws IOException {
    System.out.print(UI);
    printLine(LENGTH);
  }

  // task 0: The scenario and base program
  private static void badImplementation(Wallet wallet, Pocket pocket) throws IOException, 
                                                                             UnsupportedOperationException, 
                                                                             Exception {
    int balance = new Wallet().getBalance();
    // print balance
    System.out.println(new StringBuilder().append("Current balance: ").append(balance).toString());
    printLine(LENGTH);
    // print items
    System.out.print(Store.asString());
    printLine(LENGTH);
    // select an item
    System.out.print("Specify an item to purchase:\t");
    BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
    String itemToBuy = scanner.readLine().trim();
    printLine(LENGTH);
    // subtract item price from client wallet
    int price = Store.getProductPrice(itemToBuy);
    int newBalance = balance - price;
    if(newBalance < 0) {
      throw new UnsupportedOperationException();
    }
    wallet.setBalance(newBalance);
    System.out.println("Your new balance: " + newBalance);
    wallet.close();
    // add item to client pocket
    pocket.addProduct(itemToBuy + "\r\n");
    System.out.println("Thank you for your business!");
    pocket.close();
  }
  
  // main method
  public static void main(String[] args) throws IOException {
    printUI();
    try {
      // init client attributes
      Pocket pocket = new Pocket();
      Wallet wallet = new Wallet();
      badImplementation(wallet, pocket);
    } catch (IllegalArgumentException e) {
      System.out.println("The item specified is not available.");
    } catch (UnsupportedOperationException e) {
      System.out.println("Insufficient balance.");
    } catch (Exception e) {
      System.out.println("An error has occurred: " + e.getMessage());
    }
  }
}
