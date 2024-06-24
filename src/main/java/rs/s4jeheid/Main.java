package rs.s4jeheid;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Deck deck1 = new Deck();
        deck1.print();
        deck1.printSize();
        System.out.println("-------------------");
        deck1.mischen();
        deck1.print();
    }
}