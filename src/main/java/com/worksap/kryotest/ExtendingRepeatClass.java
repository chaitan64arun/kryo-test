package com.worksap.kryotest;

import com.worksap.kryotest.helper.MyAbstractClass;

public class ExtendingRepeatClass extends MyAbstractClass{
    
    Integer myInt = 190;
    long myPrimitiveLong = 19L;

    @Override
    public String toString() {
        return super.toString()+"--" + myInt + "--" + myPrimitiveLong;
    }
}
