package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.techconnect.misc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph implements Parcelable {

    public static final Creator<Graph> CREATOR = new Creator<Graph>() {
        @Override
        public Graph createFromParcel(Parcel in) {
            return new Graph(in);
        }

        @Override
        public Graph[] newArray(int size) {
            return new Graph[size];
        }
    };
    final private String firstVertex;//This is the first vertex id.
    private Map<String, Vertex> vertices;
    private Map<String, Edge> edges;

    //Best constructor, actually builds all connections in the graph
    public Graph(List<Vertex> V, List<Edge> E, String firstVertex) {
        vertices = new HashMap<>();
        edges = new HashMap<>();

        //Iterate over all vertices and add to the map
        for (Vertex v : V) {
            vertices.put(v.getId(), v);
        }

        //Iterate over the edges and add the edges to the respective in and out
        //lists in the vertices
        for (Edge e : E) {
            if (vertices.containsKey(e.getInV()) && vertices.containsKey(e.getOutV())) {
                edges.put(e.getId(), e);
                vertices.get(e.getOutV()).addOutEdge(e.getId());
                vertices.get(e.getInV()).addInEdge(e.getId());
            } else {
                Log.e(getClass().getName(), "Edge with nonexistant nodes: " + e.getInV() + " or " + e.getOutV());
            }
        }
        //Set the root ID in order for it to be readily accessible, as well as ID and Owner
        this.firstVertex = firstVertex;
    }

    protected Graph(Parcel in) {
        firstVertex = in.readString();
        vertices = Utils.readParcelableMap(in, Vertex.class);
        edges = Utils.readParcelableMap(in, Edge.class);
    }

    /**
     * Use this method to get the options (labels on outgoing vertices) for a specific vertex do not
     * return the edge id. Return the actual vertex id that the particular option leads to next
     *
     * @param v_id ID of the vertex to retrieve the options
     * @return
     */
    public Map<String, String> getOptions(String v_id) {
        HashMap<String, String> options = new HashMap<String, String>();
        for (String e : vertices.get(v_id).getOutEdges()) { //For each outgoing edge
            String key = getEdge(e).getLabel(); //Option label
            String value = getEdge(e).getInV();//Vertex target
            options.put(key, value);
        }
        return options;
    }

    //Only want to get root, never set it
    public String getFirstVertex() {
        return this.firstVertex;
    }

    public Vertex getVertex(String id) {
        return vertices.get(id);
    }

    public Edge getEdge(String id) {
        return edges.get(id);
    }

    public List<Vertex> getVertices() {
        ArrayList<Vertex> verts = new ArrayList<Vertex>();
        for (String key : this.vertices.keySet()) {
            verts.add(this.vertices.get(key));
        }
        return verts;
    }

    public List<Edge> getEdges() {
        ArrayList<Edge> edg = new ArrayList<Edge>();
        for (String key : this.edges.keySet()) {
            edg.add(this.edges.get(key));
        }
        return edg;
    }

    //Remove given vertex as well as any associated edges in the graph
    public Vertex removeVertex(String id) {
        Vertex toRemove = vertices.remove(id);
        for (String key : toRemove.getOutEdges()) {
            edges.remove(key);
        }
        return toRemove;
    }

    //Remove given edge, but not the nodes on either side
    public Edge removeEdge(String id) {
        return edges.remove(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstVertex);
        Utils.writeParcelableMap(parcel, 0, vertices);
        Utils.writeParcelableMap(parcel, 0, edges);
    }
}
