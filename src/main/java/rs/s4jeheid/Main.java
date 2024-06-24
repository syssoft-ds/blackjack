package rs.s4jeheid;

import rs.s4jeheid.utils.Kartentyp;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Deck deck1 = new Deck();
        deck1.print();
        deck1.printSize();
        System.out.println("-------------------");
        deck1.mischen();
        deck1.print();
        deck1.printSize();
        System.out.println("-------------------");
        System.out.println(deck1.checkDeckByType(Kartentyp.HERZ).size());


    }
}