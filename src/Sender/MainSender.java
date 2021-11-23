package Sender;

import Common.Trame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainSender {

    public static void main(String[] args) throws IOException {
        var machine = args[0];
        var port = Integer.parseInt(args[1]);
        var file = args[2];
        var goBackN = Integer.parseInt(args[3]);

        SenderClient client = new SenderClient();
        client.startConnection(machine, port);

        var con = new Trame('C', (char)goBackN);
        client.SendAndWaitAck(con);

        try(BufferedReader br = new BufferedReader((new FileReader(file)))){
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
