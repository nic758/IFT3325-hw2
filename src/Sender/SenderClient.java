package Sender;

import Common.Trame;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        var t = new Trame('F', '\0');
        var b = t.ToBytes();
        out.writeInt(b.length);
        out.write(b);

        in.close();
        out.close();
        clientSocket.close();
        System.out.println("Connection with server closed.");
    }


    public void SendAndWaitAck(Trame trame) throws IOException {
        boolean TrameReceived = false;
        while (!TrameReceived){
            System.out.println("Sending trame "+ (int)trame.getNum() +", Data : " + trame.getPayload());
            var b = sendBytes(trame.ToBytes());
            var ack = new Trame();
            ack.Receive(b);

            //TODO: if Common.Trame is lost we should send back.
            if(ack.getType() == 'A' && ack.getNum() == trame.getNum()){
                TrameReceived = true;
            }
        }
    }
}