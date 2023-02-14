package com.erenuylar.firebaseapp.model;

public class Post {

    public String uName;
    public String uSname;
    public String downloadUrl;
    public String downloadUrlProfile;
    public String date;
    public String comment;

    public Post(String uName, String uSname, String downloadUrl, String downloadUrlProfile, String date, String comment) {
        this.uName = uName;
        this.uSname = uSname;
        this.downloadUrl = downloadUrl;
        this.downloadUrlProfile = downloadUrlProfile;
        this.date = date;
        this.comment = comment;
    }
}
