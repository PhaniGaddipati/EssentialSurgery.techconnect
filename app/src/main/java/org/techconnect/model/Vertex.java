package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Parcelable, Commentable {
    public static final Creator<Vertex> CREATOR = new Creator<Vertex>() {
        @Override
        public Vertex createFromParcel(Parcel in) {
            return new Vertex(in);
        }

        @Override
        public Vertex[] newArray(int size) {
            return new Vertex[size];
        }
    };
    //These are fields which are defined by the JSON files
    private String _id;
    private String name;
    private String details;
    private List<String> resources;
    private List<String> images;
    private List<Comment> comments;
    //These are fields which will be generated once the file is read
    //Don't want to rip these to Json
    private List<String> outEdges;
    private List<String> inEdges;

    //Default constructor, helps to initialize the fields which are not initialized directly with
    //the deserializer
    public Vertex() {
        outEdges = new ArrayList<String>();
        inEdges = new ArrayList<String>();
    }

    protected Vertex(Parcel in) {
        _id = in.readString();
        name = in.readString();
        details = in.readString();
        resources = in.createStringArrayList();
        images = in.createStringArrayList();
        comments = in.createTypedArrayList(Comment.CREATOR);
        outEdges = in.createStringArrayList();
        inEdges = in.createStringArrayList();
    }

    //Use this method to determine if a vertex ends a flowchart
    public boolean hasOutEdges() {
        return !outEdges.isEmpty();
    }

    public String getId() {
        return this._id;
    }

    public void setId(String id) {
        this._id = id;
    }

    @Override
    public String getParentType() {
        return Comment.PARENT_TYPE_VERTEX;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<String> getResources() {
        return this.resources;
    }

    public void setResources(List<String> res) {
        this.resources = res;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setImages(List<String> im) {
        this.images = im;
    }

    public boolean hasImages() {
        return !this.images.isEmpty();
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getOutEdges() {
        return this.outEdges;
    }

    public void setOutEdges(List<String> e) {
        this.outEdges = e;
    }

    public void addOutEdge(String e) {
        this.outEdges.add(e);
    }

    public List<String> getInEdges() {
        return this.inEdges;
    }

    public void setInEdges(List<String> e) {
        this.inEdges = e;
    }

    public void addInEdge(String e) {
        this.inEdges.add(e);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(details);
        parcel.writeStringList(resources);
        parcel.writeStringList(images);
        parcel.writeTypedList(comments);
        parcel.writeStringList(outEdges);
        parcel.writeStringList(inEdges);
    }
}
