package rs.s4jeheid;

import rs.s4jeheid.utils.KartenRang;
import rs.s4jeheid.utils.Kartentyp;

import java.io.Serializable;

public class Karte implements Serializable {//Serializable damit übertragung in ein Karten Objekt übertragen werden kann
    Kartentyp type;
    KartenRang rang;

    public Karte(Kartentyp type, KartenRang rang) {
        this.type = type;
        this.rang = rang;
    } public Karte(String card) {
        readAsCard(card);
    }

    public Kartentyp getType() {
        return type;
    }

    public KartenRang getRang() {
        return rang;
    }

    public String toString() {
        return rang + " of " + type;
    }

    public Karte readAsCard(String card){
        String[] cardArray = card.split(" ");
        Kartentyp type = Kartentyp.valueOf(cardArray[2]);
        KartenRang rang = KartenRang.valueOf(cardArray[0]);
        return new Karte(type, rang);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Karte) {
            Karte karte = (Karte) obj;
            return karte.getType() == type && karte.getRang() == rang;
        }
        return false;
    }
}
