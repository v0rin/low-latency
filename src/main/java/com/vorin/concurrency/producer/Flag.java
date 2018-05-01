package com.vorin.concurrency.producer;

/**
 *
 * @author Adam
 */
public class Flag {

    boolean isSet;

    public Flag(boolean state) {
        this.isSet = state;
    }

    public boolean isSet() {
        return isSet;
    }

    public void set() {
        isSet = true;
    }

    public void unset() {
        isSet = false;
    }

}
