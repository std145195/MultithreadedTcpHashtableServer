import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 8888;

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("🔗 Συνδεθήκαμε στον server!");

            while (true) {
                System.out.println("\nΕισαγωγή εντολής: (A,B,C) ή (0,0 για έξοδο)");
                String command = scanner.nextLine();

                out.println(command);
                String response = in.readLine();
                System.out.println("📨 Απάντηση server: " + response);

                if ("bye".equals(response)) {
                    System.out.println("Αποσύνδεση...");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
