package com.worksap.kryotest.classes;

import com.worksap.kryotest.helper.MyInterface;

public class MyImplementingClass implements MyInterface {

    Integer myInt = 170;
    long myPrimitiveLong = 170L;

    @Override
    public String toString() {
        return myInt + "--" + myPrimitiveLong;
    }

    public void changeValues() {
        myInt = 6792323;
        myPrimitiveLong = 23265434L;
    }
}
