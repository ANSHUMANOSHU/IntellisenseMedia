package com.media.intellisensemedia.EntityClasses;

public class Video {

    public String DATA;
    public String DISPLAYNAME;
    public String LENGTH;
    public boolean STATUS_ADDED = false;

    public Video() {
    }

    public Video(String DISPLAYNAME, String LENGTH, String DATA) {
        this.DATA = DATA;
        this.DISPLAYNAME = DISPLAYNAME;
        this.LENGTH = LENGTH;
    }
}
