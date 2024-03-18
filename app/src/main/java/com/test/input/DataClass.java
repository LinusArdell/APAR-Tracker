package com.test.input;

public class DataClass {

    private String KodeQR;
    private String KondisiTabung;
    private String dataImage;

    public DataClass(String kodeQR, String kondisiString, String dataImage){
        this.KodeQR = kodeQR;
        this.KondisiTabung = kondisiString;
        this.dataImage = dataImage;
    }

    public String getKodeQR() {
        return KodeQR;
    }

    public String getKondisiTabung() {
        return KondisiTabung;
    }

    public String getDataImage() {
        return dataImage;
    }
}
