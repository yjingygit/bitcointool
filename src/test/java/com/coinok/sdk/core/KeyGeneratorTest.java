package com.coinok.sdk.core;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class KeyGeneratorTest {

    public static void main(String[] args) {
        NetworkParameters params = MainNetParams.get();

        KeyGenerator key = new KeyGenerator();

        System.out.println(key.getPrivKeyWif(params));
        System.out.println(key.getAddress(params));
        System.out.println(key.getAddressStr(params));
    }

}
