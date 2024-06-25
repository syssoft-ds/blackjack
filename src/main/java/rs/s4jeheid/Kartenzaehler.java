package rs.s4jeheid;

import java.util.ArrayList;
import java.util.HashMap;

public class Kartenzaehler {
    //Speichern der Partien und der Statistiken sowie die berrechnung der Empfehlungen für den Spieler

    int gespieltePartien;
    int gewonnenePartien;
    int verlorenePartien;
    int unentschieden;
    ArrayList<Karte> gespielteKarten = new ArrayList<>();
    HashMap<Integer,ArrayList<Karte>> partieKartenSpieler = new HashMap<>();
    HashMap<Integer,ArrayList<Karte>> partieKartenCroupier = new HashMap<>();


}
