README - Lab1 TOCTOU

HOW TO REPLICATE THE PROBLEM

1. Compile program using javac command from Terminal or similar:
        >javac ShoppingCart.java
2. Launch another Terminal in the same directory and start the ShoppingCart program on both windows:
        >java ShoppingCart

                           ### TDA602 Language-based security ###
                          ### Lab 1: TOCTOU                    ###
                         ### Shopping Cart                      ###
        ________________________________________________________________________________
        Choose an implementation:
            - enter [1] for the bad implementation;
            - enter anything else for the good implementation.
3. Type "1" and enter to boot the "bad implementation":
        1
        ### BAD IMPLEMENTATION ###
        Current balance: 30000
        ________________________________________________________________________________

        car     30000
        book    100
        pen     40
        candies 1
        ________________________________________________________________________________

        Specify an item to purchase:
3. On one window, select an item and complete the purchase:
        Specify an item to purchase:    book
        ________________________________________________________________________________

        Your new balance: 29900
        Thank you for your business!
4. On the other window, select a car and complete the purchase. The transaction will go through even though we have less than 30k coins.
        Specify an item to purchase:    car
        ________________________________________________________________________________

        Your new balance: 0
        Thank you for your business!

HOW TO FIX THE PROBLEM

1. Start the program as usual, but enter a value different from "1" to choose the "good implementation":
        Choose an implementation
                - enter [1] for the bad implementation;
                - enter anything else for the good implementation.
        i'll have a macchiato
        ### CORRECT IMPLEMENTATION ###
        Current balance: 30000
        ________________________________________________________________________________

        car	30000
        book	100
        pen	40
        candies	1
        ________________________________________________________________________________

        Specify an item to purchase:
2. Repeat steps 3 and 4 from the "bad implementation". The first transaction will succeed but the second will fail with the following output:
        Specify an item to purchase:    car
        ________________________________________________________________________________

        An error has occurred: Insufficient balance

