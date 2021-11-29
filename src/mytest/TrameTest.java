package mytest;

import Common.Trame;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TrameTest {

    @Test
    public void Test_ToBytes() throws IOException {

        var t = new Trame("test", (char)0);
        var actual = t.ToBytes();
        var expected  = new byte[]{126, 73, 0, 116, 101, 115, 116, -28, -73, 126};

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void Test_ToBytes1() throws IOException {

        var t = new Trame('C', (char)0);
        var actual = t.ToBytes();
        var expected  = new byte[]{126, 67, 0, 110, 117, 108, 108, -3, -127, 126};

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void Test_GetTrame() throws IOException {
        var b = new byte[]{126, 73, 0, 116, 101, 115, 116, -28, -73, 126};
        var stream = new DataInputStream(new ByteArrayInputStream(b));
        var actual = Trame.GetTrame(stream);
        var expected = new Trame("test", (char)0);

        Assert.assertEquals(expected.getNum(), actual.getNum());
        Assert.assertEquals(expected.getType(), actual.getType());
        Assert.assertEquals(expected.getPayload(), actual.getPayload());
        Assert.assertEquals(expected.getCRC(), actual.getCRC());
    }

    @Test
    public void Test_CRC_shouldNotMatch() throws IOException {
        //                             |Changed this byte. Original value 116
        //                             |
        var b = new byte[]{126, 73, 0, 115, 101, 115, 116, -28, -73, 126};
        var stream = new DataInputStream(new ByteArrayInputStream(b));
        var t = Trame.GetTrame(stream);

        Assert.assertFalse(t.IsCRCEquals());
    }

    @Test
    public void Test_CRC_shouldMatch() throws IOException {
        var b = new byte[]{126, 73, 0, 116, 101, 115, 116, -28, -73, 126};
        var stream = new DataInputStream(new ByteArrayInputStream(b));
        var t = Trame.GetTrame(stream);

        Assert.assertTrue(t.IsCRCEquals());
    }

    @Test
    public void Test_Bytestuff(){
        var t = new Trame();
        var data = "~this ~ is a total valid ~E string ~";
        var stuffedBytes = t.Bytestuff(data.getBytes(StandardCharsets.UTF_8));
        var actual = new String(stuffedBytes, StandardCharsets.UTF_8);
        var expected = "~this E~ is a total valid E~EE string ~";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void Test_Bytestuff1(){
        var t = new Trame();
        var data = new byte[]{126, -3, 123, 0, 69, 126,126};
        var actual = t.Bytestuff(data);
        var expected = new byte[]{126, -3, 123, 0, 69,69, 69, 126,126};

        Assert.assertArrayEquals(expected, actual);
    }
}
