package org.techconnect.network.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.techconnect.model.Comment;
import org.techconnect.model.FlowChart;
import org.techconnect.model.Vertex;

import java.lang.reflect.Type;

//This class is needed in order to write this information, so let's get this shit going
public class FlowChartSerializer implements JsonSerializer<FlowChart> {

	public JsonElement serialize(FlowChart flowchart, Type typeOfId, JsonSerializationContext context) {
		Gson myGson = new Gson();
		final JsonObject jsonObject = new JsonObject();//This is the new object which will be written to the file
		jsonObject.addProperty("_id", flowchart.getId());
		jsonObject.addProperty("name", flowchart.getName());
		jsonObject.addProperty("description", flowchart.getDescription());
		jsonObject.addProperty("updatedDate", flowchart.getUpdatedDate());
		jsonObject.addProperty("version", flowchart.getVersion());
		jsonObject.addProperty("owner", flowchart.getOwner());
		jsonObject.addProperty("score",flowchart.getScore());
		jsonObject.addProperty("upvotes",flowchart.getUpvotes());
		jsonObject.addProperty("downvotes",flowchart.getDownvotes());

	
		//Determine type
		switch(flowchart.getType()) {
			case DEVICE:
				jsonObject.addProperty("type", "device");
				break;
			case PROBLEM:
				jsonObject.addProperty("type", "problem");
			default :
				jsonObject.addProperty("type", "misc");
		}
		
		//Now, we get to have fun with the other objects
		//Resources
		final JsonArray all_res = new JsonArray();
		
		//Comments
		final JsonArray comments = new JsonArray();
		if (flowchart.getComments() != null) {
			for (Comment c : flowchart.getComments()) {
				comments.add(myGson.toJson(c));
			}
			jsonObject.add("comments", comments);
		}

		//Users
		final JsonArray users = new JsonArray();
		if (flowchart.getAllUserIds() != null) {
			for (String user : flowchart.getAllUserIds()) {
				users.add(user);
			}
			jsonObject.add("all_usr_id", comments);
		}
		
		//Now, we have to convert the TinkerGraph, which is beautiful
		JsonObject graph = new JsonObject();
		JsonArray vertices = new JsonArray();
        //JsonArray edges = new JsonArray();
        for (Vertex v : flowchart.getGraph().getVertices()) {
        	for (String res : v.getResources()) {
        		all_res.add(res);
        	}
        	vertices.add(myGson.toJson(v,Vertex.class));
        }
        //Now that I've parsed over the graph, I'll have the resources! What a concept!
		jsonObject.add("all_res", all_res);
        graph.add("vertices", vertices);
        graph.add("edges", myGson.toJsonTree(flowchart.getGraph().getEdges())); //Trying a more direct call
		graph.addProperty("firstVertex",flowchart.getGraph().getFirstVertex());//Store the reference of the first vertex in the graphs
        jsonObject.add("graph", graph);
        
        //Checking to see if there any of the optional fields are present
        if (flowchart.getImage() != null) {
        	jsonObject.addProperty("image", flowchart.getImage());
        } else {
        	jsonObject.add("image", JsonNull.INSTANCE);
        }
        if(flowchart.getResources() != null) {
        	JsonElement res = myGson.toJsonTree(flowchart.getResources());
        	jsonObject.add("resources", res); //Just to try it out, haha!
        }
		return jsonObject;
	}
}
