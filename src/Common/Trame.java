package Common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Trame {
    public static String Flag = "~";
    char Type;
    char Num;
    String Payload;
    int CRC;

    public Trame(String data, char num) {
        Type = 'I';
        Num = num;
        Payload = data;

        CalcCRC();
    }

    public Trame(char type, char num) {
        Type = type;
        Num = num;

        CalcCRC();
    }

    public Trame() {
    }

    public static Trame GetTrame(DataInputStream in) throws IOException {
        var stream = new ByteArrayOutputStream();
        var firstFlag = true;
        byte b;
        boolean processNextChar = true;
        while (true) {
            b = in.readByte();
            //if the previous char was a special char we don't want to process this char.
            if(!processNextChar){
                processNextChar =true;
                stream.write(b);
                continue;
            }
            //We do not want to process next char since it's our data.
            if(b == 'E'){
                processNextChar = false;
                continue;
            }

            stream.write(b);
            if (b == Trame.Flag.charAt(0) && firstFlag) {
                firstFlag = false;
                continue;
            }
            //Done reading the trame.
            if (b == Trame.Flag.charAt(0) && !firstFlag) {

                var t = new Trame();
                t.Receive(stream.toByteArray());
                return t;
            }
        }
    }

    private void Receive(byte[] b) {
        var stringData = new String(b, StandardCharsets.UTF_8);
        Type = stringData.charAt(1);
        Num = stringData.charAt(2);

        var payloadBytes = Arrays.copyOfRange(b, 3, b.length - 3);
        Payload = new String(payloadBytes, StandardCharsets.UTF_8);

        var CRCbytes = Arrays.copyOfRange(b, b.length - 3, b.length - 1);
        CRC = ((CRCbytes[1] & 0xff) << 8) | (CRCbytes[0] & 0xff);
    }

    public boolean IsCRCEquals() {
        var trameString = String.valueOf(Type) + String.valueOf(Num) + Payload;
        var trameBytes = trameString.getBytes(StandardCharsets.UTF_8);
        var calculatedCRC = new CRC().calc(trameBytes);

        return CRC == calculatedCRC;
    }

    public void CalcCRC() {
        var crcString = String.valueOf(Type) + String.valueOf(Num) + Payload;
        var crcBytes = crcString.getBytes(StandardCharsets.UTF_8);
        CRC = new CRC().calc(crcBytes);
    }

    public byte[] ToBytes() throws IOException {
        var trameString = Flag + String.valueOf(Type) + String.valueOf(Num) + Payload;
        var b = trameString.getBytes(StandardCharsets.UTF_8);

        var stream = new ByteArrayOutputStream();
        stream.write(b);
        byte[] CRCbytes = new byte[2];
        CRCbytes[0] = (byte) (CRC & 0xFF);
        CRCbytes[1] = (byte) ((CRC >> 8) & 0xFF);

        stream.write(CRCbytes);
        //This add the flag at the end of the trame
        stream.write(Flag.getBytes(StandardCharsets.UTF_8));

        return Bytestuff(stream.toByteArray());
    }

    public byte[] Bytestuff(byte[] b){
        var stream = new ByteArrayOutputStream();
        var removedFlag = Arrays.copyOfRange(b, 1, b.length-1);
        stream.write(b[0]);

        for (byte c:removedFlag){
            if(c == Flag.getBytes(StandardCharsets.UTF_8)[0] || c=='E'){
                stream.write('E');
            }

            stream.write(c);
        }
        stream.write(b[b.length-1]);

        return stream.toByteArray();
    }

    public void PrintToConsole() {
        System.out.println("******************************************");
        System.out.println("Type: " + String.valueOf(Type));
        System.out.println("Num: " + (int) Num);
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

    public int getCRC() {
        return CRC;
    }
}
