package Common;

import Sender.SenderClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Trame {
    //Slide page 13
    //TODO:
    String Flag = "01111110";
    char Type;
    char Num;
    String Payload;
    String CRC;

    public Trame(String data, char num) {
        Type = 'I';
        Num = num;
        Payload = data;
    }

    public Trame() {
    }

    public Trame(char type, char num) {
        Type = type;
        Num = num;
    }

    public void Receive(byte[] b) {
        var stringData = new String(b, StandardCharsets.UTF_8);
        //Flag = stringData.charAt(0);
        Type = stringData.charAt(1);
        Num = stringData.charAt(2);

        var payloadBytes = Arrays.copyOfRange(b, 3, b.length - 3);
        Payload = new String(payloadBytes, StandardCharsets.UTF_8);
        var trameLength = stringData.length();
        CRC = stringData.substring(trameLength - 3, trameLength - 1);
    }

    public byte[] ToBytes() {
        //TODO: calculate CRC
        var trameString = String.valueOf('F') + String.valueOf(Type) + String.valueOf(Num) + Payload + String.valueOf("CRF");
        return trameString.getBytes(StandardCharsets.UTF_8);
    }
    //TODO: do a print function

    public char getType() {
        return Type;
    }

    public char getNum() {
        return Num;
    }

    public String getPayload() {
        return Payload;
    }
}
