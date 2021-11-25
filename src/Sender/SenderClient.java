package Sender;

import Common.Trame;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class SenderClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(3*1000);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public byte[] sendBytes(byte[] b) throws IOException {
        try{
            out.writeInt(b.length);
            out.write(b);

            var l = in.readInt();
            var resp = new byte[l];
            in.readFully(resp, 0, l);

            return  resp;
        }
        catch (SocketTimeoutException e){
            System.out.println(e);
            System.out.println("ERROR: Timeout exception");
        }

        return new byte[0];
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
        var sending="Sending: ";

        while (!TrameReceived){
            System.out.println(sending);
            trame.PrintToConsole();
            var b = sendBytes(trame.ToBytes());

            //If we have a timeout Exception.
            if(b.length == 0){
                continue;
            }
            var ack = new Trame();
            ack.Receive(b);
            System.out.println("Receiving: ");
            ack.PrintToConsole();

            //TODO: if Trame is lost we should send back.
            if(ack.getType() == 'A' && ack.getNum() == trame.getNum()){
                TrameReceived = true;
            }

            sending = "Resending: ";
        }
    }
}