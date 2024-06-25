package rs.s4jeheid.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Statistik implements Serializable {
    //Speichern der Partien und der Statistiken sowie die berrechnung der Empfehlungen für den Spieler

    ArrayList<Spielausgang> spielverlauf = new ArrayList<>();

    int gespieltePartien;
    int gewonnenePartien;
    int verlorenePartien;
    int unentschieden;

    public Statistik() {
        this.gespieltePartien = spielverlauf.size();
        this.gewonnenePartien = 0;
        this.verlorenePartien = 0;
        this.unentschieden = 0;
    }

    public void evaluatePartie(Spielausgang ausgang) {
        spielverlauf.add(ausgang);
        gespieltePartien++;
        switch (ausgang) {
            case GEWONNEN:
                addGewonnen();
                break;
            case VERLOREN:
                addVerloren();
                break;
            case UNENTSCHIEDEN:
                addUnentschieden();
                break;
        }
    }

    public void addGewonnen() {
        this.gewonnenePartien++;
    }

    public void addVerloren() {
        this.verlorenePartien++;
    }

    public void addUnentschieden() {
        this.unentschieden++;
    }

    public int getGespieltePartien() {
        return gespieltePartien;
    }

    public int getGewonnenePartien() {
        return gewonnenePartien;
    }

    public int getVerlorenePartien() {
        return verlorenePartien;
    }

    public int getUnentschieden() {
        return unentschieden;
    }

    public double getGewinnrate() {
        return (double) gewonnenePartien / gespieltePartien;
    }

    public double getVerlustrate() {
        return (double) verlorenePartien / gespieltePartien;
    }

    public double getUnentschiedenrate() {
        return (double) unentschieden / gespieltePartien;
    }

    public String toString() {
        return "Statistik{" +
                "gespieltePartien=" + gespieltePartien +
                ", gewonnenePartien=" + gewonnenePartien +
                ", verlorenePartien=" + verlorenePartien +
                ", unentschieden=" + unentschieden +
                '}';
    }
}
