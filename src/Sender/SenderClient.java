package Sender;

import java.io.*;
import java.net.Socket;

public class SenderClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public byte[] sendBytes(byte[] b) throws IOException {
        out.writeInt(b.length);
        out.write(b);

        var l = in.readInt();
        var resp = new byte[l];
        in.readFully(resp, 0, l);

        return  resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}