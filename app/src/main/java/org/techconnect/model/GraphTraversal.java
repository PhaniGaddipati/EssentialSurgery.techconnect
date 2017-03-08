package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class allows me to have a structured approach to traversing
 * a flowchart graph object during the self help fragment. Can modify as
 * we see fit to extract data
 * Created by doranwalsten on 9/24/16.
 */
public class GraphTraversal implements Parcelable {

    public static final Creator<GraphTraversal> CREATOR = new Creator<GraphTraversal>() {
        @Override
        public GraphTraversal createFromParcel(Parcel in) {
            return new GraphTraversal(in);
        }

        @Override
        public GraphTraversal[] newArray(int size) {
            return new GraphTraversal[size];
        }
    };
    private Graph graph;
    private Map<String, String> currentOptions;
    private String currentVertexId;
    private Stack<String> historyStack = new Stack<>();//Use this to track a current traversal of the graph

    public GraphTraversal(Graph g) {
        this.graph = g;
        this.currentVertexId = g.getFirstVertex();
        this.currentOptions = g.getOptions(g.getFirstVertex());
    }

    protected GraphTraversal(Parcel in) {
        graph = in.readParcelable(Graph.class.getClassLoader());
        currentVertexId = in.readString();
        List<String> stackList = new ArrayList<>();
        in.readStringList(stackList);
        this.historyStack = new Stack<>();
        for (String s : stackList) {
            this.historyStack.push(s);
        }
        setCurrentVertex(currentVertexId);
    }

    /**
     * When we select an option to go to the next vertex in a flowchart, traverse to that vertex
     * in the graph.
     *
     * @param opt - The string option which corresponds to the next vertex in the graph to visit
     */
    public void selectOption(String opt) {
        historyStack.push(this.currentVertexId);
        this.currentVertexId = this.currentOptions.get(opt);
        this.currentOptions.clear();
        this.currentOptions = this.graph.getOptions(this.currentVertexId);
    }


    /**
     * If we need to go back to the previous vertex seen
     */
    public void stepBack() {
        setCurrentVertex(historyStack.pop());
    }

    /**
     * Used to determine if there have been previous steps in the traversal of the graph
     *
     * @return If the historyStack is not empty, there have been previous steps
     */
    public boolean hasPrevious() {
        return !historyStack.isEmpty();
    }

    public Set<String> getOptions() {
        return this.currentOptions.keySet();
    }

    /**
     * Use the currVertex string to return the current Vertex object if we need to access fields
     * This is because most operations are on the actual object itself intead of the ID
     * We store the ID in the traversal to save space (?)
     *
     * @return The corresponding vertex object
     */
    public Vertex getCurrentVertex() {
        return this.graph.getVertex(this.currentVertexId);
    }

    public void setCurrentVertex(String vertexId) {
        this.currentVertexId = vertexId;
        this.currentOptions = graph.getOptions(vertexId);
    }

    /**
     * When resotring a session from SQL, need to restore the history in the traversal
     *
     * @param history - stack of past vertex IDs
     */
    public void setHistoryStack(List<String> history) {
        for (int i = 0; i < history.size() - 2; i++) {
            this.historyStack.push(history.get(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(graph, i);
        parcel.writeString(currentVertexId);
        parcel.writeStringList(new ArrayList<>(historyStack));
    }
}
