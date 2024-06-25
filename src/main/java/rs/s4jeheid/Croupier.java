package rs.s4jeheid;

import rs.s4jeheid.utils.KartenRang;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Croupier {


    private Deck deck;
    private List<Karte> hand;
    private DatagramSocket socket;
    private final int port;
    private Map<String, Integer> playerStatistics;
    private final int WIN_THRESHOLD = 5;

    public Croupier(int numDecks, int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
        this.deck = new Deck(numDecks);
        this.hand = new ArrayList<>();
        this.playerStatistics = new HashMap<>();
        deck.mischen();
    }

    private int calculateHandValue(List<Karte> hand) {
        int value = 0;
        int numAces = 0;
        for (Karte card : hand) {
            KartenRang rang = card.getRang();
            if (rang.getValue()>9) {
                value += 10;
            } else if (rang.getValue()==1) {
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

    private String decideAction() {
        int value = calculateHandValue(hand);
        if (value < 17 || (value == 17 && hand.stream().anyMatch(card -> card.getRang().getValue() == 1))) {//TODO check calculation
            return "Hit";
        } else {
            return "Stand";
        }
    }
    private String checkWinner(List<Karte> playerHand) {
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(this.hand);
        if (playerValue > 21) {
            return "Dealer";
        } else if (dealerValue > 21 || playerValue > dealerValue) {
            return "Player";
        } else if (playerValue < dealerValue) {
            return "Dealer";
        } else {
            return "Push";
        }
    }

    private void updateStatistics(String player, boolean playerWon) {
        playerStatistics.put(player, playerStatistics.getOrDefault(player, 0) + (playerWon ? 1 : 0));
    }

    private boolean shouldEjectPlayer(String player) {
        return playerStatistics.getOrDefault(player, 0) >= WIN_THRESHOLD;
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

        if (type.equals("deal_card")) {
            Karte card = this.deck.ziehen();// nächste Karte ziehen
            response = "card_dealt;" + card;
        } else if (type.equals("get_statistics")) {
            response = "statistics;" + playerStatistics.toString();
        } else if (type.equals("check_winner")) {
            // Convert the handString to a list of cards
            List<Karte> playerHand = new ArrayList<>();
            if (!handString.isEmpty()) {
                String[] cards = handString.split(",");
                for (String card : cards) {
                    playerHand.add(new Karte(card));//TODO prüfe ob auch wirklich nur eine neue Karte erstellt wird
                }
            }

            // Croupier draws cards for his hand until he stands
            while (decideAction().equals("Hit")) {
                hand.add(deck.ziehen());
            }

            String winner = checkWinner(playerHand);
            boolean playerWon = winner.equals("Player");
            updateStatistics(player, playerWon);
            response = "winner;" + winner;

            if (shouldEjectPlayer(player)) {
                response += ";eject";
            }
        }

        byte[] responseData = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, address, port);
        socket.send(responsePacket);
    }
}
