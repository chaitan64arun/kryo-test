package com.worksap.kryotest.helper;

public abstract class MyAbstractClass {
    int myPrimitiveInt = 3;
    public Integer myInt = 6;
    
    @Override
    public String toString() {
        return myPrimitiveInt + "--" + myInt;
    }
}