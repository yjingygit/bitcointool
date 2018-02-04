package com.coinok.sdk.crypto;

import com.coinok.sdk.core.KeyGenerator;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class Bip38Test {

    public static void main(String[] args) throws Exception {
        defaultTest();
        encryptedByWIF();
    }

    public static void defaultTest() throws Exception {
        String privateKey = "5KN7MzqK5wt2TP1fQCYyHBtDrXdJuXbUzm4A9rKAteGu3Qi5CVR";
        String passphrase = "TestingOneTwoThree";

        long time1 = System.currentTimeMillis();
        String result = Bip38.encryptToBip38(privateKey, passphrase);
        long time2 = System.currentTimeMillis() - time1;

        System.out.println(time2);
        System.out.println(result);

        String encoded = "6PRVWUbkzzsbcVac2qwfssoUJAN1Xhrg6bNk8J7Nzm5H7kxEbn2Nh2ZoGg";
        NetworkParameters param = MainNetParams.get();
        String key = Bip38.decode(encoded, passphrase, param);
        System.out.println(key);
    }

    /**
     * 使用私钥的WIF格式作为密码。
     *
     * @throws Exception
     */
    public static void encryptedByWIF() throws Exception {
        String privateKey = "5KN7MzqK5wt2TP1fQCYyHBtDrXdJuXbUzm4A9rKAteGu3Qi5CVR";
        String passphrase = new KeyGenerator().getPrivKeyWif(MainNetParams.get());
        long time1 = System.currentTimeMillis();
        String result = Bip38.encryptToBip38(privateKey, passphrase);
        long time2 = System.currentTimeMillis() - time1;

        System.out.println(time2);
        System.out.println(result);
    }
}
