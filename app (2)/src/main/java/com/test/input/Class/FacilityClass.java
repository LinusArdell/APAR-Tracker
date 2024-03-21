package com.test.input.Class;

public class FacilityClass {
    private String area;
    private String alamat;
    private String key;

    public FacilityClass(String area, String alamat){
        this.area = area;
        this.alamat = alamat;
    }

    public FacilityClass(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getArea() {
        return area;
    }

    public String getAlamat() {
        return alamat;
    }
}
