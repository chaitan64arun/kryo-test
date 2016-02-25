package com.worksap.kryotest.classes;

import java.util.Arrays;
import java.util.List;

public class CollectionClass {

    List<Integer> integers = Arrays.asList(1, 2, 3, 4);

    List<Object> objects = null;

    List<?> generics = null;

    @Override
    public String toString() {
        return integers.toString() + "####" + objects.toString() + "####" + generics.toString();
    }

    public void changeValues(List<Integer> integers, List<Object> objects, List<?> generics) {
        this.integers = integers;
        this.generics = generics;
        this.objects = objects;
    }
}
