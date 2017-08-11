/**
 * Copyright (C) 2017 Mustafa Kabaktepe
 */

package com.myown.project.stage1movieapp.model;

/**
 * Movie class represents a review object.
 */
public class Video {
    private String mId;
    String mIso_639_1;
    String mIso_3166_1;
    private String mKey;
    private String mName;
    private String mSite;
    int mSize;
    private String mType;

    public Video() {

    }

    public Video(String id, String iso_639_1, String iso_3166_1, String key,
                 String name, String site, int size, String type) {
        mId = id;
        mIso_639_1 = iso_639_1;
        mIso_3166_1 = iso_3166_1;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String mSite) {
        this.mSite = mSite;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }
}
