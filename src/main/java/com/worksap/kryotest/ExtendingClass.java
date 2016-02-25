package com.worksap.kryotest;

import com.worksap.kryotest.helper.MyAbstractClass;

public class ExtendingClass extends MyAbstractClass{
    
    Integer myNewInt = 17;
    long myPrimitiveLong = 170L;

    @Override
    public String toString() {
        return super.toString()+"--" + myNewInt + "--" + myPrimitiveLong;
    }
    
    public void changeValues(){
        super.changeValues();
        myNewInt = 19923;
        myPrimitiveLong = 2324234L;
    }
}
