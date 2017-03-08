package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class FlowChart implements Parcelable, Commentable {

    public static final Creator<FlowChart> CREATOR = new Creator<FlowChart>() {
        @Override
        public FlowChart createFromParcel(Parcel in) {
            return new FlowChart(in);
        }

        @Override
        public FlowChart[] newArray(int size) {
            return new FlowChart[size];
        }
    };
    private String _id;
    private String name;
    private String description;
    private String updatedDate;
    private String version;
    private String owner;
    private Graph graph;
    private List<String> all_res;
    private List<String> all_usr_ids;
    private List<Comment> comments;
    private String image;
    private List<String> resources;
    private ChartType type;
    private int score;
    private int upvotes;
    private int downvotes;

    public FlowChart() {

    }

    protected FlowChart(Parcel in) {
        _id = in.readString();
        name = in.readString();
        description = in.readString();
        updatedDate = in.readString();
        version = in.readString();
        owner = in.readString();
        all_res = in.createStringArrayList();
        all_usr_ids = in.createStringArrayList();
        comments = in.createTypedArrayList(Comment.CREATOR);
        image = in.readString();
        resources = in.createStringArrayList();
        score = in.readInt();
        upvotes = in.readInt();
        downvotes = in.readInt();
        type = ChartType.valueOf(in.readString());
        graph = in.readParcelable(Graph.class.getClassLoader());
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    @Override
    public String getParentType() {
        return Comment.PARENT_TYPE_CHART;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int s) {
        this.score = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updateDate) {
        this.updatedDate = updateDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<String> getAllRes() {
        return this.all_res;
    }

    public void setAllRes(List<String> ar) {
        this.all_res = ar;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> c) {
        this.comments = c;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String im) {
        this.image = im;
    }

    public List<String> getResources() {
        return this.resources;
    }

    public void setResources(List<String> res) {
        this.resources = res;
    }

    public ChartType getType() {
        return this.type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(updatedDate);
        parcel.writeString(version);
        parcel.writeString(owner);
        parcel.writeStringList(all_res);
        parcel.writeStringList(all_usr_ids);
        parcel.writeTypedList(comments);
        parcel.writeString(image);
        parcel.writeStringList(resources);
        parcel.writeInt(score);
        parcel.writeInt(upvotes);
        parcel.writeInt(downvotes);
        parcel.writeString(type.name());
        parcel.writeParcelable(graph, 0);
    }

    public List<String> getAllUserIds() {
        return all_usr_ids;
    }

    public void setAllUserIds(List<String> all_user_ids) {
        this.all_usr_ids = all_user_ids;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public enum ChartType {
        DEVICE, MISC, PROBLEM
    }
}


