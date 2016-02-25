package com.worksap.kryotest;

public class StaticField {
    int myInt = 190;
    static int staticInt = 12;

    @Override
    public String toString() {
        return myInt + "--" + staticInt;
    }
    
    public void changeValues(){
        myInt = 1992323;
        staticInt = 232423;
    }
}
