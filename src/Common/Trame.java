package Common;

import Sender.SenderClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Trame {
    //Slide page 13
    //TODO: flag AND bit stuffing.
    String Flag = "01111110";
    char Type;
    char Num;
    String Payload;
    int CRC;

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

        var payloadBytes = Arrays.copyOfRange(b, 3, b.length - 2);
        Payload = new String(payloadBytes, StandardCharsets.UTF_8);

        var CRCbytes = Arrays.copyOfRange(b, b.length-2, b.length);
        CRC = ((CRCbytes[1] & 0xff) << 8) | (CRCbytes[0] & 0xff);
    }

    public boolean IsCRCEquals(){
        var trameString = String.valueOf('F') + String.valueOf(Type) + String.valueOf(Num) + Payload;
        var trameBytes = trameString.getBytes(StandardCharsets.UTF_8);
        var calculatedCRC = new CRC16CCITT().calcCRC(trameBytes);

       return CRC == calculatedCRC;
    }

    public byte[] ToBytes() throws IOException {
        var trameString = String.valueOf('F') + String.valueOf(Type) + String.valueOf(Num) + Payload;
        var b = trameString.getBytes(StandardCharsets.UTF_8);
        CRC = new CRC16CCITT().calcCRC(b);

        var stream = new ByteArrayOutputStream();
        stream.write(b);
        byte[] CRCbytes = new byte[2];
        CRCbytes[0] = (byte) (CRC & 0xFF);
        CRCbytes[1] = (byte) ((CRC >> 8) & 0xFF);

        stream.write(CRCbytes);

        return stream.toByteArray();
    }

    public void PrintToConsole(){
        System.out.println("******************************************");
        System.out.println("Type: " + String.valueOf(Type));
        System.out.println("Num: " + (int)Num);
        System.out.println("Data: " + Payload);
        System.out.println("CRC: " + CRC);
        System.out.println("******************************************");
    }
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
