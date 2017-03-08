package org.techconnect.network.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.techconnect.model.Comment;
import org.techconnect.model.Edge;
import org.techconnect.model.FlowChart;
import org.techconnect.model.FlowChart.ChartType;
import org.techconnect.model.Graph;
import org.techconnect.model.Vertex;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FlowChartDeserializer implements JsonDeserializer<FlowChart> {

    @Override
    public FlowChart deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        final JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new Gson(); //Need a Gson object to complete conversions if needed

        final String id = jsonObject.get("_id").getAsString();
        final String name = jsonObject.get("name").getAsString();
        final String desc = jsonObject.get("description").getAsString();
        final String upD = jsonObject.get("updatedDate").getAsString();
        final String v = jsonObject.get("version").getAsString();
        final String owner = jsonObject.get("owner").getAsString();
        final int score = jsonObject.get("score").getAsInt();
        final int upvotes = jsonObject.get("upvotes").getAsInt();
        final int downvotes = jsonObject.get("downvotes").getAsInt();


        //Now, getting the resources and comments fields
        final ArrayList<String> all_res = new ArrayList<>();
        final ArrayList<Comment> comments = new ArrayList<>();
        final ArrayList<String> all_users = new ArrayList<>();
        if (jsonObject.has("all_res")) {
            for (JsonElement j : jsonObject.get("all_res").getAsJsonArray()) {
                if (!j.isJsonNull())
                    all_res.add(j.getAsString());
            }
        }
        for (JsonElement j : jsonObject.get("comments").getAsJsonArray()) {
            if (!j.isJsonNull())
                comments.add(gson.fromJson(j, Comment.class));
        }
        if (jsonObject.has("all_usr_id")) {
            for (JsonElement j : jsonObject.get("all_usr_id").getAsJsonArray()) {
                if (!j.isJsonNull())
                    all_users.add(j.getAsString());
            }
        }

        //Adding the new fields which are relevant for devices/problems/other


        final ChartType type;
        switch (jsonObject.get("type").getAsString()) {
            case "device":
                type = ChartType.DEVICE;
                break;
            case "problem":
                type = ChartType.PROBLEM;
                break;
            default:
                type = ChartType.MISC;
        }

        //Now, set all of the fields of the FlowChart object
        final FlowChart flowchart = new FlowChart();
        flowchart.setId(id);
        flowchart.setName(name);
        flowchart.setDescription(desc);
        flowchart.setUpdatedDate(upD);
        flowchart.setVersion(v);
        flowchart.setOwner(owner);
        flowchart.setAllRes(all_res);
        flowchart.setAllUserIds(all_users);
        flowchart.setComments(comments);
        flowchart.setType(type);
        flowchart.setScore(score);
        flowchart.setUpvotes(upvotes);
        flowchart.setDownvotes(downvotes);

        //Here are the nullable fields
        final JsonElement image = jsonObject.get("image");
        if (image != JsonNull.INSTANCE) {
            flowchart.setImage(image.getAsString());
        } else {
            flowchart.setImage(null);
        }

        final ArrayList<String> resources;
        if (jsonObject.has("resources")) {
            resources = new ArrayList<String>();
            for (JsonElement j : jsonObject.get("resources").getAsJsonArray()) {
                resources.add(j.getAsString());
            }
            flowchart.setResources(resources);
        }

        final Graph g;
        if (jsonObject.has("graph")) {
            JsonObject temp_graph = jsonObject.get("graph").getAsJsonObject();

            //This object should have 2 properties, vertices and edges
            JsonArray vertices = temp_graph.get("vertices").getAsJsonArray();
            JsonArray edges = temp_graph.get("edges").getAsJsonArray();
            ArrayList<Vertex> verts = new ArrayList<Vertex>();
            ArrayList<Edge> edg = new ArrayList<Edge>();
            //Get list of vertices
            for (JsonElement j : vertices) {
                verts.add(gson.fromJson(j, Vertex.class));
            }
            //Get list of edges
            for (JsonElement j : edges) {
                edg.add(gson.fromJson(j, Edge.class));
            }
            //Get the first vertex from the list and set that as the root
            String root_ID = temp_graph.get("firstVertex").getAsString();

            g = new Graph(verts, edg, root_ID);
            flowchart.setGraph(g);
        }

        return flowchart;

    }

}
