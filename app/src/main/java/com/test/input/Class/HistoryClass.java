package com.test.input.Class;

public class HistoryClass {
    private String kodeQR;
    private String dataImage;
    private String dataDate;
    private String key;
    private String user, satuanBerat;
    private Integer beratTabung;

    private String lokasiTabung, merkAPAR, jenisAPAR, keterangan;
    private Boolean kondisiTabung , posisiTabung, kondisiSelang, kondisiPin, kondisiNozzle, tekananTabung, isiTabung, kesesuaianBerat;

    public HistoryClass (String kodeQR,
                         String lokasiTabung,
                         String merkAPAR,
                         Integer beratTabung,
                         String jenisAPAR,
                         Boolean isiTabung,
                         Boolean Tekanan,
                         Boolean kesesuaianBerat,
                         Boolean kondisiTabung,
                         Boolean kondisiSelang,
                         Boolean kondisiPin,
                         String keterangan,
                         String dataImage,
                         String dataDate,
                         String user,
                         Boolean kondisiNozzle,
                         Boolean posisiTabung,
                         String satuanBerat){ //String signatureUrl

        this.kodeQR = kodeQR;
        this.lokasiTabung = lokasiTabung;
        this.merkAPAR = merkAPAR;
        this.beratTabung = beratTabung;
        this.jenisAPAR = jenisAPAR;
        this.isiTabung = isiTabung;
        this.tekananTabung = Tekanan;
        this.kesesuaianBerat = kesesuaianBerat;
        this.kondisiTabung = kondisiTabung;
        this.kondisiSelang = kondisiSelang;
        this.kondisiPin = kondisiPin;
        this.keterangan = keterangan;
        this.dataDate = dataDate;
        this.dataImage = dataImage;
        this.kondisiNozzle = kondisiNozzle;
        this.posisiTabung = posisiTabung; //   this.signatureUrl = signatureUrl;
        this.user = user;
        this.satuanBerat = satuanBerat;
    }

    public HistoryClass(){
    }

    public Boolean getKondisiNozzle() {
        return kondisiNozzle;
    }

    public Boolean getPosisiTabung() {
        return posisiTabung;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getLokasiTabung() {
        return lokasiTabung;
    }

    public Integer getBeratTabung() {
        return beratTabung;
    }

    public String getMerkAPAR() {
        return merkAPAR;
    }

    public String getJenisAPAR() {
        return jenisAPAR;
    }

    public Boolean getIsiTabung() {
        return isiTabung;
    }

    public Boolean getTekananTabung() {
        return tekananTabung;
    }

    public Boolean getKesesuaianBerat() {
        return kesesuaianBerat;
    }

    public Boolean getKondisiSelang() {
        return kondisiSelang;
    }

    public Boolean getKondisiPin() {
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

    public Boolean getKondisiTabung() {
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
