package Receiver;

import java.net.ServerSocket;
import java.net.Socket;

public class MainReceiver {

    public static void main(String[] args) {
        try {
            var port = Integer.parseInt(args[0]);
            var server = new ReceiverServer();
            server.start(port);
        } catch (Exception e) {
            System.out.println(e + "\n");
        }
    }


}
