package com.neykov.podcastportal.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Tag implements Parcelable, Comparable<Tag>{

    @Expose
    private String title;
    @Expose
    private String tag;
    @Expose
    private int usage;

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public int getUsage() {
        return usage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.tag);
        dest.writeInt(this.usage);
    }

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.title = in.readString();
        this.tag = in.readString();
        this.usage = in.readInt();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int compareTo(Tag another) {
        return this.getUsage() > another.getUsage() ?
                -1 : this.getUsage() < another.getUsage() ? +1 : 0;
    }
}