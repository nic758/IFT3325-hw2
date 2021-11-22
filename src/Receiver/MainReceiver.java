package Receiver;

import java.net.ServerSocket;
import java.net.Socket;

public class MainReceiver {

    public static void main(String[] args) {

        //Sender: emetteur
        //Receiver: recepteur
        //poly gene: 1 0001 0000 0010 0001
        //trame number= 1 char
        System.out.print("Hello World!\n");

        try {
            var port = Integer.parseInt(args[0]);
            var server = new ReceiverServer();
            server.start(port);
            server.stop();
        } catch (Exception e) {
            System.out.println(e + "\n");
        }
    }


}
