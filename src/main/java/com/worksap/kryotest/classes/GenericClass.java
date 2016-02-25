package com.worksap.kryotest.classes;

public class GenericClass<T> {

    T value;

    @Override
    public String toString() {
        return value.toString();
    }

    public void changeValues(T recent) {
        value = recent;
    }
}
