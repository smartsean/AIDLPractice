package com.sean.aidlpractice.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author sean
 */
public class Message implements Parcelable {
    private int id;
    private String name;
    private String content;
    private String date;
    private int friendId;
    private String friendName;

    public Message() {
    }

    public Message(int id, String name, String content, String date, int friendId, String friendName) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date;
        this.friendId = friendId;
        this.friendName = friendName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeInt(this.friendId);
        dest.writeString(this.friendName);
    }

    protected Message(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.friendId = in.readInt();
        this.friendName = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", friendId=" + friendId +
                ", friendName='" + friendName + '\'' +
                '}';
    }
}
