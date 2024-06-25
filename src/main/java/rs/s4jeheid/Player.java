package rs.s4jeheid;

import rs.s4jeheid.utils.KartenRang;
import rs.s4jeheid.utils.Statistik;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Player {

    ArrayList<Karte> hand = new ArrayList<>();
    int balance;
    int bet;
    String[] actions = {"HIT", "STAND", "DOUBLE DOWN", "SPLIT","SURRENDER"};
    private DatagramSocket socket;
    private final int port;
    Statistik playerStatistics;


    public Player(int balance, int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.balance = balance;
        this.port = port;
    }

    public void run() {
        byte[] buf = new byte[1024];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                String[] parts = received.split(";");
                String type = parts[0];
                String player = parts.length > 1 ? parts[1] : "";
                String handString = parts.length > 2 ? parts[2] : "";

                handleReceivedMessage(type, player, handString, packet.getAddress(), packet.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleReceivedMessage(String type, String player, String handString, InetAddress address, int port) throws IOException {
        String response = "";

        switch (type) {
            case "card_dealt" -> {
                this.hand.add(new Karte(handString));// Karte vom Croupier in die Hand
            }
            case "statistics" -> response = "statistics;" + playerStatistics.toString();
            case "check_winner" -> {
                // Convert the handString to a list of cards
                List<Karte> playerHand = new ArrayList<>();
                if (!handString.isEmpty()) {
                    String[] cards = handString.split(",");
                    for (String card : cards) {
                        playerHand.add(new Karte(card));//TODO prüfe ob auch wirklich nur eine neue Karte erstellt wird
                    }
                }

//                // Croupier draws cards for his hand until he stands
//                while (decideAction().equals("Hit")) {
//                    hand.add(deck.ziehen());
//                }
//
//                String winner = checkWinner(playerHand);
//                boolean playerWon = winner.equals("Player");
//                updateStatistics(player, playerWon);
//                response = "winner;" + winner;
//
//                if (shouldEjectPlayer(player)) {
//                    response += ";eject";
//                }
            }
        }

        byte[] responseData = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, address, port);
        socket.send(responsePacket);
    }

    private void updateStatistics(String player, boolean playerWon) {

    }

    public int getHandValue() {
        int value = 0;
        int numAces = 0;
        for (Karte card : hand) {
            KartenRang rang = card.getRang();
            if (rang.getValue() > 9) {
                value += 10;
            } else if (rang.getValue() == 1) {
                numAces++;
                value += 11;
            } else {
                value += rang.getValue();
            }
        }
        while (value > 21 && numAces > 0) {
            value -= 10;
            numAces--;
        }
        return value;
    }


    public void addCard(Karte card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public void setBet(int bet) {
        this.bet = bet;
    }



}
