package a_echotcp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoTCPClient {
    private static final Scanner SCANNER = new Scanner(System.in);
    public static final String SERVER = "localhost";
    public static final int PORT = 3400;
    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;
    private Socket clientSideSocket;

    public EchoTCPClient() {
        System.out.println( "Echo TCP client is running ...");
    }

    public void init() throws Exception {
        clientSideSocket = new Socket(SERVER, PORT);
        createStreams(clientSideSocket);
        protocol(clientSideSocket);
        clientSideSocket.close();
    }

    public void protocol(Socket socket) throws Exception {
        boolean running = true;

        while (running) {
            System.out.print("Ingrese un comando: ");
            String fromUser = SCANNER.nextLine();
            toNetwork.println(fromUser);
            String fromServer = fromNetwork.readLine();
            System.out.println("[Client] From server: " + fromServer);

            if (fromUser.equals("FINISH")) {
                running = false;
            }
        }

        socket.close();
    }

    private void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String args[]) throws Exception {
        EchoTCPClient ec = new EchoTCPClient();
        ec.init();
    }
}