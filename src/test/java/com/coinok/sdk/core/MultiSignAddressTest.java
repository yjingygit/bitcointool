package com.coinok.sdk.core;

import com.coinok.sdk.header.LiteMainNetParam;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

public class MultiSignAddressTest {

    public static void main(String[] args) {
        showInfo();
        showInfo();
    }

    /**
     * 生成一个“2/2”的地址，并显示对应的私钥和脚本。
     */
    public static void showInfo() {
        MultiSignAddressGenerator generate = new MultiSignAddressGenerator();
        NetworkParameters params = TestNet3Params.get();

        KeyGenerator key1 = new KeyGenerator();
        KeyGenerator key2 = new KeyGenerator();
        KeyGenerator key3 = new KeyGenerator();

        generate.addECKey(key1.getEcKey());
        generate.addECKey(key2.getEcKey());
        generate.addECKey(key3.getEcKey());

        System.out.println("生成的地址： " + generate.generateAddress(params, 3));
        System.out.println("对应的脚本： " + generate.getScriptStr());

        System.out.println("第一把私钥： " + key1.getPrivKeyWif(params));
        System.out.println("第二把私钥： " + key2.getPrivKeyWif(params));
        System.out.println("第二把私钥： " + key3.getPrivKeyWif(params));
    }

    public static void test() {
        MultiSignAddressGenerator generate = new MultiSignAddressGenerator();
        generate.addECKey(new ECKey());
        generate.addECKey(new ECKey());
        generate.addECKey(new ECKey());

        NetworkParameters param1 = MainNetParams.get();
        NetworkParameters param2 = TestNet3Params.get();
        NetworkParameters param3 = LiteMainNetParam.get();

        String addr1 = generate.generateAddress(param1, 3);
        String addr2 = generate.generateAddress(param2, 3);
        String addr3 = generate.generateAddress(param3, 3);

        System.out.println(addr1);
        System.out.println(addr2);
        System.out.println(addr3);
    }

}
