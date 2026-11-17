package org.example;

public class TitleOnlyEquals {
    private int id;
    private String name;
    private int year;

    public TitleOnlyEquals(int id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleOnlyEquals that = (TitleOnlyEquals) o;
        return id == that.id;
    }
}