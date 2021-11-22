package Receiver;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ReceiverServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //listening to the port 'port'
        clientSocket = serverSocket.accept();
        //allow data exchange
        BufferedInputStream buffer = new BufferedInputStream(clientSocket.getInputStream());
        in = new DataInputStream(buffer);
        out = new DataOutputStream(clientSocket.getOutputStream());


        System.out.println("Waiting for connections...");
        Socket socket = serverSocket.accept();
        System.out.println("Connected with peer");

        boolean close = false;
        int dataLength = in.readInt();
        if (dataLength <= 0) {
            System.out.println("No data");
            close = true;
        }


        while (!close) {
            byte[] data = new byte[dataLength];
            in.readFully(data, 0, data.length);
            System.out.println(data.toString());
            var r = "Done".getBytes(StandardCharsets.UTF_8);
            out.writeInt(r.length);
            out.write(r);
        }

        stop();
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
