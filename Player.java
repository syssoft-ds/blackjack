import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private final List<String> hand = new ArrayList<>();
    private static int kontostand = 1000;
    private static int port;
    private static String name;
    private static final Map<String, ClientInfo> clientListe = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 2) {
            showFehler("Argumente: \"<Portnummer> <Clientname>\"");
        }
        if (!isGueltigerPort(args[0])) {
            showFehler("Ungültige Portnummer");
        } else {
            port = Integer.parseInt(args[0]);
        }
        name = args[1];

        System.out.println(name + " (Port: " + port + ") ist bereit. Nutze \"register <IP-Adresse> <Portnummer>\" um einen anderen Client zu registrieren.\nNutze \"send <registrierter Clientname> <Nachricht>\" um eine Nachricht zu senden.\nNutze \"quit\" um das Programm zu beenden.");
        new Thread(() -> empfangeNachrichten(port)).start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while (!(input = br.readLine()).equalsIgnoreCase("quit")) {
                verarbeiteEingabe(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void verarbeiteEingabe(String input) {
        String[] parts = input.split(" ");
        if (parts[0].equalsIgnoreCase("register") && parts.length == 3 && isGueltigerPort(parts[2])) {
            registriereClient(parts[1], Integer.parseInt(parts[2]));
        } else if (parts[0].equalsIgnoreCase("send")) {
            sendeNachricht(parts);
        } else {
            System.err.println("Unbekannter Befehl.");
        }
    }

    private static void registriereClient(String ip, int port) {
        try {
            String lokaleIP = InetAddress.getLocalHost().getHostAddress();
            String nachricht = String.format("Hallo, hier ist %s, meine IPv4-Adresse ist %s, meine Portnummer ist %d.", name, lokaleIP, Player.port);
            sendePacket(ip, port, nachricht);
        } catch (UnknownHostException e) {
            System.err.println("Client \"" + ip + "\" nicht gefunden.");
        }
    }

    private static void sendeNachricht(String[] parts) {
        String empfaenger = parts[1];
        ClientInfo empfaengerInfo = clientListe.get(empfaenger);
        if (empfaengerInfo != null) {
            String nachricht = String.join(" ", parts);
            sendePacket(empfaengerInfo.ip, empfaengerInfo.port, nachricht);
            System.out.println("Gesendet: \"" + nachricht + "\" an " + empfaenger + ".");
        } else {
            System.err.println("Unbekannter Client \"" + empfaenger + "\".");
        }
    }

    private static void sendePacket(String ip, int port, String nachricht) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress adresse = InetAddress.getByName(ip);
            byte[] buffer = nachricht.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adresse, port);
            socket.send(packet);
            System.out.println("Nachricht gesendet.");
        } catch (IOException e) {
            System.err.println("Nachricht an \"" + ip + "\" konnte nicht gesendet werden.");
        }
    }

    private static void empfangeNachrichten(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[4096];
            String nachricht;
            do {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                nachricht = new String(buffer, 0, packet.getLength(), StandardCharsets.UTF_8);
                verarbeiteNachricht(nachricht);
            } while (!nachricht.equalsIgnoreCase("quit"));
        } catch (IOException e) {
            System.err.println("Nachricht konnte am Port \"" + port + "\" nicht empfangen werden.");
        }
    }

    private static void verarbeiteNachricht(String nachricht) {
        if (nachricht.startsWith("Hallo, hier ist ")) {
            String[] parts = nachricht.split(", ");
            String clientName = parts[1].split(" ")[3];
            String ip = parts[2].split(" ")[5];
            String portString = parts[3].split(" ")[4];
            if (isGueltigeIP(ip) && isGueltigerPort(portString)) {
                int clientPort = Integer.parseInt(portString);
                clientListe.put(clientName, new ClientInfo(ip, clientPort));
            } else {
                System.err.println("Client \"" + clientName + "\" konnte aufgrund ungültiger Informationen nicht registriert werden.");
            }
        } else if (nachricht.equalsIgnoreCase("bet")) {
            System.out.println("Wette angefordert.");
            setzeWette();
        }
        System.out.println(nachricht);
    }

    private static void setzeWette() {
        int wette = kontostand / 10;
        ClientInfo croupierInfo = clientListe.get("c");
        sendePacket(croupierInfo.ip, croupierInfo.port, "bet " + wette);
        kontostand -= wette;
        System.out.println("Wette: " + wette + "€");
        System.out.println("Kontostand: " + kontostand + "€");
    }

    private static boolean isGueltigeIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGueltigerPort(String port) {
        try {
            int num = Integer.parseInt(port);
            return num >= 0 && num <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void showFehler(String nachricht) {
        System.err.println(nachricht);
        System.exit(-1);
    }

    private record ClientInfo(String ip, int port) {}
}
