package io.github.chaitan64arun.classes;

public class TransientField {
    int myInt = 190;
    transient int transientInt = 12;

    @Override
    public String toString() {
        return myInt + "--" + transientInt;
    }

    public void changeValues() {
        myInt = 199423;
        transientInt = 932423;
    }
}
