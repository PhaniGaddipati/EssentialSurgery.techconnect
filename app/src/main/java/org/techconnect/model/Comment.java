package org.techconnect.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.techconnect.misc.Utils;

public class Comment implements Parcelable {


    public static final String PARENT_TYPE_VERTEX = "node";
    public static final String PARENT_TYPE_CHART = "chart";

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
    private String text;
    private String attachment;
    private String ownerId;
    private String createdDate;
    private String ownerName;
    private String nodeId;


    public Comment() {
        createdDate = Utils.formatTimeMillis(System.currentTimeMillis());
    }

    protected Comment(Parcel in) {
        text = in.readString();
        attachment = in.readString();
        ownerId = in.readString();
        createdDate = in.readString();
        ownerName = in.readString();
        nodeId = in.readString();
    }

    /**
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * @param attachment The attachment
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(attachment);
        parcel.writeString(ownerId);
        parcel.writeString(createdDate);
        parcel.writeString(ownerName);
        parcel.writeString(nodeId);
    }
}
