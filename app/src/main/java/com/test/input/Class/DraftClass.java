package com.test.input.Class;

import android.net.Uri;

public class DraftClass {
    private String kodeQR;
    private String dataImage;
    private String dataDate;
    private String key, satuanBerat;
    private String user;
    private String lokasiTabung, beratTabung, merkAPAR, jenisAPAR, keterangan, kondisiNozzle, posisiTabung;
    private String isiTabung, tekananTabung, kesesuaianBerat, kondisiTabung, kondisiSelang, kondisiPin;

    public DraftClass(String kodeQR,
                            String lokasiTabung,
                            String merkAPAR,
                            String beratTabung,
                            String jenisAPAR,
                            String isiTabung,
                            String tekananTabung,
                            String kesesuaianBerat,
                            String kondisiTabung,
                            String kondisiSelang,
                            String kondisiPin,
                            String keterangan,
                            String dataImage,
                            String dataDate,
                            String user,
                            String kondisiNozzle,
                            String posisiTabung,
                            String satuanBerat){

        this.kodeQR = kodeQR;
        this.lokasiTabung = lokasiTabung;
        this.merkAPAR = merkAPAR;
        this.beratTabung = beratTabung;
        this.jenisAPAR = jenisAPAR;
        this.isiTabung = isiTabung;
        this.tekananTabung = tekananTabung;
        this.kesesuaianBerat = kesesuaianBerat;
        this.kondisiTabung = kondisiTabung;
        this.kondisiSelang = kondisiSelang;
        this.kondisiPin = kondisiPin;
        this.keterangan = keterangan;
        this.dataDate = dataDate;
        this.dataImage = dataImage;
        this.kondisiNozzle = kondisiNozzle;
        this.posisiTabung = posisiTabung;
        this.satuanBerat = satuanBerat;
        this.user = user;
    }

    public String getKondisiNozzle() {
        return kondisiNozzle;
    }

    public String getPosisiTabung() {
        return posisiTabung;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getLokasiTabung() {
        return lokasiTabung;
    }

    public String getBeratTabung() {
        return beratTabung;
    }

    public String getMerkAPAR() {
        return merkAPAR;
    }

    public String getJenisAPAR() {
        return jenisAPAR;
    }

    public String getIsiTabung() {
        return isiTabung;
    }

    public String getTekananTabung() {
        return tekananTabung;
    }

    public String getKesesuaianBerat() {
        return kesesuaianBerat;
    }

    public String getKondisiSelang() {
        return kondisiSelang;
    }

    public String getKondisiPin() {
        return kondisiPin;
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

    public String getSatuanBerat() {
        return satuanBerat;
    }
}
