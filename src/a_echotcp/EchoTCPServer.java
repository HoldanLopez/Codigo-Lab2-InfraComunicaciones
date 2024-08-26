package a_echotcp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoTCPServer {
    public static final int PORT = 3400;
    private ServerSocket listener;
    private Socket serverSideSocket;
    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;
    public EchoTCPServer() {
        System.out.println("Echo TCP server is running on port: " + PORT);
    }

    public void init() throws Exception {
        listener = new ServerSocket(PORT);
        while (true) {
            serverSideSocket = listener.accept();
            createStreams(serverSideSocket);
            protocol(serverSideSocket);
        }
    }

    private void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void protocol(Socket socket) throws Exception {
        boolean running = true;

        while (running) {
            String message = fromNetwork.readLine();
            System.out.println("[Server] From client: " + message);

            String[] parts = message.split(" ");
            String command = parts[0];
            String response = "";

            switch (command) {
                case "CONV-DEC-BIN":
                    int decimal = Integer.parseInt(parts[1]);
                    int bitWidth = Integer.parseInt(parts[2]);
                    response = convertDecToBin(decimal, bitWidth);
                    break;

                case "CONV-DEC-HEX":
                    decimal = Integer.parseInt(parts[1]);
                    int hexWidth = Integer.parseInt(parts[2]);
                    response = convertDecToHex(decimal, hexWidth);
                    break;

                case "CONV-BIN-HEXA":
                    String binary = parts[1];
                    response = convertBinToHex(binary);
                    break;

                case "GEN-CAD":
                    int length = Integer.parseInt(parts[1]);
                    if (parts.length == 3) {
                        int segmentSize = Integer.parseInt(parts[2]);
                        response = generarYSegmentarCadena(length, segmentSize);
                    } else {
                        response = generarCadena(length);
                    }
                    break;

                case "CAD-SEG":
                    length = Integer.parseInt(parts[1]);
                    int[] segmentSizes = new int[parts.length - 2];
                    for (int i = 2; i < parts.length; i++) {
                        segmentSizes[i - 2] = Integer.parseInt(parts[i]);
                    }
                    response = segmentString(length, segmentSizes);
                    break;

                case "UNI-CAD":
                    String[] segments = new String[parts.length - 1];
                    System.arraycopy(parts, 1, segments, 0, segments.length);
                    response = joinSegments(segments);
                    break;

                case "FINISH":
                    response = "Server shutting down...";
                    running = false;
                    break;

                default:
                    response = "Comando no reconocido";
                    break;
            }

            System.out.println("[Server] Sending to client: " + response);
            toNetwork.println(response);
        }

        socket.close();
    }

    public String convertDecToBin(int decimal, int numBits) {
        String binaryString = Integer.toBinaryString(decimal);
        return String.format("%" + numBits + "s", binaryString).replace(' ', '0');
    }

    public String convertDecToHex(int decimal, int hexWidth) {
        String hexString = Integer.toHexString(decimal).toUpperCase();
        return String.format("%" + hexWidth + "s", hexString).replace(' ', '0');
    }

    public String convertBinToHex(String binary) {
        int decimal = Integer.parseInt(binary, 2);
        return Integer.toHexString(decimal).toUpperCase();
    }

    public String generarCadena(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        return sb.toString();
    }

    public String generarYSegmentarCadena(int tamanio, int tamSegmento) {
        String cadenaCompleta = generarCadena(tamanio);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cadenaCompleta.length(); i += tamSegmento) {
            if (i + tamSegmento > cadenaCompleta.length()) {
                sb.append(cadenaCompleta.substring(i));
            } else {
                sb.append(cadenaCompleta, i, i + tamSegmento);
            }
            if (i + tamSegmento < cadenaCompleta.length()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public String segmentString(int length, int[] segmentSizes) {
        String fullString = generarCadena(length);
        StringBuilder sb = new StringBuilder();
        int currentIndex = 0;
        for (int size : segmentSizes) {
            if (currentIndex + size > fullString.length()) {
                sb.append(fullString.substring(currentIndex));
                break;
            } else {
                sb.append(fullString, currentIndex, currentIndex + size);
            }
            currentIndex += size;
            if (currentIndex < fullString.length()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public String joinSegments(String[] segments) {
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            sb.append(segment);
        }
        return sb.toString();
    }

    public static void main(String args[]) throws Exception {
        EchoTCPServer es = new EchoTCPServer();
        es.init();
    }
}