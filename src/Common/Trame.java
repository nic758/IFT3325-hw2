package Common;

import Sender.SenderClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Trame {
    //Slide page 13
    //TODO:bit stuffing, test, sliding_window > 1 ??.
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
        Byte b;
        while (true) {
            b = in.readByte();
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
        var unstuffedBytes = UnStuff(b);

        var stringData = new String(unstuffedBytes, StandardCharsets.UTF_8);
        Type = stringData.charAt(1);
        Num = stringData.charAt(2);

        var payloadBytes = Arrays.copyOfRange(unstuffedBytes, 3, unstuffedBytes.length - 3);
        Payload = new String(payloadBytes, StandardCharsets.UTF_8);

        var CRCbytes = Arrays.copyOfRange(unstuffedBytes, unstuffedBytes.length - 3, unstuffedBytes.length - 1);
        CRC = ((CRCbytes[1] & 0xff) << 8) | (CRCbytes[0] & 0xff);
    }

    public boolean IsCRCEquals() {
        var trameString = String.valueOf(Type) + String.valueOf(Num) + Payload;
        var trameBytes = trameString.getBytes(StandardCharsets.UTF_8);
        var calculatedCRC = new CRC16CCITT().calcCRC(trameBytes);

        return CRC == calculatedCRC;
    }

    public void CalcCRC() {
        var crcString = String.valueOf(Type) + String.valueOf(Num) + Payload;
        var crcBytes = crcString.getBytes(StandardCharsets.UTF_8);
        CRC = new CRC16CCITT().calcCRC(crcBytes);
    }

    public byte[] ToBytes() throws IOException {
        //TODO:need refactor;
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

        return BitStuff(stream.toByteArray());
    }

    private String ArrayToBinaryString(byte[] b) {
        StringBuilder bitString = new StringBuilder();

        for (byte aByte : b) {
            var s = String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0');
            bitString.append(s);
        }

        return bitString.toString();
    }

    private byte[] BitStringToByteArray(String bits) {
        var length = (int) Math.ceil((float) bits.length() / 8);
        //We add +2 on the length for the flags
        var b = new byte[length + 2];

        //Adding flag to byte array;
        var byteFlag = Flag.getBytes(StandardCharsets.UTF_8)[0];
        b[0] = byteFlag;
        b[b.length - 1] = byteFlag;
        var offset = 0;
        var count = 1;
        while (offset < bits.length()) {
            var bitsToTake = Math.min(bits.length() - offset, 8);
            var bit = bits.substring(offset, offset + bitsToTake);
            offset += 8;

            b[count] = (byte) Integer.parseInt(bit, 2);
            count++;
        }

        return b;
    }

    private byte[] UnStuff(byte[] b) {
        //Remove flag from byte array
        var removedFlag = Arrays.copyOfRange(b, 1, b.length - 1);
        var bitString = ArrayToBinaryString(removedFlag);

        int cnt = 0;
        StringBuilder unstuffed = new StringBuilder();
        boolean isAdded0 = false;
        for (int i = 0; i < bitString.length(); i++) {
            var c = bitString.charAt(i);

            if (c == '1') {
                cnt++;
                unstuffed.append(c);
                if (cnt == 5) {
                    isAdded0 = true;
                }
            } else {
                if (isAdded0) {
                    isAdded0 = false;
                    continue;
                }

                unstuffed.append(c);
                cnt=0;
            }
        }

        return BitStringToByteArray(unstuffed.toString());
    }

    private byte[] BitStuff(byte[] b) {
        //Remove flag from byte array
        var removedFlag = Arrays.copyOfRange(b, 1, b.length - 1);
        var bitString = ArrayToBinaryString(removedFlag);

        var cnt = 0;
        StringBuilder stuffedString = new StringBuilder();
        for (int i = 0; i < bitString.length(); i++) {
            var c = bitString.charAt(i);
            if (c == '1') {
                cnt++;
                if (cnt < 5) {
                    stuffedString.append(c);
                } else {
                    stuffedString.append(c).append('0');
                    cnt = 0;
                }
            } else {
                stuffedString.append(c);
                cnt = 0;
            }
        }

        return BitStringToByteArray(stuffedString.toString());
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
