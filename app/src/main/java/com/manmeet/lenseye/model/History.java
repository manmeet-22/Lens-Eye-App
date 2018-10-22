package com.manmeet.lenseye.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class History {
    private Bitmap historyImage;
    private ArrayList<Result> historyList;

    public History(Bitmap historyImage, ArrayList<Result> historyList) {
        this.historyImage = historyImage;
        this.historyList = historyList;
    }

    public ArrayList<Result> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(ArrayList<Result> historyList) {
        this.historyList = historyList;
    }

    public Bitmap getHistoryImage() {
        return historyImage;
    }

    public void setHistoryImage(Bitmap historyImage) {
        this.historyImage = historyImage;
    }
}
