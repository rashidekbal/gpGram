package com.rtech.gpgram.models;

import android.net.Uri;

import androidx.annotation.Nullable;

public class MediaModel {
    public Uri uri;
    public boolean isVideo;
    public int duration;
    public boolean isCameraIntent;

    public MediaModel(Uri uri, boolean isVideo, int duration,boolean isCameraIntent){
        this.uri=uri;
        this.isVideo=isVideo;
        this.duration=duration;
        this.isCameraIntent=isCameraIntent;


    }
}
