import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Kartenzaehler {
    private class Statistic {
        int win = 0;
        int loss = 0;
        int blackjack = 0;
    }

    private record Endpoint(String ip, int port) {}
    private record Message(Endpoint sender, Endpoint reciever, String request, String data) {}

    private int deckCount;
    private int runningCardCount;
    private double trueCardCount;
    private static Endpoint host;

    Map<String, Statistic> playerStatistic;

    public Kartenzaehler(Endpoint dealer, int hostport) {
        playerStatistic = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("1.1.1.1"), 12345);
            String hostIp = socket.getLocalAddress().getHostAddress();
            socket.disconnect();

            host = new Endpoint(hostIp, hostport);

            socket.connect(InetAddress.getByName(dealer.ip), dealer.port);
            Message message = new Message(host, dealer, "numberOfDecks", "");

            send(message, socket, InetAddress.getByName(dealer.ip), dealer.port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.deckCount = 0;
        this.runningCardCount = 0;
        this.trueCardCount = 0;

        //Start listening for requests
        System.out.println("Counting cards and give rec and stats requests on "+host.ip+":"+host.port);
        listen(12345);
    }

    public void updateCount(String card){
        switch (card) {
            case "2": case "3": case "4": case "5": case "6":
                runningCardCount += 1;
                break;
            case "10": case "J": case "Q": case "K": case "A":
                runningCardCount -= 1;
                break;
        }

        if (deckCount > 0) {
            trueCardCount = (double) runningCardCount / deckCount;
        }
    }

    public String recommendAction(int playerhand) {
        if (trueCardCount > 2) {
            if (playerhand >= 16) {
                return "stand";
            } else {
                return "hit";
            }
        } else if (playerhand >= 17) {
            return "stand";
        } else {
            return "hit";
        }
    }

    public void updateStats(String player, String result){
        if (!playerStatistic.containsKey(player)){
            playerStatistic.put(player, new Statistic());
        }
        Statistic stat = playerStatistic.get(player);
        switch (result) {
            case "win":
                stat.win += 1;
                break;
            case "loss":
                stat.loss += 1;
                break;
            case "blackjack":
                stat.blackjack += 1;
                break;
        }
    }

    public String provideStats(String player){
        Statistic stat = playerStatistic.get(player);
        return (stat == null) ? null :
                ("Statistics: Wins: " +  stat.win +
                        "\nLosses: " + stat.loss +
                        "\nBlackjacks: " + stat.blackjack);
    }

    public void listen(int port){
        Message response = null;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024];
            while (true){
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Message request = fromJson(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                System.out.println("Received: " + request);

                //Handle requests
                switch (request.request) {
                    case "numberOfDecks":
                        deckCount = Integer.parseInt(request.data);
                        response = new Message(host, request.sender, "get_decks", "success");
                        break;
                    case "update_game":
                        updateCount(request.data);
                        response = new Message(host, request.sender, "update_game", "success");
                        break;
                    case "recommend_action":
                        response = new Message(host, request.sender, "recommend_action", recommendAction(Integer.parseInt(request.data)));
                        break;
                    case "update_stats":
                        String[] data = request.data.split(":");
                        updateStats(data[0], data[1]);
                        response = new Message(host, request.sender, "update_stats", "success");
                        break;
                    case "provide_stats":
                        response = new Message(host, request.sender, "provide_stats", provideStats(request.data));
                        break;
                    default:
                        response = new Message(host, request.sender, request.request, "Invalid request");
                        break;
                }
                send(response, socket ,packet.getAddress(), packet.getPort());
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            response = new Message(host, host, "error", "Something went wrong while processing the request");
        }
    }

    public void send(Message message, DatagramSocket socket, InetAddress address, int port){
        try {
            byte[] buffer = toJson(message).getBytes(StandardCharsets.UTF_8);
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJson(Message message){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Message fromJson(String json){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        //Manage requests from players and to dealer
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Specify the dealer's IP address and port number (<ip>:<port>):");
            String dealerProcess = reader.readLine();
            String[] dealer = dealerProcess.split(":");
            System.out.println("Specify the port number for the card counter:");
            String port = reader.readLine();
            if (port.isBlank()) port = "54321";
            new Kartenzaehler(new Endpoint(dealer[0], Integer.parseInt(dealer[1])), Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
