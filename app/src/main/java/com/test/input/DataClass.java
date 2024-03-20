package com.test.input;

public class DataClass {

    private String kodeQR;
    private String kondisiTabung;
    private String dataImage;
    private String dataDate;
    private String key;

    public DataClass(String KodeQR, String KondisiString, String dataImage, String CurrentTime){
        this.kodeQR = KodeQR;
        this.kondisiTabung = KondisiString;
        this.dataImage = dataImage;
        this.dataDate = CurrentTime;
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
