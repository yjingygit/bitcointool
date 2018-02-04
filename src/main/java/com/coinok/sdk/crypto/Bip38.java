package com.coinok.sdk.crypto;

import com.coinok.sdk.core.KeyGenerator;
import com.coinok.sdk.header.HeadInfo;
import com.coinok.sdk.util.Tools;
import com.lambdaworks.crypto.SCrypt;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.BIP38PrivateKey;
import org.spongycastle.util.Arrays;

/**
 * BIP38对应的实现： https://github.com/bitcoin/bips/blob/master/bip-0038.mediawiki
 *
 * 一种使用密码加密私钥的规范。
 *
 * @author Jingyu Yang
 */
public class Bip38 {

    private static final String ALGORITHM = "AES/ECB/Pkcs7Padding";

    /**
     * 根据passphrase将私钥转为Bip38格式。
     *
     * @param privateKey
     * @param passphrase
     * @return
     * @throws Exception
     */
    public static String encryptToBip38(String privateKey, String passphrase) throws Exception {
        byte[] privateByte = Base58.decode(privateKey);

        HeadInfo head = HeadInfo.getInfoByByte(privateByte[0]);
        if (head == null) {
            throw new IllegalArgumentException("Support btc, ltc, testnet only!");
        }
        KeyGenerator gene = KeyGenerator.fromPrivkeyWif(privateKey);
        Address address = gene.getAddress(head.getParam());

        privateByte = Arrays.copyOfRange(privateByte, 1, privateByte.length - 4);

        // 1 addresshash = SHA256(SHA256(address))
        byte[] addressHash = DigestHash.sha256X2(address.toString().getBytes());
        if (addressHash == null || addressHash.length == 0) {
            throw new RuntimeException("Get address hash fail, may be SHA-256 digest is not supported!");
        }
        byte[] salt = Arrays.copyOfRange(addressHash, 0, 4);

        // 2 Derive a key from the passphrase using scrypt
        int n = 16384, r = 8, p = 8, length = 64;
        byte[] key = SCrypt.scrypt(passphrase.getBytes(), salt, n, r, p, length);

        byte[] derivedHalf1 = Arrays.copyOfRange(key, 0, 32);
        byte[] derivedHalf2 = Arrays.copyOfRange(key, 32, 64);

        // 3
        byte[] block1 = Tools.xor(Arrays.copyOfRange(privateByte, 0, 16), Arrays.copyOfRange(derivedHalf1, 0, 16));
        byte[] encryptedHalf1 = AES.encrypt(block1, derivedHalf2, null, ALGORITHM);

        byte[] block2 = Tools.xor(Arrays.copyOfRange(privateByte, 16, 32), Arrays.copyOfRange(derivedHalf1, 16, 32));
        byte[] encryptedHalf2 = AES.encrypt(block2, derivedHalf2, null, ALGORITHM);

        byte isCompress = (byte)((privateByte.length == 33 && privateByte[32] == 1) ? 0xe0 : 0xc0);

        byte[] result = new byte[39 + 4];
        result[0] = (byte)0x01;
        result[1] = (byte)0x42;
        result[2] = isCompress;

        System.arraycopy(salt, 0, result, 3, 4);
        System.arraycopy(encryptedHalf1, 0, result, 7, 16);
        System.arraycopy(encryptedHalf2, 0, result, 23, 16);

        // add checkSum
        byte[] checkSum = DigestHash.sha256X2(Arrays.copyOfRange(result, 0, 39));
        if (checkSum == null || checkSum.length == 0) {
            throw new RuntimeException("Get checkSum fail, may be SHA-256 digest is not supported!");
        }
        System.arraycopy(checkSum, 0, result, 39, 4);

        return Base58.encode(result);
    }

    /**
     * 使用passphrase解码一个Bip38格式的私钥。
     *
     * @param bip38String
     * @param passphrase
     * @param param
     * @return
     * @throws Exception
     */
    public static String decode(String bip38String, String passphrase, NetworkParameters param)
        throws Exception {
        BIP38PrivateKey bip38Key = BIP38PrivateKey.fromBase58(param, bip38String);
        return bip38Key.decrypt(passphrase).getPrivateKeyEncoded(param).toString();
    }
}
