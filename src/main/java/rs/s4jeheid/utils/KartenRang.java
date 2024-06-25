package rs.s4jeheid.utils;

public enum KartenRang {
    //2-10
//    "Ass", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Bube", "Dame", "König"

    ASS(1), ZWEI(2), DREI(3), VIER(4), FUENF(5),
    SECHS(6), SIEBEN(7), ACHT(8), NEUN(9), ZEHN(10),
    BUBE(10), DAME(10), KOENIG(10);

    private final int value;

    KartenRang(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
