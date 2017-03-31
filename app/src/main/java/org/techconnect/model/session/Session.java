package org.techconnect.model.session;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.techconnect.model.FlowChart;
import org.techconnect.model.GraphTraversal;
import org.techconnect.model.Vertex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Phani on 1/26/2016.
 * <p>
 * A particular "session" is a flowchart traversal. This logs the flow
 * of the session and generates the report.
 */
public class Session implements Parcelable {

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
    private String id;
    private FlowChart flowChart;
    private GraphTraversal traversal; //Step through the graph

    private long createdDate;
    private long finishedDate;
    private String deviceName = "";
    private String manufacturer = "";
    private String department = "";
    private String modelNumber = "";
    private String serialNumber = "";
    private String problem = "";
    private String solution = "";
    private String notes = "";
    private boolean finished = false;

    private List<String> history = new ArrayList<>(); //list of seen vertex IDs
    private List<String> optionHistory = new ArrayList<>();//list of user responses


    public Session(FlowChart flowchart) {
        this.createdDate = new Date().getTime();
        this.flowChart = flowchart;
        if (flowchart != null) {
            this.deviceName = flowchart.getName(); //Maybe? May reconsider this constructor
            this.traversal = new GraphTraversal(flowchart.getGraph());
            history.add(this.traversal.getCurrentVertex().getId());
        }
    }

    /**
     * Build a Session from a Parcel object
     *
     * @param in
     */
    public Session(Parcel in) {
        this.id = in.readString();
        this.createdDate = in.readLong();
        this.finishedDate = in.readLong();
        finished = in.readByte() != 0;
        this.deviceName = in.readString();
        this.department = in.readString();
        this.manufacturer = in.readString();
        this.modelNumber = in.readString();
        this.serialNumber = in.readString();
        this.problem = in.readString();
        this.solution = in.readString();
        this.notes = in.readString();
        in.readList(this.history, String.class.getClassLoader());
        in.readList(this.optionHistory, String.class.getClassLoader());
        this.flowChart = in.readParcelable(FlowChart.class.getClassLoader());
        this.traversal = in.readParcelable(GraphTraversal.class.getClassLoader());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //Save
    public long getCreatedDate() {
        return createdDate;
    }

    //Save
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    //Save
    public String getDepartment() {
        return department;
    }

    //Save
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;

    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> hist) {
        this.history = hist;
    }

    /**
     * Use the current history object to restore the traversal stack object
     */
    public void updateHistoryStack() {
        if (hasChart()) {
            this.traversal.setHistoryStack(history);
        } else {
            Log.e(getClass().toString(),"No Flowchart attached");
        }
    }

    public List<String> getOptionHistory() {
        return optionHistory;
    }

    public void setOptionHistory(List<String> opt_hist) {
        this.optionHistory = opt_hist;
    }

    /**
     * Return the current vertex so it's fields can be used by different view.
     * This is the crucial method needed to interact with the underlying flowchart object
     *
     * @return Current vertex the traversal object is looking at
     */
    public Vertex getCurrentVertex() {
        return this.traversal.getCurrentVertex();//Simplify where this is referenced
    }

    public void setCurrentVertex(String id) {
        if (hasChart()) {
            this.traversal.setCurrentVertex(id);
        } else {
            Log.e(getClass().toString(),"No Flowchart attached");
        }
    }

    /**
     * Need to return the current options to views so they can populate buttons and the like
     *
     * @return The keyset of the GraphTraversal object
     */
    public Set<String> getCurrentOptions() {
        return this.traversal.getOptions();
    }

    public FlowChart getFlowchart() {
        return flowChart;
    }


    public void selectOption(String option) {
        optionHistory.add(option);
        traversal.selectOption(option);//Select, update the traversal object
        history.add(traversal.getCurrentVertex().getId());
    }

    public void goBack() {
        //Safety check. In theory, should only be able to be called when the back button is enabled,
        //which is when the session has a previous step? May be able to remove
        if (traversal.hasPrevious()) {
            optionHistory.add("Back");
            traversal.stepBack();
            history.add(traversal.getCurrentVertex().getId());
        }
    }

    public boolean hasPrevious() {
        return traversal.hasPrevious();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //Decided to not write GraphTraversal object since this can be initialized from the
        //FlowChart graph object and the end of history if need be
        parcel.writeString(id);
        parcel.writeLong(createdDate);
        parcel.writeLong(finishedDate);
        parcel.writeByte((byte) (finished ? 1 : 0));
        parcel.writeString(deviceName);
        parcel.writeString(department);
        parcel.writeString(manufacturer);
        parcel.writeString(modelNumber);
        parcel.writeString(serialNumber);
        parcel.writeString(problem);
        parcel.writeString(solution);
        parcel.writeString(notes);
        parcel.writeList(history);
        parcel.writeList(optionHistory);
        parcel.writeParcelable(flowChart, 0);//Just need the flowchart, not traversal
        parcel.writeParcelable(traversal, 0);

    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public long getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(long finishedDate) {
        this.finishedDate = finishedDate;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean hasChart() {
        return this.flowChart != null;
    }
}
