package rs.s4jeheid;

import rs.s4jeheid.utils.Kartentyp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deck {
    ArrayList<Karte> karten;

    public Deck() {
        karten = new ArrayList<>();
        List<Kartentyp> kartentypList = List.of(Kartentyp.values());
        List<String> werte = Arrays.asList("Ass", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Bube", "Dame", "König");

        for(Kartentyp type: kartentypList){

        }


    }
}
