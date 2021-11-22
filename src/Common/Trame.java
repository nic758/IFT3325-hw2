package Common;

import Sender.SenderClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Trame {
    //Slide page 13
    String Flag = "01111110";
    String DestAddress;
    char Type;
    char Num;
    String Control;
    String Payload;
    //Error detection CRC
    //Frame Check Sequence
    String FCS;

    public Trame(String data){
        Payload = data;
    }

    public Trame(byte[] r){
        Type = (char)r[1];
        Num = (char)r[2];

        var payloadBytes = Arrays.copyOfRange(r, 3, r.length-2);
        Payload = new String(payloadBytes, StandardCharsets.UTF_8);
    }

    public void SendAndWaitAck(SenderClient sender) throws IOException {
        boolean TrameReceived = false;
        while (!TrameReceived){
            System.out.println("Sending data: " + Payload);
            var b = sender.sendBytes(Payload.getBytes(StandardCharsets.UTF_8));
            var ack = new Trame(b);
            //TODO: if Common.Trame is lost we should send back.
            if(ack.Type == 'A' && ack.Num == Num){
                TrameReceived = true;
            }
        }
    }

    public void Send(){

    }
}
