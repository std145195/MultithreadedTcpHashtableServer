import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
  O κώδικας του Client παραμένει ίδιος με το πρώτο ερώτημα.
 - Ο Client δε χρειάζεται να γνωρίζει αν ο Server υποστηρίζει πολλά νήματα ή όχι.
 - Στέλνει ακριβώς τις ίδιες εντολές και περιμένει απαντήσεις από τον Server.
 - Ο Server απλώς δημιουργεί νέα threads για κάθε Client.
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int port = 8888;

        Socket socket = new Socket(serverAddress, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner scanner = new Scanner(System.in);

        System.out.println("🔗 Συνδεθήκαμε στον server!");

        while (true) {
            System.out.println("\nΕισαγωγή εντολής της μορφής Α,Β,Γ: ");
            String command = scanner.nextLine();

            out.println(command);
            String response = in.readLine();
            System.out.println("📨 Απάντηση server: " + response);

            if ("bye".equals(response)) {
                System.out.println("🔴 Αποσύνδεση...");
                break;
            }
        }

        // Κλείσιμο των πόρων
        socket.close();
        out.close();
        in.close();
        scanner.close();
    }
}