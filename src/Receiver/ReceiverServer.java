package Receiver;

import Common.Trame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //listening to the port 'port'
        System.out.println("Waiting for connections...");
        clientSocket = serverSocket.accept();
        //allow data exchange
        BufferedInputStream buffer = new BufferedInputStream(clientSocket.getInputStream());
        in = new DataInputStream(buffer);
        out = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Connected with peer");

        boolean close = false;
        while (!close) {
            int dataLength = in.readInt();
            if (dataLength <= 0) {
                System.out.println("No data");
                close=true;
                break;
            }

            byte[] data = new byte[dataLength];
            in.readFully(data, 0, data.length);
            var incomingTrame = new Trame();
            incomingTrame.Receive(data);

            var resp = ProcessIncomingTrame(incomingTrame);
            if(resp == null){
                System.out.println("Stopping connection");
                break;
            }

            System.out.println("Sending :");
            resp.PrintToConsole();
            var r = resp.ToBytes();
            out.writeInt(r.length);
            out.write(r);
        }

        stop();
    }

    private Trame ProcessIncomingTrame(Trame incoming){
        System.out.println("Receiving: ");
        incoming.PrintToConsole();

        if(incoming.getType() == 'I'){
            return new Trame('A', incoming.getNum());
        }

        if(incoming.getType() == 'C'){

            return new Trame('A', incoming.getNum());
        }

        if(incoming.getType() == 'F'){
            return null;
        }

        return null;
    }

    private void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
