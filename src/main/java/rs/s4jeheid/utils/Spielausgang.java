package rs.s4jeheid.utils;

public enum Spielausgang {
    GEWONNEN, VERLOREN, UNENTSCHIEDEN;

    private int blackjacks;
    private int playerScore;
    private int croupierScore;
    private int einsatz;

    public int getBlackjacks() {
        return blackjacks;
    }

    public void setBlackjacks(int blackjacks) {
        this.blackjacks = blackjacks;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getCroupierScore() {
        return croupierScore;
    }

    public void setCroupierScore(int croupierScore) {
        this.croupierScore = croupierScore;
    }

    public int getEinsatz() {
        return einsatz;
    }

    public void setEinsatz(int einsatz) {
        this.einsatz = einsatz;
    }

}
