import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Δημιουργία hashtable με μέγεθος 2^20 για αποθήκευση δεδομένων
    private static final int HASHMAP_SIZE = (int) Math.pow(2, 20);
    private static final ConcurrentHashMap<Integer, Integer> hashMap = new ConcurrentHashMap<>(HASHMAP_SIZE);
    private static final int PORT = 8888;

    public static void main(String[] args) {
        // Άνοιγμα socket server στην καθορισμένη θύρα
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("O server ξεκίνησε στην πόρτα " + PORT);

            // Ο server παραμένει σε λειτουργία και δέχεται συνδέσεις από clients
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Αποδοχή νέας σύνδεσης
                System.out.println("Νέος client συνδέθηκε!");
                new ClientHandler(clientSocket).start(); // Δημιουργία νέου thread για κάθε client
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Κλάση που χειρίζεται κάθε Client σε ξεχωριστό νήμα
    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                // Ανάγνωση εντολών από τον client
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Λήψη: " + inputLine);
                    String response = processCommand(inputLine); // Επεξεργασία εντολής
                    out.println(response); // Αποστολή απάντησης στον client
                    if ("bye".equals(response)) break; // Τερματισμός αν ο client αποσυνδεθεί
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Client αποσυνδέθηκε.");
            }
        }

        // Συνάρτηση που επεξεργάζεται την εντολή του client
        private String processCommand(String command) {
            String[] parts = command.split(","); // Διαχωρισμός της εντολής σε τμήματα

            int action = Integer.parseInt(parts[0]); // Ο πρώτος αριθμός καθορίζει τη λειτουργία
            int key = Integer.parseInt(parts[1]); // Ο δεύτερος αριθμός είναι το κλειδί

            // Διαχείριση εντολών: 0 (κλείσιμο), 1 (εισαγωγή), 2 (διαγραφή), 3 (αναζήτηση)
            if (action == 0) {
                System.out.println("Κλείσιμο σύνδεσης.");
                return "bye"; // Επιστροφή μηνύματος αποσύνδεσης
            } else if (action == 1) {
                int value = Integer.parseInt(parts[2]); // Ο τρίτος αριθμός είναι η τιμή για εισαγωγή
                hashMap.put(key, value); // Προσθήκη στη δομή δεδομένων
                return "1"; // Επιτυχής εισαγωγή
            } else if (action == 2) {
                // Διαγραφή κλειδιού και επιστροφή του αντίστοιχου μηνύματος
                return hashMap.remove(key) != null ? "1" : "0";
            } else if (action == 3) {
                // Αναζήτηση κλειδιού και επιστροφή της τιμής του αν υπάρχει
                return hashMap.containsKey(key) ? hashMap.get(key).toString() : "0";
            } else {
                return "0"; // Λανθασμένη εντολή
            }
        }
    }
}