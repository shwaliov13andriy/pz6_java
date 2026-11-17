package org.example;

public class ObjectBox {
    private Object item;

    public void put(Object item) {
        this.item = item;
    }

    public Object get() {
        return item;
    }
}