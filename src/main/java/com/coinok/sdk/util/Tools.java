package com.coinok.sdk.util;

import com.coinok.sdk.crypto.DigestHash;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供一些方便使用的工具方法。
 *
 * @author Jingyu Yang
 */
public class Tools {

    /**
     * 将输入的两个字符数组进行异或。
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public static byte[] xor(byte[] arr1, byte[] arr2) {
        if (arr1 == null || arr2 == null) {
            throw new IllegalArgumentException("两个字符数组都不能为空！");
        }

        if (arr1.length != arr2.length) {
            return null;
        }
        byte[] result = new byte[arr1.length];
        for (int i = 0, len = arr1.length; i < len; i++) {
            result[i] = (byte) (arr1[i] ^ arr2[i]);
        }
        return result;
    }

    /**
     * 对输入进行校验。
     * <p>
     * 校验规则： sha256X2(input[0, length - 4])和input[length - 4, length]进行比较。
     *
     * @param input
     * @return
     */
    public static boolean check(byte[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("传入的字符串为空！");
        }

        int len = input.length;
        // 待校验数据。
        byte[] toCheck = Arrays.copyOfRange(input, 0, len - 4);
        byte[] checkCode = DigestHash.sha256X2(toCheck);
        if (checkCode == null || checkCode.length == 0) {
            throw new RuntimeException("Sha256 run fail!");
        }

        // 四位校验位。
        byte[] checkSum = Arrays.copyOfRange(input, len - 4, len);
        return Arrays.equals(checkSum, Arrays.copyOfRange(checkCode, 0, 4));
    }

    /**
     * 返回Base58(version + input + checkSum)
     * <p>
     * 其中checkSum = sha256(sha256(version + input));
     *
     * @param version 对应NetworkParameters中的“addressHeader”或“P2SHHeader”。
     * @param input   对应公钥或私钥的字符数组。
     * @return
     */
    public static String byteToString(byte version, byte[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("传入的字符串为空！");
        }

        int length = input.length;

        // 地址由3部分组成：version + pubKeyHash + checkSum。
        byte[] address = new byte[1 + length];
        // 加入version。
        address[0] = version;
        // 加入pubKeyHash部分，此时数组中的数据为keyHash（version + pubKeyHash）部分。
        System.arraycopy(input, 0, address, 1, length);

        return byteToString(address);
    }

    /**
     * 返回Base58(input + checkSum)
     * <p>
     * 其中checkSum = sha256(sha256(input));
     *
     * @param input
     * @return
     */
    public static String byteToString(byte[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("传入的字符串为空！");
        }

        int length = input.length;

        // 地址由3部分组成：version + pubKeyHash + checkSum。
        byte[] result = new byte[length + 4];
        // 加入pubKeyHash部分，此时数组中的数据为keyHash（version + pubKeyHash）部分。
        System.arraycopy(input, 0, result, 0, length);

        // 对keyHash连续进行两次sha256加密。
        byte[] checkSum = DigestHash.sha256X2(input);
        // 加入checkSum的前4位。
        System.arraycopy(checkSum, 0, result, length, 4);

        return Base58.encode(result);
    }

    /**
     * 根据输入的Hex形式的公钥字符串和网络类型，获取相应的地址字符串。
     *
     * @param pubKeyHex： Hex形式的公钥字符串
     * @param params：    网络类型
     * @return
     */
    public static String pubKeyHexToAddress(String pubKeyHex, NetworkParameters params) {
        if (pubKeyHex == null || params == null) {
            throw new IllegalArgumentException("公钥字符串和网络类型都不能为空！");
        }

        Pattern pattern = Pattern.compile("[0-9a-fA-F]{66}");
        Matcher matcher = pattern.matcher(pubKeyHex);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("错误的公钥字符串！");
        }

        byte[] pubKey = Utils.HEX.decode(pubKeyHex);
        ECKey key = ECKey.fromPublicOnly(pubKey);
        Address address = Address.fromKey(params, key, Script.ScriptType.P2SH);
        return address.toString();
    }

    /**
     * 根据ECKey和网络类型获取对应的地址字符串。
     *
     * @param ecKey
     * @param params
     * @return
     */
    public static String ecKeyToAddress(ECKey ecKey, NetworkParameters params) {
        if (ecKey == null || params == null) {
            throw new IllegalArgumentException("eckey和网络类型两个参数都不能为空！");
        }

        byte[] pubKey = ecKey.getPubKeyHash();
        return Tools.byteToString((byte) params.getAddressHeader(), pubKey);
    }

    /**
     * 使用HmacSHA512计算data的哈希值。
     *
     * @param data
     * @param keySeed
     * @return
     */
    public static byte[] hmacSha512(byte[] data, byte[] keySeed) {
        if (data == null || keySeed == null) {
            throw new IllegalArgumentException("数据和密钥种子都不能为空！");
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKey key = new SecretKeySpec(keySeed, "HmacSHA512");
            mac.init(key);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
