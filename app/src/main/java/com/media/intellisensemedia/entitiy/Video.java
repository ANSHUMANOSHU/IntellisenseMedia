package com.media.intellisensemedia.entitiy;

public class Video {

    public String DATA;
    public String DISPLAYNAME;
    public String LENGTH;

    // for online
    public String VIEWS;
    public String STAMP;
    public String THUMBNAIL;


    public Video() {
    }

    public Video(String DATA, String DISPLAYNAME, String LENGTH, String VIEWS, String STAMP, String THUMBNAIL) {
        this.DATA = DATA;
        this.DISPLAYNAME = DISPLAYNAME;
        this.LENGTH = LENGTH;
        this.VIEWS = VIEWS;
        this.STAMP = STAMP;
        this.THUMBNAIL = THUMBNAIL;
    }

    public Video(String DISPLAYNAME, String LENGTH, String DATA) {
        this.DATA = DATA;
        this.DISPLAYNAME = DISPLAYNAME;
        this.LENGTH = LENGTH;
    }
}
