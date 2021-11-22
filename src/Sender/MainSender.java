package Sender;

import Common.Trame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainSender {

    public static void main(String[] args) throws IOException {
	// write your code here
        SenderClient client = new SenderClient();
        client.startConnection("127.0.0.1", 4444);

        try(BufferedReader br = new BufferedReader((new FileReader("")))){
            String line;
            while ((line = br.readLine()) != null){
                var t = new Trame(line);
                t.SendAndWaitAck(client);
            }
        }

        client.stopConnection();
        System.out.println("Done sending the file.");
        System.out.println("Connection with server closed.");
    }
}
