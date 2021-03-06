package io.github.chaitan64arun.classes;

import io.github.chaitan64arun.helper.MyAbstractClass;

public class ExtendingRepeatClass extends MyAbstractClass {

    Integer myInt = 190;
    long myPrimitiveLong = 19L;

    @Override
    public String toString() {
        return super.toString() + "--" + myInt + "--" + myPrimitiveLong;
    }

    public void changeValues() {
        super.changeValues();
        myInt = 1992323;
        myPrimitiveLong = 2324234L;
    }
}
