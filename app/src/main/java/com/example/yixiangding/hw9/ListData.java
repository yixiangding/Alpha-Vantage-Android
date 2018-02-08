package com.example.yixiangding.hw9;


/**
    Sealing of JSON data for ListView to use
 */
public class ListData {
    private String name;
    private String value;

    public ListData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
