package securityLab1;

import java.util.HashMap;
import java.util.Map;

public class Store {
    /**
     * Store.products is a HashMap with the products and their prices.
     * You can access them as:
     *      Integer price = Store.getProductPrice("book");
     */
    private final static Map<String,Integer> products= new HashMap<String,Integer>();
		
    static {
        products.put("candies", 1);
        products.put("car", 30000);
        products.put("pen", 40);
        products.put("book", 100);
    }

    /**
     * Returns the product list as a String. 
     *
     * @return    product list
     */
    public static String asString(){
        String res = "";
        for (String p : products.keySet()) {
            res += p + "\t" + products.get(p) + "\n";
        }
        return res;
    }

    /**
     * Returns the product list as a String. 
     *
     * @param     product          product name to get the price of (e.g. "car")
     * @return    product list
     */
    public static Integer getProductPrice(String product) {
        if(products.containsKey(product)) {
            return products.get(product);
        }

        throw new IllegalArgumentException("Product " + product + " is not in store");
    }
}
