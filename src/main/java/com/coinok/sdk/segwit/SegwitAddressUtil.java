package com.coinok.sdk.segwit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Bytes;
import org.bitcoinj.core.Utils;

/**
 * Bech32 encode and decode util.
 *
 * @author Jingyu Yang
 */
public class SegwitAddressUtil {

    /**
     * hrp in MainNet.
     */
    private static final String HRP_MAIN = "bc";
    /**
     * hrp in TESTNet.
     */
    private static final String HRP_TEST = "tb";

    /**
     * 使用默认版本获取地址字符串。
     *
     * @param scriptPubKey
     * @return
     */
    public static String encode(String scriptPubKey) {
        return encode(scriptPubKey, true);
    }

    /**
     * 使用默认版本获取地址字符串。
     *
     * @param scriptPubKey
     * @return
     */
    public static String encode(String scriptPubKey, boolean isMainNet) {
        if (isMainNet) {
            return encode(HRP_MAIN.getBytes(), (byte)0, Utils.HEX.decode(scriptPubKey));
        } else {
            return encode(HRP_TEST.getBytes(), (byte)0, Utils.HEX.decode(scriptPubKey));
        }
    }

    /**
     * 将指定witness的script转换为地址格式。
     *
     * @param hrp
     * @param witnessVersion
     * @param scriptPubKey
     * @return
     */
    public static String encode(byte[] hrp, byte witnessVersion, byte[] scriptPubKey) {
        byte[] scriptBits = convertBits(scriptPubKey, 8, 5, true);

        byte[] data = new byte[scriptBits.length + 1];
        data[0] = witnessVersion;
        System.arraycopy(scriptBits, 0, data, 1, scriptBits.length);

        return Bech32.encode(hrp, data);
    }

    public static String decode(String address) {
        DataPair<byte[], byte[]> dataPair = Bech32.decode(address);
        byte[] hrp = dataPair.getHrp();
        byte[] data = dataPair.getData();

        String hrpStr = new String(hrp).toLowerCase();
        if (!HRP_MAIN.equals(hrpStr) && !HRP_TEST.equals(hrpStr)) {
            throw new RuntimeException("Invalid human-readable-part!");
        }

        byte witnessVersion = data[0];
        if (witnessVersion < 0 || witnessVersion > 16) {
            throw new RuntimeException("Invalid witness version!");
        }

        byte[] scriptBits = convertBits(Arrays.copyOfRange(data, 1, data.length), 5, 8, false);
        int scriptLength = scriptBits.length;
        if (scriptLength < 2 || scriptLength > 40) {
            throw new RuntimeException("Invalid witness length!");
        }

        if (witnessVersion == 0 && scriptBits.length != 20 && scriptBits.length != 32) {
            throw new RuntimeException("Witness version and script length not match!");
        }
        return Utils.HEX.encode(scriptBits);
    }

    /**
     * 转换进制。
     *
     * @param data
     * @param fromBits
     * @param toBits
     * @param isPadding0
     * @return
     */
    private static byte[] convertBits(byte[] data, int fromBits, int toBits, boolean isPadding0) {
        int acc = 0;
        int bits = 0;
        int maxv = (1 << toBits) - 1;
        int maxAcc = (1 << (fromBits + toBits - 1)) - 1;

        List<Byte> result = new ArrayList<>();

        for (byte value : data) {
            short bb = (short)(value & 0XFF);
            if (bb < 0 || (bb >> fromBits) != 0) {
                throw new RuntimeException();
            }

            acc = ((acc << fromBits) | bb) & maxAcc;
            bits += fromBits;

            while (bits >= toBits) {
                bits -= toBits;
                result.add((byte)((acc >> bits) & maxv));
            }
        }

        if (isPadding0) {
            if (bits > 0) {
                result.add((byte)((acc << (toBits - bits)) & maxv));
            }
        } else if (bits >= fromBits || (byte)((acc << (toBits - bits)) & maxv) != 0) {
            throw new RuntimeException();
        }

        return Bytes.toArray(result);
    }

    public static void main(String[] args) {
        //String hex = "751e76e8199196d454941c45d1b3a323f1433bd6";
        //
        ////byte[] bytes = Utils.HEX.decode(hex);
        ////for(int bb : bytes){
        ////    System.out.println(bb);
        ////}
        //
        //System.out.println(encode("bc".getBytes(), (byte)0, Utils.HEX.decode(hex)));

        System.out.println(decode("bc1rw5uspcuh"));

    }
}
