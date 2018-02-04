package com.coinok.sdk.crypto;

import com.coinok.sdk.core.KeyGenerator;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class AESForCryptoJSTest {

    public static void main(String[] args) throws Exception {
        encryptByWIF();
    }

    /**
     * 使用私钥加解密私钥的测试。
     */
    public static void encryptByWIF() throws Exception {
        KeyGenerator vaultKeyG = new KeyGenerator();
        KeyGenerator pwdG = new KeyGenerator();
        NetworkParameters params = MainNetParams.get();
        String vaultKey = vaultKeyG.getPrivKeyWif(params);
        String pwd = pwdG.getPrivKeyWif(params);

        System.out.println("保险柜私钥： " + vaultKey);
        System.out.println("加密用私钥： " + pwd);

        String encrypted = AESForCryptoJS.encryptToBase64(vaultKey, pwd);
        System.out.println("加密后内容： " + encrypted);

        String data = AESForCryptoJS.decrypt(encrypted, pwd);
        System.out.println("反解后内容： " + data);
    }
}
