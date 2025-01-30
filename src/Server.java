import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9999;
    private static final int SIZE = 1048576;

    // We assume that the hash table only stores positive (> 0) values.
    // Therefore a key with 0 (zero) value indicates that nothing is stored there.
    // Since the key is an integer we decided to use an array of integers as a hashtable,
    // instead of using Hashtable implementation of the core library.
    private final int[] HASH_TABLE = new int[SIZE];

    private ServerSocket serverSocket = null;
    private final Object lock = new Object();

    public static void main(String[] args) {
        new Server(PORT).accept();
    }

    /**
     * The constructor starts the server.
     *
     * @param port the port that the server listens to.
     */
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
    }

    /**
     * This method initiates the accept process for the server and processes the requests from clients.
     */
    public void accept() {
        while (true) {
            System.out.println("Waiting for connections...");
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket).start();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
    }

    /**
     * An internal class which creates a thread to serve each client.
     */
    private class ServerThread extends Thread {
        private final Socket clientSocket;

        public ServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            System.out.println("Connection successful!");
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;

            System.out.println("Waiting for a command...");

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // this method performs the communication through the streams
                processInput(in, out);
            } catch (IOException e) {
                System.err.println("IOException...");
            }

            // close the streams and the sockets
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error when closing sockets");
            }
        }

        /**
         * This method performs the communication through the streams
         */
        private void processInput(BufferedReader in, PrintWriter out) throws IOException {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server: " + inputLine);

                String[] inputDataString = inputLine.split(",");
                // We consider that the input will have two or three numbers separated with comma.
                // Everything else will be considered as invalid input and a negative status value will be returned.
                if (inputDataString.length < 2 || inputDataString.length > 3) {
                    System.out.println("Server: Invalid input");
                    out.println(0);
                    continue;
                }

                int command = -1;
                int key = -1;
                int value = -1;

                // check if we have integers in the data
                try {
                    command = Integer.parseInt(inputDataString[0].trim());
                    key = Integer.parseInt(inputDataString[1].trim());
                    if (inputDataString.length == 3) {
                        value = Integer.parseInt(inputDataString[2].trim());
                    }
                } catch (ArithmeticException e) {
                    System.out.println("Server: Invalid input");
                    out.println(0);
                    continue;
                }

                // validate and process the command
                switch (command) {
                    case 0:
                        // Terminates the while loop and then the server
                        if (key == 0) {
                            System.out.println("Server: End command");
                            return;
                        } else {
                            System.out.println("Server: Invalid input");
                            out.println(0);
                        }
                        break;
                    case 1:
                        out.println(insert(key, value));
                        break;
                    case 2:
                        out.println(delete(key));
                        break;
                    case 3:
                        out.println(search(key));
                        break;
                    default:
                        System.out.println("Server: Invalid command: " + command);
                        out.println(0);
                        break;
                }
            }
        }

        /**
         * Process the insert command
         */
        private int insert(int key, int value) {
            synchronized (lock) {
                if (key < 0 || key > SIZE - 1) {
                    System.out.println("Server: Invalid key: " + key);
                    return 0;
                }
                if (value < 1) {
                    System.out.println("Server: Invalid value: " + value);
                    return 0;
                }

                System.out.println("Server: Wrote key " + key + " value " + value);
                HASH_TABLE[key] = value;
                return 1;
            }
        }

        /**
         * Process the delete command
         */
        private int delete(int key) {
            synchronized (lock) {
                if (key < 0 || key > SIZE - 1) {
                    System.out.println("Server: Invalid key: " + key);
                    return 0;
                }
                System.out.println("Server: Deleted key " + key);
                HASH_TABLE[key] = 0;
                return 1;
            }
        }

        /**
         * Process the search command
         */
        private int search(int key) {
            if (key < 0 || key > SIZE - 1) {
                System.out.println("Server: Invalid key: " + key);
                return 0;
            }
            System.out.println("Server: Search key " + key + " value " + HASH_TABLE[key]);
            return HASH_TABLE[key];
        }
    }
}