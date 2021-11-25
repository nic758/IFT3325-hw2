package Common;
//SRC: https://www.javatips.net/api/myHealthHub-master/src/de/tudarmstadt/dvs/myhealthassistant/myhealthhub/commontools/CRC16CCITT.java

public class CRC16CCITT {

    private static String TAG = "CRC16CCITT";
    private static boolean D = false;

    int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

    public static void main() {
        // byte[] testBytes = "123456789".getBytes("ASCII");
        //byte[] bytes = args[0].getBytes();
    }

    public int calcCRC(byte[] bytes) {
        int crc = 0xFFFF;          // initial value

        //if (D) Log.d(TAG, "Packet length: " + bytes.length);
        //if (D) printPacket(bytes.length, bytes, "CRC16");

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        return crc;
    }

    // For debugging
    public void printPacket(int bytes, byte[] buffer, String info) {
        String text = "";
        for (int i = 0; i < bytes; i++) {
            text += i + ": " + buffer[i] + " |";
        }

       // if (D) Log.d(TAG, info + " " + text);
    }
}