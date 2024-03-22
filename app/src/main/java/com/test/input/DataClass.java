package com.test.input;

public class DataClass {

    private String kodeQR;
    private String kondisiTabung;
    private String dataImage;
    private String dataDate;
    private String key;

    private String user;

    public DataClass(String KodeQR, String KondisiString, String dataImage, String CurrentTime, String user){
        this.kodeQR = KodeQR;
        this.kondisiTabung = KondisiString;
        this.dataImage = dataImage;
        this.dataDate = CurrentTime;
        this.user = user;
    }

    public DataClass(){
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
