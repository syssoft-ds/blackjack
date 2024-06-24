package rs.s4jeheid;

import rs.s4jeheid.utils.KartenRang;
import rs.s4jeheid.utils.Kartentyp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deck {
    ArrayList<Karte> karten;

    public Deck() {
        karten = new ArrayList<>();
        for (Kartentyp type : Kartentyp.values()) {
            for (KartenRang rang : KartenRang.values()) {
                karten.add(new Karte(type, rang));
            }
        }
    }

    public void mischen() {
        List<Karte> kartenList = getKarten();
        // mischen mit Fisher-Yates Algorithmus (https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle)
        // also einfach die Kartenliste durchgehen und für jede Karte eine zufällige Position in der Liste wählen
        // und die Karten tauschen
        for (int i = 0; i < kartenList.size()-1; i++) {
            int j = i + (int) (Math.random() * (kartenList.size() - i));
            Karte temp = kartenList.get(i);
            kartenList.set(i, kartenList.get(j));
            kartenList.set(j, temp);

        }

        karten = new ArrayList<>(kartenList);

    }

    public Karte ziehen() {
        return karten.removeFirst();
    }

    public int size() {
        return karten.size();
    }

    public void addSpecific(Karte karte) {
        karten.add(karte);
    }

    public void addMany(List<Karte> karten) {
        this.karten.addAll(karten);
    }

    public void addDeck(Deck deck) {
        karten.addAll(deck.karten);
    }

    public void clear() {
        karten.clear();
    }

    public boolean isEmpty() {
        return karten.isEmpty();
    }

    public void removeSpecific(Karte karte) {
        karten.remove(karte);
    }

    public void removeMany(List<Karte> karten) {
        this.karten.removeAll(karten);
    }

    public void removeDeck(Deck deck) {
        karten.removeAll(deck.karten);
    }

    public List<Karte> getKarten() {
        return karten;
    }

    public void setKarten(ArrayList<Karte> karten) {
        this.karten = karten;
    }

    public void print() {
        for (Karte karte : karten) {
//            System.out.println(karte.type + " " + karte.rang);
            System.out.println(karte.toString());

        }
    }

    public void printSize() {
        System.out.println(karten.size());
    }

    public ArrayList<Karte> checkDeckByType(Kartentyp type) {
        // check if deck contains all the cards of a specific type
        ArrayList<Karte> gefundenKarten = new ArrayList<>();
        // in einem Deck können maximal 4 Karten eines Typs sein,
        // also 13 Karten pro Typ
        //13 * 4 = 52 Karten im Deck
        for (Karte karte : karten) {
            if (karte.getType() == type) {
                gefundenKarten.add(karte);
            }
        }
        return gefundenKarten;
    }

}
