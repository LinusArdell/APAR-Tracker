package com.test.input.Class;

public class VersionClass {
    private String link;
    private String keterangan;
    private String versi;
    private String tanggal, requirement;

    public VersionClass() {
        // Default constructor required for calls to DataSnapshot.getValue(File.class)
    }

    public VersionClass(String link, String keterangan, String versi, String tanggal, String requirement) {
        this.link = link;
        this.keterangan = keterangan;
        this.versi = versi;
        this.tanggal = tanggal;
        this.requirement = requirement;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getVersi() {
        return versi;
    }

    public void setVersi(String versi) {
        this.versi = versi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
