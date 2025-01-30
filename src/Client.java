import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static final String HOSTNAME = "127.0.0.1";
    public static final int PORT = 9999;

    private Socket socket;

    public static void main(String[] args) {
        new Client(HOSTNAME, PORT).startCommunication();
    }

    public Client(String hostname, int port) {
        System.out.println("Attempting to connect to host " + hostname + " on port " + port);

        try {
            socket = new Socket(hostname, port);
            System.out.println("Client: connected");
        } catch (IOException e) {
            System.err.println("Client: connection error.");
            System.exit(1);
        }
    }

    private void startCommunication() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Client: Couldn't get I/O");
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        System.out.println("Type command: ");
        try {
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equals("0,0")) {
                    System.out.print("exit command!");
                    break;
                }
                System.out.println("result: " + in.readLine());
                System.out.print("next command: ");
            }
        } catch (IOException e) {
            System.err.println("Error when closing sockets");
        }

        // close the streams and the sockets
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing sockets");
        }
    }
}