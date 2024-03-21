package com.test.input.Class;

public class DataClass {

    private String kodeQR;
    private String kondisiTabung;
    private String dataImage;
    private String dataDate;
    private String key;
    private String area;

    public DataClass(String KodeQR, String KondisiString, String dataImage, String CurrentTime, String area){
        this.kodeQR = KodeQR;
        this.kondisiTabung = KondisiString;
        this.dataImage = dataImage;
        this.dataDate = CurrentTime;
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public DataClass(){
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getKodeQR() {
        return kodeQR;
    }

    public String getKondisiTabung() {
        return kondisiTabung;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDataDate() {
        return dataDate;
    }
}
