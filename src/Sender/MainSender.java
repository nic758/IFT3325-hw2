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

        var con = new Trame('C', (char)0);
        client.SendAndWaitAck(con);

        try(BufferedReader br = new BufferedReader((new FileReader("simpleFile.txt")))){
            String line;
            int trameNumber = 0;
            while ((line = br.readLine()) != null){
                var t = new Trame(line, (char)trameNumber);
                client.SendAndWaitAck(t);
            }
        }

        client.stopConnection();
        System.out.println("Done sending the file.");
    }
}
