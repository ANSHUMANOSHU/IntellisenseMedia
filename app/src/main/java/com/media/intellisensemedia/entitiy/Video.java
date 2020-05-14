package com.media.intellisensemedia.entitiy;

public class Video {

    public String DATA;
    public String DISPLAYNAME;
    public String LENGTH;

    public Video() {
    }

    public Video(String DISPLAYNAME, String LENGTH, String DATA) {
        this.DATA = DATA;
        this.DISPLAYNAME = DISPLAYNAME;
        this.LENGTH = LENGTH;
    }
}
