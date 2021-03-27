package com.coinok.sdk.core;

import com.coinok.sdk.util.Tools;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;

import java.util.Arrays;

/**
 * 用于生成、解析私钥的公共类。
 *
 * @author Jingyu Yang
 */
public class KeyGenerator {

    /**
     * 公、私钥对。
     */
    private ECKey ecKey;

    /**
     * 是否压缩格式。
     */
    private boolean compressed;

    /**
     * 随机生成一个新的私钥对。
     */
    public KeyGenerator() {
        this.ecKey = new ECKey();
        this.compressed = true;
    }

    /**
     * 逐个设定参数获取一个新对象。
     *
     * @param eckey：
     * @param compressed：
     */
    public KeyGenerator(ECKey eckey, boolean compressed) {
        this.ecKey = eckey;
        this.compressed = compressed;
    }

    /**
     * 从一个WIF格式的私钥解析出对应的数据。
     *
     * @param keyWif
     * @return
     */
    public static KeyGenerator fromPrivateKeyWif(String keyWif) {
        byte[] keyArr;
        try {
            keyArr = Base58.decode(keyWif);
        } catch (AddressFormatException e) {
            throw new IllegalArgumentException("Base58解码失败，错误的私钥字符串！");
        }
        int len = keyArr.length;

        if (!Tools.check(keyArr)) {
            throw new IllegalArgumentException("非法的私钥字符串，校验位错误！");
        }

        boolean compressed;
        byte[] data;
        if (keyArr.length == 33 + 1 + 4 && keyArr[33] == (byte) 1) {
            data = Arrays.copyOfRange(keyArr, 1, len - 5);
            compressed = true;
        } else if (keyArr.length == 32 + 1 + 4) {
            data = Arrays.copyOfRange(keyArr, 1, len - 4);
            compressed = false;
        } else {
            throw new IllegalArgumentException("Wrong number of bytes for a private key, not 32 or 33");
        }

        int a = ((int) data[0]) & 0x80;
        if (a > 0) {
            byte[] temp = new byte[data.length + 1];
            temp[0] = (byte) 0;

            System.arraycopy(data, 0, temp, 1, data.length);
            data = temp;
        }

        ECKey ecKey = ECKey.fromPrivate(data, compressed);
        return new KeyGenerator(ecKey, compressed);
    }

    /**
     * 根据给定的网络类型，获取WIF格式的私钥。
     *
     * @return
     */
    public String getPrivateKeyWif(NetworkParameters params) {
        byte[] bytes;
        byte[] privByteArr = Utils.bigIntegerToBytes(ecKey.getPrivKey(), 32);
        if (compressed) {
            bytes = new byte[33];
            System.arraycopy(privByteArr, 0, bytes, 0, 32);
            bytes[32] = 1;
        } else {
            bytes = privByteArr;
        }

        return Tools.byteToString((byte) params.getDumpedPrivateKeyHeader(), bytes);
    }

    /**
     * 根据给定的网络类型，获取对应的地址。
     *
     * @param params
     * @return
     */
    public Address getAddress(NetworkParameters params) {
        return Address.fromKey(params, ecKey, Script.ScriptType.P2SH);
    }

    /**
     * 根据给定的网络类型，获取对应的地址。
     *
     * @param params
     * @return
     */
    public String getAddressStr(NetworkParameters params) {
        return Tools.byteToString((byte) params.getAddressHeader(), ecKey.getPubKeyHash());
    }

    /**
     * 获取16进制编码的公钥字符串。
     *
     * @return
     */
    public String getPublicKeyHex() {
        return Utils.HEX.encode(this.ecKey.getPubKey());
    }

    public ECKey getEcKey() {
        return ecKey;
    }

    public boolean isCompressed() {
        return compressed;
    }
}
