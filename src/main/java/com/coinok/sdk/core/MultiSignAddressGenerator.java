package com.coinok.sdk.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;

/**
 * 多签地址生成类。
 *
 * @author Jingyu Yang
 */
public class MultiSignAddressGenerator {

    /**
     * 生成多签地址需要的公钥列表。
     */
    private List<ECKey> ecKeyList = new ArrayList<ECKey>();

    /**
     * 多签地址的脚本。
     */
    private Script redeemScript;

    private int minSignNum;

    /**
     * 添加一个公钥。
     *
     * @param pubKey
     * @throws Exception
     */
    public void addECKey(ECKey pubKey) {
        if (pubKey == null || this.ecKeyList.size() >= 16) {
            throw new IllegalArgumentException("最多只能添加16个非空公钥！");
        }
        this.ecKeyList.add(pubKey);
        this.redeemScript = null;
        this.minSignNum = 0;
    }

    /**
     * 替换队列中指定位置的公钥。只能替换已经添加的公钥。
     *
     * @param index
     * @param pubKey
     * @return
     */
    public boolean setECKey(int index, ECKey pubKey) {
        if (index < 0 || index > this.ecKeyList.size() - 1 || pubKey == null) {
            return false;
        }
        this.ecKeyList.set(index, pubKey);
        this.redeemScript = null;
        this.minSignNum = 0;
        return true;
    }

    /**
     * 根据传入的公钥，构建指定类型网络的多签地址。
     *
     * @param params
     * @param minSignNum
     * @return
     * @throws IllegalArgumentException
     */
    public String generateAddress(NetworkParameters params, int minSignNum) {
        int size = this.ecKeyList.size();

        if (size < 2) {
            throw new IllegalArgumentException("添加的公钥数量不足！");
        }
        if (minSignNum < 1) {
            throw new IllegalArgumentException("生成的地址最少需要一个签名！");
        }
        if (minSignNum > size) {
            minSignNum = size;
        }

        this.redeemScript = ScriptBuilder.createMultiSigOutputScript(minSignNum, ecKeyList);
        Script p2shScript = ScriptBuilder.createP2SHOutputScript(redeemScript);
        this.minSignNum = minSignNum;
        return LegacyAddress.fromScriptHash(params, ScriptPattern.extractHashFromP2SH(p2shScript)).toBase58();
    }

    /**
     * 获取签名脚本。
     *
     * @return
     */
    public Script getRedeemScript() {
        return this.redeemScript;
    }

    /**
     * 获取16进制编码的签名脚本。
     *
     * @return
     * @throws Exception
     */
    public String getScriptStr() {
        if (this.redeemScript == null) {
            return null;
        }
        return Utils.HEX.encode(redeemScript.getProgram());
    }

    /**
     * 获取生成多签地址的公钥列表。
     *
     * @return
     */
    public List<ECKey> getEcKeyList() {
        return ecKeyList;
    }

    /**
     * 设置公钥列表。
     *
     * @param ecKeyList： 不能为空，且会将null元素剔除。
     */
    public void setEcKeyList(List<ECKey> ecKeyList) {
        if (ecKeyList == null) {
            return;
        }

        // 列表非空时，剔除null元素。
        if (ecKeyList.size() != 0) {
            ecKeyList.removeIf(Objects::isNull);
        }

        this.ecKeyList = ecKeyList;
        this.redeemScript = null;
        this.minSignNum = 0;
    }

    /**
     * 返回生成的多签地址签名所需的最少数量。
     *
     * @return
     */
    public int getMinSignNum() {
        return minSignNum;
    }

    /**
     * 返回生成多签地址的公钥的总数量。
     *
     * @return
     */
    public int getMaxSignNum() {
        return this.ecKeyList.size();
    }
}
