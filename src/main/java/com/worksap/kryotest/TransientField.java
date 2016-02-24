package com.worksap.kryotest;

public class TransientField {
    int myInt = 190;
    transient int transientInt = 12;

    @Override
    public String toString() {
        return myInt + "--" + transientInt;
    }
}
