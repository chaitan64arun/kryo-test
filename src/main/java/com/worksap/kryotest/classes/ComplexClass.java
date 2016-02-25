package com.worksap.kryotest.classes;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class ComplexClass<T> {

    int number = 1090;

    CollectionClass collectionClass;

    GenericClass<T> genericClass;

    ImmutablePair<Integer, T> immutablePair;

    T value;

    @Override
    public String toString() {
        return number + "##" + collectionClass.toString() + "##" + immutablePair.toString() + "##" + value.toString();
    }

    public void changeValues(int number, CollectionClass collectionClass, GenericClass<T> genericClass, ImmutablePair<Integer, T> immutablePair, T value) {
        this.number = number;
        this.collectionClass = collectionClass;
        this.genericClass = genericClass;
        this.immutablePair = immutablePair;
        this.value = value;
    }
}
