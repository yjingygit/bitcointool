package com.coinok.sdk.core;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class MSAGTest {

    public static void main(String[] args) {
        MultiSignAddressGenerator builder = new MultiSignAddressGenerator();

        for (int i = 0; i < 10; i++) {
            ECKey key = new ECKey();
            builder.addECKey(key);
        }

        NetworkParameters params = MainNetParams.get();
        String address = builder.generateAddress(params, 8);

        System.out.println(address);
        System.out.println(builder.getScriptStr());
    }

}
