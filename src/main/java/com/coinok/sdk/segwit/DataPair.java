package com.coinok.sdk.segwit;

/**
 * to delivery the Base32 params and result.
 *
 * @author Jingyu Yang
 */
public class DataPair<T, P> {

    /**
     * human readable part.
     */
    private T hrp;

    /**
     * data part.
     */
    private P data;

    public DataPair() {
        super();
    }

    public DataPair(T hrp, P data) {
        super();
        this.hrp = hrp;
        this.data = data;
    }

    public T getHrp() {
        return hrp;
    }

    public void setHrp(T hrp) {
        this.hrp = hrp;
    }

    public P getData() {
        return data;
    }

    public void setData(P data) {
        this.data = data;
    }
}
