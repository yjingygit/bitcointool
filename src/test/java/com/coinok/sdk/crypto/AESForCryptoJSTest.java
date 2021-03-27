package com.coinok.sdk.crypto;

import com.coinok.sdk.core.KeyGenerator;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class AESForCryptoJSTest {

    public static void main(String[] args) throws Exception {

        String str = "U2FsdGVkX1/MzVY8O3rRTG8GDXnksGX+aPo0GSvwlH23I+Ee8OlIXyz1ERUuAvu1NeTH8ZUhl1dR4V55yxUHo0359uyu4qvLcKU0qs7KRtcK+4oD8QYs8Y42bh2zOLmJ";
        String pwd = "d870e50d67062b12071246aee93fdd967823edd59ad3e007f2154bc47123f332be";

        String result = AESForCryptoJS.decrypt(str, pwd);
        System.out.println(result);


//        encryptByWIF();
    }

    /**
     * 使用私钥加解密私钥的测试。
     */
    public static void encryptByWIF() throws Exception {
        KeyGenerator vaultKeyG = new KeyGenerator();
        KeyGenerator pwdG = new KeyGenerator();
        NetworkParameters params = MainNetParams.get();
        String vaultKey = vaultKeyG.getPrivateKeyWif(params);
        String pwd = pwdG.getPrivateKeyWif(params);

        System.out.println("保险柜私钥： " + vaultKey);
        System.out.println("加密用私钥： " + pwd);

        String encrypted = AESForCryptoJS.encryptToBase64(vaultKey, pwd);
        System.out.println("加密后内容： " + encrypted);

        String data = AESForCryptoJS.decrypt(encrypted, pwd);
        System.out.println("反解后内容： " + data);
    }
}
