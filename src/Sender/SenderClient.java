package Sender;

import Common.Trame;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SenderClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(3 * 1000);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public void SendBytes(byte[] b) throws IOException {
        try {
            out.write(b);
        } catch (SocketTimeoutException e) {
            System.out.println(e);
            System.out.println("ERROR: Timeout exception");
        }
    }

    public void stopConnection() throws IOException {
        var t = new Trame('F', '\0');
        var b = t.ToBytes();
        out.write(b);

        in.close();
        out.close();
        clientSocket.close();
        System.out.println("Connection with server closed.");
    }


    public void SendAndWaitAck(Trame trame) throws IOException {
        boolean TrameReceived = false;
        var sending = "Sending: ";

        while (!TrameReceived) {
            System.out.println(sending);
            var byteTrame = trame.ToBytes();
            trame.PrintToConsole();
            SendBytes(byteTrame);

            try {
                var ack = Trame.GetTrame(in);
                System.out.println("Receiving: ");
                ack.PrintToConsole();

                if (ack.getType() == 'A' && ack.getNum() == trame.getNum()) {
                    TrameReceived = true;
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Socket time out.");
            }


            sending = "Resending: ";
        }
    }
}