package com.bhxx.xs_family.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ScheduleModel implements Parcelable {
    private int sdId;
    //作息时间
    private String sdTime;
    //课程描述
    private String sdContent;
    //班级类型0小1中2大
    private String sdClassType;
    private int sdParkId;
    private String sdParkName;

    public ScheduleModel(int sdId, String sdTime, String sdContent, String sdClassType, int sdParkId, String sdParkName) {
        super();
        this.sdId = sdId;
        this.sdTime = sdTime;
        this.sdContent = sdContent;
        this.sdClassType = sdClassType;
        this.sdParkId = sdParkId;
        this.sdParkName = sdParkName;
    }

    protected ScheduleModel(Parcel in) {
        sdId = in.readInt();
        sdTime = in.readString();
        sdContent = in.readString();
        sdClassType = in.readString();
        sdParkId = in.readInt();
        sdParkName = in.readString();
    }

    public int getSdParkId() {
        return sdParkId;
    }

    public void setSdParkId(int sdParkId) {
        this.sdParkId = sdParkId;
    }

    public String getSdParkName() {
        return sdParkName;
    }

    public void setSdParkName(String sdParkName) {
        this.sdParkName = sdParkName;
    }


    public String getSdClassType() {
        return sdClassType;
    }

    public void setSdClassType(String sdClassType) {
        this.sdClassType = sdClassType;
    }

    public String getSdContent() {
        return sdContent;
    }

    public void setSdContent(String sdContent) {
        this.sdContent = sdContent;
    }

    public String getSdTime() {
        return sdTime;
    }

    public void setSdTime(String sdTime) {
        this.sdTime = sdTime;
    }

    public int getSdId() {
        return sdId;
    }

    public void setSdId(int sdId) {
        this.sdId = sdId;
    }

    public static final Creator<ScheduleModel> CREATOR = new Creator<ScheduleModel>() {
        @Override
        public ScheduleModel createFromParcel(Parcel in) {
            return new ScheduleModel(in);
        }

        @Override
        public ScheduleModel[] newArray(int size) {
            return new ScheduleModel[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sdId);
        dest.writeString(sdTime);
        dest.writeString(sdContent);
        dest.writeString(sdClassType);
        dest.writeInt(sdParkId);
        dest.writeString(sdParkName);
    }
}
