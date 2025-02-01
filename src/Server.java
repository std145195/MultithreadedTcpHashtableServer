import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
/**
Για να μπορεί να εξυπηρετεί περισσότερους clients τροποποιήθηκε ο κωδικας ως εξής:
    - δημιουργία ClientHandler που κάνει extend την κλάση Thread
    - ο server κάθε φορά που δέχεται μήνυμα από client δημιουργεί ενα object ClientHandler, δηλαδή ανοίγει ένα νέο thread
    - η μέθοδος processCommand δεν είναι πια static και πρέπει να έχει πρόσβαση στο instance hashMap
 **/
public class Server {
    private static final int PORT = 8888;
    private static final ConcurrentHashMap<Integer, Integer> hashMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server ξεκίνησε στην πόρτα " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Δέχεται νέο client
                System.out.println("Νέος client συνδέθηκε!");

                // Δημιουργεί νέο νήμα για τον client
                new ClientHandler(clientSocket, hashMap).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Κλάση που χειρίζεται κάθε Client σε ξεχωριστό νήμα
class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ConcurrentHashMap<Integer, Integer> hashMap;

    public ClientHandler(Socket socket, ConcurrentHashMap<Integer, Integer> hashMap) {
        this.clientSocket = socket;
        this.hashMap = hashMap;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client Message: " + inputLine);
                String response = processCommand(inputLine);
                out.println(response);
                if ("bye".equals(response)) break;
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

    private String processCommand(String command) {
        String[] parts = command.split(",");

        int action = Integer.parseInt(parts[0]);
        int key = Integer.parseInt(parts[1]);
        // 0 (τέλος επικοινωνίας), 1 (insert), 2 (delete) και 3 (search)
        if (action == 0) {
            System.out.println("Κλείσιμο σύνδεσης.");
            return "bye";
        } else if (action == 1) {
            int value = Integer.parseInt(parts[2]);
            hashMap.put(key, value);
            return "1"; // Επιτυχής εισαγωγή
        } else if (action == 2) {
            return hashMap.remove(key) != null ? "1" : "0"; // Διαγραφή
        } else if (action == 3) {
            return hashMap.containsKey(key) ? hashMap.get(key).toString() : "0"; // Αναζήτηση
        } else {
            return "0"; // Λανθασμένη εντολή
        }
    }
}
