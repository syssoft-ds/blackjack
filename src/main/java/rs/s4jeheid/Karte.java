package rs.s4jeheid;

import rs.s4jeheid.utils.KartenRang;
import rs.s4jeheid.utils.Kartentyp;

public class Karte {
    Kartentyp type;
    KartenRang rang;

    public Karte(Kartentyp type, KartenRang rang) {
        this.type = type;
        this.rang = rang;
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

    public boolean equals(Object obj) {
        if (obj instanceof Karte) {
            Karte karte = (Karte) obj;
            return karte.getType() == type && karte.getRang() == rang;
        }
        return false;
    }
}
