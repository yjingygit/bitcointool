package com.coinok.sdk.segwit;

/**
 * Base32
 *
 * @author Jingyu Yang
 */
public class Bech32 {

    private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    private static final int[] GENERATOR = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    private static int polymod(byte[] value) {
        int result = 1;

        for (byte bb : value) {
            int temp = (result >> 25);
            result = (result & 0X1ffffff) << 5 ^ bb;
            for (int i = 0; i < 5; i++) {
                result ^= ((temp >> i) & 1) == 1 ? GENERATOR[i] : 0;
            }
        }
        return result;
    }

    private static byte[] hrpExpand(byte[] hrp) {
        int length = hrp.length;
        byte[] buf1 = new byte[length];
        byte[] buf2 = new byte[length];

        for (int i = 0, len = hrp.length; i < len; i++) {
            buf1[i] = (byte)(hrp[i] >> 5);
            buf2[i] = (byte)(hrp[i] & 0x1f);
        }

        byte[] result = new byte[length * 2 + 1];
        System.arraycopy(buf1, 0, result, 0, length);
        result[length] = (byte)0X0;
        System.arraycopy(buf2, 0, result, length + 1, length);
        return result;
    }

    private static boolean verifyCheckSum(byte[] hrp, byte[] data) {
        byte[] expanded = hrpExpand(hrp);

        byte[] temp = new byte[expanded.length + data.length];
        System.arraycopy(expanded, 0, temp, 0, expanded.length);
        System.arraycopy(data, 0, temp, expanded.length, data.length);

        return polymod(temp) == 1;
    }

    private static byte[] createCheckSum(byte[] hrp, byte[] data) {
        byte[] expanded = hrpExpand(hrp);

        byte[] temp = new byte[expanded.length + data.length + 6];
        System.arraycopy(expanded, 0, temp, 0, expanded.length);
        System.arraycopy(data, 0, temp, expanded.length, data.length);

        int polymod = polymod(temp) ^ 1;
        byte[] result = new byte[6];
        for (int i = 0; i < 6; i++) {
            result[i] = (byte)((polymod >> 5 * (5 - i)) & 31);
        }
        return result;
    }

    /**
     * encode the hrp and data with Base32.
     *
     * @param hrp
     * @param data
     * @return
     */
    public static String encode(byte[] hrp, byte[] data) {
        byte[] checkSum = createCheckSum(hrp, data);

        byte[] temp = new byte[checkSum.length + data.length];
        System.arraycopy(data, 0, temp, 0, data.length);
        System.arraycopy(checkSum, 0, temp, data.length, checkSum.length);

        for (int i = 0, len = temp.length; i < len; i++) {
            temp[i] = (byte)CHARSET.charAt(temp[i]);
        }

        byte[] result = new byte[hrp.length + 1 + temp.length];
        System.arraycopy(hrp, 0, result, 0, hrp.length);
        result[hrp.length] = (byte)0X31;
        System.arraycopy(temp, 0, result, hrp.length + 1, temp.length);

        return new String(result);
    }

    /**
     * decode th Bech32 string into hrp and data.
     *
     * @param str
     * @return
     */
    public static DataPair<byte[], byte[]> decode(String str) {
        if (str == null || str.trim().length() == 0) {
            throw new RuntimeException("Bech32 String to decode can't be empty!");
        }
        if (str.length() < 8 || str.length() > 90) {
            throw new RuntimeException("Bech32 String to decode is more than 90 or less than 8!");
        }
        if (!str.equals(str.toUpperCase()) && !str.equals(str.toLowerCase())) {
            throw new RuntimeException("Bech32 String to decode nust only upper case or lower case!");
        }

        for (byte bb : str.getBytes()) {
            if (bb < 0X21 || bb > 0X7e) {
                throw new RuntimeException("Bech32 String characters out of range!");
            }
        }

        String strLower = str.toLowerCase();

        int pos = strLower.lastIndexOf("1");
        if (pos < 0) {
            throw new RuntimeException("Bech32 String missing separator!");
        } else if (pos + 7 > strLower.length()) {
            throw new RuntimeException("Bech32 String too short checkSum!");
        }

        String temp = strLower.substring(pos + 1);
        for (int i = 0, len = temp.length(); i < len; i++) {
            if (CHARSET.indexOf(temp.charAt(i)) == -1) {
                throw new RuntimeException("Bech32 String characters out of range!");
            }
        }

        byte[] hrp = strLower.substring(0, pos).getBytes();
        if (hrp.length == 0) {
            throw new RuntimeException("Bech32 String has no HRP!");
        }

        byte[] data = new byte[strLower.length() - pos - 1];
        for (int i = 0, j = pos + 1; j < strLower.length(); i++, j++) {
            data[i] = (byte)CHARSET.indexOf(strLower.charAt(j));
        }

        if (!verifyCheckSum(hrp, data)) {
            throw new RuntimeException("Bech32 String check fail!");
        }

        byte[] result = new byte[data.length - 6];
        System.arraycopy(data, 0, result, 0, data.length - 6);

        return new DataPair<>(hrp, result);
    }

}
