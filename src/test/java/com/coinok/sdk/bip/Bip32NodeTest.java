package com.coinok.sdk.bip;

import org.bitcoinj.core.Utils;

public class Bip32NodeTest {

    public static void main(String[] args) throws Exception {
        Bip32NodeTest test = new Bip32NodeTest();
        test.testVectors1();
        test.testVectors2();
    }

    /**
     * 网站上的示例1。
     */
    public void testVectors1() {
        // Master (hex): 000102030405060708090a0b0c0d0e0f
        byte[] seed = Utils.HEX.decode("000102030405060708090a0b0c0d0e0f");

        // Chain m
        Bip32Node master = Bip32Node.getMasterKey(seed);
        System.out.println("Chain m");
        System.out.println(master.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(master.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        int num = Integer.MAX_VALUE + 1;

        // Chain m/0H
        Bip32Node subNode = master.getChild(0 + num);
        System.out.println("Chain m/0H");
        System.out.println(subNode.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subNode.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0H/1
        Bip32Node subSubNode = subNode.getChild(1);
        System.out.println("Chain m/0H/1");
        System.out.println(subSubNode.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0H/1/2H
        Bip32Node subSubNode2 = subSubNode.getChild(2 + num);
        System.out.println("Chain m/0H/1/2H");
        System.out.println(subSubNode2.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode2.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0H/1/2H/2
        Bip32Node subSubNode22 = subSubNode2.getChild(2);
        System.out.println("Chain m/0H/1/2H/2");
        System.out.println(subSubNode22.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode22.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0H/1/2H/2/1000000000
        Bip32Node subSubNode221000000000 = subSubNode22.getChild(1000000000);
        System.out.println("Chain m/0H/1/2H/2/1000000000");
        System.out.println(subSubNode221000000000.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode221000000000.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
    }

    /**
     * 网站上的示例2。
     */
    public void testVectors2() {
        // Master (hex):
        // fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542
        byte[] seed =
            Utils.HEX
                .decode(
                    "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542");

        // Chain m
        Bip32Node master = Bip32Node.getMasterKey(seed);
        System.out.println("Chain m");
        System.out.println(master.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(master.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        int num = Integer.MAX_VALUE + 1;

        // Chain m/0
        Bip32Node subNode = master.getChild(0);
        System.out.println("Chain m/0");
        System.out.println(subNode.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subNode.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0/2147483647H
        Bip32Node subSubNode = subNode.getChild(2147483647 + num);
        System.out.println("Chain m/0/2147483647H");
        System.out.println(subSubNode.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0/2147483647H/1
        Bip32Node subSubNode2 = subSubNode.getChild(1);
        System.out.println("Chain m/0/2147483647H/1");
        System.out.println(subSubNode2.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode2.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0/2147483647H/1/2147483646H
        Bip32Node subSubNode22 = subSubNode2.getChild(2147483646 + num);
        System.out.println("Chain m/0/2147483647H/1/2147483646H");
        System.out.println(subSubNode22.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode22.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println();

        // Chain m/0/2147483647H/1/2147483646H/2
        Bip32Node subSubNode222 = subSubNode22.getChild(2);
        System.out.println("Chain m/0/2147483647H/1/2147483646H/2");
        System.out.println(subSubNode222.privSerialize(Bip32Node.TYPE_BITCOIN, true));
        System.out.println(subSubNode222.pubSerialize(Bip32Node.TYPE_BITCOIN, true));
    }
}
